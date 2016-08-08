package at.favre.app.bankathon16.misc;

import android.support.annotation.NonNull;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class Util {
    public static String formatWithCurrencyCode(final long amountCent, final String currencyCode) {
        DecimalFormat df2 = new DecimalFormat("#,###,###,##0.00 " + currencyCode);
        return df2.format(new BigDecimal(amountCent).divide(new BigDecimal(100)).doubleValue());
    }


    public static long getMoneyFromET(@NonNull EditText et) throws NumberFormatException {
        String monetaryValue = et.getText().toString().trim();
        BigDecimal bd = new BigDecimal(monetaryValue);
        return bd.multiply(BigDecimal.valueOf(100)).intValue();
    }
}
