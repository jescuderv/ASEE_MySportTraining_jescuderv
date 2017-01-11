package asee.giiis.unex.es.mysporttraining.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
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

    // Line Chart
    private LineChart mChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        mChart = (LineChart) view.findViewById(R.id.graphic_score);

        // Retrieve data from Firebase
        retrieveFirebaseData();

        return view;
    }


    //========================================//
            // RETRIEVE DATA FIREBASE //
    //========================================//

    private void retrieveFirebaseData() {
        if (mUser != null) {
            // Firebase ref: /exerciseList/"user"
            mTrainingRef = mRootRef.child("exerciseList").child(mUser.getUid());
            mTrainingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        initGraphicView(dataSnapshot);
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
        // Get all Dates for each Exercise
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            for (DataSnapshot ds2 : ds.getChildren()) {
                Activity activity = ds2.getValue(Activity.class);
                exerciseList.add(activity);
            }
        }

        // Sort by Date
        Collections.sort(exerciseList, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        String date = "";
        Integer score = 0;
        Integer maxScore = 0;
        if (exerciseList.size() > 0) {
            date = exerciseList.get(0).getDate();
        }


        List<Entry> entries = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        // Group all scores for the same date
        Integer j = 0;
        for (int i = 0; i < exerciseList.size(); i++) {
            // If dates are equals, score++
            if (exerciseList.get(i).getDate().equals(date)) {
                score += exerciseList.get(i).getScore();
            } else {
                // Else, add entry to Line Chart
                entries.add(new Entry(j, score));
                j++;
                dateList.add(date);
                if (score > maxScore) {
                    maxScore = score;
                }
                score = exerciseList.get(i).getScore();
            }
            date = exerciseList.get(i).getDate();
        }
        entries.add(new Entry(j, score));
        dateList.add(date);

        // Set dataset to Line Chart
        LineDataSet dataSet = new LineDataSet(entries, "Puntuación");
        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);

        // Get format value x to date value
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter(dateList));
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);

        // 3 min values for x-axis scalable
        if (xAxis.getAxisMaximum() > 2) {
            mChart.setVisibleXRange(0, 3);
        }
        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // Reload view
        mChart.invalidate();

//        series.setOnDataPointTapListener(new OnDataPointTapListener() {
//            @Override
//            public void onTap(Series series, DataPointInterface dataPoint) {
//                Toast.makeText(getActivity(), getDate((long) dataPoint.getX()) + ": " + (int) dataPoint.getY()
//                        + " puntos", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    // Custom Class to format values form x-axis to date values
    public class MyCustomXAxisValueFormatter implements IAxisValueFormatter {

        private List<String> mDateList;

        public MyCustomXAxisValueFormatter(List<String> dateList) {
            this.mDateList = dateList;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mDateList.size() == 1){
                return mDateList.get(0);
            } else {
                return mDateList.get((int) value);
            }
        }
    }


}
