package com.example.myapplication;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    Thread myThread;
    GPSTracker gps;
    String text = "";
    private final static String FILE_NAME = "content.txt";

    public void onCreate() {
        gps = new GPSTracker(MyService.this);
        System.out.println("onCreate");
        super.onCreate();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        gps = new GPSTracker(MyService.this);
        System.out.println("osStartComand");
        someTask();
        return START_STICKY;
    }

    void someTask() {
        System.out.println("someTask");
        myThread = new Thread( // создаём новый поток
                new Runnable() { // описываем объект Runnable в конструкторе
                    int e = 10;
                    public void run() {
                        System.out.println("tread");
                        try {
                            if(gps.canGetLocation()){
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                // \n is for new line
                                System.out.println("Your Location is - \nLat: "
                                        + latitude + "\nLong: " + longitude);
                                saveText(latitude,longitude);
                            }else{
                                // can't get location
                                gps.showSettingsAlert();
                            }
                            TimeUnit.SECONDS.sleep(e);
                            gps.getLocation();
                            System.out.println("slepp");
                            someTask();
                        }
                        catch (InterruptedException e) {
                        }
                    }
                }
        );
        myThread.start();
    }
    public void saveText(double a, double b){


        FileOutputStream fos = null;
        try {
            String text1 = "Your Location is - \nLat: " + a + "\nLong: " + b;
            text += "\n"+text1;
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());
        }
        catch(IOException ex) {

        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

            }
        }
    }
    @Override
    public void onDestroy() {
        myThread.interrupt();
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}