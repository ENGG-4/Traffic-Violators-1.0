package violators.traffic.com.trafficviolators;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    Button startbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startbtn = (Button) findViewById(R.id.startbutton);
        startbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        setContentView(R.layout.activity_signup);
    }
}
