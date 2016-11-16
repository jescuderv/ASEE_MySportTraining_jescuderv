package asee.giiis.unex.es.mysporttraining.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import asee.giiis.unex.es.mysporttraining.R;
import asee.giiis.unex.es.mysporttraining.ResultsMapActivity;

public class ActivityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        Button stopButton = (Button) view.findViewById(R.id.activity_stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResultsMapActivity();
            }
        });
        return view;
    }

    private void startResultsMapActivity(){
        Intent intent = new Intent(getActivity(), ResultsMapActivity.class);
        startActivity(intent);
    }

}
