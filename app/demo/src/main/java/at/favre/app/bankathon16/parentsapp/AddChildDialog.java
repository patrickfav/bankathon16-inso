package at.favre.app.bankathon16.parentsapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import at.favre.app.bankathon16.R;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class AddChildDialog extends DialogFragment {
    private static final String TAG = AddChildDialog.class.getSimpleName();

    public static void show(FragmentManager fragmentManager) {
        Bundle args = new Bundle();
        AddChildDialog fragment = new AddChildDialog();
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.ParentAlertDialogStyle);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_addchild, null, false);
        // dialog.setView(v);

        final EditText kidName = (EditText) v.findViewById(R.id.et_name);

        return new AlertDialog.Builder(getActivity())
                // .setIcon(R.d2nrawable.alert_dialog_icon)
                .setTitle("Neues Kind")
                .setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name =
                                        kidName.getText().toString().trim();

                                // ((AddChildDialog)getActivity()).doPositiveClick();
                                if (!name.isEmpty())
                                    ((ParentMainActivity) getActivity()).addNewChild(name);

                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // ((AddChildDialog)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

}
