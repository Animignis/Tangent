package com.enricotj.tangent.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.R;
import com.enricotj.tangent.models.CurrentUser;
import com.enricotj.tangent.models.Story;
import com.enricotj.tangent.models.StoryNode;
import com.enricotj.tangent.models.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by enricotj on 2/14/2016.
 * TODO: This entire class needs to be refactored to only get the current user's favorites
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> implements ValueEventListener{

    private ArrayList<Story> mStories = new ArrayList<>();
    private ArrayList<StoryNode> mRootNodes = new ArrayList<>();

    private Firebase mFirebaseStories;

    private StoryAdapter.StoryNodeSelectCallback mStoryNodeSelectCallback;

    private final int PREVIEW_TEXT_CHAR_LIMIT = 33;

    public FavoritesAdapter(StoryAdapter.StoryNodeSelectCallback storyNodeSelectCallback) {
        mStoryNodeSelectCallback = storyNodeSelectCallback;

        mFirebaseStories = new Firebase(Constants.FIREBASE_USERS);
        mFirebaseStories.child(CurrentUser.getInstance().getKey()).addListenerForSingleValueEvent(this);
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        Map<String, Boolean> favorites = user.getFavorites();
        for (String fav : favorites.keySet()) {
            if (favorites.get(fav)) {
                Firebase favRef = new Firebase(Constants.FIREBASE_STORIES + "/" + fav);
                favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Deserialize the JSON.
                        final Story story = dataSnapshot.getValue(Story.class);

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
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
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
