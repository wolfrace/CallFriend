package com.fiivt.ps31.callfriend;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Setter;

public class SignificantEventEditDialog extends DialogFragment {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private final long[] spinnerItemIdToReminderTime = {
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.DAYS.toMillis(2),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(30)};

    @Setter
    private OnDataSetChangedListener listener;
    private EditText eventNameText;
    private CircleImageView eventIcon;
    private EditText eventDateText;
    private Spinner reminderTimeSpinner;
    private int eventId;

    private long getReminderTime() {
        long id = reminderTimeSpinner.getSelectedItemId();
        return spinnerItemIdToReminderTime[(int) id];
    }

    private int getReminderTimeSpinnerIdByTime(long delta) {
        int id = 0;
        while(id < spinnerItemIdToReminderTime.length) {
            if (spinnerItemIdToReminderTime[id] >= delta) {
                break;
            }
            ++id;
        }
        return id;
    }

    public interface OnDataSetChangedListener {
        void onDataSetChanged(int eventId, String eventName, Date eventDate, long reminderTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.significant_event_edit_dialog, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        eventIcon = (CircleImageView) view.findViewById(R.id.significant_event_icon);
        eventNameText = (EditText) view.findViewById(R.id.significant_event_name);
        eventDateText = (EditText) view.findViewById(R.id.significant_event_date);
        reminderTimeSpinner = (Spinner) view.findViewById(R.id.reminder_time_spinner);

        Bundle args = getArguments();
        if (args != null) {
            setEventData(args);
        }

        bindButtonEventsListeners(view);
        return view;
    }

    private void bindButtonEventsListeners(View view) {
        eventDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerFragment(new DatePickerFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(Date date) {
                        setEventDate(date);
                    }
                });
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        Button okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryApplyNewData();
            }
        });
    }

    private void tryApplyNewData() {
        String name = eventNameText.getText().toString();
        Date date = (Date) eventDateText.getTag();
        long reminderTime = getReminderTime();

        if (name.isEmpty()) {
            onError(R.string.empty_event_name_hint);
            return;
        }

        if (date == null) {
            onError(R.string.empty_event_date_hint);
            return;
        }

        if (listener != null) {
            listener.onDataSetChanged(eventId, name, date, reminderTime);
        }
        getDialog().dismiss();
    }

    private void onError(int errorStringResId) {
        Toast
                .makeText(getActivity(), errorStringResId, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    @SuppressWarnings("all")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setEventData(Bundle args) {
        eventId = args.getInt("id");
        int iconResId = args.getInt("iconResId");
        long time = args.getLong("reminderTime");
        String name = args.getString("eventName");
        Date date = (Date) args.getSerializable("eventDate");


        setEventDate(date);
        eventNameText.setText(name);
        setEventIcon(iconResId);

        int spinnerElementId = getReminderTimeSpinnerIdByTime(time);
        reminderTimeSpinner.setSelection(spinnerElementId);
    }

    private void setEventIcon(int iconResId) {
        if (iconResId <= 0) {
            iconResId = R.drawable.ic_event_special;
        }
        eventIcon.setImageResource(iconResId);
    }

    private void setEventDate(Date date) {
        if (date != null) {
            String dateAsText = DATE_FORMAT.format(date);
            eventDateText.setText(dateAsText);
            eventDateText.setTag(date);
        }
    }

    public void showDatePickerFragment(DatePickerFragment.OnDateSetListener listener) {
        FragmentManager manager = getFragmentManager();
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setOnDateSetListener(listener);
        datePicker.show(manager, "datePicker");
    }
}
