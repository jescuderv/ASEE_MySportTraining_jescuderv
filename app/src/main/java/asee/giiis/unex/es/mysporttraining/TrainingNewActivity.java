package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Objects.Activity;

public class TrainingNewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_new);

        Button button = (Button) findViewById(R.id.create_training_add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrainingSelectCategory();
            }
        });
    }

    private void startTrainingSelectCategory(){
        Intent intent = new Intent(this, TrainingSelectCategoryActivity.class);
        startActivity(intent);
    }


}
