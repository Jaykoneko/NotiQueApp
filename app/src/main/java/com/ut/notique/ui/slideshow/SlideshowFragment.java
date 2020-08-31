package com.ut.notique.ui.slideshow;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.ut.notique.MainActivity;
import com.ut.notique.PDFViewer;
import com.ut.notique.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    private SlideshowViewModel slideshowViewModel;
    private View roott;
    private int cBeca=0, tBecas;
    protected Activity parent;
    private ArrayList<ImageView> imgs;
    private ArrayList<TextView> titulos, sTitulos, resumenes;
    private ArrayList<MaterialButton> cancBt;
    private ArrayList<MaterialCardView> cards;
    private ArrayList<ImageButton> botones;
    private ArrayList<Beca> becas;
    private Statement st;
    private ScrollView ScrollB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        roott=root;
        initUI(root);
        parent=getActivity();
        ((MainActivity)getActivity()).setCurrent(this);
        parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Context context=parent.getApplicationContext();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
                    StrictMode.setThreadPolicy(policy); // Establecer politica
                    Connection connection = DriverManager.getConnection(getString(R.string.connection_string)); // Datos provisionales para la prueba de bd
                    //System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    st = connection.createStatement();
                    ResultSet rs=st.executeQuery("getBecas");
                    becas=new ArrayList<>();
                    ArrayList<Beca> tmp=new ArrayList<>();
                    int aux=0;
                    while(rs.next()){
                        Beca temp=new Beca(rs.getInt("id_Becas"),rs.getString("Nombre"),rs.getString("Fecha_publicacion"),rs.getString("Resumen"),rs.getBytes("Imagen"),rs.getInt("Tipo"));
                        tmp.add(temp);
                        //System.out.println("~~~~~~~~~~~~~ Añadiendo beca ~~~~~~~~~~~~");
                        aux++;
                    }
                    for(int v=tmp.size()-1;v>0;v--){
                       becas.add(tmp.get(v));
                    }
                    if(aux==0){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"No hay becas que mostrar",Toast.LENGTH_LONG).show();
                                for(int i=0;i<cards.size();i++){
                                    cards.get(i).setVisibility(View.GONE);
                                }
                            }
                        });
                        return;
                    }else{
                        tBecas=aux-1;
                        cBeca=0;
                        //System.out.println(aux + " becas añadidas correctamente");
                    }
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postBecas(0);
                        }
                    });
                    if(tBecas<5){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                botones.get(1).setVisibility(View.GONE);
                            }
                        });
                    }
                }catch(SQLException ex){
                    //System.out.println("XXXXXXXXXXXXX Algo salió mal en: "+ex.getMessage());
                }
            }
        });
        botones.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cBeca-10<=0){
                    botones.get(0).setVisibility(View.GONE);
                    cBeca=0;
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postBecas(cBeca);
                        }
                    });
                    ScrollB.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollB.scrollTo(0,0);
                        }
                    });
                }else {
                    cBeca=cBeca-10;
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postBecas(cBeca);
                        }
                    });
                    ScrollB.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollB.scrollTo(0,0);
                        }
                    });

                }
                botones.get(1).setVisibility(View.VISIBLE);
            }
        });
        botones.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botones.get(0).setVisibility(View.VISIBLE);
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postBecas(cBeca);
                    }
                });
                ScrollB.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollB.scrollTo(0,0);
                    }
                });

                if(cBeca>=tBecas){
                    botones.get(1).setVisibility(View.GONE);
                }
            }
        });
        return root;
    }
    public void initUI(View root) {
        imgs = new ArrayList<>();
        titulos = new ArrayList<>();
        sTitulos = new ArrayList<>();
        cancBt = new ArrayList<>();
        resumenes = new ArrayList<>();
        cards = new ArrayList<>();
        botones = new ArrayList<>();

        imgs.add((ImageView) root.findViewById(R.id.Bimgc1));
        imgs.add((ImageView) root.findViewById(R.id.Bimgc2));
        imgs.add((ImageView) root.findViewById(R.id.Bimgc3));
        imgs.add((ImageView) root.findViewById(R.id.Bimgc4));
        imgs.add((ImageView) root.findViewById(R.id.Bimgc5));

        //--------------------------------

        titulos.add((TextView) root.findViewById(R.id.BtxtT1));
        titulos.add((TextView) root.findViewById(R.id.BtxtT2));
        titulos.add((TextView) root.findViewById(R.id.BtxtT3));
        titulos.add((TextView) root.findViewById(R.id.BtxtT4));
        titulos.add((TextView) root.findViewById(R.id.BtxtT5));

        //--------------------------------

        sTitulos.add((TextView) root.findViewById(R.id.BtxtSt1));
        sTitulos.add((TextView) root.findViewById(R.id.BtxtSt2));
        sTitulos.add((TextView) root.findViewById(R.id.BtxtSt3));
        sTitulos.add((TextView) root.findViewById(R.id.BtxtSt4));
        sTitulos.add((TextView) root.findViewById(R.id.BtxtSt5));

        //---------------------------------

        cancBt.add((MaterialButton) root.findViewById(R.id.BbtnC1));
        cancBt.add((MaterialButton) root.findViewById(R.id.BbtnC2));
        cancBt.add((MaterialButton) root.findViewById(R.id.BbtnC3));
        cancBt.add((MaterialButton) root.findViewById(R.id.BbtnC4));
        cancBt.add((MaterialButton) root.findViewById(R.id.BbtnC5));


        //---------------------------------

        resumenes.add((TextView) root.findViewById(R.id.BtxtR1));
        resumenes.add((TextView) root.findViewById(R.id.BtxtR2));
        resumenes.add((TextView) root.findViewById(R.id.BtxtR3));
        resumenes.add((TextView) root.findViewById(R.id.BtxtR4));
        resumenes.add((TextView) root.findViewById(R.id.BtxtR5));

        //----------------------------------

        cards.add((MaterialCardView)root.findViewById(R.id.Bcard));
        cards.add((MaterialCardView)root.findViewById(R.id.Bcard2));
        cards.add((MaterialCardView)root.findViewById(R.id.Bcard3));
        cards.add((MaterialCardView)root.findViewById(R.id.Bcard4));
        cards.add((MaterialCardView)root.findViewById(R.id.Bcard5));

        //----------------------------------

        botones.add((ImageButton) root.findViewById(R.id.BbtnAtr));
        botones.add((ImageButton) root.findViewById(R.id.BbtnAd));


        // Inicializar animaciones de carga de imagenes
        ScrollB = root.findViewById(R.id.ScrollB);
        ScrollB.post(new Runnable() {
            @Override
            public void run() {
                ScrollB.scrollTo(0,0);
            }
        });

        for(int i=0;i<5;i++){
            imgs.get(i).setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(R.drawable.utn).into(imgs.get(i));
        }
    }

    public void postBecas(int index){ // Mostrar noticias desde un indice
        System.out.println("~~~~~~~~~~~~~ Posteando becas desde el index " + index);
        //determinar cuantas cards se van a usar
        int aux=tBecas-cBeca; // Determinar la diferencia entre el total de noticias y la noticia actual
        int contint=index; // Variable para llevar la cuenta de la noticia actual
        if(aux>=5){ // Si la diferencia de la noticia actual y el total es mayor o igual a 5
            aux=5; // Poner el auxiliar a 5 para evitar Overflows
            if(tBecas<5){ // Si el total de noticias es menor a 5
                aux=tBecas; // El auxiliar es igual al total de noticias
            }
        }else{
            for(int l=0;l<5;l++){ //Recorrer los CardView
                if(l>=aux){ // Si el indice es mayor o igual al auxiliar (Cards que no se usan)
                    final int finalL = l; // Solo finales pueden ser usadas en UI
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // UI
                            cards.get(finalL).setVisibility(View.GONE); // Poner la card a GONE
                        }
                    });
                }
            }
        }
        if(index==0 && tBecas>5){ // Si el indice (noticia inicial para el metodo) es igual a 0 y el total de noticias es mayor a 5
            for(int k=0;k<5;k++){ // Recorrer los 5 CardView
                final int finalK = k; // Final para el UI
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cards.get(finalK).setVisibility(View.VISIBLE); // Poner visible los Cardview que se usan ( talvez esta de mas, pero mas vale que esté lol)
                    }
                });
            }
        }

        for(int i=0;i<aux;i++){ // Recorrer los cardview desde 0 hasta el número que se vayan a usar
            final int finalI = i; // Final I para el UI
            final int finalContint=contint; // Final Contint para el UI
            final int finalI1 = i;
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() { // UI
                    cards.get(finalI).setVisibility(View.VISIBLE); //Poner el card a usar en visible
                    titulos.get(finalI).setText(becas.get(finalContint).getTitulo()); // Setear el titulo
                    sTitulos.get(finalI).setText(becas.get(finalContint).getSubtitulo()); // Seter la fecha
                    resumenes.get(finalI).setText(becas.get(finalContint).getResumen()); // Setear el resumen
                    imgs.get(finalI1).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgs.get(finalI).setImageBitmap(BitmapFactory.decodeByteArray(becas.get(finalContint).getImagen(),0,becas.get(finalContint).getImagen().length)); // Setear la imagen
                    if(becas.get(finalContint).getTipo()==1){// Si la noticia es PDF
                        cancBt.get(finalI).setText("Abrir PDF"); // Cambiar texto del botón a Abrir PDF
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(View v) { // Establecer listener
                                try{
                                    File temp = File.createTempFile("temp",".pdf"); // Crear archivo temporal
                                    //noinspection ResultOfMethodCallIgnored
                                    temp.createNewFile(); // Crear archivo físico
                                    FileOutputStream fos = new FileOutputStream(temp); // Crear stream de escritura para el archivo
                                    ResultSet rs=st.executeQuery("getBecadata "+becas.get(finalContint).getId()); // Obtener el PDF de la base de datos
                                    if(rs.next()){ // Si hay datos
                                        byte[] dat = rs.getBytes("Datos"); // Guardar el pdf en un byte[]
                                        fos.write(dat); // Escribir el byte[] al archivo
                                        fos.flush(); // Forzar la escritura de los bytes
                                        temp.deleteOnExit(); // Establecer en borrar al salir de la app
                                        Intent intent = new Intent(parent.getApplicationContext(), PDFViewer.class); // Crear intent para abrir el PDFViewer
                                        intent.putExtra("file",temp.toPath().toString()); // Poner el path del archivo en el intent
                                        parent.startActivity(intent); // Ejecutar el PDFViewer
                                    }
                                }catch (Exception ex){
                                    System.out.println("XXXXXXXXXXXXX Error en: "+ex.getMessage());
                                }

                            }
                        });
                    }else{ // Si es Link
                        cancBt.get(finalI).setText("Abrir Link"); // Poner texto del botón en Abrir Link
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { // Listener
                                try {
                                    ResultSet rs = st.executeQuery("getBecadata " + becas.get(finalContint).getId()); // Descargar link
                                    if(rs.next()) {
                                        String url = new String(rs.getBytes("Datos"), StandardCharsets.UTF_8); // Convertir los bytes del link a string y guardarlo en variable

                                        Intent in = new Intent(Intent.ACTION_VIEW); // Intent action view para abrir el navegador
                                        in.setData(Uri.parse(url)); // Agregar el url con el prefijo http://
                                        parent.getApplicationContext().startActivity(in); // Ejecutar activity del PDFViewer
                                    }
                                }catch (SQLException ex){
                                    System.out.println("XXXXXXXXXXXXXXXXX Error en: " + ex.getMessage());
                                }
                            }
                        });
                    }
                }
            });
            contint++; // Aumentar el contint
        }
        cBeca+=aux; // La noticia actual aumenta en auxilar
    }

}
class Beca{
    private String titulo, subtitulo, resumen;
    private byte[] imagen, datos;
    private int id,tipo;

    public Beca(int id, String titulo, String subtitulo, String resumen, byte[] imagen,int tipo){
        this.id=id;
        this.titulo=titulo;
        this.subtitulo=subtitulo;
        this.resumen=resumen;
        this.imagen=imagen;
        this.tipo=tipo;
    }
    public String getTitulo(){
        return titulo;
    }
    public String getSubtitulo(){
        return subtitulo;
    }
    public String getResumen(){
        return resumen;
    }
    public byte[] getImagen() {
        return imagen;
    }

    public int getId() {
        return id;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }
    public int getTipo(){
        return tipo;
    }
}
