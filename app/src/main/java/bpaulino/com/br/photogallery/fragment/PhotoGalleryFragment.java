package bpaulino.com.br.photogallery.fragment;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bpaulino.com.br.photogallery.R;
import bpaulino.com.br.photogallery.model.GalleryItem;
import bpaulino.com.br.photogallery.service.FlickrService;
import bpaulino.com.br.photogallery.service.ThumbnailDownloaderService;

/**
 * Created by bruno on 12/9/15.
 */
public class PhotoGalleryFragment extends Fragment {

    private static int GRID_COLUMNS = 3;
    private static String TAG = "PhotoGalleryFragment";

    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloaderService<PhotoHolder> mThumbnailDownloaderService;

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        mThumbnailDownloaderService = new ThumbnailDownloaderService<>();
        mThumbnailDownloaderService.start();
        mThumbnailDownloaderService.getLooper();
        Log.i(TAG, "BACKGROUND Thread Started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_COLUMNS));

        setupAdapter();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloaderService.quit();
        Log.i(TAG, "BACKGROUND Thread Destroyed");

    }

    // =========================================================================================
    // RECYCLERVIEW HOLDER
    // =========================================================================================
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

    }

    // =========================================================================================
    // RECYCLERVIEW ADAPTER
    // =========================================================================================
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> items) {
            mGalleryItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.placeholder);
            holder.bindDrawable(placeholder);
            mThumbnailDownloaderService.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    // =========================================================================================
    // SETUP ADAPTER IN RECYCLERVIEW
    // =========================================================================================
    public void setupAdapter() {
        // test if the fragment was attached to the Activity
        if(isAdded()) {
            mRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    // =========================================================================================
    // BACKGROUND TASKS
    // =========================================================================================
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrService().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }
}
