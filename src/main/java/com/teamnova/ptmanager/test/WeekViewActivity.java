package com.teamnova.ptmanager.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;

import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.databinding.ActivityMemberHomeBinding;
import com.teamnova.ptmanager.databinding.ActivityWeekViewBinding;



//implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener
public class WeekViewActivity extends AppCompatActivity {
    /*DateTime selectedDate;

    WeekCalendar week_cal_topbar;

    WeekView week_day_view;

    private List<jutt.com.zcalenderviewextended.day.WeekViewEvent> weekEvents;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_week_view);

        week_cal_topbar = findViewById(R.id.week_cal_topbar);
        week_day_view = findViewById(R.id.week_day_view);

        Intent in = getIntent();
        //selectedDate = (DateTime) in.getExtras().get("SELECTED");
        selectedDate  = new DateTime(2021,6,7,0,0);

        week_day_view.goToDate(selectedDate.toGregorianCalendar());

        week_cal_topbar.setSelectedDate(selectedDate);

        week_cal_topbar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                selectedDate = dateTime;

                // fetch events from api or intenet and store it in there

                // then finally go to selected date again
                week_day_view.goToDate(selectedDate.toGregorianCalendar());
            }
        });

        // Set an action when any event is clicked.
        week_day_view.setOnEventClickListener(this);
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        week_day_view.setMonthChangeListener(this);
        // Set long press listener for events.
        week_day_view.setEventLongPressListener(this);

        addFakeEvents();

        setupDateTimeInterpreter();*/
    }

    /*private void setupDateTimeInterpreter() {
        week_day_view.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEEE , MMMM d, yyyy", Locale.getDefault());
                return weekdayNameFormat.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? ((hour-12) == 0 ? "12 PM" : (hour-12) + " PM") : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    public void addFakeEvents(){
        weekEvents  = new ArrayList<jutt.com.zcalenderviewextended.day.WeekViewEvent>();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8 - 1);
        startTime.set(Calendar.YEAR, 2017);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        endTime.set(Calendar.MONTH, 8 - 1);
        jutt.com.zcalenderviewextended.day.WeekViewEvent event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorLightRed));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 30);
        endTime.set(Calendar.MONTH, 8-1);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorAccent));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 4);
        startTime.set(Calendar.MINUTE, 20);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 5);
        endTime.set(Calendar.MINUTE, 0);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorBlack));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 5);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        endTime.set(Calendar.MONTH, 8-1);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorFacebook));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 5);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8 - 1);
        startTime.set(Calendar.YEAR, 2017);
        startTime.add(Calendar.DATE, 1);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        endTime.set(Calendar.MONTH, 8 - 1);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorGrey));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 15);
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorGreen));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 1);
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorGreydark));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        startTime.set(Calendar.HOUR_OF_DAY, 15);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2018);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.colorPrimary));
        weekEvents.add(event);

        //AllDay event
        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 23);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(7, getEventTitle(startTime),null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.colorGreytext));
        weekEvents.add(event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 8);
        startTime.set(Calendar.HOUR_OF_DAY, 2);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 10);
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(8, getEventTitle(startTime),null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.colorGreytext));
        weekEvents.add(event);

        // All day event until 00:00 next day
        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 10);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        startTime.set(Calendar.MONTH, 8-1);
        startTime.set(Calendar.YEAR, 2017);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 11);
        event = new jutt.com.zcalenderviewextended.day.WeekViewEvent(8, getEventTitle(startTime), null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.colorPrimary));
        weekEvents.add(event);
    }*/

    /*protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(jutt.com.zcalenderviewextended.day.WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public List<? extends jutt.com.zcalenderviewextended.day.WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<jutt.com.zcalenderviewextended.day.WeekViewEvent> localEvents = new ArrayList<>();

        for (jutt.com.zcalenderviewextended.day.WeekViewEvent myEvent: weekEvents) {
            int month = myEvent.getStartTime().get(Calendar.MONTH);
            int year = myEvent.getStartTime().get(Calendar.YEAR);

            if (year == newYear && month == newMonth-1) {
                localEvents.add(myEvent);
            }
        }
        return localEvents;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }*/
}