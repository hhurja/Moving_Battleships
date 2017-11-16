package Controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import movingbattleship.org.focus.R;

public class HolidayBlocking extends Activity implements View.OnClickListener {

    Button block;
    Button dont_block;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.holiday_blocking);
        block = (Button) findViewById(R.id.yes_block_id);
        block.setOnClickListener(this);
        dont_block = (Button) findViewById(R.id.no_block_id);
        dont_block.setOnClickListener(this);
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
