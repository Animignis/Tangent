package com.enricotj.tangent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNodeFragment} interface
 * to handle interaction events.
 * Use the {@link AddNodeFragment#} factory method to
 * create an instance of this fragment.
 */
public class AddNodeFragment extends Fragment {
    private static final String ARG_PARENT = "parent";
    private int mParent;

    public AddNodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParent = getArguments().getInt(ARG_PARENT, -1);
        } else {
            mParent = -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_node, container, false);

        final EditText titleText = (EditText)rootView.findViewById(R.id.node_new_title);
        final EditText bodyText = (EditText)rootView.findViewById(R.id.node_new_body);

        Button addButton = (Button)rootView.findViewById(R.id.button_add_node);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref = new Firebase(Constants.FIREBASE_URL);
                AuthData auth = ref.getAuth();
                String author = auth.getUid();
                String title = titleText.getText().toString();
                String body = bodyText.getText().toString();
                long timestamp = (new Date()).getTime();

                StoryNode storyNode = new StoryNode(author, title, body, timestamp, null);

                Firebase newStoryNode = ref.child("nodes").push();
                newStoryNode.setValue(storyNode);
                Story story = new Story(storyNode.getTimestamp(), 0, newStoryNode.getKey(), 1, 1);
                ref.child("stories").push().setValue(story);

                getActivity().onBackPressed();
            }
        });

        Button cancelButton = (Button)rootView.findViewById(R.id.button_cancel_add_node);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
