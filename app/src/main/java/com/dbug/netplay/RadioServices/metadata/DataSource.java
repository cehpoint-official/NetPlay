package com.dbug.netplay.RadioServices.metadata;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

final class DataSource implements HttpDataSource, RadioMetadataListener {

    private class IcyHeader {
        String channels;
        String bitrate;
        String station;
        String genre;
        String url;
    }

    private static final String MP3 = "audio/mpeg";
    private static final String AAC = "audio/aac";
    private static final String AACP = "audio/aacp";
    private static final String OGG = "application/ogg";
    private static final String ICY_METADATA = "Icy-Metadata";
    private static final String ICY_METAINT = "icy-metaint";


    private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference<>();

    private final Call.Factory callFactory;
    private final String userAgent;
    private final Predicate<String> contentTypePredicate;
    private final TransferListener transferListener;
    private final MetadataListener metadataListener;
    private final CacheControl cacheControl;
    private final HashMap<String, String> requestProperties;

    private DataSpec dataSpec;
    private Response response;
    private InputStream responseByteStream;
    private boolean opened;

    private long bytesToSkip;
    private long bytesToRead;
    private long bytesSkipped;
    private long bytesRead;

    private IcyHeader icyHeader;

    public DataSource(Call.Factory callFactory, String userAgent,
                      Predicate<String> contentTypePredicate, TransferListener transferListener,
                      MetadataListener metadataListener,
                      CacheControl cacheControl) {
        this.callFactory = Assertions.checkNotNull(callFactory);
        this.userAgent = Assertions.checkNotEmpty(userAgent);
        this.contentTypePredicate = contentTypePredicate;
        this.transferListener = transferListener;
        this.metadataListener = metadataListener;
        this.cacheControl = cacheControl;
        this.requestProperties = new HashMap<>();
    }

    @Override
    public Uri getUri() {
        return response == null ? null : Uri.parse(response.request().url().toString());
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return response == null ? null : response.headers().toMultimap();
    }

    @Override
    public void setRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);
        synchronized (requestProperties) {
            requestProperties.put(name, value);
        }
    }

    @Override
    public void clearRequestProperty(String name) {
        Assertions.checkNotNull(name);
        synchronized (requestProperties) {
            requestProperties.remove(name);
        }
    }

    @Override
    public void clearAllRequestProperties() {
        synchronized (requestProperties) {
            requestProperties.clear();
        }
    }

    @Override
    public int getResponseCode() {
        return 0;
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        //Empty Method
    }

    @Override
    public long open(DataSpec dataSpec) throws HttpDataSourceException {
        this.dataSpec = dataSpec;
        this.bytesRead = 0;
        this.bytesSkipped = 0;
        setRequestProperty(ICY_METADATA, "1");
        Request request = makeRequest(dataSpec);
        try {
            response = callFactory.newCall(request).execute();
            responseByteStream = getInputStream(response);

        } catch (IOException e) {
            throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e,
                    dataSpec, HttpDataSourceException.TYPE_OPEN);
        }

        int responseCode = response.code();

        if (!response.isSuccessful()) {
            Map<String, List<String>> headers = request.headers().toMultimap();
            closeConnectionQuietly();
            @SuppressWarnings("deprecation") InvalidResponseCodeException exception = new InvalidResponseCodeException(
                    responseCode, headers, dataSpec);
            if (responseCode == 416) {
                exception.initCause(new DataSourceException(DataSourceException.POSITION_OUT_OF_RANGE));
            }
            throw exception;
        }

        MediaType mediaType = response.body().contentType();
        String contentType = mediaType != null ? mediaType.toString() : null;
        if (contentTypePredicate != null ) {//&& !contentTypePredicate.evaluate(contentType)
            closeConnectionQuietly();
            throw new InvalidContentTypeException(contentType, dataSpec);
        }


        bytesToSkip = responseCode == 200 && dataSpec.position != 0 ? dataSpec.position : 0;

        // Determine the length of the data to be read, after skipping.
        if (dataSpec.length != C.LENGTH_UNSET) {
            bytesToRead = dataSpec.length;
        } else {
            long contentLength = response.body().contentLength();
            bytesToRead = contentLength != -1 ? (contentLength - bytesToSkip) : C.LENGTH_UNSET;
        }

        opened = true;
        if (transferListener != null) {
            transferListener.onTransferStart(this, dataSpec, true);
        }

        return bytesToRead;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        try {
            skipInternal();
            return readInternal(buffer, offset, readLength);
        } catch (IOException e) {
            throw new HttpDataSourceException(e, dataSpec, HttpDataSourceException.TYPE_READ);
        }
    }

    @Override
    public void close() throws HttpDataSourceException {
        if (opened) {
            opened = false;
            if (transferListener != null) {
                transferListener.onTransferEnd(this, dataSpec, true);

            }
            closeConnectionQuietly();
        }
    }

    private Request makeRequest(DataSpec dataSpec) {
        boolean allowGzip = (dataSpec.flags & DataSpec.FLAG_ALLOW_GZIP) != 0;

        HttpUrl url = HttpUrl.parse(dataSpec.uri.toString());
        Request.Builder builder = new Request.Builder().url(url);
        if (cacheControl != null) {
            builder.cacheControl(cacheControl);
        }
        synchronized (requestProperties) {
            for (Map.Entry<String, String> property : requestProperties.entrySet()) {
                builder.addHeader(property.getKey(), property.getValue());
            }
        }
        builder.addHeader("User-Agent", userAgent);
        if (!allowGzip) {
            builder.addHeader("Accept-Encoding", "identity");
        }

        return builder.build();
    }

    private InputStream getInputStream(Response response) {
        String contentType = response.header("Content-Type");
        setIcyHeader(response.headers());
        InputStream in = response.body().byteStream();
        switch (contentType) {
            case MP3:
            case AAC:
            case AACP:
                int interval = (response.header(ICY_METAINT) == null) ? 8192 : Integer.parseInt(response.header(ICY_METAINT));
                in = new RadioStreamInput(in, interval, null, this);
                break;
            case OGG:
                in = new RadiooInputStream(in, this);
                break;
        }
        return in;
    }

    private void skipInternal() throws IOException {
        if (bytesSkipped == bytesToSkip) {
            return;
        }

        // Acquire the shared skip buffer.
        byte[] skipBuffer = skipBufferReference.getAndSet(null);
        if (skipBuffer == null) {
            skipBuffer = new byte[4096];
        }

        while (bytesSkipped != bytesToSkip) {
            int readLength = (int) Math.min(bytesToSkip - bytesSkipped, skipBuffer.length);
            int read = responseByteStream.read(skipBuffer, 0, readLength);
            if (Thread.interrupted()) {
                throw new InterruptedIOException();
            }
            if (read == -1) {
                throw new EOFException();
            }
            bytesSkipped += read;
            if (transferListener != null) {
                transferListener.onBytesTransferred(this, dataSpec, true, read);
            }
        }

        // Release the shared skip buffer.
        skipBufferReference.set(skipBuffer);
    }


    private int readInternal(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (bytesToRead != C.LENGTH_UNSET) {
            long bytesRemaining = bytesToRead - bytesRead;
            if (bytesRemaining == 0) {
                return C.RESULT_END_OF_INPUT;
            }
            readLength = (int) Math.min(readLength, bytesRemaining);
        }

        int read = responseByteStream.read(buffer, offset, readLength);
        if (read == -1) {
            if (bytesToRead != C.LENGTH_UNSET) {
                // End of stream reached having not read sufficient data.
                throw new EOFException();
            }
            return C.RESULT_END_OF_INPUT;
        }

        bytesRead += read;
        if (transferListener != null) {
            transferListener.onBytesTransferred(this, dataSpec, true, read);
        }
        return read;
    }

    private void closeConnectionQuietly() {
        response.body().close();
        response = null;
        responseByteStream = null;
    }

    private void setIcyHeader(Headers headers) {
        if (icyHeader == null) {
            icyHeader = new IcyHeader();
        }
        icyHeader.station = headers.get("icy-name");
        icyHeader.url = headers.get("icy-url");
        icyHeader.genre = headers.get("icy-genre");
        icyHeader.channels = headers.get("icy-channels");
        icyHeader.bitrate = headers.get("icy-br");
    }

    @Override
    public void onMetadataReceived(String artist, String song, String show) {
        if (metadataListener != null) {
            RMetadata rMetadata = new RMetadata(artist, song, show, icyHeader.channels, icyHeader.bitrate, icyHeader.station, icyHeader.genre, icyHeader.url);
            metadataListener.onMetadataReceived(rMetadata);
        }
    }
}