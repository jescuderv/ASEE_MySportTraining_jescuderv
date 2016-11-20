package asee.giiis.unex.es.mysporttraining.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import asee.giiis.unex.es.mysporttraining.R;
import asee.giiis.unex.es.mysporttraining.TrainingNewActivity;


public class TrainingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        Button addTrainingButton = (Button) view.findViewById(R.id.add_training_button);
        addTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateTraining();
            }
        });

        return view;
    }

    private void startCreateTraining(){
        Intent intent = new Intent(getActivity(), TrainingNewActivity.class);
        startActivity(intent);
    }
}