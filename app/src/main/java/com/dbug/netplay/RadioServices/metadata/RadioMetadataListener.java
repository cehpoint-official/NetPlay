

package com.dbug.netplay.RadioServices.metadata;

interface RadioMetadataListener {
    void onMetadataReceived(String artist, String song, String show);
}
