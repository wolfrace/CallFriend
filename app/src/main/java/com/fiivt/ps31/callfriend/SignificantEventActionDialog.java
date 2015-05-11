package com.fiivt.ps31.callfriend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import lombok.Setter;

public class SignificantEventActionDialog extends DialogFragment {

    public interface OnSignificantEventActionClick {
        void onEditClick();
        void onDeleteClick();
    }

    @Setter
    private OnSignificantEventActionClick listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.significant_event_actions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    switch (which)
                    {
                        case 0:
                            listener.onEditClick();
                            break;
                        case 1:
                            listener.onDeleteClick();
                            break;
                    }
                }
            }
        });
        return builder.create();
    }
}
