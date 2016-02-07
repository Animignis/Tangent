package com.enricotj.tangent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.R;
import com.enricotj.tangent.models.StoryNode;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

/**
 * Created by kassalje on 1/24/2016.
 */
public class StoryNodeAdapter extends RecyclerView.Adapter<StoryNodeAdapter.ViewHolder> implements ChildEventListener{

    private ArrayList<StoryNode> mStoryNodes = new ArrayList<>();

    private Firebase mFirebase;

    public StoryNodeAdapter() {
        mFirebase = new Firebase(Constants.FIREBASE_URL + "nodes");
        mFirebase.addChildEventListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_node_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StoryNode storyNode = mStoryNodes.get(position);
        holder.mTitleText.setText(storyNode.getTitle());
        holder.mBodyPreviewText.setText(storyNode.getBody());
    }

    @Override
    public int getItemCount() {
        return mStoryNodes.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        // Deserialize the JSON.
        StoryNode storyNode = dataSnapshot.getValue(StoryNode.class);
        //Log.d(Constants.TAG, "" + storyNode.getBranches()[0]);
        // We set the key ourselves.
        storyNode.setKey(dataSnapshot.getKey());
        // Add it to our local list and display it
        mStoryNodes.add(0, storyNode);
        notifyDataSetChanged();
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
}
