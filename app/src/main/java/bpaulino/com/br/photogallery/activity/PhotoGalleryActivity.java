package bpaulino.com.br.photogallery.activity;

import android.support.v4.app.Fragment;

import bpaulino.com.br.photogallery.fragment.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
