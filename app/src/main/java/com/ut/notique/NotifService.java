package com.ut.notique;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NotifService extends Service {
    private static String TAG = "NotifService";
    private Connection con;
    private Statement st;
    private Handler handler;
    private Runnable runnable;
    private final int runtime = 4000;
    private int nAnt = 0, bAnt = 0, cAnt = 0, mAnt=0;
    private PendingIntent pendingIntent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotInfoChannelForeground";
            String description = "Canal de notificaciones de NotInfo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NotInfoChannel", name, importance);
            channel.setDescription(description);
            NotificationChannel channel2 = new NotificationChannel("NotInfoChannel","NotinfoChannel",NotificationManager.IMPORTANCE_HIGH);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel2);
        }
        Notification notification = new NotificationCompat.Builder(this, "NotInfoChannelForeground")
                .setContentTitle("Bienvenido a NotInfo")
                .setContentText("Disfruta de la información")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        //Inicializar la conexión con la base de datos para manejar la info del servicio
        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
            StrictMode.setThreadPolicy(policy); // Establecer politica
            con = DriverManager.getConnection(getString(R.string.connection_string)); // Datos provisionales para la prueba de bd
            st = con.createStatement();
            checkStatus(0);

        }catch (SQLException ex){
            System.out.println("Error en: " + ex.getMessage());
        }
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable,runtime);
                try {
                    Intent intent = new Intent(NotifService.this, NotifService.class);
                    startService(intent);
                }catch(IllegalStateException ex){
                    System.out.println("~~~~~~~~~~~~~~~~~ No se puede iniciar el servicio, saltando...");
                }
            }
        };
        handler.post(runnable);
    }
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        checkStatus(1);
        flags = START_NOT_STICKY;
        return flags;
    }
    public void checkStatus(int c){
        try {
            ResultSet rs = st.executeQuery("getNoticiasC");
            if(rs.next()){
                if(c==0) {
                    cAnt = rs.getInt("Cant");
                }else{
                    int t = rs.getInt("Cant");
                    if(t>cAnt){cAnt=t;alert(0);}
                    if(t<cAnt){cAnt=t;}
                }
            }
            if(rs.next()){
                if(c==0){
                    mAnt = rs.getInt("Cant");
                }else{
                    int t=rs.getInt("Cant");
                    if(t>mAnt){mAnt=t;alert(1);}
                    if(t<mAnt){mAnt=t;}
                }
            }
            if(rs.next()){
                if(c==0){
                    bAnt = rs.getInt("Cant");
                }else{
                    int t=rs.getInt("Cant");
                    if(t>bAnt){bAnt=t;alert(2);}
                    if(t<bAnt){bAnt=t;}
                }
            }
            if(rs.next()){
                if(c==0){
                    nAnt = rs.getInt("Cant");
                }else{
                    int t=rs.getInt("Cant");
                    if(t>nAnt){nAnt=t;alert(3);}
                    if(t<nAnt){nAnt=t;}
                }
            }
        }catch (SQLException ex){}
    }
    public void alert(int c){
        String textContent = "";
        Intent i = new Intent(NotifService.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        switch (c){
            case 0:
                textContent= "Un nuevo calendario ha sido publicado!";
                break;
            case 1:
                textContent= "Un nuevo meme ha sido publicado!";
                break;
            case 2:
                textContent= "Una nueva beca ha sido publicada!";
                break;
            case 3:
                textContent= "Una nueva noticia ha sido publicada!";
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NotInfoChannel")
                .setSmallIcon(R.drawable.noticia)
                .setContentTitle("Una nueva nota ha sido publicada!")
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotifService.this);
        notificationManager.notify(1,builder.build());
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Handler h = new Handler(getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(),"Task muerta", Toast.LENGTH_LONG).show();
            }
        });
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}