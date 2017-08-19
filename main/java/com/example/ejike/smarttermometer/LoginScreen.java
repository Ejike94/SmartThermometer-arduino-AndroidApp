package com.example.ejike.smarttermometer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;




public class LoginScreen extends AppCompatActivity {

    public final static String EXTRA_USER = "com.nnodi.ejike.smarttermometer,MESSAGE1";
    public final static String EXTRA_PASS = "com.nnodi.ejike.smarttermometer.MESSAGE2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
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


    //CheckPassword
    public void CheckPassword(View view)
    {

        String user = ((EditText) findViewById(R.id.editUser)).getText().toString();
        String pass =((EditText) findViewById(R.id.editPass)).getText().toString();

        Intent intent = new Intent(this, activityTermometer.class);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_PASS, pass);
        startActivity(intent);
    }

}
