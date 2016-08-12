package com.thelastmonkey.kidlocaliza;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thelastmonkey.kidlocaliza.util.KidLocalizaConstantes;

public class Data_Kid extends AppCompatActivity {

    EditText txtNombre;
    EditText txtZonaSegura;
    Button btnGuardar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__kid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtNombre = (EditText)findViewById(R.id.txtNombre);
        txtZonaSegura =(EditText)findViewById(R.id.txtZonaSegura);

        btnGuardar = (Button)findViewById(R.id.btnGuardar);

        //Flecha en el menú para ir hacia atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Recojo una preferencia para las preferencias
                KidLocalizaConstantes.prefs = getPreferences(MODE_APPEND);

                //Se activa la edicion
                SharedPreferences.Editor editor = KidLocalizaConstantes.prefs.edit();

                //Escribo los valores 2 String
                editor.putString(KidLocalizaConstantes.KID_LOCALIZA_NOMBRE, txtNombre.getText().toString());
                editor.putString(KidLocalizaConstantes.KID_LOCALIZA_ZONA_DE_SEGURIDAD, txtZonaSegura.getText().toString());

                //Se vuelca los datos
                editor.commit();

                Toast.makeText(Data_Kid.this, "Datos Almacenados!!", Toast.LENGTH_SHORT).show();

            }
        });

        //Al iniciarse el programa cojo las preferencias de los usuarios
        KidLocalizaConstantes.prefs = getPreferences(MODE_APPEND);

        //Seteo con los valores de prefs
        txtNombre.setText(KidLocalizaConstantes.prefs.getString(KidLocalizaConstantes.KID_LOCALIZA_NOMBRE,""));
        txtZonaSegura.setText(KidLocalizaConstantes.prefs.getString(KidLocalizaConstantes.KID_LOCALIZA_ZONA_DE_SEGURIDAD,""));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //Sobreescribiendo este método regreso al menú principal
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
