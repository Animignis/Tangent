package com.enricotj.tangent.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.R;
import com.enricotj.tangent.models.Story;
import com.enricotj.tangent.models.StoryNode;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kassalje on 1/24/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> implements ChildEventListener{

    private ArrayList<Story> mStories = new ArrayList<>();
    private ArrayList<StoryNode> mRootNodes = new ArrayList<>();

    private Firebase mFirebaseStories;

    private StoryNodeSelectCallback mStoryNodeSelectCallback;

    private final int PREVIEW_TEXT_CHAR_LIMIT = 33;

    private Map<String, String> filters = new HashMap<>();
    private int limit = 20;

    public StoryAdapter(StoryNodeSelectCallback storyNodeSelectCallback) {
        mStoryNodeSelectCallback = storyNodeSelectCallback;

        filters.put("Newest Stories", "dateCreated");
        filters.put("Most Read", "views");
        filters.put("Most Popular", "numfavorites");
        filters.put("Recently Updated", "lastupdated");

        mFirebaseStories = new Firebase(Constants.FIREBASE_STORIES);
        mFirebaseStories.removeEventListener(this);
        mFirebaseStories.orderByChild("dateCreated").limitToFirst(limit).addChildEventListener(this);
    }

    public void refresh() {
        mFirebaseStories.removeEventListener(this);
        mStories.clear();
        mRootNodes.clear();
        notifyDataSetChanged();
    }

    public void switchFilter(String filter) {
        refresh();
        mFirebaseStories.orderByChild(filters.get(filter)).limitToFirst(limit).addChildEventListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_node_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StoryNode storyNode = mRootNodes.get(position);
        final Story story = mStories.get(position);
        String title = trimPreviewText(storyNode.getTitle());
        String body = trimPreviewText(storyNode.getBody());
        holder.mTitleText.setText(title);
        holder.mBodyPreviewText.setText(body);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoryNodeSelectCallback.onStoryNodeSelect(storyNode, story.getKey());
            }
        });
    }

    private String trimPreviewText(String text) {
        String ret = text;
        if (ret.length() > PREVIEW_TEXT_CHAR_LIMIT) {
            ret = ret.substring(0, PREVIEW_TEXT_CHAR_LIMIT);
            ret += "...";
        }
        return ret;
    }

    @Override
    public int getItemCount() {
        return mRootNodes.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        // Deserialize the JSON.
        final Story story = dataSnapshot.getValue(Story.class);
        //Log.d(Constants.TAG, "" + story.getRoot());

        // We set the key ourselves.
        story.setKey(dataSnapshot.getKey());

        Firebase ref = new Firebase(Constants.FIREBASE_NODES + "/" + story.getRoot());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final StoryNode storyNode = dataSnapshot.getValue(StoryNode.class);
                storyNode.setKey(dataSnapshot.getKey());
                mRootNodes.add(0, storyNode);
                mStories.add(0, story);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mBodyPreviewText;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.title_text);
            mBodyPreviewText = (TextView) itemView.findViewById(R.id.preview_text);
        }
    }

    public interface StoryNodeSelectCallback {
        public void onStoryNodeSelect(StoryNode storyNode, String storyKey);
    }
}
