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
import android.widget.Toast;

import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;

/**
 * Created by sschallerl on 4/23/16.
 */
public class SavingGoalDialog extends DialogFragment {

    public static final String TAG = SavingGoalDialog.class.getSimpleName();

    EditText et_name;
    EditText et_price;

    public static void show(FragmentManager fragmentManager) {
        Bundle args = new Bundle();
        SavingGoalDialog fragment = new SavingGoalDialog();
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_savegoal, null);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_price = (EditText) view.findViewById(R.id.et_price);

//        et_name.addTextChangedListener(this);
//        et_price.addTextChangedListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Neues Sparziel")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString().trim();

                        long price;
                        try {
                            price = Util.getMoneyFromET(et_price);

                            if (name.isEmpty()) throw new Exception();

                            ((ChildMainActivity) getActivity()).createSaveGoal(name, price);


                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "So wird des nix.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog doode = builder.create();

        return doode;
    }

}
