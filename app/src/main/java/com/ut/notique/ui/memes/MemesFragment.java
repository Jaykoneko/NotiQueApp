package com.ut.notique.ui.memes;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.ut.notique.MainActivity;
import com.ut.notique.R;
import com.google.android.material.card.MaterialCardView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@SuppressWarnings({"deprecation", "unused"})
public class MemesFragment extends Fragment {

    private MemesViewModel memesViewModel;
    private View roott;
    private int cMeme=0, tMemes;
    protected Activity parent;
    private ArrayList<ImageView> imgs;
    private ArrayList<TextView>  resumenes;
    private ArrayList<MaterialCardView> cards;
    private ArrayList<ImageButton> botones;
    private ArrayList<Memes> memes;
    private Statement st;
    private ScrollView ScrollB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MemesViewModel memesViewModel = ViewModelProviders.of(this).get(MemesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_memes, container, false);
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
                    ResultSet rs=st.executeQuery("getMeme");
                    memes=new ArrayList<>();
                    int aux=0;
                    ArrayList<Memes> tmp=new ArrayList<>();
                    while(rs.next()){
                        Memes temp=new Memes(rs.getInt("id_meme"),rs.getString("contenido_meme"),rs.getBytes("imagen_meme"),rs.getString("Fecha_publicacion"));
                        tmp.add(temp);
                        System.out.println("~~~~~~~~~~~~~ Añadiendo meme ~~~~~~~~~~~~");
                        aux++;
                    }
                    for(int v=tmp.size()-1;v>0;v--){
                        memes.add(tmp.get(v));
                    }
                    if(aux==0){
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"No hay contenido que mostrar",Toast.LENGTH_LONG).show();
                                for(int i=0;i<cards.size();i++){
                                    cards.get(i).setVisibility(View.GONE);
                                }
                            }
                        });
                        return;
                    }else{
                        if(aux>3){
                            tMemes=aux-1;
                        }
                        else{
                            tMemes=aux;
                        }
                        cMeme=0;
                        System.out.println(aux + " memes añadidas correctamente");
                    }
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postMemes(0);
                            if(tMemes<4) botones.get(0).setVisibility(View.GONE);
                        }
                    });
                }catch(SQLException ex){
                    System.out.println("XXXXXXXXXXXXX Algo salió mal en: "+ex.getMessage());
                }
            }
        });
        botones.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cMeme-10<=0){
                    botones.get(0).setVisibility(View.GONE);
                    cMeme=0;
                    postMemes(cMeme);
                    ScrollB.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollB.scrollTo(0,0);
                        }
                    });
                }else {
                    cMeme=cMeme-10;
                    postMemes(cMeme);
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
                postMemes(cMeme);
                ScrollB.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollB.scrollTo(0,0);
                    }
                });

                if(cMeme>=tMemes){
                    botones.get(1).setVisibility(View.GONE);
                }
            }
        });
        return root;

    }
    public void postMemes(int index){
        System.out.println("~~~~~~~~~~~~~ Posteando memes desde el index " + index);
        //determinar cuantas cards se van a usar
        int aux=tMemes-cMeme;
        int contint=index;
        if(aux>=5){
            aux=5;
        }else{
            for(int l=0;l<5;l++){
                if(l>=aux){
                    cards.get(l).setVisibility(View.GONE);
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
                    resumenes.get(finalI).setText(memes.get(finalContint).getContenido_meme());
                    imgs.get(finalI1).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imgs.get(finalI).setImageBitmap(BitmapFactory.decodeByteArray(memes.get(finalContint).getImagen_meme(),0,memes.get(finalContint).getImagen_meme().length));
                }
                        });
            contint++;
        }
        cMeme+=aux;
        if(tMemes<4){
            botones.get(1).setVisibility(View.GONE);
        }
    }

    public void initUI(View root) {
        imgs = new ArrayList<>();
        resumenes = new ArrayList<>();
        cards = new ArrayList<>();
        botones = new ArrayList<>();

        imgs.add((ImageView) root.findViewById(R.id.Mimgc1));
        imgs.add((ImageView) root.findViewById(R.id.Mimgc2));
        imgs.add((ImageView) root.findViewById(R.id.Mimgc3));
        imgs.add((ImageView) root.findViewById(R.id.Mimgc4));
        imgs.add((ImageView) root.findViewById(R.id.Mimgc5));

        //--------------------------------

        resumenes.add((TextView) root.findViewById(R.id.MtxtR1));
        resumenes.add((TextView) root.findViewById(R.id.MtxtR2));
        resumenes.add((TextView) root.findViewById(R.id.MtxtR3));
        resumenes.add((TextView) root.findViewById(R.id.MtxtR4));
        resumenes.add((TextView) root.findViewById(R.id.MtxtR5));

        //----------------------------------

        cards.add((MaterialCardView)root.findViewById(R.id.Mcard));
        cards.add((MaterialCardView)root.findViewById(R.id.Mcard2));
        cards.add((MaterialCardView)root.findViewById(R.id.Mcard3));
        cards.add((MaterialCardView)root.findViewById(R.id.Mcard4));
        cards.add((MaterialCardView)root.findViewById(R.id.Mcard5));

        //----------------------------------

        botones.add((ImageButton) root.findViewById(R.id.MbtnAtr));
        botones.add((ImageButton) root.findViewById(R.id.MbtnAd));


        // Inicializar animaciones de carga de imagenes
        ScrollB = root.findViewById(R.id.ScrollM);
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

}

class Memes{
    private int id_meme;
    private String contenido_meme;
    private byte [] imagen_meme;
    private String Fecha_publicacion;

    public Memes(int id_meme,String contenido_meme,byte[] imagen_meme, String Fecha_publicacion){
        this.id_meme=id_meme;
        this.contenido_meme=contenido_meme;
        this.imagen_meme=imagen_meme;
        this.Fecha_publicacion=Fecha_publicacion;
    }
    public int getId_meme(){
        return id_meme;
    }
    public void setId_meme(int id_meme){
        this.id_meme=id_meme;
    }
    public String getContenido_meme(){
        return  contenido_meme;
    }
    public void  setContenido_meme(String contenido_meme){
        this.contenido_meme=contenido_meme;
    }
    public byte[] getImagen_meme(){
        return imagen_meme;
    }
    public void setImagen_meme(byte[] imagen_meme){
        this.imagen_meme=imagen_meme;
    }
    public String getFecha_publicacion(){
        return Fecha_publicacion;
    }
    public void setFecha_publicacion(String fecha_publicacion){
        this.Fecha_publicacion=fecha_publicacion;
    }

}
