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

public class ShareToFacebook extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.holiday_blocking);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yes_block_id:
                this.finish();
                break;
            case R.id.no_block_id:
                this.finish();
                break;
        }
    }
}