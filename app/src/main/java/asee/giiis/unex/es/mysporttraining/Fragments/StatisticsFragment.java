package asee.giiis.unex.es.mysporttraining.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.Objects.Activity;
import asee.giiis.unex.es.mysporttraining.R;


public class StatisticsFragment extends Fragment {


    // Reference root JSON database
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mTrainingRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    private FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    private GraphView mGraph;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        mGraph = (GraphView) view.findViewById(R.id.graphic_score);

        retrieveFirebaseData();

        return view;
    }


    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void retrieveFirebaseData() {
        if (mUser != null) {
            mTrainingRef = mRootRef.child("exerciseList").child(mUser.getUid());
            mTrainingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        initGraphicView(dataSnapshot);
                        mGraph.invalidate();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    private void initGraphicView(DataSnapshot dataSnapshot) throws ParseException {
        List<Activity> exerciseList = new ArrayList<>();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            for (DataSnapshot ds2 : ds.getChildren()) {
                Activity activity = ds2.getValue(Activity.class);
                exerciseList.add(activity);
            }
        }

        Log.i("tamaño, evento", Integer.toString(exerciseList.size()));

        // Sort by Date
        Collections.sort(exerciseList, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        List<DataPoint> dataPoints = new ArrayList<>();
        String date = "";
        Integer score = 0;
        Integer maxScore = 0;
        if (exerciseList.size() > 0) {
            date = exerciseList.get(0).getDate();
        }


        for (int i = 0; i < exerciseList.size(); i++) {
            if (exerciseList.get(i).getDate().equals(date)) {
                score += exerciseList.get(i).getScore();
            } else {
                dataPoints.add(new DataPoint(new SimpleDateFormat("yyyy-MM-dd").parse(date),
                        (long) score));
//                series.appendData(new DataPoint(new SimpleDateFormat("yyyy-MM-dd").parse(date),
//                        (long) score), false, 100);
                if (score > maxScore) {
                    maxScore = score;
                }
                score = exerciseList.get(i).getScore();
            }
            date = exerciseList.get(i).getDate();

        }
//        series.appendData(new DataPoint(new SimpleDateFormat("yyyy-MM-dd").parse(date),
//                (long) score), false, 100);
        dataPoints.add(new DataPoint(new SimpleDateFormat("yyyy-MM-dd").parse(date),
                (long) score));
        DataPoint[] dataPointsArray = new DataPoint[dataPoints.size()];
        dataPointsArray = dataPoints.toArray(dataPointsArray);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointsArray);
        mGraph.addSeries(series);

        // set date label formatter
        mGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        mGraph.getGridLabelRenderer().setNumVerticalLabels(5);

        // activate horizontal zooming and scrolling
        mGraph.getViewport().setScalable(true);


        // set manual y bounds to have nice steps
        mGraph.getViewport().setMinY(10);
        mGraph.getViewport().setMaxY(maxScore);
        mGraph.getViewport().setYAxisBoundsManual(true);


        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        mGraph.getGridLabelRenderer().setHumanRounding(false);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), getDate((long) dataPoint.getX()) + ": " + (int) dataPoint.getY()
                        + " puntos", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
