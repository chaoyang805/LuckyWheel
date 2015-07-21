package me.chaoyang805.luckywheel;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

    private ImageView ivToggle;
    private LuckyWheel luckyWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivToggle = (ImageView) findViewById(R.id.ivToggle);
        luckyWheel = (LuckyWheel) findViewById(R.id.lucky_wheel);
        ivToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (luckyWheel.isStarted()) {
                    ivToggle.setImageResource(R.drawable.start);
                    luckyWheel.stop();
                } else {
                    ivToggle.setImageResource(R.drawable.stop);
                    double random = Math.random();
                    int index = -1;
                    if (random >= 0 && random < 0.9) {
                        index = 0;
                        Log.d("TAG", "单反相机！");
                    } else {
                        Log.d("TAG", "iPhone！！");
                        index = 1;
                    }
                    luckyWheel.start(60, index);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
