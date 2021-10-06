package com.dp1.route4d.model;

import java.util.HashMap;
import java.util.Map;

public class BestSolution {
    private double costo;
    private Map<Integer, Map<Integer, Cisterna>> rutas;

    public BestSolution(double costo, Map<Integer, Map<Integer, Cisterna>> rutas){
        this.costo = new Double (costo);
        this.rutas = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Cisterna>> entry : rutas.entrySet()){
            Map<Integer,Cisterna> rutaTipo = new HashMap<>();
            Map<Integer,Cisterna> rutaACopiar = entry.getValue();
            for (Map.Entry<Integer,Cisterna> entradaCisterna : rutaACopiar.entrySet()){
                Cisterna cisterna = new Cisterna(entradaCisterna.getValue().getRuta(),entradaCisterna.getValue().getCarga(),
                        entradaCisterna.getValue().getTipo(),entradaCisterna.getValue().getCosto(),entradaCisterna.getValue().getTipoCodigo(),
                        entradaCisterna.getValue().getPesoBruto(),entradaCisterna.getValue().getPesoTotal(),
                        entradaCisterna.getValue().getHoraSalida(), entradaCisterna.getValue().getHoraLlegada(),
                        entradaCisterna.getValue().isDisponible(),entradaCisterna.getValue().getDuracionEntrega());
                rutaTipo.put(entradaCisterna.getKey(), cisterna);
            }

            this.rutas.put(entry.getKey(),rutaTipo);
        }
    }

    public BestSolution(){};

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Map<Integer, Map<Integer, Cisterna>> getRutas() {
        return rutas;
    }

    public void setRutas(Map<Integer, Map<Integer, Cisterna>> rutas) {
        this.rutas = rutas;
    }


}