package com.enricotj.tangent.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.MainActivity;
import com.enricotj.tangent.R;
import com.enricotj.tangent.models.CurrentUser;
import com.enricotj.tangent.models.Story;
import com.enricotj.tangent.models.StoryNode;
import com.enricotj.tangent.models.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReaderFragment} interface
 * to handle interaction events.
 * Use the {@link ReaderFragment#} factory method to
 * create an instance of this fragment.
 */
public class ReaderFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    public static final String ARG_NODE = "node";
    public static final String ARG_STORY_KEY = "story_key";

    private StoryNode mNode;
    private String mStoryKey;

    private HomeFragment.OnLogoutListener mListener;

    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNode = getArguments().getParcelable(ARG_NODE);
            mStoryKey = getArguments().getString(ARG_STORY_KEY);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (mNode.getParent() == null) {
                final Firebase storyRef = new Firebase(Constants.FIREBASE_STORIES + "/" + mStoryKey);
                storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Story story = dataSnapshot.getValue(Story.class);
                        story.setViews(story.getViews() + 1);
                        storyRef.setValue(story);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_reader, container, false);

        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.toolbar_reader);
        getActivity().getMenuInflater().inflate(R.menu.menu_reader, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitle(mNode.getTitle());
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteBlue));
        toolbar.setNavigationIcon(R.drawable.ic_back_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(this);

        if (CurrentUser.getInstance().getFavorites().containsKey(mStoryKey) && CurrentUser.getInstance().getFavorites().get(mStoryKey)) {
            MenuItem item = toolbar.getMenu().findItem(R.id.action_favorite);
            item.setChecked(true);
            item.setIcon(android.R.drawable.star_big_on);
            item.setTitle(R.string.action_unfavorite);
        }

        final TextView authorText = (TextView) rootView.findViewById(R.id.node_author);
        Firebase ref = new Firebase(Constants.FIREBASE_USERS);
        ref.child(mNode.getAuthor()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                authorText.setText("By: " + dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        TextView timestampText = (TextView) rootView.findViewById(R.id.node_timestamp);
        Date timestamp = new Date(mNode.getTimestamp());
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.getDefault());
        String time = df.format(timestamp);
        timestampText.setText(time);

        TextView bodyText = (TextView) rootView.findViewById(R.id.node_body);
        bodyText.setText(mNode.getBody());

        final Button randomNextButton = (Button) rootView.findViewById(R.id.button_view_random_branch);
        randomNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRandomBranch();
            }
        });

        final Button nextButton = (Button) rootView.findViewById(R.id.button_view_branches);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBranches();
            }
        });

        final Button addButton = (Button) rootView.findViewById(R.id.button_add_branch);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Transition to AddNodeFragment (send mNode as parcelable argument)
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new AddNodeFragment();

                Bundle args = new Bundle();
                args.putParcelable(AddNodeFragment.ARG_PARENT, mNode);
                args.putString(AddNodeFragment.ARG_STORY_KEY, mStoryKey);
                fragment.setArguments(args);

                ft.replace(R.id.fragment, fragment, Constants.TAG);
                ft.addToBackStack("add_node_" + mNode.getKey());
                ft.commit();
            }
        });

        randomNextButton.setEnabled(false);
        randomNextButton.setVisibility(View.GONE);

        nextButton.setEnabled(false);
        nextButton.setVisibility(View.GONE);

        addButton.setEnabled(false);
        addButton.setVisibility(View.GONE);

        Firebase nodeRef = new Firebase(Constants.FIREBASE_NODES + "/" + mNode.getKey());
        nodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StoryNode node = dataSnapshot.getValue(StoryNode.class);
                if (node.getBranches().keySet().isEmpty()) {
                    addButton.setEnabled(true);
                    addButton.setVisibility(View.VISIBLE);
                }
                else {
                    randomNextButton.setEnabled(true);
                    randomNextButton.setVisibility(View.VISIBLE);
                    nextButton.setEnabled(true);
                    nextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return rootView;
    }

    private void navigateToRandomBranch() {
        Object[] branches = mNode.getBranches().keySet().toArray();

        Random rng = new Random();
        String branch = branches[rng.nextInt(branches.length)].toString();

        Firebase ref = new Firebase(Constants.FIREBASE_NODES + "/" + branch);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final StoryNode storyNode = dataSnapshot.getValue(StoryNode.class);
                storyNode.setKey(dataSnapshot.getKey());

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new ReaderFragment();

                Bundle args = new Bundle();
                args.putParcelable(ARG_NODE, storyNode);
                args.putString(ARG_STORY_KEY, mStoryKey);
                fragment.setArguments(args);

                ft.replace(R.id.fragment, fragment, Constants.TAG);
                ft.addToBackStack("read_node_" + storyNode.getKey());
                ft.commit();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showBranches() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new BranchFragment();

        Bundle args = new Bundle();
        args.putParcelable(BranchFragment.ARG_NODE, mNode);
        args.putString(BranchFragment.ARG_STORY_KEY, mStoryKey);
        fragment.setArguments(args);

        ft.replace(R.id.fragment, fragment, Constants.TAG);
        ft.addToBackStack("show_branches_for_node_" + mNode.getKey());
        ft.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeFragment.OnLogoutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLogoutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                User user = CurrentUser.getInstance();
                Log.d(Constants.TAG, "USER: " + user.getUsername());
                if (item.isChecked()) {
                    item.setIcon(android.R.drawable.star_big_off);
                    item.setTitle(R.string.action_favorite);
                    item.setChecked(false);

                    Map<String, Boolean> favorites = user.getFavorites();
                    favorites.put(mStoryKey, false);
                    user.setFavorites(favorites);
                    CurrentUser.getInstance().setFavorites(favorites);
                    Firebase userRef = new Firebase(Constants.FIREBASE_USERS + "/" + user.getKey());
                    userRef.setValue(user);
                }
                else {
                    item.setIcon(android.R.drawable.star_big_on);
                    item.setTitle(R.string.action_unfavorite);
                    item.setChecked(true);

                    Map<String, Boolean> favorites = user.getFavorites();
                    if (!favorites.containsKey(mStoryKey)) {
                        //update number of favorites for current story
                        final Firebase storyRef = new Firebase(Constants.FIREBASE_STORIES + "/" + mStoryKey);
                        storyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Story story = dataSnapshot.getValue(Story.class);
                                story.setNumfavorites(story.getNumfavorites() + 1);
                                storyRef.setValue(story);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                    favorites.put(mStoryKey, true);
                    user.setFavorites(favorites);
                    CurrentUser.getInstance().setFavorites(favorites);
                    Firebase userRef = new Firebase(Constants.FIREBASE_USERS + "/" + user.getKey());
                    userRef.setValue(user);
                }
                return true;
            case R.id.action_home:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount() - 1; ++i) {
                    fm.popBackStack();
                }
                getActivity().onBackPressed();
                return true;
            case R.id.action_logout:
                mListener.onLogout();
                return true;
        }
        return false;
    }
}
