package asee.giiis.unex.es.mysporttraining.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.FriendsAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.User;
import asee.giiis.unex.es.mysporttraining.R;

public class FriendsFragment extends Fragment {

    // Dialog
    private final static String DIALOG_ACCEPT_BUTTON = "Aceptar";
    private static final String DIALOG_CANCEL_BUTTON = "CANCELAR";


    // Reference root JSON database
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mFriendsRef;
    private DatabaseReference mUsersRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();


    // RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<User> mFriendList = new ArrayList<>();

    private boolean mEnc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        retrieveFriendsFirebase();

        Button button = (Button) view.findViewById(R.id.friends_add_friends_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendFirebase();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friend_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        return view;
    }


    //========================================//
    // RETRIEVE DATA FIREBASE //
    //========================================//

    private void retrieveFriendsFirebase() {
        if (mUser != null) {
            mFriendsRef = mRootRef.child("friendList").child(mUser.getUid());
            mFriendsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    getFriendList(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    getFriendList(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFriendList(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        mFriendList.add(user);

        if (mFriendList.size() > 0) {
            // Adapter
            mAdapter = new FriendsAdapter(this.getActivity(), mFriendList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    //========================================//
    // DIALOG - ADD FRIENDS TO FIREBASE //
    //========================================//

    private void addFriendFirebase() {

        final EditText input = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons
        builder.setPositiveButton(DIALOG_ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!input.getText().toString().isEmpty()) {
                    if (mUser != null) {
                        mUsersRef = mRootRef.child("users");
                        mUsersRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                addFriend(dataSnapshot, input.getText().toString());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                addFriend(dataSnapshot, input.getText().toString());
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(DIALOG_CANCEL_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Introducir correo electrónico del usuario a agregar:");
        builder.setView(input);
        AlertDialog dialog = builder.create();
        dialog.show();
        if (mEnc) {
            Toast.makeText(getContext(), "Usuario agregado como amigo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
        }
    }


    private void addFriend(DataSnapshot dataSnapshot, String email) {
        User user = dataSnapshot.getValue(User.class);
        if (user.getEmail().equals(email) && !mUser.getEmail().equals(email)) {
            mFriendsRef = mRootRef.child("friendList").child(mUser.getUid());
            mFriendsRef.child(dataSnapshot.getKey()).setValue(user);
            mEnc = true;
        }
    }

}
