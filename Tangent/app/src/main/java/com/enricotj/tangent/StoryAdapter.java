package com.enricotj.tangent;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by kassalje on 1/24/2016.
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> implements ChildEventListener{

    private ArrayList<Story> mStories = new ArrayList<>();
    private ArrayList<StoryNode> mRootNodes = new ArrayList<>();

    private Firebase mFirebaseStories;
    private Firebase mFirebaseStoryNodes;

    private StoryNodeSelectCallback mStoryNodeSelectCallback;

    public StoryAdapter(StoryNodeSelectCallback storyNodeSelectCallback) {
        mStoryNodeSelectCallback = storyNodeSelectCallback;

        mFirebaseStories = new Firebase(Constants.FIREBASE_STORIES);
        mFirebaseStories.addChildEventListener(this);

        mFirebaseStoryNodes = new Firebase(Constants.FIREBASE_NODES);
    }

    public void add(StoryNode storyNode) {
        Firebase newStoryNode = mFirebaseStoryNodes.push();
        newStoryNode.setValue(storyNode);
        Story story = new Story(storyNode.getTimestamp(), 0, newStoryNode.getKey(), 1, 1);
        mFirebaseStories.push().setValue(story);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_node_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StoryNode storyNode = mRootNodes.get(position);
        holder.mTitleText.setText(storyNode.getTitle());
        holder.mBodyPreviewText.setText(storyNode.getBody());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoryNodeSelectCallback.onStorySelect(storyNode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRootNodes.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        // Deserialize the JSON.
        final Story story = dataSnapshot.getValue(Story.class);
        Log.d(Constants.TAG, "" + story.getRoot());

        // We set the key ourselves.
        story.setKey(dataSnapshot.getKey());

        Firebase ref = new Firebase(Constants.FIREBASE_NODES + "/" + story.getRoot());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mStories.add(0, story);
                final StoryNode storyNode = dataSnapshot.getValue(StoryNode.class);
                storyNode.setKey(dataSnapshot.getKey());
                mRootNodes.add(0, storyNode);
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
        public void onStorySelect(StoryNode storyNode);
    }
}
