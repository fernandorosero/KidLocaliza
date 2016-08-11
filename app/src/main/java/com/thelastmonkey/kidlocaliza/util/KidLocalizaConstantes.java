package com.thelastmonkey.kidlocaliza.util;

import android.content.SharedPreferences;

/**
 * Created by tatof on 05/08/2016.
 */
public class KidLocalizaConstantes {
    public static final int RESTA_SUMA_UNO = 1;

    public static final int DISTANCIA_MINIMA = 1;
    public static final int DISTANCIA_MAXIMA = 70;

    public static final int ID_AVISO = 0;

    public final static String LOG_KIDLOCALIZA = "KidLocaliza";

    //Declaro las constantes para almacenar las prefernecias de usuario
    public static final String KID_LOCALIZA_NOMBRE = "KID_LOCALIZA_NOMBRE";
    public static final String KID_LOCALIZA_ZONA_DE_SEGURIDAD = "KID_LOCALIZA_ZONA_DE_SEGURIDAD";
    public static SharedPreferences prefs;
}
