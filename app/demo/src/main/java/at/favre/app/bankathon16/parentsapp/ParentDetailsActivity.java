package at.favre.app.bankathon16.parentsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import at.favre.app.bankathon16.R;
import at.favre.app.bankathon16.misc.Util;
import at.favre.app.bankathon16.model.User;

/**
 * Created by PatrickF on 23.04.2016.
 */
public class ParentDetailsActivity extends AppCompatActivity {

    private static final String KEY_USER = "USER";

    private User user;

    public static void start(Context context, User user) {
        Intent starter = new Intent(context, ParentDetailsActivity.class);
        starter.putExtra(KEY_USER, user);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_details);

        user = getIntent().getParcelableExtra(KEY_USER);

        ((Toolbar) findViewById(R.id.toolbar)).setTitle("Kontoübersicht");
        ((TextView) findViewById(R.id.main_title)).setText(user.getName() + "'s Konto");
        ((TextView) findViewById(R.id.current_amount)).setText("Verfügbar: " + Util.formatWithCurrencyCode(user.getAmountInCent(), "€"));
        ((TextView) findViewById(R.id.saved_amount)).setText("Angespart: " + Util.formatWithCurrencyCode(user.getSavedAmount(), "€"));

    }
}
