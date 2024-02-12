package com.dbug.netplay.RadioServices.metadata;

import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

import okhttp3.CacheControl;
import okhttp3.Call;

public final class DataSourceFactory extends HttpDataSource.BaseFactory {

    private final Call.Factory callFactory;
    private final String userAgent;
    private final TransferListener transferListener;
    private final MetadataListener metadataListener;
    private final CacheControl cacheControl;

    public DataSourceFactory(Call.Factory callFactory, String userAgent,
                             TransferListener transferListener,
                             MetadataListener metadataListener) {
        this(callFactory, userAgent, transferListener, metadataListener, null);
    }

    private DataSourceFactory(Call.Factory callFactory, String userAgent,
                              TransferListener transferListener,
                              MetadataListener metadataListener, CacheControl cacheControl) {
        this.callFactory = callFactory;
        this.userAgent = userAgent;
        this.transferListener = transferListener;
        this.metadataListener = metadataListener;
        this.cacheControl = cacheControl;
    }

    @Override
    protected HttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties requestProperties) {
        return new DataSource(callFactory, userAgent, null, transferListener, metadataListener, cacheControl);
    }

}
