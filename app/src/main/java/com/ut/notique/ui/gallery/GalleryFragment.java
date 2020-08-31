package com.ut.notique.ui.gallery;
import android.app.Activity;
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

import com.ut.notique.MainActivity;
import com.ut.notique.PDFViewer;
import com.ut.notique.R;
import com.bumptech.glide.Glide;
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

public class GalleryFragment extends Fragment { // Noticias por carrera
    private ArrayList<ImageView> imgs; // ArrayLists de componentes
    private ArrayList<TextView> titulos, sTitulos, resumenes;
    private ArrayList<MaterialButton> cancBt;
    private ArrayList<MaterialCardView> cards;
    private ArrayList<ImageButton> botones;
    private ArrayList<Noticia> noticias;
    protected Activity parent; // Activity para correr runOnUIThread dentro de las AsyncTasks
    private Statement st; // Statement de la bd
    private int cNoticia = 0, tNoticias; // Variables de control de flujo de las noticias
    private ScrollView ScrollB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false); // Cosas raras de fragment
        initUI(root);
        parent=this.getActivity();
        parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((MainActivity)getActivity()).setCurrent(this);
        getNot();
        botones.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cNoticia-10<=0){
                    botones.get(0).setVisibility(View.GONE);
                    cNoticia=0;
                    postNoticias(cNoticia);
                    ScrollB.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollB.scrollTo(0,0);
                        }
                    });
                }else {
                    cNoticia=cNoticia-10;
                    postNoticias(cNoticia);
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
                postNoticias(cNoticia);
                ScrollB.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollB.scrollTo(0,0);
                    }
                });

                if(cNoticia>=tNoticias){
                    botones.get(1).setVisibility(View.GONE);
                }
            }
        });
        return root;
    }
    public void getNot(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
                    StrictMode.setThreadPolicy(policy); // Establecer politica
                    Connection connection = DriverManager.getConnection(getString(R.string.connection_string)); // Datos provisionales para la prueba de bd
                    System.out.println("°°°°°°°°°°°°°°°°°°°°° Conectao :D °°°°°°°°°°°°°°°°°");
                    st = connection.createStatement();
                    ResultSet rs=st.executeQuery("getNoticiasCarreras");
                    noticias=new ArrayList<>();
                    ArrayList<Noticia> tmp=new ArrayList<>();
                    int aux=0;
                    while(rs.next()){
                        Noticia temp=new Noticia(rs.getString("Nombre"),rs.getString("Fecha_publicacion"),rs.getString("Resumen"),rs.getBytes("Imagen"),rs.getInt("idNoticia"),rs.getInt("Tipo"));
                        tmp.add(temp);
                        System.out.println("~~~~~~~~~~~~~ Añadiendo Noticia ~~~~~~~~~~~~");
                        aux++;
                    }
                    for(int v=tmp.size()-1;v>0;v--){
                        noticias.add(tmp.get(v));
                    }
                    if(noticias.size()>5){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                botones.get(1).setVisibility(View.GONE);
                            }
                        });
                    }
                    if(aux==0){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"No hay noticias que mostrar",Toast.LENGTH_LONG).show();
                                for(int i=0;i<cards.size();i++){
                                    cards.get(i).setVisibility(View.GONE);
                                }
                            }
                        });
                        return;
                    }else{
                        tNoticias=aux-1;
                        cNoticia=0;
                        System.out.println(aux + " noticias añadidas correctamente");
                    }
                    if(tNoticias<5){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                botones.get(1).setVisibility(View.GONE);
                            }
                        });

                    }
                    postNoticias(0);
                }catch(SQLException ex){
                    System.out.println("XXXXXXXXXXXXX Algo salió mal en: "+ex.getMessage());
                }
            }
        });
    }
    public void initUI(View root) { // Metodo para rellenar los componentes gráficos
        imgs = new ArrayList<>();
        titulos = new ArrayList<>();
        sTitulos = new ArrayList<>();
        cancBt = new ArrayList<>();
        resumenes = new ArrayList<>();
        cards = new ArrayList<>();
        botones = new ArrayList<>();

        imgs.add((ImageView) root.findViewById(R.id.Cimgc1));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc2));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc3));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc4));
        imgs.add((ImageView) root.findViewById(R.id.Cimgc5));

        //--------------------------------

        titulos.add((TextView) root.findViewById(R.id.CtxtT1));
        titulos.add((TextView) root.findViewById(R.id.CtxtT2));
        titulos.add((TextView) root.findViewById(R.id.CtxtT3));
        titulos.add((TextView) root.findViewById(R.id.CtxtT4));
        titulos.add((TextView) root.findViewById(R.id.CtxtT5));

        //--------------------------------

        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt1));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt2));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt3));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt4));
        sTitulos.add((TextView) root.findViewById(R.id.CtxtSt5));

        //---------------------------------

        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC1));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC2));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC3));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC4));
        cancBt.add((MaterialButton) root.findViewById(R.id.CbtnC5));


        //---------------------------------

        resumenes.add((TextView) root.findViewById(R.id.CtxtR1));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR2));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR3));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR4));
        resumenes.add((TextView) root.findViewById(R.id.CtxtR5));

        //----------------------------------

        cards.add((MaterialCardView)root.findViewById(R.id.Ccard));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard2));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard3));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard4));
        cards.add((MaterialCardView)root.findViewById(R.id.Ccard5));

        //----------------------------------

        botones.add((ImageButton) root.findViewById(R.id.CbtnAtr));
        botones.add((ImageButton) root.findViewById(R.id.CbtnAd));

        ScrollB = root.findViewById(R.id.ScrollG);
        ScrollB.post(new Runnable() {
            @Override
            public void run() {
                ScrollB.scrollTo(0,0);
            }
        });
        // Inicializar animaciones de carga de imagenes

        for(int i=0;i<5;i++){
            imgs.get(i).setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(R.drawable.utn).into(imgs.get(i));
        }
    }
    public void postNoticias(int index){ // Mostrar noticias desde un indice
        System.out.println("~~~~~~~~~~~~~ Posteando noticias desde el index " + index);
        //determinar cuantas cards se van a usar
        int aux=(tNoticias-cNoticia);
        int contint=index;
        if(aux>=5){
            aux=5;
        }else{
            for(int l=0;l<5;l++){
                if(l>=aux){
                    final int finalL = l;
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cards.get(finalL).setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
        for(int i=0;i<aux;i++){
            final int finalI = i;
            final int finalContint=contint;
            final int finalI1 = i;
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cards.get(finalI).setVisibility(View.VISIBLE);
                    titulos.get(finalI).setText(noticias.get(finalContint).getNombre());
                    sTitulos.get(finalI).setText(noticias.get(finalContint).getFecha());
                    resumenes.get(finalI).setText(noticias.get(finalContint).getResumen());
                    imgs.get(finalI1).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgs.get(finalI).setImageBitmap(BitmapFactory.decodeByteArray(noticias.get(finalContint).getImagen(),0,noticias.get(finalContint).getImagen().length));
                    if(noticias.get(finalContint).getTipo()==1){
                        cancBt.get(finalI).setText("Abrir PDF");
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(View v) {
                                try{
                                    File temp = File.createTempFile("temp",".pdf");
                                    //noinspection ResultOfMethodCallIgnored
                                    temp.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(temp);
                                    ResultSet rs=st.executeQuery("getData "+noticias.get(finalContint).getId());
                                    if(rs.next()){
                                        byte[] dat = rs.getBytes("Datos");
                                        fos.write(dat);
                                        fos.flush();
                                        temp.deleteOnExit();
                                        System.out.println("********************** Tamaño: " + temp.length());
                                        System.out.println("---------------- Abriendo pdf");
                                        Intent intent = new Intent(parent.getApplicationContext(), PDFViewer.class);
                                        intent.putExtra("file",temp.toPath().toString());
                                        parent.startActivity(intent);
                                    }
                                }catch (Exception ex){
                                }
                            }
                        });
                    }else{
                        cancBt.get(finalI).setText("Abrir Link");
                        cancBt.get(finalI).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ResultSet rs = st.executeQuery("getData " + noticias.get(finalContint).getId());
                                    if(rs.next()) {
                                        String url = new String(rs.getBytes("Datos"), StandardCharsets.UTF_8);
                                        Intent in = new Intent(Intent.ACTION_VIEW);
                                        in.setData(Uri.parse(url));
                                        parent.getApplicationContext().startActivity(in);
                                    }
                                }catch (Exception ex){}
                            }
                        });
                    }
                }
            });
            contint++;
        }
        cNoticia+=aux;
    }
}

class Noticia{ // Clase noticia de puros datos.
    public final int id;
    public final int tipo;
    public final String Nombre;
    public final String Fecha;
    public final String Resumen;
    public final byte[] Imagen;

    public Noticia(String Nombre, String Fecha, String Resumen, byte[] Imagen,int id, int tipo){
        this.Nombre=Nombre;
        this.Fecha=Fecha;
        this.Resumen = Resumen;
        this.Imagen = Imagen;
        this.id=id;
        this.tipo=tipo;
    }
    public String getNombre(){
        return Nombre;
    }
    public String getFecha(){
        return Fecha;
    }

    public String getResumen() {
        return Resumen;
    }

    public int getId() {
        return id;
    }

    public int getTipo() {
        return tipo;
    }

    public byte[] getImagen() {
        return Imagen;
    }
}