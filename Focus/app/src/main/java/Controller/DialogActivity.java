package Controller;

/**
 * Created by shabina on 10/14/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import movingbattleship.org.focus.R;

public class DialogActivity extends Activity implements OnClickListener {

    Button ok_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ok_btn = (Button) findViewById(R.id.ok_btn_id);
        ok_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn_id:
                this.finish();
                break;
        }
    }
}
