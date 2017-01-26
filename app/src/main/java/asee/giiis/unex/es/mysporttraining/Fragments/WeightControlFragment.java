package asee.giiis.unex.es.mysporttraining.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asee.giiis.unex.es.mysporttraining.Adapters.WeightControlAdapter;
import asee.giiis.unex.es.mysporttraining.R;

public class WeightControlFragment extends Fragment {

    // Dialog
    private final static String DIALOG_OK = "ACEPTAR";
    private static final String DIALOG_CANCEL_BUTTON = "CANCELAR";
    // Map
    private final static String MAP_WEIGHT = "weight";
    private final static String MAP_DATE = "date";

    // DatabaseReference Firebase
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mWeightControlRef;
    private DatabaseReference mUsersRef;

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();

    // RecyclerView
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<Map<String, String>> mWeightList = new ArrayList<>();

    // Dialog textview date and weight
    private static TextView dateView;
    private static String dateString;
    private Date mDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weight_control, container, false);

       retrieveDataFirebase();

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.weight_ctrl_add_info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeightInfo();
            }
        });

        // Get Recycler View
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_weight_control);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Best perfomance if content do not change the layout size of RecyclerView
        mRecyclerView.setHasFixedSize(true);


        return view;
    }


    //========================================//
            // RETRIEVE DATA FIREBASE //
    //========================================//

    private void retrieveDataFirebase() {
        if (mUser != null){
            mWeightControlRef = mRootRef.child("weightControl").child(mUser.getUid());
            mWeightControlRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getInfoWeightControl(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getInfoWeightControl(DataSnapshot dataSnapshot) {
        mWeightList.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Map<String, String> weightControl = (Map<String, String>) ds.getValue();
            mWeightList.add(weightControl);

            // Adapter
            if (mWeightList.size() > 0) {
                mAdapter = new WeightControlAdapter(getContext(), mWeightList);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }


    //========================================//
        // DIALOG - ADD DATA TO FIREBASE //
    //========================================//

    private void addWeightInfo() {
        // AlertDialog - set new weight and date info
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_input_weight_control, null);
        builder.setView(v);


        // Dialog weight
        final TextInputLayout weight = (TextInputLayout) v.findViewById(R.id.dialog_input_layout_weight);


        // Dialog date
        dateView = (TextView) v.findViewById(R.id.dialog_weight_date_input);
        // Set default date to dialog
        setDefaultDate();
        // Show picker when click
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        // Dialog-title
        builder.setTitle("Agregar información de peso");
        // Dialog-positive button ("aceptar")
        builder.setPositiveButton(DIALOG_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mUser != null) {
                            // boolean to detect any error
                            boolean error = false;
                            final String weightData = weight.getEditText().getText().toString();
                            if (weightData.isEmpty()) {
                                error = true;
                            }
                            if (!error) {
                                // New map to set data
                                Map<String, String> map = new HashMap<>();
                                map.put(MAP_WEIGHT, weightData);
                                map.put(MAP_DATE, dateString);
                                mWeightList.add(map);
                                // Firebase ref: /root/weightControl/"user"
                                mWeightControlRef = mRootRef.child("weightControl").child(mUser.getUid());
                                mWeightControlRef.push().setValue(map);
                                // Firebase ref: /root/users/"user"
                                mUsersRef = mRootRef.child("users").child(mUser.getUid());
                                // Update weight attribute for user with last weight added
                                Map<String, Object> taskMap = new HashMap<>();
                                taskMap.put("weight", Integer.parseInt(weightData));
                                mUsersRef.updateChildren(taskMap);

                            } else {
                                Toast.makeText(getContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
        builder.setNegativeButton(DIALOG_CANCEL_BUTTON, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    //========================================//
                // DIALOG DATE //
    //========================================//

    private void setDefaultDate() {
        // Default is current time
        mDate = new Date();
        mDate = new Date(mDate.getTime());

        Calendar c = Calendar.getInstance();
        c.setTime(mDate);

        setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dateView.setText(dateString);

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

    private void showDatePickerDialog() {
        DialogFragment dialog = new DatePickerFragment();
        dialog.show(getActivity().getFragmentManager(), "datepicker");
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

}