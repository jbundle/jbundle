package org.jbundle.test.db.mem;

import org.jbundle.test.db.mem.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class memActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPickDate = (Button) findViewById(R.id.button1);
        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (text != null)
                    mPickDate.setText(text);
                else
                {
                    DBTest test = new DBTest();
                    new Thread(test).start();
                }
            }
        });
    }
    Button mPickDate = null;
    
    public static String text = null;
}