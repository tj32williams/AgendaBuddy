package com.example.myapplication.customcalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Part of the open source code
 *
 * The Custom Calendar
 *
 * ask me (Brian) for more info and I'll try to explain. I do not fully understand either
 * If your wanna change or have change (i.e commit to gitHub) please tell me so I know (or I guess tell the group as well)
 */
public class CustomCalendarView extends LinearLayout {

    ImageButton nextButton, previousButton;
    TextView currentDate;
    GridView gridView;

    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();
    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    /* How this works (i think or at least how I understand it so far):
     * Basically SQLite has a local database (as joshua said)
     * This instance variable acts sort of like a 'pointer'
     * I say this because every time we want to access the database in a method, this variable gets a reference to the local files of the database
     * Then we do whatever we need to do while we have that reference (read/write to the database)
     * Then once we are done accessing it or we are done in a method, we have to 'close' it --> i.e. get rid of the reference (for safety reasons?)
     */
    DBOpenHelper dbOpenHelper;

    /**
     * Constructor
     * @param context
     */
    public CustomCalendarView(Context context) {
        super(context);
    }

    /**
     * Main Constructor --> used in MainActivity.java
     * @param context
     * @param attrs
     */
    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetUpCalendar();

        /* change the month display backwards (ex: from March to February */
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });

        /* change the month display forwards (ex: from August to September */
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });

        /* Tapping a day on the calendar will invoke the 'addEvent' prompt/option */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
                final EditText eventName = addView.findViewById(R.id.eventName);

                final TextView eventStartTime = addView.findViewById(R.id.eventStartTime);
                ImageButton setStartTime = addView.findViewById(R.id.setEventStartTime);
                final CheckBox alarmMe = addView.findViewById(R.id.alarmMe);

                final TextView eventEndTime = addView.findViewById(R.id.eventEndTime);
                ImageButton setEndTime = addView.findViewById(R.id.setEventEndTime);
                final EditText eventPriority = addView.findViewById(R.id.eventPriority);

                final EditText eventNotes = addView.findViewById(R.id.eventNotes);


                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(position));
                alarmYear = dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

                Button addEvent = addView.findViewById(R.id.addEvent);

                /* things related to setting the start time of the event */
                setStartTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat hFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                String event_Time = hFormat.format(c.getTime());
                                eventStartTime.setText(event_Time);
                                alarmHour = c.get(Calendar.HOUR_OF_DAY);
                                alarmMinute = c.get(Calendar.MINUTE);

                            }
                        }, hours, minutes, false);
                        timePickerDialog.show();
                    }
                });

                /* things to do with setting up the end time of the event */
                setEndTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.setTimeZone(TimeZone.getDefault());
                                SimpleDateFormat hFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                String event_Time = hFormat.format(c.getTime());
                                eventEndTime.setText(event_Time);

                            }
                        }, hours, minutes, false);
                        timePickerDialog.show();
                    }
                });


                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                /* things related to when you press the addEvent button */
                addEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /* check if the 'notify me' checkbox is checked or not --> basically check if user wants to be notified or not */
                        if(alarmMe.isChecked()) {
                            saveEvent(eventName.getText().toString(), eventStartTime.getText().toString(), eventEndTime.getText().toString(), date, month, year,"on", eventPriority.getText().toString(), eventNotes.getText().toString());
                            SetUpCalendar();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute);
                            setAlarm(calendar, eventName.getText().toString(), eventStartTime.getText().toString(), getRequestCode(date
                                , eventName.getText().toString(), eventStartTime.getText().toString()));
                            alertDialog.dismiss();
                        } else {
                            saveEvent(eventName.getText().toString(), eventStartTime.getText().toString(), eventEndTime.getText().toString(), date, month, year,"off", eventPriority.getText().toString(), eventNotes.getText().toString());
                            SetUpCalendar();
                            alertDialog.dismiss();
                        }

                    }
                });

                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();

            }
        });

        /* press and hold on a day in the calendar will show a list of events that are set on that day */
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
                RecyclerView recyclerView = showView.findViewById(R.id.eventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(), CollectEventByDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();

                /* This means when you press outside of the 'prompt' box */
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SetUpCalendar();
                    }
                });

                return true;
            }
        });

    }

    /**
     * Something to do with notifications as well (i think)
     *
     * @param date
     * @param event
     * @param time
     * @return
     */
    private int getRequestCode(String date, String event, String time) {
        int code = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readIDEvents(date, event, time, database);
        while(cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();

        return code;
    }

    /**
     * Something to do to set up the alarm/notification of an event if the user wants to be notified
     *
     * @param calendar
     * @param event
     * @param time
     * @param requestCode
     */
    private void setAlarm(Calendar calendar, String event, String time, int requestCode) {
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("event", event);
        intent.putExtra("time", time);
        intent.putExtra("id", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * Fetch events from database and save that to an ArrayList data structure
     *
     * @param date
     * @return
     */
    private ArrayList<Events> CollectEventByDate(String date) {
        ArrayList<Events> arrayList = new ArrayList<>();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEvents(date, database);
        while(cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.START_TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            // need to add priority here
            Events events = new Events(event,time, Date, month, year);
            arrayList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();

        return arrayList;
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Save event to database specifically 'dbOpenHelper'
     * notice that it calls ___.SaveEvent(...) --> the method found in DBOpenHelper.java
     *
     * @param event
     * @param startTime
     * @param endTime
     * @param date
     * @param month
     * @param year
     * @param notify
     * @param priority
     * @param notes
     */
    private void saveEvent(String event, String startTime, String endTime, String date, String month, String year, String notify, String priority, String notes) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        // need to add priority here
        dbOpenHelper.SaveEvent(event, startTime, endTime, date, month, year, notify, priority, notes, database);
        dbOpenHelper.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize all the position and references of the different things in the Calendar Page (i think)
     */
    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        nextButton = view.findViewById(R.id.nextBtn);
        previousButton = view.findViewById(R.id.previousBtn);
        currentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridView);
    }

    /**
     * Sets and display the gridView of the calendar
     */
    private void SetUpCalendar() {
        String dateCurrent = dateFormat.format(calendar.getTime());
        currentDate.setText(dateCurrent);

        /* fills in the days of the month in the gridView */
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        collectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while(dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        myGridAdapter = new MyGridAdapter(context, dates, calendar, eventsList);
        gridView.setAdapter(myGridAdapter);

    }

    /**
     * Get all the events on the month we are in
     *
     * @param Month the month displayed on the calendar
     * @param Year the year displayed on the calendar
     */
    private void collectEventsPerMonth(String Month, String Year) {
        eventsList.clear();
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.readEventsPerMonth(Month, Year, database);
        while(cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.START_TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            Events events = new Events(event, time, date, month, year);
            eventsList.add(events);
        }
        cursor.close();
        dbOpenHelper.close();
    }



}
