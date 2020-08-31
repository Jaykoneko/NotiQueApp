package com.ut.notique;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private Fragment current;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Permitir acceso a la app para la bd
        StrictMode.setThreadPolicy(policy); // Establecer politica  *******************
        super.onCreate(savedInstanceState); //
        Intent i = new Intent(getApplicationContext(), NotifService.class);
        i.putExtra("KEY1", 1);
        ContextCompat.startForegroundService(this,i);
        setContentView(R.layout.activity_main); // Relacion con la vista
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {  //
            @Override
            public void onClick(View view) {
                refresh();
            }

        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set o        f Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder( //Items de la navbar *****
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_calendario,R.id.nav_memes)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    public void refresh(){
        Snackbar.make(current.getView(), "Recargando últimas noticias", Snackbar.LENGTH_LONG)  // Idea de refresh
                .setAction("Segundo", null).show();
        FragmentManager fragmentManager = current.getParentFragmentManager();
        FragmentTransaction fragTransaction =   fragmentManager.beginTransaction();
        fragTransaction.detach(current);
        fragTransaction.attach(current);
        fragTransaction.commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void setCurrent(Fragment current){
        this.current=current;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(current.getView()!=null) refresh();
    }
}