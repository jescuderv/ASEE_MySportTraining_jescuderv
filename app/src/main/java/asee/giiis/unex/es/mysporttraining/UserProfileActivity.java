package asee.giiis.unex.es.mysporttraining;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import asee.giiis.unex.es.mysporttraining.Objects.User;


public class UserProfileActivity extends AppCompatActivity {

    // Reference root JSON database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef;
    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    // Reference from layout
    private TextView mUsername;
    private TextView mScore;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mHeight;
    private TextView mWeight;
    private TextView mAge;
    private TextView mSex;
    private TextView mPhysicalCondition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (mUser != null) {
            // Retrieve user from Firebase: /root/users/"userID"
            mUsersRef =  mRootRef.child("users").child(mUser.getUid());

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
        } else{
            Toast.makeText(this, "Error en la recuperación del usuario de la base de" +
                    " datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get user information profile and show
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
