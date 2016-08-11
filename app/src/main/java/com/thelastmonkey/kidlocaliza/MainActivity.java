package com.thelastmonkey.kidlocaliza;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.thelastmonkey.kidlocaliza.KidDTO.KidDTO;
import com.thelastmonkey.kidlocaliza.protocol.IBeacon;
import com.thelastmonkey.kidlocaliza.protocol.IBeaconListener;
import com.thelastmonkey.kidlocaliza.protocol.IBeaconProtocol;
import com.thelastmonkey.kidlocaliza.protocol.Utils;
import com.thelastmonkey.kidlocaliza.util.KidLocalizaConstantes;
import com.thelastmonkey.kidlocaliza.util.KidLocalizaUtil;

import java.util.Timer;
import java.util.TimerTask;

import static  com.thelastmonkey.kidlocaliza.R.drawable.monkey_azul;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IBeaconListener {

    private static final int REQUEST_ENABLE_BT = 1 ;
    private static final int REQUEST_BLUETOOTH_ENABLE = 1;
    private final int PERMISO_LOCALIZACION = 1;

    Button btnMas;
    Button btnMenos;
    TextView textViewDistancia;
    TextView textViewDistanciaReal;
    Switch switchVibration;


    private BluetoothAdapter bluetoothAdapter;
    private LocationManager locationManager;
    AlertDialog alert = null;

    //Configurar UUID
    public static final byte[] UUID = {(byte)0xA7,(byte)0xAE,(byte)0x2E,(byte)0xB7,(byte)0x1F,(byte)0x00,(byte)0x41,(byte)0x68,(byte)0xB9,(byte)0x9B,(byte)0xA7,(byte)0x49,(byte)0xBA,(byte)0xC1,(byte)0xCA,(byte)0x64};
    private IBeacon beaconSeguimiento1 = new IBeacon(UUID,1 , 2);
    private IBeaconProtocol iBeaconProtocol;

    private void scanBeacon(){
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Scanning");
        iBeaconProtocol = iBeaconProtocol.getInstance(this);

        //Filtro basado en el que tengo definido por defecto
        iBeaconProtocol.setScanUUID(UUID);

        if(!IBeaconProtocol.configureBluetoothAdapter(this)){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_ENABLE);
        }else {
            iBeaconProtocol.setListener(this);
            if(iBeaconProtocol.isScanning())
                iBeaconProtocol.scanIBeacons(false);
            iBeaconProtocol.reset();
            iBeaconProtocol.scanIBeacons(true);

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnMas = (Button)findViewById(R.id.btnMas);
        btnMenos = (Button)findViewById(R.id.btnMenos);
        textViewDistancia = (TextView)findViewById(R.id.textViewDistancia);
        textViewDistanciaReal = (TextView)findViewById(R.id.textViewDistanciaReal);
        switchVibration = (Switch)findViewById(R.id.switchVibration);

        //Cargo las preferencias para los datos
        KidLocalizaConstantes.prefs = getPreferences(MODE_PRIVATE);

        textViewDistancia.setText(KidLocalizaConstantes.prefs.getString(KidLocalizaConstantes.KID_LOCALIZA_NOMBRE, "01"));
        

        //Intent activarUbicacion = new Intent(Intent.  )

        //ACTIVO BLUETOOTH
        activarBluetooth();

        //Dependiendo de la version se activan los permisos
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Toast.makeText(MainActivity.this, "Aqui los codigos para versiones superiores a M", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Version inferior a M ", Toast.LENGTH_SHORT).show();
        }

        iBeaconProtocol = IBeaconProtocol.getInstance(this);
        iBeaconProtocol.setListener(this);

        TimerTask searchIbeaconTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanBeacon();
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(searchIbeaconTask, 200, 25000);
        //Listener para los botones
        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(textViewDistancia.getText().toString()) > KidLocalizaConstantes.DISTANCIA_MINIMA){
                    textViewDistancia.setText(aumentaCaracter((Integer.parseInt(textViewDistancia.getText().toString()) - KidLocalizaConstantes.RESTA_SUMA_UNO))
                             + Integer.toString(Integer.parseInt(textViewDistancia.getText().toString()) - KidLocalizaConstantes.RESTA_SUMA_UNO));
                }
                cambioBackgroundAndColorTextView(Integer.parseInt(textViewDistanciaReal.getText().toString()));
            }
        });

        btnMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(textViewDistancia.getText().toString()) < KidLocalizaConstantes.DISTANCIA_MAXIMA){
                    textViewDistancia.setText(aumentaCaracter((Integer.parseInt(textViewDistancia.getText().toString()) + KidLocalizaConstantes.RESTA_SUMA_UNO))
                            + Integer.toString(Integer.parseInt(textViewDistancia.getText().toString()) + KidLocalizaConstantes.RESTA_SUMA_UNO));
                }
                cambioBackgroundAndColorTextView(Integer.parseInt(textViewDistanciaReal.getText().toString()));
            }
        });

        //Listener Switch
        switchVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchVibration.isChecked())
                {
                    Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Es ON");
                }else
                {
                    Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Es OFF");
                }
            }
        });



        /**
         * Realizo la comprobación de los permisos Ubicación GPS
         *
         */

         if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA, "El permiso está denegado y hay que solicitarlo a través de la App.");

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA, "Aquí se vuelve a solicitar los permisos.");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}
                                     ,PERMISO_LOCALIZACION);
                //AlertNoGps();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}
                        ,PERMISO_LOCALIZACION);
                //AlertNoGps();
            }
        }else{
            Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Permiso otorgado");
             turnGPSOn();
             locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
             /*
             if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                 Toast.makeText(MainActivity.this, "No está activado el GPS Y hay que activarlo", Toast.LENGTH_SHORT).show();
                 Intent  intentActivarGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                 startActivity(intentActivarGPS);
             }
            */
            // AlertNoGps();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startActivityForResult(Intent enabledBluetooth) {
        //Aqui lo que se quiera hacer despues de habilitar o deshabilitar el Bluetooth
    }

    /**
     * Método que activa el Bluetooth
     */
    private void activarBluetooth(){
        /**
         * Compruebo si el dispositivo tiene Bluetooth
         *
         */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(),"Este dispositivo no tiene Bluetooth",Toast.LENGTH_SHORT).show();
        }else{
            //Detecto el estado en el que se encuentra el adaptador Bluetooth ON/OFF
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
                Toast.makeText(MainActivity.this, "Bluetooh activado", Toast.LENGTH_SHORT).show();
            }else
            {
                //Activo el Bluetooth
                Toast.makeText(MainActivity.this,
                        "Esta aplicación necesita activar el Bluetooth para su correcto funcionamiento",
                        Toast.LENGTH_LONG).show();

                Intent enabledBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enabledBluetooth, REQUEST_ENABLE_BT);
            }

        }
    }

    /**
     * Método que permite lanzar un aviso Broadcast cuando se abandona la zona de seguridad
     * @param distancia Valor de la distancia que se encuentra el beacon
     */
    public void avisoSimpleKidLocaliza(int distancia){
        //Extraigo el icono
        Bitmap icono = BitmapFactory.decodeResource(getResources(),monkey_azul);
        //Creo el aviso
        NotificationCompat.Builder avisoSimpleKidLocaliza = new NotificationCompat.Builder(this);
        avisoSimpleKidLocaliza.setSmallIcon(android.R.drawable.sym_def_app_icon);
        avisoSimpleKidLocaliza.setLargeIcon(icono);
        avisoSimpleKidLocaliza.setContentTitle("Aviso de salida de zona de seguridad");
        avisoSimpleKidLocaliza.setContentText("Marcos se aleja está a: ");
        avisoSimpleKidLocaliza.setContentInfo(Integer.toString(distancia) + " metros!");
        //Vibración
        long[] vibracion = {0, 400, 200,600};
        avisoSimpleKidLocaliza.setVibrate(vibracion);
        //Encender el led de avisos
        avisoSimpleKidLocaliza.setLights(Color.WHITE,100,1000);

        Intent intentActivaMain = null;
        intentActivaMain = new Intent(MainActivity.this,MainActivity.class);

        assert intentActivaMain != null;
        intentActivaMain.putExtra("Id",KidLocalizaConstantes.ID_AVISO);

        //Creo el PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentActivaMain, 0);

        //Adjunto el PendingIntetn a la notificación
        avisoSimpleKidLocaliza.setContentIntent(pendingIntent);

        //Cuando se haga click en la notificacion y desaparezca
        avisoSimpleKidLocaliza.setAutoCancel(true);

        //Genero el aviso
        Notification aviso = avisoSimpleKidLocaliza.build();

        //Llamo al gestor de notificaciones para lanzarlo
        NotificationManager miNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //Lanzo el aviso
        miNotificationManager.notify(1,aviso);
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Se ha lanzado el aviso!");



    }
    private void turnGPSOn() {

        Log.i("localiza***","Entra al metodo");
        String provider = android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }
    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Toast.makeText(MainActivity.this, "Aqui llamo al layout ajustes", Toast.LENGTH_SHORT).show();
            Intent intenDatosKid = new Intent(MainActivity.this, Data_Kid.class);
            startActivity(intenDatosKid);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void enterRegion(IBeacon ibeacon) {
        textViewDistanciaReal.setText(Integer.toString(ibeacon.getProximity()));
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Entra en la región!!!");
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Es la proximidad:" + ibeacon.getProximity());
    }

    @Override
    public void exitRegion(IBeacon ibeacon) {
        textViewDistanciaReal.setText(Integer.toString(ibeacon.getProximity()));
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"Sale de la región: " + ibeacon.getProximity());
    }

    @Override
    public void beaconFound(IBeacon ibeacon) {
        textViewDistanciaReal.setText(Integer.toString(ibeacon.getProximity()));
        cambioBackgroundAndColorTextView(ibeacon.getProximity());
        if((ibeacon.getProximity() > Integer.parseInt(textViewDistancia.getText().toString())) && switchVibration.isChecked()) {
            avisoSimpleKidLocaliza(ibeacon.getProximity());
        }
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA, "Beacon encontrado!!" + ibeacon.toString());
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA, "Se encuentra a:" + ibeacon.getProximity());
    }

    @Override
    public void searchState(int state) {
        Log.i(KidLocalizaConstantes.LOG_KIDLOCALIZA,"El estado es: " + state);
    }

    @Override
    public void operationError(int status) {

    }

    /**
     * Método que cambia de color si está dentro o fuera de la zona de seguridad
     * @param distBeacon es la distancia a la que se encuentra
     */
    public void cambioBackgroundAndColorTextView(int distBeacon){
        if(distBeacon > Integer.parseInt(textViewDistancia.getText().toString())){
            textViewDistanciaReal.setBackgroundColor(Color.parseColor("#c73053"));
            textViewDistanciaReal.setTextColor(Color.parseColor("#ffffff"));
        }
        else{
            textViewDistanciaReal.setBackgroundColor(Color.parseColor("#e6fdff"));
            textViewDistanciaReal.setTextColor(Color.parseColor("#383838"));
        }

    }

    /**
     * Método que aumenta un cero a la izquierda si el valor de la Distancia de alerta
     * es menor a 10
     * @param beaconDistanciaSeguridad devuelve 0 ó ""
     * @return
     */
    public String aumentaCaracter(int beaconDistanciaSeguridad){
        String aumentoValor = "";
        if(beaconDistanciaSeguridad <= 9){
            aumentoValor = "0";
        }
        return aumentoValor;
    }
}
