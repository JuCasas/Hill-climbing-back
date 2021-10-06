package com.example.SAGSystems.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Cisterna {
    public ArrayList<PuntoEntrega> ruta = new ArrayList<>();
    public int carga;
    public int tipo;
    public double costo;
    private String tipoCodigo;
    private double pesoBruto;
    private double pesoTotal;
    private LocalTime horaSalida; //SALIDA DE LA CENTRAL, cuando llega un pedido sale
    private LocalTime horaLlegada; //LLEGADA DE LA CENTRAL, llegada luego de entregar todos los pedidos
    private double duracionEntrega;
    private boolean disponible; //DISPONIBLE PARA LLEVAR PEDIDOS

    public Cisterna() {
        PuntoEntrega puntoInicial = new PuntoEntrega();
        puntoInicial.setPunto(0);
        puntoInicial.setCantidad(-1);
        DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.horaLlegada = LocalTime.parse("00:00:00",parseFormat);
        this.horaSalida = LocalTime.parse("00:00:00",parseFormat);
        this.disponible = true;
        ruta.add(puntoInicial);
    }

    public Cisterna(ArrayList<PuntoEntrega> ruta,int carga,int tipo, double costo,String tipoCodigo,double pesoBruto,
                    double pesoTotal,LocalTime horaSalida,LocalTime horaLlegada,boolean disponible,double duracionEntrega) {
        for (PuntoEntrega puntoEntrega : ruta) {
            PuntoEntrega nuevoPunto = new PuntoEntrega();
            nuevoPunto.setCantidad(puntoEntrega.getCantidad());
            nuevoPunto.setPunto(puntoEntrega.getPunto());
            nuevoPunto.setHoraEntregaCisterna(puntoEntrega.getHoraEntregaCisterna());
            //int nuevaRuta = new Integer(ruta.get(i));
            this.ruta.add(nuevoPunto);
            this.carga = carga;
            this.tipo = tipo;
            this.costo = costo;
            this.tipoCodigo = tipoCodigo;
            this.pesoBruto = pesoBruto;
            this.pesoTotal = pesoTotal;
            this.horaSalida = horaSalida;
            this.horaLlegada = horaLlegada;
            this.disponible = disponible;
            this.duracionEntrega = duracionEntrega;
        }
    }

    public int getCarga() {
        return carga;
    }

    public void addRutaPoint(PuntoEntrega point) {
        this.ruta.add(point);
    }

    public int getTamRuta(){
        return this.ruta.size();
    }

    public ArrayList<PuntoEntrega> getRuta(){
        return this.ruta;
    }

    public void setRuta(ArrayList<PuntoEntrega> ruta){
        this.ruta = ruta;
    }

    public void setCarga(int carga) {
        this.carga = carga;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getTipoCodigo() {
        return tipoCodigo;
    }

    public void setTipoCodigo(String tipoCodigo) {
        this.tipoCodigo = tipoCodigo;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }

    public double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public double getDuracionEntrega() {
        return duracionEntrega;
    }

    public void setDuracionEntrega(double duracionEntrega) {
        this.duracionEntrega = duracionEntrega;
    }
}
