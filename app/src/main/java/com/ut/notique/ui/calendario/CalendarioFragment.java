package com.ut.notique.ui.calendario;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ut.notique.MainActivity;
import com.ut.notique.R;
import com.bumptech.glide.Glide;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings({"deprecation", "unused"})
public class CalendarioFragment extends Fragment { // Fragmento de calendario (Nueva forma de programar las tareas)
    private ScaleGestureDetector sgd;
    private float mScaleFactor = 1f;
    private Connection connection;
    private ImageView calendario;
    private TextView fecha, titl;
    private Activity parent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.ut.notique.ui.calendario.CalendarioViewModel calendarioViewModel = ViewModelProviders.of(this).get(CalendarioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendario, container, false); // Coasas raras de fragments
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    sgd.onTouchEvent(event);
                    return true;
            }
        });
        calendario=root.findViewById(R.id.calImg);
        fecha=root.findViewById(R.id.fecha);
        titl=root.findViewById(R.id.calendario);
        parent=this.getActivity(); //Obtener activity para ejecutar en hilo de UI desde AsyncTasks
        parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        calendario.setScaleType(ImageView.ScaleType.FIT_CENTER);
        sgd= new ScaleGestureDetector(this.getContext(), new ScaleListener());
        ((MainActivity)getActivity()).setCurrent(this);
        Glide.with(this).load(R.drawable.utn).into(calendario); // Poner GIF de cargando en el calendario
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() { // Ejecutar AsyncTask
                System.out.println("°°°°°°°°°°°°°° doInBK iniciado °°°°°°°°°°°°°°");
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
                    StrictMode.setThreadPolicy(policy); // Establecer politica
                    connection = DriverManager.getConnection(getString(R.string.connection_string)); // Datos provisionales para la prueba de bd
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    Statement st = connection.createStatement(); // Crear statement para las query
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Statement listo °°°°°°°°°°°°°°°°°°");
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Listo para ejecutar operaciones con la base de datos :D °°°°°°°°°°°°°°°°°°");
                    ResultSet rs = st.executeQuery("getCalendarioA"); // Descargar calendario desde la bd
                    if(rs.next()){ // Si hay datos
                        final byte[] imagen = rs.getBytes("Imagen_Calendario"); // Bytes de imagen
                        final String fechas = rs.getString("fecha"); // Fecha de publicación
                        final String titcal = rs.getString("titulo_Calendario");
                        parent.runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() { // Correr en hilo de la UI
                                calendario.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                calendario.setImageBitmap(BitmapFactory.decodeByteArray(imagen,0,imagen.length)); // Convertir los bytes a un bitmap y mostrarlo
                                fecha.setText("Fecha de actualización: "+fechas); // Mostrar fecha
                                titl.setText(titcal);
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX Error en: "+e.getMessage() + " XXXXXXXXXXXXXXXX");
                }
            }
        });
        return root;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,Math.min(mScaleFactor,10.0f));
            calendario.setScaleX(mScaleFactor);
            calendario.setScaleY(mScaleFactor);
            return true;
        }
    }

}
