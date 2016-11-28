package asee.giiis.unex.es.mysporttraining;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import asee.giiis.unex.es.mysporttraining.Objects.User;


public class UserProfileActivity extends AppCompatActivity {

    TextView mUsername;
    TextView mScore;
    TextView mFirstName;
    TextView mLastName;
    TextView mEmail;
    TextView mHeight;
    TextView mWeight;
    TextView mAge;
    TextView mSex;
    TextView mPhysicalCondition;

    // Reference root JSON database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootRef.child("users").child("idUserPrueba");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUsername = (TextView) findViewById(R.id.usr_prof_username);
        mScore = (TextView) findViewById(R.id.usr_prof_score);
        mFirstName = (TextView) findViewById(R.id.usr_prof_first_name_value);
        mLastName = (TextView) findViewById(R.id.usr_prof_last_name_value);
        mEmail = (TextView) findViewById(R.id.usr_prof_email_value);
        mHeight = (TextView) findViewById(R.id.usr_prof_height_value);
        mWeight = (TextView) findViewById(R.id.usr_prof_weight_value);
        mAge = (TextView) findViewById(R.id.usr_prof_age_value);
        mSex = (TextView) findViewById(R.id.usr_prof_sex_value);
        mPhysicalCondition = (TextView) findViewById(R.id.usr_prof_physical_condition_value);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                mUsername.setText(user.getUsername());
                mScore.setText(user.getScore().toString());
                mFirstName.setText(user.getFirstName());
                mLastName.setText(user.getLastName());
                mEmail.setText(user.getEmail());
                mHeight.setText(user.getHeight().toString());
                mWeight.setText(user.getWeight().toString());
                mAge.setText(user.getAge().toString());
                mSex.setText(user.getSex());
                mPhysicalCondition.setText(user.getPhysicalCondition());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
