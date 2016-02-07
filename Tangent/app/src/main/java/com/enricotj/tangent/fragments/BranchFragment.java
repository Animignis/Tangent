package com.enricotj.tangent.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.R;
import com.enricotj.tangent.adapters.BranchAdapter;
import com.enricotj.tangent.adapters.StoryAdapter;
import com.enricotj.tangent.models.StoryNode;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BranchFragment} interface
 * to handle interaction events.
 * Use the {@link BranchFragment} factory method to
 * create an instance of this fragment.
 */
public class BranchFragment extends Fragment implements StoryAdapter.StoryNodeSelectCallback {
    public static final String ARG_NODE = "node";
    public static final String ARG_STORY_KEY = "story_key";

    private StoryNode mNode;
    private String mStoryKey;

    public BranchFragment() {
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
        View rootView =  inflater.inflate(R.layout.fragment_branch, container, false);

        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.toolbar_branches);
        toolbar.setTitle(mNode.getTitle());
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteBlue));
        toolbar.setNavigationIcon(R.drawable.ic_back_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_branch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.branches_recyclerview);
        recyclerView.setAdapter(new BranchAdapter(this, mNode, mStoryKey));
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStoryNodeSelect(StoryNode storyNode, String storyKey) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ReaderFragment();

        Bundle args = new Bundle();
        args.putParcelable(ReaderFragment.ARG_NODE, storyNode);
        args.putString(ReaderFragment.ARG_STORY_KEY, storyKey);
        fragment.setArguments(args);

        ft.replace(R.id.fragment, fragment, Constants.TAG);
        ft.addToBackStack("read_node_" + storyNode.getKey());
        ft.commit();
    }
}
