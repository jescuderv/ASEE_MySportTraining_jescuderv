package asee.giiis.unex.es.mysporttraining;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.TrainingSelectActivityAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.Activity;

public class TrainingNewActivity extends AppCompatActivity {

    private static final String DIALOG_OK_BUTTON = "OK";
    private static final String TRAINING_NAME = "trainingTitle";

    // Recycler view
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();

    private String mTrainingName = "";

    // DatabaseReference Firebase
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mActivitiesRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_new);

        // Get exercises from firebase and set Adapter
        retrieveExerciseListFirebase();

        // Get Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_exercises_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);


        Button buttonAdd = (Button) findViewById(R.id.create_training_add_button);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrainingSelectCategory();
            }
        });

        Button buttonFinish = (Button) findViewById(R.id.create_training_finish_button);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMainActivity();
            }
        });
    }

    // Intent to select an exercise
    private void startTrainingSelectCategory(){
        Intent intent = new Intent(this, TrainingSelectCategoryActivity.class);
        intent.putExtra("trainingTitle", mTrainingName);
        startActivity(intent);
    }

    // Intent to finish exercise
    private void returnMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    //========================================//
            // RETRIEVE DATA FIREBASE //
    //========================================//
    private void retrieveExerciseListFirebase() {
        mExerciseList.clear();
        // Get training name from Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mTrainingName = (String) bundle.get(TRAINING_NAME);
        }
        if (mUser != null) {
            // Firebase ref: /exerciseList/"user"/"exerciseList"
            mActivitiesRef = mRootRef.child("exerciseList").child(mUser.getUid()).child(mTrainingName);
            // Child event for get all activities for a training
            mActivitiesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    retrieveExercises(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    // Add all training exercises to list adapter
    private void retrieveExercises(DataSnapshot dataSnapshot){
        Activity activity = dataSnapshot.getValue(Activity.class);
        mExerciseList.add(activity);

        // Adapter
        if (mExerciseList.size() > 0) {
            mAdapter = new TrainingSelectActivityAdapter(this, mExerciseList,
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
    private void exerciseDialog(Activity item){
        // AlertDialog - SHow exercises details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
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


}
