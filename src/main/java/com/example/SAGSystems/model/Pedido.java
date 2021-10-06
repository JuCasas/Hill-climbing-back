package com.example.SAGSystems.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Pedido {
    private LocalTime horaRecepcion;
    private int diaRecepcion;
    private int cordX;
    private int cordY;
    private int cantidad;
    private int plazoEntrega;
    private LocalTime horaEntrega;



    public int getCordX() {
        return cordX;
    }

    public void setCordX(int cordX) {
        this.cordX = cordX;
    }

    public int getCordY() {
        return cordY;
    }

    public void setCordY(int cordY) {
        this.cordY = cordY;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPlazoEntrega() {
        return plazoEntrega;
    }

    public void setPlazoEntrega(int plazoEntrega) {
        this.plazoEntrega = plazoEntrega;
    }


    public LocalTime getHoraRecepcion() {
        return horaRecepcion;
    }

    public void setHoraRecepcion(LocalTime horaRecepcion) {
       // System.out.println("HORA DE RECEPCION A SETEAR: "+horaRecepcion);
        this.horaRecepcion = horaRecepcion;
       // System.out.println("HORA DE RECEPCION SETEADA: "+getHoraRecepcion());
    }

    public void imprimirPedido(){
        System.out.print("HORA RECEPCION: "+this.horaRecepcion+ "PLAZO ENTREGA: "+this.plazoEntrega);
    }

    public LocalTime getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(LocalTime horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public int getDiaRecepcion() {
        return diaRecepcion;
    }

    public void setDiaRecepcion(int diaRecepcion) {
        this.diaRecepcion = diaRecepcion;
    }
}
