package violators.traffic.com.trafficviolators;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(this,LoginActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                }
                catch(InterruptedException e) { }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };

        timer.start();
    }
}
