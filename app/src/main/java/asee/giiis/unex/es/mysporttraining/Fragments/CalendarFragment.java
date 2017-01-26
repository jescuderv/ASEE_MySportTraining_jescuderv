package asee.giiis.unex.es.mysporttraining.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import asee.giiis.unex.es.mysporttraining.Objects.Activity;
import asee.giiis.unex.es.mysporttraining.R;

public class CalendarFragment extends Fragment {

    // Reference root JSON database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef;
    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();


    CompactCalendarView mCompactCalendar;
    private SimpleDateFormat mDateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        mCompactCalendar = (CompactCalendarView) view.findViewById(R.id.compact_calendar_view);
        mCompactCalendar.setUseThreeLetterAbbreviation(true);

        TextView text = (TextView) view.findViewById(R.id.mes);
        text.setText(mDateFormatMonth.format(new Date()));


        if (mUser != null){
            // Firebase ref: /exerciseList/"user"
            mUsersRef = mRootRef.child("exerciseList").child(mUser.getUid());
            mUsersRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    getExercises(dataSnapshot, view);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    getExercises(dataSnapshot, view);
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

    return view;
    }

    // Get all exercises from user
    private void getExercises(DataSnapshot dataSnapshot, final View view){
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            // Get activity snapshot
            final Activity activity = ds.getValue(Activity.class);

            // Converse String date to Date
            String dtStart = activity.getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(dtStart);
                Long milisecond = date.getTime();

                // Create new Event with information about one exercise
                Event event = new Event(Color.RED, milisecond, activity.getName());
                mCompactCalendar.addEvent(event);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Calendar listener to show exercises
            mCompactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                @Override
                public void onDayClick(Date dateClicked) {
                    // List events of calendar
                    List<Event> events = mCompactCalendar.getEvents(dateClicked);

                    // Show all events for a day
                    for(int i=0; i < events.size(); i++){
                        Toast.makeText(getActivity(), "Actividad " + (i+1) + ": "
                                + events.get(i).getData().toString(), Toast.LENGTH_SHORT).show();
                    }
                    if (events.size()==0){
                        Toast.makeText(getActivity(), "Ninguna actividad planeada para hoy", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    // Change date when scroll
                    TextView text = (TextView) view.findViewById(R.id.mes);
                    text.setText(mDateFormatMonth.format(firstDayOfNewMonth));
                }
            });
        }
    }



}



