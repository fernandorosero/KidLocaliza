package com.thelastmonkey.kidlocaliza.KidDTO;

public class KidDTO {
    private String nombre;
    private int edad;
    private byte[] beconUUID;
    private int major;
    private int minor;
    private int distancia;

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public byte[] getBeconUUID() {
        return beconUUID;
    }

    public void setBeconUUID(byte[] beconUUID) {
        this.beconUUID = beconUUID;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }



}
