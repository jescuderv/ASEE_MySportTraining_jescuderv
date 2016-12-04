package asee.giiis.unex.es.mysporttraining.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.StringOnClikcAdapter;
import asee.giiis.unex.es.mysporttraining.R;
import asee.giiis.unex.es.mysporttraining.TrainingNewActivity;


public class TrainingFragment extends Fragment {

    private final String DIALOG_ACCEPT_BUTTON = "ACEPTAR";
    private final String DIALOG_CANCEL_BUTTON = "CANCELAR";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> mTrainingList = new ArrayList<>();

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mActivitiesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveshowdInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        // Get exercises from firebase and set Adapter
        retrieveTrainingListFirebase();

        // Get Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_training);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

        Button addTrainingButton = (Button) view.findViewById(R.id.add_training_button);
        addTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateTraining();
            }
        });

        return view;
    }

    // Intent to create a new Training
    private void startCreateTraining() {
        final EditText input = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons
        builder.setPositiveButton(DIALOG_ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startCreateTrainingActivity(input.getText().toString());
            }
        });
        builder.setNegativeButton(DIALOG_CANCEL_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle("Nombre de entrenamiento");
        builder.setView(input);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startCreateTrainingActivity(String title){
        Intent intent = new Intent(getActivity(), TrainingNewActivity.class);
        intent.putExtra("trainingTitle", title);
        startActivity(intent);
    }

    //========================================//
            // RETRIEVE DATA FIREBASE //
    //========================================//
    private void retrieveTrainingListFirebase(){
        mTrainingList.clear();
        // Firebase ref: /exerciseList/"user"
        mActivitiesRef = mRootRef.child("exerciseList").child("idUsuarioPrueba");
        // Child event for get all activities for a training
        mActivitiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                retrieveTrainings(dataSnapshot);
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

    // Add all training names to list adapter
    private void retrieveTrainings(DataSnapshot dataSnapshot){
        String trainingName = dataSnapshot.getKey();
        mTrainingList.add(trainingName);

        // Adapter
        if (mTrainingList.size() > 0) {
            mAdapter = new StringOnClikcAdapter(getContext(), mTrainingList,
                    new StringOnClikcAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String item) {
                            Log.i("TODO", "todoooo");
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
        }
    }
}