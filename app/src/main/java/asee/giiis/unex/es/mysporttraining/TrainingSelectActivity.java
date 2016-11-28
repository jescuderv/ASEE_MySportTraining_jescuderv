package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.TrainingSelectActivityAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.Activity;

public class TrainingSelectActivity extends AppCompatActivity {

    final static String CATEGORY = "category";
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mActivitiesRef;

    private String mCategory;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_select);

        retrieveExerciseListFirebase();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_exercises_category);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

    }


    private void retrieveExerciseListFirebase() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCategory = (String) bundle.get(CATEGORY);
        }
        mActivitiesRef = mRootRef.child("activities").child(mCategory);
        mActivitiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addExercise(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addExercise(dataSnapshot);
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

    private void addExercise(DataSnapshot ds) {
        Activity activity = ds.getValue(Activity.class);
        mExerciseList.add(activity);

        // Adapter
        if (mExerciseList.size() > 0) {
            mAdapter = new TrainingSelectActivityAdapter(this, mExerciseList,
                    new TrainingSelectActivityAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Activity item) {
                            Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                        }
                    });
            mRecyclerView.setAdapter(mAdapter);
        }
    }




}
