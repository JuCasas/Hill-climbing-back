package com.dp1.route4d.reader;

import com.dp1.route4d.model.Pedido;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class readerPedidos {
    public ArrayList<ArrayList<Integer>> distancias = new ArrayList<>();
    public Map<Integer, Pedido> hola = createMap();

    public readerPedidos(){}
    public readerPedidos(ArrayList<ArrayList<Integer>> distancias){
        this.distancias = distancias;
    }

    private static Map<Integer,Pedido> createMap() {
        Map<Integer,Pedido> myMap = new HashMap<Integer,Pedido>();
        Pedido firstCoordinates = new Pedido();
        firstCoordinates.setCordX(10);
        firstCoordinates.setCordY(8);
        myMap.put(0,firstCoordinates);
        String plazoEntregaString;
        try {
            File file = new File("src/main/resources/pedidos.txt");
            Scanner scan = new Scanner(file);
            scan.useDelimiter(",");
            int cordX, cordY,quantity,plazoEntrega,counter=1;
            DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime horaRecepcion;
            LocalTime aux;
            String horaString;
            int diaRecepcion;
            while(scan.hasNextLine()){
              Pedido pedido = new Pedido();
              horaString = scan.next();
              cordX = Integer.parseInt(scan.next());
              cordY = Integer.parseInt(scan.next());
              quantity = Integer.parseInt(scan.next());
              plazoEntregaString = scan.nextLine();
                //SETEANDO
                diaRecepcion = Integer.parseInt(horaString.substring(0,2));
              horaRecepcion = LocalTime.parse(horaString.substring(3,8)+":00",parseFormat);
              if (plazoEntregaString.length()==2) plazoEntrega =Integer.parseInt( plazoEntregaString.substring(1,2));
              else plazoEntrega =Integer.parseInt( plazoEntregaString.substring(1,3));
              pedido.setCordY(cordY);
              pedido.setCordX(cordX);
              pedido.setCantidad(quantity);
              pedido.setHoraRecepcion(horaRecepcion);
              pedido.setPlazoEntrega(plazoEntrega);
              pedido.setDiaRecepcion(diaRecepcion);
              myMap.put(counter,pedido);
              counter+=1;
            }
            scan.close();
            //IMPRESION DE MAPA
//            for (int i=1;i<= myMap.size()-1;i++){
//                System.out.println("PEDIDO "+i);
//                myMap.get(i).imprimirPedido();
//            }
        }
        catch (Exception e){
            System.out.println(e);
        }

        return myMap;
    }

    public double euclideanDistance(ArrayList<Integer> firstCoords,ArrayList<Integer> secondsCoords) {
        //DISTANCIA ENTRE 2 PUNTOS POR CATETOS
        return Math.abs(firstCoords.get(0)-secondsCoords.get(0)) + Math.abs(firstCoords.get(1)-secondsCoords.get(1));
        //return  Math.sqrt(Math.pow((firstCoords.get(0)-firstCoords.get(1)), 2) + Math.pow((secondsCoords.get(0)-secondsCoords.get(1)), 2));
    }

    public ArrayList<ArrayList<Double>> getMatrix() {
        ArrayList<ArrayList<Double>> matrix = new ArrayList<>();
        for (Map.Entry<Integer, Pedido> entry : getHola().entrySet()) {
            ArrayList<Double> rowDistance = new ArrayList<>();
            for (int i = 0; i< getHola().size(); i++) {
                if (i==entry.getKey()) rowDistance.add((double) 0);
                else {
                    ArrayList<Integer> firstCoords = new ArrayList<>();
                    ArrayList<Integer> secondCoords = new ArrayList<>();
                    firstCoords.add(entry.getValue().getCordX());
                    firstCoords.add(entry.getValue().getCordY());
                    secondCoords.add(getHola().get(i).getCordX());
                    secondCoords.add(getHola().get(i).getCordY());
                    rowDistance.add(BigDecimal.valueOf(euclideanDistance(firstCoords, secondCoords))
                            .setScale(2, RoundingMode.HALF_UP).doubleValue());
                }
            }
            matrix.add(rowDistance);
        }
        return matrix;
    }

    public Map<Integer, Pedido> getHola() {
        return hola;
    }

    public void setHola(Map<Integer, Pedido> hola) {
        this.hola = hola;
    }
}
