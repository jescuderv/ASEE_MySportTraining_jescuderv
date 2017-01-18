package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import asee.giiis.unex.es.mysporttraining.Fragments.TodayTrainingFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.CalendarFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.FriendsFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.StatisticsFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.TrainingFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.WeightControlFragment;
import asee.giiis.unex.es.mysporttraining.Objects.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // DatabaseReference Firebase
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUsersRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();


    // Header items
    private TextView mUsername;
    private TextView mFirstName;
    private TextView mScore;
    private CircleImageView mImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar and navigationDrawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get header
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        // Get elements of header and complete with Firebase database
        mUsername = (TextView) header.findViewById(R.id.nav_usr_username);
        mFirstName = (TextView) header.findViewById(R.id.nav_usr_first_name);
        mScore = (TextView) header.findViewById(R.id.nav_usr_score);
        mImageProfile = ((CircleImageView) header.findViewById(R.id.nav_usr_profile_image));


        // Intent for UserProfileActivity
        mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUserProfileActivity();
            }
        });


        // Item "Activity" as default
        getSupportActionBar().setTitle("Actividad");
        navigationView.setCheckedItem(R.id.nav_activity);
        setFragment(0);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_help:
                break;
            case R.id.action_about:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean optionSelected = false;

        // Set fragment according item selected
        switch (item.getItemId()) {
            case R.id.nav_activity:
                setFragment(0);
                optionSelected = true;
                break;
            case R.id.nav_training:
                setFragment(1);
                optionSelected = true;
                break;
            case R.id.nav_statistics:
                setFragment(2);
                optionSelected = true;
                break;
            case R.id.nav_calendar:
                setFragment(3);
                optionSelected = true;
                break;
            case R.id.nav_weightControl:
                setFragment(4);
                optionSelected = true;
                break;
            case R.id.nav_friends:
                setFragment(5);
                optionSelected = true;
                break;
            case R.id.nav_logout:
                // SignOut mFirebaseAuth
                if (mUser != null) {
                    mFirebaseAuth.signOut();
                    finish();
                    // Return to login activity
                    startLoginctivity();
                    Toast.makeText(this, "Usuario desconectado", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (optionSelected) {
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(int option) {
        // Create fragment
        Fragment fragment = null;
        boolean fragmentTransaction = false;
        switch (option) {
            case 0:
                fragment = new TodayTrainingFragment();
                fragmentTransaction = true;
                break;
            case 1:
                fragment = new TrainingFragment();
                fragmentTransaction = true;
                break;
            case 2:
                fragment = new StatisticsFragment();
                fragmentTransaction = true;
                break;
            case 3:
                fragment = new CalendarFragment();
                fragmentTransaction = true;
                break;
            case 4:
                fragment = new WeightControlFragment();
                fragmentTransaction = true;
                break;
            case 5:
                fragment = new FriendsFragment();
                fragmentTransaction = true;
                break;
        }

        if (fragmentTransaction) {
            // Set fragment on content_main
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }

    }


    //========================================//
    // INTENTS //
    //========================================//

    private void startUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void startLoginctivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //========================================//
    // FIREBASE HEADER //
    //========================================//


    @Override
    protected void onStart() {

        super.onStart();
        if (mUser != null) {
            // FirebaseReference: /root/users/"user"
            mUsersRef = mRootRef.child("users").child(mUser.getUid());
            mUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    mUsername.setText(user.getUsername());
                    mFirstName.setText(user.getFirstName());
                    mScore.setText(user.getScore().toString() + " puntos");
                    // If image profile URL don't is default
                    if (!user.getUriImageProfile().equals("default")) {
                        // Picasso library to get image from URL profile image user
                        Picasso.with(MainActivity.this).load(user.getUriImageProfile()).into(mImageProfile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
