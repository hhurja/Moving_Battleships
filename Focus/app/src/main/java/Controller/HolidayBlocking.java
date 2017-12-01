package Controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import Model.FocusModel;
import Model.Schedule;
import movingbattleship.org.focus.R;

public class HolidayBlocking extends Activity implements View.OnClickListener {

    Button block;
    Button dont_block;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.holiday_blocking);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }
}
