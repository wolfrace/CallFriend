package com.fiivt.ps31.callfriend;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.Setter;

public class SignificantEventEditDialog extends DialogFragment {

    private final long[] spinnerItemIdToReminderTime = {
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.DAYS.toMillis(2),
            TimeUnit.DAYS.toMillis(7),
            TimeUnit.DAYS.toMillis(30)};

    @Setter
    private OnDataSetChangedListener listener;
    private EditText eventNameText;
    private EditText eventDateText;
    private Spinner reminderTimeSpinner;

    public long getReminderTime() {
        long id = reminderTimeSpinner.getSelectedItemId();
        return spinnerItemIdToReminderTime[(int) id];
    }

    public interface OnDataSetChangedListener {
        void onDataSetChanged(String eventName, Date eventDate, long reminderTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.significant_event_edit_dialog, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        eventNameText = (EditText) view.findViewById(R.id.significant_event_name);
        eventDateText = (EditText) view.findViewById(R.id.significant_event_date);
        reminderTimeSpinner = (Spinner) view.findViewById(R.id.reminder_time_spinner);


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
                        String dateAsText = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
                        eventDateText.setText(dateAsText);
                        eventDateText.setTag(date);
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
            Toast
                    .makeText(getActivity(), R.string.empty_event_name_hint, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (listener != null) {
            listener.onDataSetChanged(name, date, reminderTime);
        }
        getDialog().dismiss();
    }

    @Override
    @SuppressWarnings("all")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void showDatePickerFragment(DatePickerFragment.OnDateSetListener listener) {
        FragmentManager manager = getFragmentManager();
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setOnDateSetListener(listener);
        datePicker.show(manager, "datePicker");
    }
}
