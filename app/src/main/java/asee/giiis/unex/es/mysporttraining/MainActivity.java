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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import asee.giiis.unex.es.mysporttraining.Fragments.ActivityFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.CalendarFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.FriendsFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.StatisticsFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.TrainingFragment;
import asee.giiis.unex.es.mysporttraining.Fragments.WeightControlFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Intent for UserProfileActivity
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        CircleImageView imageProfile = ((CircleImageView) header.findViewById(R.id.nav_usr_profile_image));
        imageProfile.setOnClickListener(new View.OnClickListener() {
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
                if (mFirebaseAuth.getCurrentUser() != null){
                    mFirebaseAuth.signOut();
                    finish();
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
        Fragment fragment = null;
        boolean fragmentTransaction = false;
        switch (option) {
            case 0:
                fragment = new ActivityFragment();
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();
        }

    }

    private void startUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void startLoginctivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
