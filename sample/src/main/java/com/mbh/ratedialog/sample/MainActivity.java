package com.mbh.ratedialog.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import com.mbh.ratedialog.RateDialog;

public class MainActivity extends AppCompatActivity {
    TextView tv_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_note = (TextView) findViewById(R.id.tv_note);

        boolean ratingDialogShowed = checkForRating();
        if (ratingDialogShowed) {
            tv_note.setText("Showed The Rating Dialog");
        } else {
            tv_note.setText("Not yet!");
        }
    }

    private boolean checkForRating() {
        RateDialog.Config config = new RateDialog.Config();
        config.setInstallDays(2); // after installation with 7 days show it
        config.setLaunchTimes(2); // after launch times show it
        config.setMessage(R.string.rate_message);
        config.setmNoThanks(R.string.rate_no_thanks);
        config.setmOkButton(R.string.rate_ok);
        config.setmRemindMeLater(R.string.rate_remind_me);
        config.setTitle(R.string.rate_title);
        return RateDialog.onStart(config, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
