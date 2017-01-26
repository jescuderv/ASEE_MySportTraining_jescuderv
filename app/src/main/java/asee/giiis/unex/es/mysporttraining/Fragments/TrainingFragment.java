package asee.giiis.unex.es.mysporttraining.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.StringOnClikcAdapter;
import asee.giiis.unex.es.mysporttraining.R;
import asee.giiis.unex.es.mysporttraining.TrainingNewActivity;


public class TrainingFragment extends Fragment {

    private static final String DIALOG_ACCEPT_BUTTON = "ACEPTAR";
    private static final String DIALOG_CANCEL_BUTTON = "CANCELAR";
    private static final String DIALOG_DETAILS_BUTTON = "DETALLES";
    private static final String DIALOG_DELETE_BUTTON = "BORRAR";

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    // Recycler View
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<String> mTrainingList = new ArrayList<>();

    // DatabaseReference Firebase
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


    //========================================//
            // CREATE NEW TRAINING //
    //========================================//

    // Intent to create a new Training
    private void startCreateTraining() {
        final EditText input = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Add the buttons
        builder.setPositiveButton(DIALOG_ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!input.getText().toString().isEmpty()) {
                    startCreateTrainingActivity(input.getText().toString());
                } else{
                    Toast.makeText(getContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
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
        if (mUser != null) {
            mActivitiesRef = mRootRef.child("exerciseList").child(mUser.getUid());
            // Child event for get all activities for a training
            mActivitiesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    retrieveTrainings(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    // Add all training names to list adapter
    private void retrieveTrainings(DataSnapshot dataSnapshot){
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            String trainingName = ds.getKey();
            mTrainingList.add(trainingName);
        }

        // Adapter
        if (mTrainingList.size() >= 0) {
            mAdapter = new StringOnClikcAdapter(getContext(), mTrainingList,
                    new StringOnClikcAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String item) {
                            onItemOptions(item);
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
        }
    }


    //========================================//
            // DIALOG ON CLICK TRAINING //
    //========================================//
    private void onItemOptions(final String item){
        // Dialog with training options
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set title and message
        builder.setTitle("Nombre de entrenamiento");
        builder.setMessage(item);

        // Add the buttons
        builder.setNeutralButton(DIALOG_DETAILS_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show details from training clicked
                Intent intent = new Intent(getActivity(), TrainingNewActivity.class);
                intent.putExtra("trainingTitle", item);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(DIALOG_DELETE_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mRootRef.child("exerciseList").child(mUser.getUid()).child(item).removeValue();
                mTrainingList.clear();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}