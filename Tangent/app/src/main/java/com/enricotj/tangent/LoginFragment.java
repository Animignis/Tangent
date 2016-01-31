package com.enricotj.tangent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    private EditText mPasswordView;
    private EditText mEmailView;
    private View mLoginForm;
    private View mProgressSpinner;
    private boolean mLoggingIn;
    private OnLoginListener mListener;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggingIn = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.email);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mLoginForm = rootView.findViewById(R.id.login_form);
        mProgressSpinner = rootView.findViewById(R.id.login_progress);
        View loginButton = rootView.findViewById(R.id.email_sign_in_button);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        View createAccountButton = rootView.findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(null, null, null);
            }
        });
        return rootView;
    }

    public void login() {
        if (mLoggingIn) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.invalid_password));
            focusView = mPasswordView;
            cancelLogin = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.field_required));
            focusView = mEmailView;
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.invalid_email));
            focusView = mEmailView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            mLoggingIn = true;
            mListener.onLogin(email, password);
            hideKeyboard();
        }
    }

    private void createAccount(final String emailPre, final String usernamePre, final String passwordPre) {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create Account");
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_account, null, false);
                builder.setView(view);
                final EditText emailText = (EditText) view.findViewById(R.id.dialog_create_account_email_text);
                final EditText usernameText = (EditText) view.findViewById(R.id.dialog_create_account_username_text);
                final EditText passwordText = (EditText) view.findViewById(R.id.dialog_create_account_password_text);
                final EditText passwordConfirmText = (EditText) view.findViewById(R.id.dialog_create_account_password_confirm_text);

                if (emailPre != null) {
                    emailText.setText(emailPre);
                }
                if (usernamePre != null) {
                    usernameText.setText(usernamePre);
                }
                if (passwordPre != null) {
                    passwordText.setText(passwordPre);
                    passwordConfirmText.setText(passwordPre);
                }

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String email = emailText.getText().toString();
                        final String username = usernameText.getText().toString();
                        final String password = passwordText.getText().toString();
                        final String passwordConfirm = passwordConfirmText.getText().toString();

                        final AlertDialog errorDialog = new AlertDialog.Builder(getActivity()).create();
                        errorDialog.setTitle("Error");
                        errorDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        createAccount(email, username, password);
                                    }
                                });

                        if (!isEmailValid(email)) {
                            errorDialog.setMessage("The email you have entered is invalid.");
                            errorDialog.show();
                            return;
                        }
                        if (!isUsernameValid(username)) {
                            errorDialog.setMessage("Your username must contain at least 5 characters.");
                            errorDialog.show();
                        }
                        if (!isPasswordValid(password)) {
                            errorDialog.setMessage("Your password must contain at least 5 characters.");
                            errorDialog.show();
                            return;
                        }
                        if (!password.equals(passwordConfirm)) {
                            errorDialog.setMessage("The passwords you have entered do not match.");
                            errorDialog.show();
                            return;
                        }

                        showProgress(true);
                        mLoggingIn = true;

                        final Firebase ref = new Firebase(Constants.FIREBASE_URL);
                        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                // Check if username exists
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    for (DataSnapshot props : child.getChildren()) {
                                        Log.d(Constants.TAG, props.getValue().toString());
                                        if (props.getKey().equals("username") && props.getValue().toString().equals(username)) {
                                            errorDialog.setMessage("An account with this username already exists.");
                                            errorDialog.show();
                                            showProgress(false);
                                            mLoggingIn = false;
                                            return;
                                        }
                                    }
                                }
                                // username does not exist, create user and log in
                                ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                                    @Override
                                    public void onSuccess(Map<String, Object> result) {
                                        String uid = (String) result.get("uid").toString();
                                        mListener.onLoginNewAccount(email, username, password);
                                        hideKeyboard();
                                    }

                                    @Override
                                    public void onError(FirebaseError error) {
                                        switch (error.getCode()) {
                                            case FirebaseError.EMAIL_TAKEN:
                                                errorDialog.setMessage("An account with this email already exists.");
                                                errorDialog.show();
                                                break;
                                            case FirebaseError.INVALID_EMAIL:
                                                errorDialog.setMessage("The email you have entered is invalid.");
                                                errorDialog.show();
                                                break;
                                            default:
                                                errorDialog.setMessage("UNKNOWN FIREBASE ERROR");
                                                errorDialog.show();
                                                break;
                                        }
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "create_account");
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
    }

    public void onLoginError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.login_error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();

        showProgress(false);
        mLoggingIn = false;
    }

    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void invalidLogin() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Invalid Login Credentials")
                .setMessage("Please make sure your email and password are correct.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        showProgress(false);
        mLoggingIn = false;
    }

    public interface OnLoginListener {
        void onLogin(String email, String password);
        void onLoginNewAccount(String email, String username, String password);
    }
}
