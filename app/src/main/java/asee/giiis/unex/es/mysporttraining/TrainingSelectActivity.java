package asee.giiis.unex.es.mysporttraining;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.CalendarContract.Events;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import asee.giiis.unex.es.mysporttraining.Adapters.TrainingSelectActivityAdapter;
import asee.giiis.unex.es.mysporttraining.Objects.Activity;
import asee.giiis.unex.es.mysporttraining.Objects.User;

public class TrainingSelectActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;

    private final static String CATEGORY = "category";
    private final static String TRAINING_NAME = "trainingTitle";
    // Dialog
    private final static String DIALOG_ACCEPT_BUTTON = "Aceptar";
    private static final String DIALOG_CANCEL_BUTTON = "CANCELAR";

    // DatabaseReference Firebase
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mActivitiesRef;
    private DatabaseReference mUsersRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    // Static variables for time and date
    private static String timeString;
    private static String dateString;
    private static TextView dateView;
    private static TextView timeView;

    private String mCategory;
    private Date mDate;
    private String mTrainingName = "";

    // RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Activity> mExerciseList = new ArrayList<>();

    // Calendar Provider Params
    private static int mDayCalendar;
    private static int mMonthCalendar;
    private static int mYearCalendar;
    private static int mHourCalendar;
    private static int mMinuteCalendar;
    private Activity mItemCalendar;

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
        mExerciseList.clear();
        // Get category from intent extras
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCategory = (String) bundle.get(CATEGORY);
            mTrainingName = (String) bundle.get(TRAINING_NAME);
        }
        //  Firebase ref: /root/activities/"category"
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
        String string = "+ " + Integer.toString(item.getScore()) + " puntos";
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
                if (mUser != null) {
                    // Add Exercise to Firebase
                    // Firebase ref: /root/exerciseList/"user"/"exerciseList"
                    mActivitiesRef = mRootRef.child("exerciseList").child(mUser.getUid()).child(mTrainingName);
                    item.setDate(dateString);
                    item.setHour(timeString);
                    item.setCategory(parseCategory());
                    // Add a new activity to Firebase ref
                    mActivitiesRef.push().setValue(item);

                    contentProviderCalendar();
                    mItemCalendar = item;

                    // Update user SCORE
                    // Firebase ref: /root/users/"user"
                    mUsersRef = mRootRef.child("users").child(mUser.getUid());
                    // Transaction to update SCORE
                    mUsersRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            // Get user
                            User user = mutableData.getValue(User.class);
                            // Set score = exercise score + user actual score
                            user.setScore(item.getScore() + user.getScore());
                            // Update user
                            mutableData.setValue(user);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            // Transaction completed
                        }
                    });
                    // Intent to Activity TrainingNewActivity
                    returnActivityNewTraining();
                }
            }
        });
        builder.setNegativeButton(DIALOG_CANCEL_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Intent to Activity TrainingNewActivity
    private void returnActivityNewTraining() {
        Intent intent = new Intent(this, TrainingNewActivity.class);
        intent.putExtra(TRAINING_NAME, mTrainingName);
        startActivity(intent);
    }

    //========================================//
    // DIALOG TIME AND DATE //
    //========================================//
    private void setDefaultDateTime() {
        // Default is current time
        mDate = new Date();
        mDate = new Date(mDate.getTime());

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
        // Content Provider calendar variables
        mYearCalendar = year;
        mMonthCalendar = monthOfYear;
        mDayCalendar = dayOfMonth;

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
        // Content Provider calendar variables
        mHourCalendar = hourOfDay;
        mMinuteCalendar = minute;

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
        dialog.show(getFragmentManager(), "datepicker");
    }

    private void showTimePickerDialog() {
        DialogFragment dialog = new TimePickerFragment();
        dialog.show(getFragmentManager(), "timepicker");
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


    //========================================//
    // PARSE CATEGORY TO SHOW USER //
    //========================================//

    private String parseCategory() {
        switch (mCategory) {
            case "cardio":
                return "Cardio";
            case "collective":
                return "Colectiva";
            case "elongation":
                return "Estiramiento";
            case "gym_weights":
                return "Pesas";
            case "sports":
                return "Deporte";
            case "strength":
                return "Fuerza";
        }
        return null;
    }


    private void contentProviderCalendar() {

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(mYearCalendar, mMonthCalendar, mDayCalendar, mHourCalendar, mMinuteCalendar);
        Calendar endTime = Calendar.getInstance();
        endTime.set(mYearCalendar, mMonthCalendar, mDayCalendar, 23, 59);

        final ContentValues event = new ContentValues();
        event.put(Events.CALENDAR_ID, 1);
        event.put(Events.TITLE, "MySportTraining: Entrenamiento de " + mUser.getEmail());
        event.put(Events.ORGANIZER, mUser.getEmail());
        event.put(Events.DESCRIPTION, "Entrenamiento: " + mItemCalendar.getName() + "\nHora de comienzo: " +
                mItemCalendar.getHour() + "\nCategoría: " + mItemCalendar.getCategory() + "\nPuntuación: " + mItemCalendar.getScore().toString());
        event.put(Events.DTSTART, beginTime.getTimeInMillis());
        event.put(Events.DTEND, endTime.getTimeInMillis());
        event.put(Events.ALL_DAY, 0);

        String timeZone = TimeZone.getDefault().getID();
        event.put(Events.EVENT_TIMEZONE, timeZone);

        Uri uri;


        if (Build.VERSION.SDK_INT >= 8) {
            uri = Uri.parse("content://com.android.calendar/events");
        } else {
            uri = Uri.parse("content://calendar/events");
        }

        this.getContentResolver().insert(uri, event);

//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.WRITE_CALENDAR)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    android.Manifest.permission.WRITE_CALENDAR)) {
//                Toast.makeText(this, "permisos", Toast.LENGTH_SHORT).show();
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_CALENDAR},
//                        MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
//
//                // MY_PERMISSIONS_REQUEST_WRITE_CALENDAR is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CALENDAR) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Calendar beginTime = Calendar.getInstance();
//                beginTime.set(mYearCalendar, mMonthCalendar, mDayCalendar, mHourCalendar, mMinuteCalendar);
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(mYearCalendar, mMonthCalendar, mDayCalendar, 23, 59);
//
//                final ContentValues event = new ContentValues();
//                event.put(Events.CALENDAR_ID, 1);
//                event.put(Events.TITLE, "MySportTraining: Entrenamiento de " + mUser.getEmail());
//                event.put(Events.ORGANIZER, mUser.getEmail());
//                event.put(Events.DESCRIPTION, "Entrenamiento: " + mItemCalendar.getName() + "\nHora de comienzo: " +
//                        mItemCalendar.getHour() + "\nCategoría: " + mItemCalendar.getCategory() + "\nPuntuación: " + mItemCalendar.getScore().toString());
//                event.put(Events.DTSTART, beginTime.getTimeInMillis());
//                event.put(Events.DTEND, endTime.getTimeInMillis());
//                event.put(Events.ALL_DAY, 0);
//
//                String timeZone = TimeZone.getDefault().getID();
//                event.put(Events.EVENT_TIMEZONE, timeZone);
//
//                Uri uri;
//
//
//                if (Build.VERSION.SDK_INT >= 8) {
//                    uri = Uri.parse("content://com.android.calendar/events");
//                } else {
//                    uri = Uri.parse("content://calendar/events");
//                }
//
//                this.getContentResolver().insert(uri, event);
//            }
//        }
//    }

}
