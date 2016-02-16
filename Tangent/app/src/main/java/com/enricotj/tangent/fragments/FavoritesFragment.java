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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.enricotj.tangent.Constants;
import com.enricotj.tangent.R;
import com.enricotj.tangent.adapters.FavoritesAdapter;
import com.enricotj.tangent.adapters.StoryAdapter;
import com.enricotj.tangent.models.StoryNode;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment implements Toolbar.OnMenuItemClickListener, StoryAdapter.StoryNodeSelectCallback {

    private HomeFragment.OnLogoutListener mListener;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        // set up toolbar
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_favorites);
        getActivity().getMenuInflater().inflate(R.menu.menu_favorites, mToolbar.getMenu());
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setTitle(getString(R.string.toolbar_title_favorites));
        mToolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteBlue));
        mToolbar.setNavigationIcon(R.drawable.ic_back_small);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.favorites_recyclerview);
        mRecyclerView.setAdapter(new FavoritesAdapter(this));
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        return rootView;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_logout:
                Log.d(Constants.TAG, "LOGOUT Menu Item Clicked!");
                mListener.onLogout();
                return true;
        }
        return false;
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

}
