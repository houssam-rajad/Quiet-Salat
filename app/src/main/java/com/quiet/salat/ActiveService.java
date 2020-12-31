package com.quiet.salat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.quiet.salat.Channels.CHANNEL1;
import static com.quiet.salat.Channels.CHANNEL2;


public class ActiveService extends Service{
    NotificationManagerCompat managerCompat;
    private AudioManager audiomanager;
    Date stopTime;
    volatile String duration="30";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        managerCompat = NotificationManagerCompat.from(getApplicationContext());
        audiomanager= (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent intent1=new Intent(this,MainActivity.class);
        if (intent !=null && intent.getExtras()!=null)
            duration=intent.getExtras().getString("dur");
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent1,0);
        Notification not=new NotificationCompat.Builder(this,CHANNEL1)
                .setSmallIcon(R.drawable.ic_not_logo)
                .setColor(getResources().getColor(R.color.lightGreen)).setContentTitle("Quiet Salat")
        .setContentText("I will stay here to silence your phone during prayer times").setContentIntent(pendingIntent).build();
        startForeground(1,not);

        Log.d("your","outttttta");
        if(intent!=null)
        {
            duration=intent.getExtras().getString("dur");
            RunThread subh=new RunThread(this);
            new Thread(subh).start();
        }
        return START_STICKY;
    }



    public class RunThread implements Runnable{
        Context context;
        RunThread(Context c)
        {this.context=c;}
        @Override
        public void run() {

            while(!MainActivity.check_switches_and()) {
                try {
                    Thread.sleep(59000);
                    long date=System.currentTimeMillis();
                    SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
                    String dateString=sdf.format(date);
                    Date currentDate=sdf.parse(dateString);
                    Log.d("your","99");
                    if(SalatData.shared.salatList.get(0).isEnabled && dateString.equals(SalatData.shared.salatList.get(0).time)) {
                        Notification builder = new NotificationCompat.Builder(context,CHANNEL1)
                                .setSmallIcon(R.drawable.ic_not_logo)
                                .setContentText("حان موعد صلاة الصبح")
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        managerCompat.notify(2,builder);
                                                stopTime=silenceMyPhone(dateString,duration);
                    }
                    if(SalatData.shared.salatList.get(1).isEnabled && dateString.equals(SalatData.shared.salatList.get(1).time)) {
                        Notification builder = new NotificationCompat.Builder(context,CHANNEL1)
                                .setSmallIcon(R.drawable.ic_not_logo)
                                .setContentText("حان موعد صلاة الظهر")
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        managerCompat.notify(2,builder);
                        stopTime=silenceMyPhone(dateString,duration);
                    }
                    if(SalatData.shared.salatList.get(2).isEnabled && dateString.equals(SalatData.shared.salatList.get(2).time)) {
                        Notification builder = new NotificationCompat.Builder(context,CHANNEL1)
                                .setSmallIcon(R.drawable.ic_not_logo)
                                .setContentText("حان موعد صلاة العصر")
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        managerCompat.notify(2,builder);
                        stopTime=silenceMyPhone(dateString,duration);
                    }
                    if(SalatData.shared.salatList.get(3).isEnabled && dateString.equals(SalatData.shared.salatList.get(3).time)) {
                        Notification builder = new NotificationCompat.Builder(context,CHANNEL1)
                                .setSmallIcon(R.drawable.ic_not_logo)
                                .setContentText("حان موعد صلاة المغرب")
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();
                        managerCompat.notify(2,builder);
                        stopTime=silenceMyPhone(dateString,duration);
                    }
                    if(SalatData.shared.salatList.get(4).isEnabled && dateString.equals(SalatData.shared.salatList.get(4).time)) {
                        Notification builder = new NotificationCompat.Builder(context,CHANNEL1)
                                .setSmallIcon(R.drawable.ic_not_logo)
                                .setContentText("حان موعد صلاة العشاء")
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .build();

                        managerCompat.notify(2,builder);
                        stopTime=silenceMyPhone(dateString,duration);
                    }
                    if(stopTime!=null && isDonePraying(currentDate)) {
                        audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);}

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Date silenceMyPhone(String time,String period)
    {
        long dperiod = Long.parseLong(period);
        dperiod=dperiod*60000;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        try {
            audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            Date startime = format.parse(time);
            Date stoptime=startime ;
            stoptime.setTime(startime.getTime()+dperiod);
            return stoptime;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isDonePraying(Date current)
    {
        if(current.after(stopTime))
        {
            stopTime=null;
            return true;
       }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("your","destroyed");

    }


}
