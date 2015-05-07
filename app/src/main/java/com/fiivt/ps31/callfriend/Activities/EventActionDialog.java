package com.fiivt.ps31.callfriend.Activities;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fiivt.ps31.callfriend.R;
import lombok.Getter;
import lombok.Setter;

public class EventActionDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private EventAction[] actions;

    @Setter
    private EventActionClickListener clickListener;

    public enum EventActionType {
        ACCEPT(0),
        DISMISS(2),
        PUT_OFF(1);

        private int id;

        private EventActionType(int id) {
            this.id = id;
        }

        public static EventActionType getById(int id) {
            for(EventActionType type : values()) {
                if (type.id == id){
                    return type;
                }
            }
            return PUT_OFF;
        }
    }

    public interface EventActionClickListener {
        void onClick(EventActionType actionType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_action_dialog_frament, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        actions = createActions();
        return view;
    }

    private EventAction[] createActions() {
        return new EventAction[] {
            new EventAction(R.color.text_days_left_color_urgently, getString(R.string.event_action_accept)),
            new EventAction(R.color.text_days_left_color_urgently, getString(R.string.event_action_put_off)),
            new EventAction(R.color.text_days_left_color_urgently, getString(R.string.event_action_dismiss))};
    }

    @Override
    @SuppressWarnings("all")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EventActionsAdapter adapter = new EventActionsAdapter(getActivity(), actions);
        ListView actionList = (ListView) getView().findViewById(R.id.event_actions_list);
        actionList.setAdapter(adapter);
        actionList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (clickListener != null) {
            clickListener.onClick(EventActionType.getById(position));
        }
    }


    @Getter
    private class EventAction {
        private int icon;
        private String title;

        public EventAction(int icon, String title) {
            this.icon = icon;
            this.title = title;
        }
    }

    private class EventActionsAdapter extends ArrayAdapter<EventAction> {

        public EventActionsAdapter(Context context, EventAction[] actions) {
            super(context, R.layout.event_action_list_item, actions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = createNewView(parent);
            EventAction action = getItem(position);
            //todo replace set background method on set image res
            //((ImageView) view.findViewById(R.id.event_action_icon)).setImageResource();
            ((TextView) view.findViewById(R.id.event_action_title)).setText(action.getTitle());
            return view;
        }

        private View createNewView(ViewGroup parent) {
            return ((LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.event_action_list_item, parent, false);
        }

    }
}
