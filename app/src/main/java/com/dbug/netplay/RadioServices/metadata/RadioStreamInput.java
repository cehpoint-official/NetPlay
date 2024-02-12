package com.dbug.netplay.RadioServices.metadata;

import androidx.annotation.NonNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RadioStreamInput extends FilterInputStream {

    private final String characterEncoding;
    private final RadioMetadataListener radioMetadataListener;
    private final int interval;
    private int remaining;

    public RadioStreamInput(InputStream in, int interval, String characterEncoding, RadioMetadataListener radioMetadataListener) {
        super(in);
        this.interval = interval;
        this.characterEncoding = characterEncoding != null ? characterEncoding : "UTF-8";
        this.radioMetadataListener = radioMetadataListener;
        this.remaining = interval;
    }

    @Override
    public int read() throws IOException {
        int ret = super.read();

        if (--remaining == 0) {
            getMetadata();
        }

        return ret;
    }

    @Override
    public int read(@NonNull byte[] buffer, int offset, int len ) throws IOException {
        int ret = super.in.read( buffer, offset, remaining < len ? remaining : len );

        if (remaining == ret) {
            getMetadata();
        } else {
            remaining -= ret;
        }

        return ret;
    }

    private int readFully(byte[] buffer, int offset, int size) throws IOException {
        int n;
        int oo = offset;

        while (size > 0 && (n = in.read( buffer, offset, size )) != -1) {
            offset += n;
            size -= n;
        }

        return offset - oo;
    }

    private void getMetadata() throws IOException {
        remaining = interval;
        int size = super.in.read();
        if (size < 1) return;
        size *= 16;
        byte[] buffer = new byte[ size ];
        size = readFully(buffer, 0, size );
        for (int i=0; i < size; i++) {
            if (buffer[i] == 0) {
                size = i;
                break;
            }
        }

        String s;

        try {
            s = new String(buffer, 0, size, characterEncoding );
        }
        catch (Exception e) {
            return;
        }
        parseMetadata(s);
    }

    private void parseMetadata(String data) {
        Matcher match = Pattern.compile("StreamTitle='([^;]*)'").matcher(data.trim());
        if (match.find())
        {

            String[] metadata = match.group(1).split(" - ");
            switch (metadata.length) {
                case 3:
                    metadataReceived(metadata[1], metadata[2], metadata[0]);
                    break;
                case 2:
                    metadataReceived(metadata[0], metadata[1], null);
                    break;
                case 1:
                    metadataReceived(null, null, metadata[0]);
            }
        }
    }

    private void metadataReceived(String artist, String song, String show) {
        if (this.radioMetadataListener != null) {
            this.radioMetadataListener.onMetadataReceived(artist, song, show);
        }
    }
}

