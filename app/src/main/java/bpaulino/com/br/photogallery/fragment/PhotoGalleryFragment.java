package bpaulino.com.br.photogallery.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bpaulino.com.br.photogallery.R;
import bpaulino.com.br.photogallery.model.GalleryItem;
import bpaulino.com.br.photogallery.service.FlickrService;

/**
 * Created by bruno on 12/9/15.
 */
public class PhotoGalleryFragment extends Fragment {

    private static int GRID_COLUMNS = 3;
    private static String TAG = "PhotoGalleryFragment";

    private RecyclerView mRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
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


    // =========================================================================================
    // RECYCLERVIEW HOLDER
    // =========================================================================================
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView mCaptionTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mCaptionTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            String caption = galleryItem.getCaption();
            if ( caption.length() > 50 ) {
                caption = caption.substring(0, 50);
            }
            mCaptionTextView.setText(caption);
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
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
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
