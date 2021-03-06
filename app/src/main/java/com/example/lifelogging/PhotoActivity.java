package com.example.lifelogging;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

import im.delight.android.location.SimpleLocation;

import static android.os.Environment.DIRECTORY_PICTURES;


public class PhotoActivity extends AppCompatActivity{

    public Button button;
    public ImageView imageView;

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private SimpleLocation mLocation;
    private String imageFilePath = "";
    String currlatitude, currlongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        button = (Button) findViewById(R.id.button);
        imageView = findViewById(R.id.image);
        mLocation = new SimpleLocation(this);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 currlatitude = Double.toString(mLocation.getLatitude());
                 currlongitude = Double.toString(mLocation.getLongitude());
                openCameraIntent();
            }
        });
        {

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        mLocation.beginUpdates();

        // ...
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        mLocation.endUpdates();

        // ...

        super.onPause();
    }

    //opens camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            try{
                getDirectoryFiles(photoFile.getName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(Uri.parse(imageFilePath));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //savefile to lifelogging app directory internalstorage>android>data>com.example.lifelogging>files>Pictures(can change this to another location)
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }





    //pass from directory loop, get tags lat and long trim and convert from dms to degrees return string;
    /*
    private String printImageTags(String file) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(new File(file));
        String lat = null;
        String lon = null;
        double latd, latm, lats;
        double lond, lonm, lons;
        String latfinal="", lonfinal="";
        String latlon = null;

        String pattern = "^([NSEW])?(-)?(\\d+(?:\\.\\d+)?)[Â°Âº:d\\s]\\s?(?:(\\d+(?:\\.\\d+)?)['â€™â€˜â€²:]\\s?(?:(\\d{1,2}(?:\\.\\d+)?)(?:\"|â€³|â€™â€™|'')?)?)?\\s?([NSEW])?";
        Pattern r = Pattern.compile(pattern);
        //get gps metadata
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                Log.d("PhotoActivity", tagName);
                String desc = tag.getDescription();
                switch (tagName) {
                    case "GPS Latitude":
                        lat = desc;
                        break;
                    case "GPS Longitude":
                        lon = desc;
                        break;
                }
            }
        }
        //strip unwanted data and convert.

        Matcher m = r.matcher(lat);
        if (m.find( )) {
            latd = Double.parseDouble(m.group(3));
            latm = Double.parseDouble(m.group(4));
            lats = Double.parseDouble(m.group(5));

            if (m.group(2) != null) {
                latfinal = m.group(2) + Double.toString(latd + ((latm / 60) + (lats / 3600)));
            }else{
                latfinal = Double.toString(latd + ((latm / 60) + (lats / 3600)));
            }
        }

        Matcher x = r.matcher(lon);
        if (x.find( )) {
            lond = Double.parseDouble(x.group(3));
            lonm = Double.parseDouble(x.group(4));
            lons = Double.parseDouble(x.group(5));

            if (x.group(2) != null) {
                lonfinal = x.group(2)+Double.toString((lond +((lonm/60)+(lons/3600))));
            }else{
                lonfinal = Double.toString((lond +((lonm/60)+(lons/3600))));
            }

        }

        latlon = lonfinal+","+latfinal;


        return latlon;

    }
    */


    //iterates through all picture files, getting gps meta data and creating json file.
    private void getDirectoryFiles(String filename) throws Exception {

        String latlong = null;
        String output = null;

        //writefile
        File f = new File("/storage/emulated/0/Android/data/com.example.lifelogging/files/geo.geojson");
        output = readFromFile();
        Log.d("zzz", output.toString());

        if(!f.exists())
        {
            output = "{\"type\":\"FeatureCollection\",\"features\":[";
            try (FileWriter filew = new FileWriter("/storage/emulated/0/Android/data/com.example.lifelogging/files/geo.geojson")){
                latlong = currlongitude+","+currlatitude;
                output += "{\"type\":\"Feature\",\"properties\":{\"name\":\""+filename+"\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":["+latlong+"]}}";
                output += "]}";
                filew.write(output);
                filew.close();
                Log.d("PhotoActivity", output); //testlog
            }
        }
        else{
               FileWriter filew = new FileWriter("/storage/emulated/0/Android/data/com.example.lifelogging/files/geo.geojson");
                Log.d("zzz", output.toString()); //testlog

                latlong = currlongitude+","+currlatitude;

                output = output.substring(0, output.length()-2);

                output += ",{\"type\":\"Feature\",\"properties\":{\"name\":\""+filename+"\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":["+latlong+"]}}";
                output += "]}";

                filew.write(output);
                filew.close();
        }

    }



    private String readFromFile() {

        String ret = "";

        try {
            FileInputStream fis = new FileInputStream(new File("/storage/emulated/0/Android/data/com.example.lifelogging/files/geo.geojson"));

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                fis.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.d("aaa", ret);
        return ret;
    }


}