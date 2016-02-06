package com.enricotj.tangent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReaderFragment} interface
 * to handle interaction events.
 * Use the {@link ReaderFragment#} factory method to
 * create an instance of this fragment.
 */
public class ReaderFragment extends Fragment {
    public static final String ARG_NODE = "node";
    public static final String ARG_STORY_KEY = "story_key";

    private StoryNode mNode;
    private String mStoryKey;

    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNode = getArguments().getParcelable(ARG_NODE);
            mStoryKey = getArguments().getString(ARG_STORY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_reader, container, false);

        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.toolbar_reader);
        toolbar.setTitle(mNode.getTitle());
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteBlue));
        toolbar.setNavigationIcon(R.drawable.ic_back_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        final TextView authorText = (TextView) rootView.findViewById(R.id.node_author);
        Firebase ref = new Firebase(Constants.FIREBASE_USERS);
        ref.child(mNode.getAuthor()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                authorText.setText(dataSnapshot.getValue(String.class));
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

        Button randomNextButton = (Button) rootView.findViewById(R.id.button_view_random_branch);
        randomNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRandomBranch();
            }
        });

        Button nextButton = (Button) rootView.findViewById(R.id.button_view_branches);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBranches();
            }
        });

        if (mNode.getBranches().keySet().isEmpty()) {
            randomNextButton.setEnabled(false);
            randomNextButton.setVisibility(View.GONE);
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.GONE);
        }

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
        // TODO

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
