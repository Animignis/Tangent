package com.enricotj.tangent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

    private StoryNode mNode;

    public ReaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNode = getArguments().getParcelable(ARG_NODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_reader, container, false);

        TextView titleText = (TextView) rootView.findViewById(R.id.node_title);
        titleText.setText(mNode.getTitle());
        TextView bodyText = (TextView) rootView.findViewById(R.id.node_body);
        bodyText.setText(mNode.getBody());

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
}
