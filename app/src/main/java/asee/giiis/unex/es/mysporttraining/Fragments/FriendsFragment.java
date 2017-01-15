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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.FriendsAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.User;
import asee.giiis.unex.es.mysporttraining.R;
import de.hdodenhof.circleimageview.CircleImageView;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Retrive data from Firebase to recycler view
        retrieveFriendsFirebase(view);

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

    private void retrieveFriendsFirebase(final View view) {
        if (mUser != null) {
            // Firebase ref: /friendlist/"userid"
            mFriendsRef = mRootRef.child("friendList").child(mUser.getUid());
            mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getFriendList(dataSnapshot, view);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getFriendList(DataSnapshot dataSnapshot, View view) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            mFriendList.add(user);
        }

        if (mFriendList.size() > 0) {
            // Adapter
            mAdapter = new FriendsAdapter(this.getActivity(), mFriendList);
            mRecyclerView.setAdapter(mAdapter);
        }

        //podium
        podium(view);
    }

    private void podium(final View view) {
        // Reference to our own user to compare score
        DatabaseReference myUserRef = mRootRef.child("users").child(mUser.getUid());
        myUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<User> friendListAux = new ArrayList<>(mFriendList);
                friendListAux.add(dataSnapshot.getValue(User.class));

                // Sort by score
                Collections.sort(friendListAux, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getScore() == null || o2.getScore() == null)
                            return 0;
                        return o1.getScore().compareTo(o2.getScore());
                    }
                });

                // Set image profile in podium
                if (friendListAux.size() >= 1) {
                    CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.friends_first_place);
                    Picasso.with(getActivity()).load(friendListAux.get(friendListAux.size() - 1).getUriImageProfile()).into(circleImageView);
                    if (friendListAux.size() >= 2) {
                        CircleImageView circleImageView2 = (CircleImageView) view.findViewById(R.id.friends_second_place);
                        Picasso.with(getActivity()).load(friendListAux.get(friendListAux.size() - 2).getUriImageProfile()).into(circleImageView2);
                        if (friendListAux.size() >= 3) {
                            CircleImageView circleImageView3 = (CircleImageView) view.findViewById(R.id.friends_third_place);
                            Picasso.with(getActivity()).load(friendListAux.get(friendListAux.size() - 3).getUriImageProfile()).into(circleImageView3);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                        mUsersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                addFriend(dataSnapshot, input.getText().toString());
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
    }


    private void addFriend(DataSnapshot dataSnapshot, String email) {
        boolean enc = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            // User doesn't exist or user = me
            if (user.getEmail().equals(email) && !mUser.getEmail().equals(email)) {
                mFriendsRef = mRootRef.child("friendList").child(mUser.getUid());
                mFriendsRef.child(ds.getKey()).setValue(user);
                enc = true;
            }
        }
        if (enc) {
            Toast.makeText(getContext(), "Usuario agregado como amigo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "El usuario introducido no existe", Toast.LENGTH_SHORT).show();
        }
    }


}
