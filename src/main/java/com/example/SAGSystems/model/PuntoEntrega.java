package com.example.SAGSystems.model;

import java.time.LocalTime;

public class PuntoEntrega {
    private int punto;
    private int cantidad;
    private LocalTime horaEntregaCisterna;
    private boolean esEntregaFinal; //LA ENTREGA ES FINAL

    public PuntoEntrega(){
        this.esEntregaFinal = false;
    }

    public PuntoEntrega(int punto, int cantidad){
        this.punto = punto;
        this.cantidad = cantidad;
        this.esEntregaFinal = false;
    }

    public int getPunto() {
        return punto;
    }

    public void setPunto(int punto) {
        this.punto = punto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalTime getHoraEntregaCisterna() {
        return horaEntregaCisterna;
    }

    public void setHoraEntregaCisterna(LocalTime horaEntregaCisterna) {
        this.horaEntregaCisterna = horaEntregaCisterna;
    }

    public boolean isEsEntregaFinal() {
        return esEntregaFinal;
    }

    public void setEsEntregaFinal(boolean esEntregaFinal) {
        this.esEntregaFinal = esEntregaFinal;
    }
}
