package at.favre.app.bankathon16.childapp;

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
import at.favre.app.bankathon16.misc.Util;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class SaveMoneyDialog extends DialogFragment {
    private static final String TAG = SaveMoneyDialog.class.getSimpleName();

    private static final String KEY_AMOUNT = "AMOUNT";

    public static void show(FragmentManager fragmentManager, int currentAmount) {
        Bundle args = new Bundle();
        args.putInt(KEY_AMOUNT, currentAmount);
        SaveMoneyDialog fragment = new SaveMoneyDialog();
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.ParentAlertDialogStyle);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_savemoney, null, false);
        // dialog.setView(v);

        final EditText amountEditText = (EditText) v.findViewById(R.id.et_saveAmount);

        return new AlertDialog.Builder(getActivity())
                // .setIcon(R.d2nrawable.alert_dialog_icon)
                .setTitle("Du hast " + Util.formatWithCurrencyCode(getArguments().getInt(KEY_AMOUNT, 0), "€"))
                .setView(v)
                .setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    Double amountEuro = Double.parseDouble(amountEditText.getText().toString());
                                    Integer amountInCent = (int) (amountEuro * 100);

                                    // ((AddChildDialog)getActivity()).doPositiveClick();
                                    ((ChildSaveActivity) getActivity()).updateSavedAmount(amountInCent);
                                }catch (Exception ex) {
                                    
                                }
                            }
                        }
                )
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // ((AddChildDialog)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

}
