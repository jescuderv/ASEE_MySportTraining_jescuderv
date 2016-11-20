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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import asee.giiis.unex.es.mysporttraining.R;

public class CalendarFragment extends Fragment {


    CompactCalendarView mCompactCalendar;
    private SimpleDateFormat mDateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_calendar, container, false);


        mCompactCalendar = (CompactCalendarView) view.findViewById(R.id.compact_calendar_view);
        mCompactCalendar.setUseThreeLetterAbbreviation(true);


        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        Event ev1 = new Event(Color.RED, 1477040400000L, "Ejercicios musculación pecho");
        mCompactCalendar.addEvent(ev1);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.RED, 1477040400000L, "Running");
        mCompactCalendar.addEvent(ev2);



        TextView text = (TextView) view.findViewById(R.id.mes);
        text.setText(mDateFormatMonth.format(new Date()));


        mCompactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = mCompactCalendar.getEvents(dateClicked);

                for(int i=0; i < events.size(); i++){
                    Toast.makeText(getActivity(), "Actividad " + i + ": "
                            + events.get(i).getData().toString(), Toast.LENGTH_SHORT).show();
                }
                if (events.size()==0){
                    Toast.makeText(getActivity(), "Ninguna actividad planeada para hoy", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                TextView text = (TextView) view.findViewById(R.id.mes);
                text.setText(mDateFormatMonth.format(firstDayOfNewMonth));
            }
        });

    return view;
    }

}



