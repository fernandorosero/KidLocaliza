package com.thelastmonkey.kidlocaliza.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.thelastmonkey.kidlocaliza.KidDTO.KidDTO;

public class KidLocalizaUtil {

    public void metodoPrueba() {
        System.out.println("metodoPrueba()");
    }

    public void muestroKidDTO(KidDTO kidDTO) {
        System.out.println("Nombre:" + kidDTO.getNombre());
        System.out.println("Edad:" + kidDTO.getEdad());
        System.out.println("Major:" + kidDTO.getMajor());
        System.out.println("Es el UUID:    ");
    }

}
