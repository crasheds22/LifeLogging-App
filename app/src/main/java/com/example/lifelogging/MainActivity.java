//MainActvity.java: This is the main program for our app
package com.example.lifelogging;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void takeAPhoto(View view) {
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        startActivity(photoIntent);
    }

    public void viewOnMaps(View view) {
        Intent mapIntent = new Intent(this, ViewOnMapActivity.class);
        startActivity(mapIntent);
    }

    public void viewInAR(View view) {
        Intent arIntent = new Intent(this, ViewInARActivity.class);
        startActivity(arIntent);
    }
}
