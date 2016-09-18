package com.uncgcapstone.android.seniorcapstone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleFragment";
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());


    public ScheduleFragment() {
        // Required empty public constructor
    }

    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        compactCalendar = (CompactCalendarView) v.findViewById(R.id.compactcalendar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(dateFormatForMonth.format(compactCalendar.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ((MainActivity)getActivity()).getSupportActionBar().setTitle(dateFormatForMonth.format(dateClicked));
                List<Event> bookingsFromMap = compactCalendar.getEvents(dateClicked);
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
                if(bookingsFromMap != null){
                    Log.d(TAG, bookingsFromMap.toString());
                    //mutableBookings.clear();
                    for(Event booking : bookingsFromMap){
                        //mutableBookings.add((String)booking.getData());
                    }
                    //adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                ((MainActivity)getActivity()).getSupportActionBar().setTitle(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        return v;
    }

}
