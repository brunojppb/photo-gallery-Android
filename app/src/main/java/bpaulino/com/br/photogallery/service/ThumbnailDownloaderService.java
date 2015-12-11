package bpaulino.com.br.photogallery.service;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by bruno on 12/11/15.
 */
public class ThumbnailDownloaderService<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";

    public ThumbnailDownloaderService() {
        super(TAG);
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
    }


}
