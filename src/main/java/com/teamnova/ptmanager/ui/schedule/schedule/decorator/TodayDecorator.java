package com.teamnova.ptmanager.ui.schedule.schedule.decorator;

import android.content.Context;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.teamnova.ptmanager.R;

public class TodayDecorator implements DayViewDecorator {
    CalendarDay today = CalendarDay.today();
    Context context;

    public TodayDecorator(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(context.getDrawable(R.drawable.background_date));
    }
}
