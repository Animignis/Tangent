package com.enricotj.tangent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        Toolbar toolbar = (Toolbar)rootView.findViewById(R.id.toolbar_reader);
        toolbar.setTitle(mNode.getTitle());
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteBlue));

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
