package asee.giiis.unex.es.mysporttraining;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Adapters.TrainingSelectActivityAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.Activity;

public class TrainingSelectActivity extends AppCompatActivity {

    // 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
    private static final int SEVEN_DAYS = 604800000;

    private final static String CATEGORY = "category";
    private final static String DIALOG_ACCEPT_BUTTON = "Aceptar";

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mActivitiesRef;


    private static String timeString;
    private static String dateString;
    private static TextView dateView;
    private static TextView timeView;

    private String mCategory;
    private Date mDate;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_select);

        // Get exercises from firebase and set Adapter
        retrieveExerciseListFirebase();

        // Get Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_exercises_category);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);

    }

    //========================================//
            // RETRIEVE DATA FIREBASE //
    //========================================//
    private void retrieveExerciseListFirebase() {
        // Get category from intent extras
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCategory = (String) bundle.get(CATEGORY);
        }
        //  Firebase ref: /root/activieties/"category"
        mActivitiesRef = mRootRef.child("activities").child(mCategory);
        // Child event for get all activities from a category
        mActivitiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addExercise(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addExercise(dataSnapshot);
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

    // Add a exercise selected to list adapter
    private void addExercise(DataSnapshot dataSnapshot) {
        Activity activity = dataSnapshot.getValue(Activity.class);
        mExerciseList.add(activity);

        // Adapter
        if (mExerciseList.size() > 0) {
            mAdapter = new TrainingSelectActivityAdapter(this, mExerciseList,
                    new TrainingSelectActivityAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Activity item) {
                            exerciseDialog(item);
                        }
                    });
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    //========================================//
                    // DIALOG //
    //========================================//
    private void exerciseDialog(final Activity item) {
        // AlertDialog - set info for an exercise
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_input_exercise, null);
        builder.setView(v);

        // Dialog-Score
        TextView scoreView = (TextView) v.findViewById(R.id.dialog_score);
        String string = "Score: " + Integer.toString(item.getScore());
        scoreView.setText(string);

        // Dialog-date and Dialog-time
        dateView = (TextView) v.findViewById(R.id.dialog_date_input);
        timeView = (TextView) v.findViewById(R.id.dialog_hour_input);

        // Set default time and date to dialog
        setDefaultDateTime();

        // Show picker when click
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Dialog-title
        builder.setTitle(item.getName());
        // Dialog-positive button ("aceptar")
        builder.setPositiveButton(DIALOG_ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Firebase ref: /root/exerciseList/"user"/"exerciseList"
                mActivitiesRef = mRootRef.child("exerciseList").child("idUsuarioPrueba").child("lista2");
                item.setDate(dateString);
                item.setHour(timeString);
                // Add a new activity to Firebase ref
                mActivitiesRef.push().setValue(item);
                // Intent to Activity TrainingNewActivity
                returnActivityNewTraining();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Intent to Activity TrainingNewActivity
    private void returnActivityNewTraining(){
        Intent intent = new Intent(this, TrainingNewActivity.class);
        startActivity(intent);
    }

    //========================================//
            // DIALOG TIME AND DATE //
    //========================================//
    private void setDefaultDateTime() {

        // Default is current time + 7 days
        mDate = new Date();
        mDate = new Date(mDate.getTime() + SEVEN_DAYS);

        Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        dateView.setText(dateString);

        setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.MILLISECOND));

        timeView.setText(timeString);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

        // Increment monthOfYear for Calendar/Date -> Time Format setting
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        dateString = year + "-" + mon + "-" + day;
    }

    private static void setTimeString(int hourOfDay, int minute, int mili) {
        String hour = "" + hourOfDay;
        String min = "" + minute;

        if (hourOfDay < 10)
            hour = "0" + hourOfDay;
        if (minute < 10)
            min = "0" + minute;

        timeString = hour + ":" + min + ":00";
    }

    private void showDatePickerDialog() {
        DialogFragment dialog = new DatePickerFragment();
        dialog.show(getFragmentManager(),"datepicker");
    }

    private void showTimePickerDialog() {
        DialogFragment dialog = new TimePickerFragment();
        dialog.show(getFragmentManager(),"timepicker");
    }


    //========================================//
          // DIALOG FRAGMENT FOR PICKERS //
    //========================================//

    // DialogFragment used to pick a Activity deadline date
    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);

            dateView.setText(dateString);
        }

    }

    // DialogFragment used to pick an Activity deadline time
    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTimeString(hourOfDay, minute, 0);

            timeView.setText(timeString);
        }
    }


}
