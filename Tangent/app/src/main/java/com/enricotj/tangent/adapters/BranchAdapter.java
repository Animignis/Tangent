package com.enricotj.tangent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by kassalje on 1/24/2016.
 */
public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> implements ValueEventListener{

    private ArrayList<StoryNode> mBranches = new ArrayList<>();
    private ArrayList<String> mBranchKeysListening = new ArrayList<>();
    private String mStoryKey;

    private StoryAdapter.StoryNodeSelectCallback mStoryNodeSelectCallback;

    private final int PREVIEW_TEXT_CHAR_LIMIT = 33;

    private Firebase mFirebaseNodes;

    public BranchAdapter(StoryAdapter.StoryNodeSelectCallback storyNodeSelectCallback, StoryNode node, String storyKey) {
        mStoryNodeSelectCallback = storyNodeSelectCallback;
        mStoryKey = storyKey;

        mFirebaseNodes = new Firebase(Constants.FIREBASE_NODES);
        mFirebaseNodes.child(node.getKey()).addListenerForSingleValueEvent(this);
        for (String branch : node.getBranches().keySet()) {
            //mFirebaseNodes.child(branch).addListenerForSingleValueEvent(new BranchValueEventListener());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_node_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BranchAdapter.ViewHolder holder, int position) {
        final StoryNode branch = mBranches.get(position);
        String title = trimPreviewText(branch.getTitle());
        holder.mBranchText.setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoryNodeSelectCallback.onStoryNodeSelect(branch, mStoryKey);
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
        return mBranches.size();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        StoryNode node = dataSnapshot.getValue(StoryNode.class);
        for (String n : node.getBranches().keySet()) {
            if (!mBranchKeysListening.contains(n)) {
                mBranchKeysListening.add(n);
                mFirebaseNodes.child(n).addListenerForSingleValueEvent(new BranchValueEventListener());
            }
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mBranchText;

        public ViewHolder(View itemView) {
            super(itemView);
            mBranchText = (TextView) itemView.findViewById(R.id.branch_text);
        }
    }

    private class BranchValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final StoryNode node = dataSnapshot.getValue(StoryNode.class);
            node.setKey(dataSnapshot.getKey());
            mBranches.add(0, node);
            notifyDataSetChanged();
        }
        @Override
        public void onCancelled(FirebaseError firebaseError) { }
    }
}
