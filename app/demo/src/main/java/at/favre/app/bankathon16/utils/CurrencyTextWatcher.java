package at.favre.app.bankathon16.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

/**
 * @author Florian Rauscha
 */
public class CurrencyTextWatcher implements TextWatcher {

    private final WeakReference<EditText> editTextWeakReference;

    public CurrencyTextWatcher(EditText mEditText) {
        editTextWeakReference = new WeakReference<>(mEditText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    /*
        EditText editTex = editTextWeakReference.get();
        if (!s.toString().equals(editTex.getText())) {
            editTex.removeTextChangedListener(this);
            String cleanString = s.toString().replaceAll("[$,.]", "");
            double parsed = Double.parseDouble(cleanString.replaceAll("[^\\d]", ""));
            String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));
            editTex.setText(formatted);
            editTex.setSelection(formatted.length());

            editTex.addTextChangedListener(this);
        }
        */
    }

    @Override
    public void afterTextChanged(Editable s) {
        String foo = s.toString().trim();

        if (foo.isEmpty())
            return;

        try {
            BigDecimal bd = new BigDecimal(s.toString());

            long lv = bd.multiply(BigDecimal.valueOf(100)).longValue();

            Log.wtf("dude", String.format("%d", lv));

            if (lv < 100) {
                editTextWeakReference.get().setError("Er ist zu klein!");

            } else if (lv > 1000000) {
                editTextWeakReference.get().setError("It's too big it doesn't fit.");
            }

            editTextWeakReference.get().setError(null);


        } catch (NumberFormatException e) {
            editTextWeakReference.get().setError("dude, no");
        }
    }

}