package asee.giiis.unex.es.mysporttraining.Fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.TrainingSelectActivityAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.Activity;
import asee.giiis.unex.es.mysporttraining.R;

public class TodayTrainingFragment extends Fragment {

    private final static String DIALOG_OK_BUTTON = "OK";

    // Recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();


    // DatabaseReference Firebase
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mActivitiesRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today_training, container, false);

        // Get exercises from firebase and set Adapter
        retrieveExerciseListFirebase();

        // Get Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_exercises_today);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        return view;
    }



    //========================================//
             // RETRIEVE DATA FIREBASE //
    //========================================//
    private void retrieveExerciseListFirebase() {
        mExerciseList.clear();

        if (mUser != null) {
            // Firebase ref: /exerciseList/"user"/"exerciseList"
            mActivitiesRef = mRootRef.child("exerciseList").child(mUser.getUid());
            // Event for get all activities for a training
            mActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    retrieveExercises(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void retrieveExercises(DataSnapshot dataSnapshot){
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                Activity activity = dataSnapshot2.getValue(Activity.class);
                // Get substring for day of an exercise and compare with current day
                if (activity.getDate().substring(8, 10).equals(getCurrentDay())) {

                    mExerciseList.add(activity);
                }
            }
        }

        // Adapter
        if (mExerciseList.size() >= 0) {
            mAdapter = new TrainingSelectActivityAdapter(getActivity(), mExerciseList,
                    new TrainingSelectActivityAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Activity item) {
                            exerciseDialog(item);
                        }
                    });
            mRecyclerView.setAdapter(mAdapter);
        }
    }



    //========================================//
                    // DIALOG //
    //========================================//
    private void exerciseDialog(final Activity item){
        // AlertDialog - SHow exercises details
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_input_exercise, null);
        builder.setView(v);

        // Dialog-title
        builder.setTitle(item.getName());

        // Dialog-score
        TextView scoreView = (TextView) v.findViewById(R.id.dialog_score);
        String string = "+ " + Integer.toString(item.getScore()) + " puntos";
        scoreView.setText(string);

        // Dialog-date and Dialog-time
        TextView dateView = (TextView) v.findViewById(R.id.dialog_date_input);
        dateView.setText(item.getDate());
        TextView timeView = (TextView) v.findViewById(R.id.dialog_hour_input);
        timeView.setText(item.getHour());

        // Dialog OK button
        builder.setNegativeButton(DIALOG_OK_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private String getCurrentDay(){
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        String day = String.valueOf(dayOfMonth);

        return day;
    }
}
