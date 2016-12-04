package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class TrainingSelectCategoryActivity extends AppCompatActivity {

    final static String CATEGORY = "category";
    private final String TRAINING_NAME = "trainingTitle";

    private String mTrainingName = "";

    private TextView mSportCategory;
    private TextView mCardioCategory;
    private TextView mCollectiveCategory;
    private TextView mGymWeightsCategory;
    private TextView mStrengthCategory;
    private TextView mElongationCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_select_category);

        Intent intentAux = getIntent();
        Bundle bundle = intentAux.getExtras();
        if (bundle != null) {
            mTrainingName = (String) bundle.get(TRAINING_NAME);
        }

        mSportCategory = (TextView) findViewById(R.id.training_category_sports);
        mCardioCategory = (TextView) findViewById(R.id.training_category_cardio);
        mCollectiveCategory = (TextView) findViewById(R.id.training_category_collective);
        mGymWeightsCategory = (TextView) findViewById(R.id.training_category_gym_weights);
        mStrengthCategory = (TextView) findViewById(R.id.training_category_strength);
        mElongationCategory = (TextView) findViewById(R.id.training_category_elongation);

        final Intent intent = new Intent(this, TrainingSelectActivity.class);
        intent.putExtra("trainingTitle", mTrainingName);

        mSportCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "sports");
                startActivity(intent);
            }
        });

        mCardioCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "cardio");
                startActivity(intent);
            }
        });

        mCollectiveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "collective");
                startActivity(intent);
            }
        });

        mGymWeightsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "gym_weights");
                startActivity(intent);
            }
        });

        mStrengthCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "strength");
                startActivity(intent);
            }
        });

        mElongationCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(CATEGORY, "elongation");
                startActivity(intent);
            }
        });

    }
}
