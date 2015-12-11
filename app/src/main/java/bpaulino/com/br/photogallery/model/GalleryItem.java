package bpaulino.com.br.photogallery.model;

/**
 * Created by bruno on 12/10/15.
 */
public class GalleryItem {

    private String mCaption;
    private String mId;
    private String mUrl;

//    public GalleryItem(String caption, String id, String url) {
//        mCaption = caption;
//        mId = id;
//        mUrl = url;
//    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return mCaption;
    }
}
