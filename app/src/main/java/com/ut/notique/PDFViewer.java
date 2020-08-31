package com.ut.notique;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;
import java.util.Objects;

public class PDFViewer extends AppCompatActivity { // Activity para mostrar el PDF

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_viewer);
        PDFView pdf = findViewById(R.id.viewer); // Ligar el PDFViewer
        Bundle extras = getIntent().getExtras(); // Obtener los extras del intent
        File arch = new File(Objects.requireNonNull(Objects.requireNonNull(extras).getString("file"))); // Crear un objeto file desde el archivo de pdf
        System.out.println("°°°°°°°°°°°° Peso del archivo en la clase de PDF : " + arch.length() + " °°°°°°°°°");
        pdf.fromFile(arch).enableSwipe(true).scrollHandle(new DefaultScrollHandle(this)).swipeHorizontal(false).onError(new OnErrorListener() { // Establecer ajustes y archivo del PDFViewer
            @Override
            public void onError(Throwable t) {
                System.out.println("XXXXXXXXXXXXXX Algo salio mal cos' : " + t.getMessage()); // En caso de error
            }
        }).enableAntialiasing(true).spacing(10).pageFitPolicy(FitPolicy.WIDTH).load(); // Mas ajustes
    }
}