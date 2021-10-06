package com.dp1.route4d.algorithm;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.dp1.route4d.model.*;
import com.dp1.route4d.reader.readerPedidos;
import com.dp1.route4d.reader.readerVehiculos;
import com.dp1.route4d.model.*;

public class HillClimbing {

    private ArrayList<ArrayList<Double>> distancias = new ArrayList<ArrayList<Double>>();
    private BestSolution nuevaSolucion = new BestSolution();
    private HashMap<Integer, Cisternas> cisternas = new HashMap<>();
    private Map<Integer, Pedido> coordinatesAndOrders = new HashMap<>();
    private LocalTime horaLocal;
    private int diaLocal;
    private double costoTotal=0;
    public HillClimbing() {
    }

    public void setCostoTotal(double costoTotal){
        this.costoTotal = costoTotal;
    }

    public double getCostoTotal(){
        return this.costoTotal;
    }

    public void createMatrix() throws FileNotFoundException {
        readerPedidos CoordinatesAndOrders = new readerPedidos();
        this.coordinatesAndOrders = CoordinatesAndOrders.getHola();// EN UNO EMPIEZA EL PRIMER PEDIDO. 0 ES EL LOCAL INICIAL
        this.distancias = CoordinatesAndOrders.getMatrix();
    }

    public void readVehicles() throws FileNotFoundException {
        readerVehiculos readVehicles = new readerVehiculos();
        cisternas = readVehicles.getDataCisternas();
        // KEY DEL MAP EMPIEZA EN 1 --> CUIDADO

    }


    public Integer retornaRandom(int inferior, int superior) {
        Random r = new Random();
        int low = inferior;
        int high = superior+1;
        int result = r.nextInt(high-low) + low;
        return result;
    }

    public double addCosto(ArrayList<ArrayList<Double>> distancias, Cisterna cisterna) {
        ArrayList<PuntoEntrega> ruta = new ArrayList<>(cisterna.getRuta());
        double consumoPetroleo = hallarConsumoPetroleo(distancias,ruta,cisterna.getPesoTotal());
        cisterna.setCosto(consumoPetroleo);
        return consumoPetroleo;
    }

    public int añadirPedidoARuta(int typeOfVehicle,Map<Integer,Map<Integer, Cisterna>> rutas,int quantityOrder,int i,
                                  HashMap<Integer, Cisternas> vehicles,int actualQuantityVehicles,int totalQuantityVehicles
                                  ){
        int cargaActual;
        double consumoPetroleo;
        //i = NUMERO DE PEDIDO
        if (!rutas.containsKey(typeOfVehicle)) {
            Cisterna cisterna = new Cisterna();
            cisterna.setCarga(quantityOrder);
            PuntoEntrega puntoEntrega = new PuntoEntrega();
            puntoEntrega.setPunto(i);
            puntoEntrega.setCantidad(quantityOrder);
            cisterna.addRutaPoint(puntoEntrega);
            cisterna.setTipo(typeOfVehicle);
            cisterna.setTipoCodigo(vehicles.get(typeOfVehicle).getTipo());
            cisterna.setPesoBruto(vehicles.get(typeOfVehicle).getPesoBruto());
            cisterna.setPesoTotal(cisterna.getPesoBruto() + ((double) quantityOrder /2));

            Map<Integer, Cisterna> ruta = new HashMap<>();
            ruta.put(1, cisterna);
            rutas.put(typeOfVehicle,ruta);
            vehicles.get(typeOfVehicle).setCantidad(actualQuantityVehicles - 1);
            return 1;
        }
        else {
            for (int k=1;k<=totalQuantityVehicles;k++) {
                if (rutas.get(typeOfVehicle).containsKey(k)){
                    cargaActual = rutas.get(typeOfVehicle).get(k).getCarga();
                    //NUEVA RUTA
                    ArrayList<PuntoEntrega> puntosEntregas = new ArrayList<>();
                    for (int p=0;p<rutas.get(typeOfVehicle).get(k).getRuta().size();p++){
                        PuntoEntrega puntito = new PuntoEntrega(rutas.get(typeOfVehicle).get(k).getRuta().get(p).getPunto(),
                                rutas.get(typeOfVehicle).get(k).getRuta().get(p).getCantidad());
                        puntosEntregas.add(puntito);
                    }
                    PuntoEntrega puntito = new PuntoEntrega(i,quantityOrder);
                    puntosEntregas.add(puntito);
                    consumoPetroleo = hallarConsumoPetroleo(distancias,puntosEntregas,
                            rutas.get(typeOfVehicle).get(k).getPesoTotal());
                    /////////////////////////////////////////////////
                    if (cargaActual+quantityOrder>vehicles.get(typeOfVehicle).getCapacidad()||consumoPetroleo>25
                    || !rutas.get(typeOfVehicle).get(k).isDisponible()){
                        continue;
                    }
                    else {
                        rutas.get(typeOfVehicle).get(k).setCarga(cargaActual + quantityOrder);
                        PuntoEntrega puntoEntrega = new PuntoEntrega();
                        puntoEntrega.setPunto(i);
                        puntoEntrega.setCantidad(quantityOrder);
                        rutas.get(typeOfVehicle).get(k).setPesoTotal(rutas.get(typeOfVehicle).get(k).getPesoTotal() +
                                ((double) quantityOrder /2));
                        rutas.get(typeOfVehicle).get(k).addRutaPoint(puntoEntrega);
                        return 1;
                    }
                }
                else {
                    Cisterna newCisterna = new Cisterna();
                    newCisterna.setCarga(quantityOrder);
                    PuntoEntrega puntoEntrega = new PuntoEntrega();
                    puntoEntrega.setPunto(i);
                    puntoEntrega.setCantidad(quantityOrder);
                    newCisterna.addRutaPoint(puntoEntrega);
                    newCisterna.setTipo(typeOfVehicle);
                    newCisterna.setTipoCodigo(vehicles.get(typeOfVehicle).getTipo());
                    newCisterna.setPesoBruto(vehicles.get(typeOfVehicle).getPesoBruto());
                    newCisterna.setPesoTotal(newCisterna.getPesoBruto() + ((double) quantityOrder /2));
                    rutas.get(typeOfVehicle).put(k, newCisterna);
                    vehicles.get(typeOfVehicle).setCantidad(actualQuantityVehicles - 1);
                    return 1;
                }
            }
            return -1;
        }
    }

    public boolean posibleAsignar(HashMap<Integer, Cisternas> vehicles,HashMap<Integer, Cisternas> cisternas,int cantidad,
                                  Map<Integer,Map<Integer, Cisterna>> rutas){
        //VEHICLES CONTIENE CANTIDAD ACTUAL
        //CISTERNAS CONTIENE CANTIDAD TOTAL
        int cantidadActual; //VEHICULOS LIBRES
        boolean asignado = false;
        for (int i=1;i<=vehicles.size();i++) {
            cantidadActual = vehicles.get(i).getCantidad();
            cantidad -= cantidadActual * vehicles.get(i).getCapacidad();
            if (cantidad<=0) {
                asignado = true;
                break;
            }
            if (cantidad<vehicles.get(i).getCapacidad()){
                for (int k=1;k<=cisternas.get(i).getCantidad()-vehicles.get(i).getCantidad();k++){
                    cantidadActual -= cisternas.get(i).getCapacidad()-rutas.get(i).get(k).getCarga();
                    if (cantidadActual<=0) {
                        asignado = true;
                        break;
                    }
                }
            }
            if (asignado) break;
        }
        return asignado;
    }

    public int crearSolucionRandom(ArrayList<ArrayList<Double>> distancias,
                                   Map<Integer, Pedido> coordinatesAndOrders,
                                   HashMap<Integer, Cisternas> cisternas,int pedidoInf,int pedidoSup) throws FileNotFoundException {

        int  quantityOrder,actualQuantityVehicles=-1,typeOfVehicle=-1,cargaActual,totalQuantityVehicles=-1;
        int capacidadVehiculo,capacidad2,cantidad2,cantidadAAsignar,totalCantidadVehiculos,añadido,numPedidoNoAsignado=-1;
        boolean pedidoAsignado=false,noHayCapacidad = false;
        double costoTotal=0;
        readerVehiculos readVehicles = new readerVehiculos();
        HashMap<Integer, Cisternas> vehicles = readVehicles.getDataCisternas();
        Map<Integer,Map<Integer, Cisterna>> rutas = new HashMap<>();
        if(pedidoInf!=1){
            BestSolution copiaSolucion = new BestSolution (nuevaSolucion.getCosto(),nuevaSolucion.getRutas());
            rutas = copiaSolucion.getRutas();
        }



        for (int i = pedidoInf; i <= pedidoSup; i++) {
            quantityOrder = coordinatesAndOrders.get(i).getCantidad();
            noHayCapacidad = false;
            pedidoAsignado = false;
            for (int j = 1; j<=vehicles.size();j++) {
                actualQuantityVehicles = vehicles.get(j).getCantidad();
                totalQuantityVehicles = cisternas.get(j).getCantidad();
                capacidadVehiculo = vehicles.get(j).getCapacidad();
                if (capacidadVehiculo<quantityOrder || actualQuantityVehicles==0){
                    if (j!=vehicles.size()) continue;
                    else {
                        noHayCapacidad = true;
                        //NO EXISTE CAMION CON CAPACIDAD SUFICIENTE PARA ESTE PEDIDO, SE REPARTE
                        if(!posibleAsignar(vehicles,cisternas,quantityOrder,rutas)) break;
                        for (int k=1;k<=vehicles.size();k++){
                            cantidad2 = vehicles.get(k).getCantidad();
                            totalCantidadVehiculos = cisternas.get(k).getCantidad();
                            capacidad2 = vehicles.get(k).getCapacidad();
                            if (cantidad2==0) continue;
                            while(true){
                                if (vehicles.get(k).getCantidad()==0) {
                                    break;
                                }
                                if (quantityOrder<= capacidad2) {
                                    añadido = añadirPedidoARuta(k,rutas,quantityOrder,i,vehicles,cantidad2,
                                            totalCantidadVehiculos);
                                    if (añadido==-1) break;
                                    pedidoAsignado = true;
                                    break;
                                }
                                else {
                                    añadido = añadirPedidoARuta(k,rutas,capacidad2,i,vehicles,cantidad2,
                                            totalCantidadVehiculos);
                                    if (añadido == -1) break;
                                    else quantityOrder-= capacidad2;
                                }

                            }
                            if (pedidoAsignado) break;
                            else continue;
                        }
                    }
                }
                else {
                    //pedidoAsignado = true;
                    typeOfVehicle = j;
                    noHayCapacidad = false;
                    añadido = añadirPedidoARuta(typeOfVehicle,rutas,quantityOrder,i,vehicles,
                            actualQuantityVehicles,totalQuantityVehicles);
                    if(añadido==-1) {
                        if (j==vehicles.size()){
                            //NO EXISTE CAMION CON CAPACIDAD PARA ESTE PEDIDO
                            if(!posibleAsignar(vehicles,cisternas,quantityOrder,rutas)) break;
                            for (int k=1;k<=vehicles.size();k++){
                                cantidad2 = vehicles.get(k).getCantidad();
                                totalCantidadVehiculos = cisternas.get(k).getCantidad();
                                capacidad2 = vehicles.get(k).getCapacidad();
                                if (cantidad2==0) continue;
                                while(true){
                                    if (vehicles.get(k).getCantidad()==0) {
                                        break;
                                    }
                                    if (quantityOrder<= capacidad2) {
                                        añadido = añadirPedidoARuta(k,rutas,quantityOrder,i,vehicles,cantidad2,
                                                totalCantidadVehiculos);
                                        if (añadido==-1) break;
                                        pedidoAsignado = true;
                                        break;
                                    }
                                    else {
                                        añadido = añadirPedidoARuta(k,rutas,capacidad2,i,vehicles,cantidad2,
                                                totalCantidadVehiculos);
                                        if (añadido == -1) break;
                                        else quantityOrder-= capacidad2;
                                    }

                                }
                                if (pedidoAsignado) break;
                                else continue;
                            }

                        }
                        else continue;
                    }
                    else {
                        pedidoAsignado = true;
                        break;
                    }
                }
            }
//            if (!noHayCapacidad) añadirPedidoARuta(typeOfVehicle,rutas,quantityOrder,i,vehicles,
//                    actualQuantityVehicles,totalQuantityVehicles);
            if (!pedidoAsignado) {
                numPedidoNoAsignado = i;
                break;
            }
        };
        //AÑADIENDO COSTOS Y SETEANDO HORAS SOLO A CISTERNAS DISPONIBLES
        for (int i=1;i<=vehicles.size();i++){
            if (rutas.containsKey(i)){
                for (int j=1;j<=rutas.get(i).size();j++) {
                    if (!rutas.get(i).containsKey(j)) break;
                    else {
                        if (rutas.get(i).get(j).isDisponible()){
                            costoTotal += addCosto(distancias,rutas.get(i).get(j));
                            setearHorasCisterna(distancias,rutas.get(i).get(j));
                        }
                    }
                }
            }
            else continue;
        }
        //for (Map.Entry<Integer,Cisterna> entry: mejorSolucion.getRutas().get(i).entrySet())
        //IMPRESION RUTAS
        System.out.println("------------SOLUCION INICIAL DE ITERACION--------------");
        if(rutas.size()!=0) {
            for (int i=1;i<=vehicles.size();i++){
                System.out.println("TIPO DE CAMION "+i);
                if (rutas.containsKey(i)) {
                    //for (int j=1;j<=rutas.get(i).size();j++) {
                    for (Map.Entry<Integer,Cisterna> entry: rutas.get(i).entrySet()){
                        System.out.println("CAMION "+entry.getKey()+" TIENE LA RUTA CON CARGA "+entry.getValue().getCarga()+" Y COST" +
                                "O "+ entry.getValue().getCosto());
                        System.out.println("CAMION "+entry.getKey()+" TIENE UN PESO BRUTO DE "+entry.getValue().getPesoBruto()+" Y TOT" +
                                "AL DE "+ entry.getValue().getPesoTotal()+ " TIPO DE CODIGO: "+entry.getValue().getTipoCodigo());
                        for (int k=0;k<entry.getValue().getTamRuta();k++){
                            if (k!=entry.getValue().getTamRuta()-1)System.out.print(entry.getValue().getRuta().get(k).getPunto()+"--->");
                            else System.out.println(entry.getValue().getRuta().get(k).getPunto()+"---> 0");
                        }
                    }
                }
                else System.out.println("NO HAY RUTAS PARA ESTE TIPO DE CAMION");
            }
            System.out.println("EL COSTO TOTAL DE LA RUTA ES: "+costoTotal);
            System.out.println("---------------------------------------------------------------");
            this.nuevaSolucion.setCosto(costoTotal);
            this.nuevaSolucion.setRutas(rutas);
        }
        if (numPedidoNoAsignado==-1) return -1;
        else return numPedidoNoAsignado;
    }

    void impresionRutas(HashMap<Integer, Cisternas> cisternas, BestSolution mejorSolucion){
        int comparacion = 1;
        System.out.println("______________________________");
        for (int i=1;i<=cisternas.size();i++){
            System.out.println("TIPO DE CAMION "+i);
            //for (Map.Entry<Integer, Map<Integer, Cisterna>> entry : rutas.entrySet())
            if (mejorSolucion.getRutas().containsKey(i)) {
                for (Map.Entry<Integer,Cisterna> entry: mejorSolucion.getRutas().get(i).entrySet()) {

                        System.out.println("CAMION "+entry.getKey()+" TIENE LA RUTA CON CARGA "+entry.getValue().getCarga()+
                                " Y COST" +
                                "O "+ entry.getValue().getCosto());
                        System.out.println("CAMION "+entry.getKey()+" TIENE UN PESO BRUTO DE "+entry.getValue().getPesoBruto()+
                                " Y TOT" +
                                "AL DE "+ entry.getValue().getPesoTotal()+ " TIPO DE CODIGO: "+
                                entry.getValue().getTipoCodigo());
                        System.out.println("CAMION "+entry.getKey()+" SALIO DEL ALMACEN A LA HORA: "+entry.getValue().getHoraSalida());
                        System.out.println("CAMION "+entry.getKey()+" LLEGO AL  ALMACEN A LA HORA: "+entry.getValue().getHoraLlegada());
                        System.out.println("PARA ENTREGAR SUS PEDIDOS SE DEMORO: "+entry.getValue().getHoraSalida().until(
                                entry.getValue().getHoraLlegada(), ChronoUnit.SECONDS));
                        //SETEAMOS DISPONIBILIDAD
                        entry.getValue().setDisponible(false);
                        ////////////////////////////////
                        for (int k=0;k<entry.getValue().getTamRuta();k++){
                            if (k!=entry.getValue().getTamRuta()-1)System.out.print(entry.getValue()
                                    .getRuta().get(k).getPunto()+"--->");
                            else System.out.println(entry.getValue().getRuta().get(k).getPunto()+"---> 0");
                        }
                }
            }
            else System.out.println("NO HAY RUTAS PARA ESTE TIPO DE CAMION");
        }
        System.out.println("EL COSTO TOTAL DE LA RUTA ES: "+mejorSolucion.getCosto());
        System.out.println("______________________________");
    }

    void llenarPedidoHoraEntrega(Map<Integer, Pedido> coordinatesAndOrders,BestSolution mejorSolucion){
        //SETEAR HORAS DE ENTREGAS
        int comparacion;
        for (int i=1;i<=cisternas.size();i++) {
            if (mejorSolucion.getRutas().containsKey(i)){
                for (int j=1;j<=mejorSolucion.getRutas().get(i).size();j++) {
                    if (mejorSolucion.getRutas().get(i).containsKey(j)){
                        for (int k=0;k<mejorSolucion.getRutas().get(i).get(j).getTamRuta();k++){
                            if (k>=1){
                                if (coordinatesAndOrders.get(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getPunto()).getHoraEntrega()
                                        ==null){
                                    coordinatesAndOrders.get(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getPunto())
                                            .setHoraEntrega(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getHoraEntregaCisterna());
                                }
                                else {
                                    comparacion = coordinatesAndOrders.get(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getPunto())
                                            .getHoraEntrega().compareTo(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getHoraEntregaCisterna());
                                    if (comparacion<1) coordinatesAndOrders.get(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getPunto())
                                            .setHoraEntrega(mejorSolucion.getRutas().get(i).get(j).getRuta().get(k).getHoraEntregaCisterna());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//        List<Integer> ciudades = IntStream.rangeClosed(0,matrizDistancia.size()-1).boxed().collect(Collectors.toList()); //Crear una lista de 0 a 3
//        ArrayList<Integer> solucion = new ArrayList<>();
//        Integer ciudadRandom,first=1;
//        for (int i=0;i<matrizDistancia.size();i++){
//            //NUEVA SOL
//            if(first==1){
//                solucion.add(0);
//                ciudades.remove(0);
//                first = 0;
//                continue;
//            }
//            //NUEVA SOL
//            ciudadRandom = ciudades.get(retornaRandom(ciudades));
//            solucion.add(ciudadRandom);
//            ciudades.remove(ciudadRandom);
//        }
//        return solucion;
//    }

    public void setearHorasCisterna(ArrayList<ArrayList<Double>> matrizDistancia,Cisterna cisterna){
        //NO SETEAR CISTERNAS QUE NO ESTAN DISPONIBLES
        if (!cisterna.isDisponible()) return;
        /////////////
        LocalTime horaRecepcionPedido = null;
        ArrayList<PuntoEntrega> solucionActual = cisterna.getRuta();
        int primerIndice, segundoIndice,segundosTrayecto;
        double distanciaActual = 0;
        ArrayList<Integer> puntos = new ArrayList<>();
        for (int i=0;i<solucionActual.size();i++) {
            puntos.add(solucionActual.get(i).getPunto());
        }
        for (int i = 0; i < puntos.size(); i++) {
            //Integer primerIndice = i - 1;
            //if (primerIndice == -1) primerIndice = solucionActual.size() - 1;
            //if (i==0) horaRecepcionPedido = this.coordinatesAndOrders.get(solucionActual.get(i+1).getPunto()).getHoraRecepcion();
            if (i==0) horaRecepcionPedido = this.horaLocal;
            primerIndice = puntos.get(i);
            if (i==puntos.size()-1) segundoIndice = 0;
            else segundoIndice = puntos.get(i+1);
            distanciaActual += matrizDistancia.get(primerIndice).get(segundoIndice);
            //SETEAR HORA DE ENTREGA DE PUNTO DE PEDIDO
            segundosTrayecto = (int)(distanciaActual * 3600)/50;
            if (i==0) cisterna.setHoraSalida(horaRecepcionPedido);
            if(i+2==puntos.size()) cisterna.setDuracionEntrega((distanciaActual*3600)/50);
            if (i==puntos.size()-1) cisterna.setHoraLlegada(horaRecepcionPedido.plusSeconds(segundosTrayecto));
            else solucionActual.get(i+1).setHoraEntregaCisterna(horaRecepcionPedido.plusSeconds(segundosTrayecto));
        }
    }


    public Double hallarConsumoPetroleo(ArrayList<ArrayList<Double>> matrizDistancia, ArrayList<PuntoEntrega> solucionActual,
                                        double pesoTotal) {
        //AHORA SE HALLA CUANTO PETROLEO VA GASTAR EN EL TRAYECTO
        LocalTime horaRecepcionPedido;
        double distanciaActual = 0,consumoPetroleo=0;
        int primerIndice, segundoIndice,segundosTrayecto;
        ArrayList<Integer> puntos = new ArrayList<>();
        for (int i=0;i<solucionActual.size();i++) {
            puntos.add(solucionActual.get(i).getPunto());
        }
        for (int i = 0; i < puntos.size(); i++) {
            //Integer primerIndice = i - 1;
            //if (primerIndice == -1) primerIndice = solucionActual.size() - 1;
            primerIndice = puntos.get(i);
            if (i==puntos.size()-1) segundoIndice = 0;
            else segundoIndice = puntos.get(i+1);
            distanciaActual += matrizDistancia.get(primerIndice).get(segundoIndice);
        }
        //FORMULA ( EN GALONES )
        consumoPetroleo = ((distanciaActual * pesoTotal) / 150);
//        System.out.println("RUTA A SACAR CONSUMO DE PETROLEO: "+puntos);
//        System.out.println("DISTANCIA: "+distanciaActual);
//        System.out.println("CONSUMO DE PETROLEO: "+consumoPetroleo);
        return BigDecimal.valueOf(consumoPetroleo).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

//    public ArrayList<ArrayList<Integer>> obtenerVecinos(ArrayList<Integer> solucion) {
//        ArrayList<ArrayList<Integer>> vecinos = new ArrayList<>();
//        int first = 1;
//        for (int i = 1; i < solucion.size(); i++) {
//            for (int j = i + 1; j < solucion.size(); j++) {
//                ArrayList<Integer> vecino = new ArrayList<Integer>(solucion);
//                vecino.set(i, solucion.get(j));
//                vecino.set(j, solucion.get(i));
//                vecinos.add(vecino);
//            }
//        }
//        return vecinos;
//    }

    public ArrayList<Integer> devolverPuntos(ArrayList<PuntoEntrega> pedidos) {
        ArrayList<Integer> puntos = new ArrayList<>();
        for (PuntoEntrega pedido : pedidos) {
            puntos.add(pedido.getPunto());
        }
        return puntos;
    }

    public double mutacionLocal(BestSolution copia, int cantidadCisternas,Map<Integer,Pedido> pedidos,
                                 ArrayList<ArrayList<Double>> distancias) {
        int index1 =-1,index2 = -1,index3,index4,ruta1,ruta2;
        double costoAnterior,costoNuevo,costo1,costo2,nuevoCostoTotal;
        PuntoEntrega punto1 = new PuntoEntrega();
        PuntoEntrega punto2 = new PuntoEntrega();
        do{
            index1 = retornaRandom(1,cantidadCisternas);
        }while(!copia.getRutas().containsKey(index1));
//        System.out.println("MUTACION LOCAL --- ERROR");
//        System.out.println("EL INDEX 1 ES (CAMION DE TIPO): "+index1);

        index2 = retornaRandomCisterna(copia.getRutas().get(index1));
//        System.out.println("EL INDEX 2 ES: "+index2);
//        System.out.println("LA RUTA DEL CAMION ES: "+devolverPuntos(copia.getRutas().get(index1).get(index2).getRuta()));
        index3 = retornaRandom(1,copia.getRutas().get(index1).get(index2).getRuta().size()-1);
        index4 = retornaRandom(1,copia.getRutas().get(index1).get(index2).getRuta().size()-1);
        if (index3==index4) return -1;
        //DEBE ESTAR DISPONIBLE
        if (!copia.getRutas().get(index1).get(index2).isDisponible()) return -1;
        else {
            ArrayList<PuntoEntrega> newPath = new ArrayList<>(copia.getRutas().get(index1).get(index2).getRuta());
            //ruta1 = copia.getRutas().get(index1).get(index2).getRuta().get(index3).getPunto();
            //ruta2 = copia.getRutas().get(index1).get(index2).getRuta().get(index4).getPunto();
            punto1  = copia.getRutas().get(index1).get(index2).getRuta().get(index3);
            punto2 = copia.getRutas().get(index1).get(index2).getRuta().get(index4);
            newPath.set(index3,punto2);
            newPath.set(index4,punto1);
            costoAnterior = copia.getRutas().get(index1).get(index2).getCosto();
            costoNuevo = hallarConsumoPetroleo(distancias,newPath,copia.getRutas().get(index1).get(index2).getPesoTotal());
            nuevoCostoTotal = this.nuevaSolucion.getCosto() - costoAnterior + costoNuevo;
            if (nuevoCostoTotal<this.nuevaSolucion.getCosto()) {
                copia.getRutas().get(index1).get(index2).getRuta().set(index3,punto2);
                copia.getRutas().get(index1).get(index2).getRuta().set(index4,punto1);
                copia.getRutas().get(index1).get(index2).setCosto(costoNuevo);
                setearHorasCisterna(distancias,copia.getRutas().get(index1).get(index2));
                copia.setCosto(nuevoCostoTotal);
                return nuevoCostoTotal;

            }
            else return -1;
        }
    }

    void homoGeneizarPuntos(ArrayList<PuntoEntrega> puntosEntregas,PuntoEntrega puntoEntrante){
        ArrayList<Integer> puntos = devolverPuntos(puntosEntregas);
        int index;
        if (puntos.contains(puntoEntrante)){
            index = puntos.indexOf(puntoEntrante.getPunto());
            puntosEntregas.get(index).setCantidad(puntosEntregas.get(index).getCantidad() + puntoEntrante.getCantidad());
            puntosEntregas.remove(puntos.lastIndexOf(puntoEntrante.getPunto()));
        }
    }

    double hallarTiempoEntrega(ArrayList<ArrayList<Double>> matrizDistancia,ArrayList<PuntoEntrega> solucionActual){
        int primerIndice, segundoIndice;
        double distanciaActual = 0;
        ArrayList<Integer> puntos = new ArrayList<>();
        for (int i=0;i<solucionActual.size();i++) {
            puntos.add(solucionActual.get(i).getPunto());
        }
        for (int i = 0; i < puntos.size()-1; i++) {
            //Integer primerIndice = i - 1;
            //if (primerIndice == -1) primerIndice = solucionActual.size() - 1;
            primerIndice = puntos.get(i);
            if (i==puntos.size()-1) segundoIndice = 0;
            else segundoIndice = puntos.get(i+1);
            distanciaActual += matrizDistancia.get(primerIndice).get(segundoIndice);
        }
        return (distanciaActual * 3600)/50;
    }

    int retornaRandomCisterna(Map<Integer,Cisterna> mapaCisternas){
        int tam1,contador=1,numRandom;
        tam1 = mapaCisternas.size();
        numRandom = retornaRandom(1,tam1);
        //for (Map.Entry<Integer, Map<Integer, Cisterna>> entry : rutas.entrySet())
        for (Map.Entry<Integer,Cisterna> entry: mapaCisternas.entrySet()){
            if (contador==numRandom) return entry.getKey();
            else contador++;
        }
        return 1;
    }

    public double mutacionGlobal(BestSolution copia, int cantidadCisternas,Map<Integer,Pedido> pedidos,
                                 ArrayList<ArrayList<Double>> distancias){
        int index1=-1,index2=-1,tam1,tam2,tam3,tam4,index3,index4,index5,index6,ruta1,ruta2,rutaAux;
        double costo1,costo2,costo1Anterior,costo2Anterior,nuevoCosto;
        double peso1Anterior,peso2Anterior,peso1TotalAnterior,peso2TotalAnterior;
        double nuevoPeso1,nuevoPeso2;
        double antiguoTiempo1,antiguoTiempo2,nuevoTiempo1,nuevoTiempo2,tiempoAlto;
        boolean primeroAlto = false,segundoAlto=false;
        String tipo1Anterior,tipo2Anterior;
        PuntoEntrega punto1 = new PuntoEntrega();
        PuntoEntrega punto2 = new PuntoEntrega();
        boolean onlyOne = (copia.getRutas().size()==1);
        if (onlyOne){
            for (int i=1;i<=cantidadCisternas;i++) {
                if (copia.getRutas().containsKey(i)) {
                    index1 = i;
                    index2 = i;
                    break;
                }
            }
        }
        else {
            do {
                index1 = retornaRandom(1,cantidadCisternas);
                index2 = retornaRandom(1,cantidadCisternas);
            } while ((!copia.getRutas().containsKey(index1) || !copia.getRutas().containsKey(index2)));
        }

        tam1 = copia.getRutas().get(index1).size();
        tam2 = copia.getRutas().get(index2).size();
//        index3 = retornaRandom(1,tam1);
//        index4 = retornaRandom(1,tam2);
        if (index1==index2 && tam1==1){
            //ENCONTRO UN CAMINO SIN SALIDA, NECESARIO SALIR
            return -1;
        }
        do {
            index3 = retornaRandomCisterna(copia.getRutas().get(index1));
            index4 = retornaRandomCisterna(copia.getRutas().get(index2));
//            System.out.println(index3);
//            System.out.println(index4);
//            System.out.println("NO SALGO DE LOOP");
//            if (index3==index4) System.out.println("IGUAL 1");
//            if (index1==index2) System.out.println("IGUAL 2");
        } while((index3==index4) && (index1==index2));
//        index3 = retornaRandomCisterna(copia.getRutas().get(index1));
//        index4 = retornaRandomCisterna(copia.getRutas().get(index2));
        //SE ESCOJIO CISTERNAS DISPONIBLES SINO RETORNAMOS
        if (!copia.getRutas().get(index1).get(index3).isDisponible() ||!copia.getRutas().get(index2).get(index4).isDisponible()){
            return -1;
        }

        //SUPONIENDO QUE SOLO SALE DEL ALMACEN PRINCIPAL
        tam3 = copia.getRutas().get(index1).get(index3).getRuta().size() -1;
        tam4 = copia.getRutas().get(index2).get(index4).getRuta().size() -1;
        index5 = retornaRandom(1,tam3);
        index6 = retornaRandom(1,tam4);
        //System.out.println(index1 + " " + index2 + " " + index3 + " " + index4 + " " + index5 + " " + index6);

        ////////////////
        ruta1 = copia.getRutas().get(index1).get(index3).getRuta().get(index5).getPunto();
        punto1 = copia.getRutas().get(index1).get(index3).getRuta().get(index5);
        //rutaAux = copia.getRutas().get(index1).get(index3).getRuta().get(index5);

        ruta2 = copia.getRutas().get(index2).get(index4).getRuta().get(index6).getPunto();
        punto2 = copia.getRutas().get(index2).get(index4).getRuta().get(index6);
        ArrayList<PuntoEntrega> firstPath = new ArrayList<>();
        for (int i=0;i<copia.getRutas().get(index1).get(index3).getRuta().size();i++) {
            PuntoEntrega punto = new PuntoEntrega(copia.getRutas().get(index1).get(index3).getRuta().get(i).getPunto(),
                    copia.getRutas().get(index1).get(index3).getRuta().get(i).getCantidad());
            firstPath.add(punto);
        }
        ArrayList<PuntoEntrega> secondPath = new ArrayList<>();
        for (int i=0;i<copia.getRutas().get(index2).get(index4).getRuta().size();i++) {
            PuntoEntrega punto = new PuntoEntrega(copia.getRutas().get(index2).get(index4).getRuta().get(i).getPunto(),
                    copia.getRutas().get(index2).get(index4).getRuta().get(i).getCantidad());
            secondPath.add(punto);
        }
        firstPath.set(index5,punto2);
        secondPath.set(index6,punto1);
        homoGeneizarPuntos(firstPath,punto2);
        homoGeneizarPuntos(secondPath,punto1);
        nuevoPeso1 = copia.getRutas().get(index1).get(index3).getPesoBruto() +
                ((double)(punto2.getCantidad() + copia.getRutas().get(index1).get(index3).getCarga() - punto1.getCantidad())/2);

        nuevoPeso2 = copia.getRutas().get(index2).get(index4).getPesoBruto() +
                ((double)(punto1.getCantidad() + copia.getRutas().get(index2).get(index4).getCarga() - punto2.getCantidad())/2);
        costo1 = hallarConsumoPetroleo(distancias,firstPath,nuevoPeso1);
        costo2 = hallarConsumoPetroleo(distancias,secondPath,nuevoPeso2);
        //HALLANDO TIEMPOS
//        antiguoTiempo1 = hallarTiempoEntrega(distancias,copia.getRutas().get(index1).get(index3).getRuta());
//        antiguoTiempo2 = hallarTiempoEntrega(distancias,copia.getRutas().get(index2).get(index4).getRuta());
        antiguoTiempo1 = copia.getRutas().get(index1).get(index3).getDuracionEntrega();
        antiguoTiempo2 = copia.getRutas().get(index2).get(index4).getDuracionEntrega();
        nuevoTiempo1 = hallarTiempoEntrega(distancias,firstPath);
        nuevoTiempo2 = hallarTiempoEntrega(distancias,secondPath);
        /////////////////////////////////////////
        costo1Anterior = copia.getRutas().get(index1).get(index3).getCosto();
        costo2Anterior = copia.getRutas().get(index2).get(index4).getCosto();
        peso1Anterior = copia.getRutas().get(index1).get(index3).getPesoBruto();
        peso2Anterior = copia.getRutas().get(index2).get(index4).getPesoBruto();
        peso1TotalAnterior = copia.getRutas().get(index1).get(index3).getPesoTotal();
        peso2TotalAnterior = copia.getRutas().get(index2).get(index4).getPesoTotal();
        nuevoCosto = this.nuevaSolucion.getCosto() - costo1Anterior-costo2Anterior + costo1 + costo2;
        if (antiguoTiempo1>14400) primeroAlto = true;
        if(antiguoTiempo2>14400) segundoAlto = true;
        //LOS DOS TIEMPOS SON ALTOS
        if (primeroAlto && segundoAlto) return -1;
        //NO HAY CAPACIDAD NI PETROLEO PARA HACER LA MTACION
        if(punto2.getCantidad() + copia.getRutas().get(index1).get(index3).getCarga() - punto1.getCantidad() >
                this.cisternas.get(index1).getCapacidad() || punto1.getCantidad() + copia.getRutas().get(index2).get(index4).getCarga()
                - punto2.getCantidad() > this.cisternas.get(index2).getCapacidad() || costo1>25 || costo2>25)
            return -1;

        //POLITICA DE 4 HORAS MINIMO : 4*3600 = 14400 segundos --> PRIORIDAD
        if((primeroAlto && nuevoTiempo1<antiguoTiempo1 && nuevoTiempo2<=14000) || (
                segundoAlto && nuevoTiempo2<antiguoTiempo2 && nuevoTiempo1<=14400)
                ){
            //TOMAR POLITICA DE HORAS COMO PRIORIDAD
            System.out.println("CAMBIO GLOBAL POR TIEMPO");
            System.out.println("anterior tiempo 1:" + antiguoTiempo1);
            System.out.println("anterior tiempo 2:" + antiguoTiempo2);
            System.out.println("nuevo tiempo 1:" + nuevoTiempo1);
            System.out.println("nuevo tiempo 2:" + nuevoTiempo2);
            copia.getRutas().get(index1).get(index3).setRuta(firstPath);
            copia.getRutas().get(index2).get(index4).setRuta(secondPath);
            copia.getRutas().get(index1).get(index3).setCosto(costo1);
            copia.getRutas().get(index2).get(index4).setCosto(costo2);
            copia.getRutas().get(index1).get(index3).setCarga(
                    punto2.getCantidad() + copia.getRutas().get(index1).get(index3).getCarga() - punto1.getCantidad());
            copia.getRutas().get(index2).get(index4).setCarga(
                    punto1.getCantidad() + copia.getRutas().get(index2).get(index4).getCarga() - punto2.getCantidad());
            copia.getRutas().get(index1).get(index3).setPesoTotal(peso1Anterior +
                    ((double) (copia.getRutas().get(index1).get(index3).getCarga()) / 2));
            copia.getRutas().get(index2).get(index4).setPesoTotal(peso2Anterior +
                    ((double) (copia.getRutas().get(index2).get(index4).getCarga()) / 2));
            //SETEAMOS HORAS

            setearHorasCisterna(distancias,copia.getRutas().get(index1).get(index3));
            setearHorasCisterna(distancias,copia.getRutas().get(index2).get(index4));
            copia.setCosto(nuevoCosto);
            return nuevoCosto;

        }
        //EN PEDIDOS, INDEX 2 ES CANTIDAD PEDIDA
        //PARA PRIMER CISTERNA INTERCAMBIADO
//        if(pedidos.get(ruta2).get(2) + copia.getRutas().get(index1).get(index3).getCarga() - pedidos.get(ruta1).get(2) >
//                this.cisternas.get(index1).getCapacidad() || pedidos.get(ruta1).get(2) + copia.getRutas().get(index2).get(index4).getCarga()
//                - pedidos.get(ruta2).get(2) > this.cisternas.get(index2).getCapacidad())  {
//            return -1;
//        }
        //ArrayList<PuntoEntrega> firstPath = new ArrayList<>(copia.getRutas().get(index1).get(index3).getRuta());
        //ArrayList<PuntoEntrega> secondPath = new ArrayList<>(copia.getRutas().get(index2).get(index4).getRuta());

        tipo1Anterior = copia.getRutas().get(index1).get(index3).getTipoCodigo();
        tipo2Anterior = copia.getRutas().get(index2).get(index4).getTipoCodigo();
        //ULTIMO: OPTIMIZACION DE RUTAS
        if (nuevoCosto < this.nuevaSolucion.getCosto()){
            System.out.println("ANTERIOR RUTA 1:" + devolverPuntos(copia.getRutas().get(index1).get(index3).getRuta()));
            System.out.println("ANTERIOR RUTA 2:" + devolverPuntos(copia.getRutas().get(index2).get(index4).getRuta()));
            System.out.println("ANTEIOR COSTO 1:" + costo1Anterior);
            System.out.println("ANTEIOR COSTO 2:" + costo2Anterior);
            System.out.println("NUEVA RUTA 1:" + devolverPuntos(firstPath));
            System.out.println("NUEVA RUTA 2:" + devolverPuntos(secondPath));
            System.out.println("NUEVO COSTO 1:" + costo1);
            System.out.println("NUEVO COSTO 2:" + costo2);
            System.out.println("ANTERIOR PESO 1 " + peso1TotalAnterior);
            System.out.println("ANTERIOR PESO 2 " + peso2TotalAnterior);
            System.out.println("NUEVO PESO 1 " + nuevoPeso1);
            System.out.println("NUEVO PESO 2 " + nuevoPeso2);
            //copia.getRutas().get(index1).get(index3).getRuta().set(index5,punto2);
            //copia.getRutas().get(index2).get(index4).getRuta().set(index6,punto1);
            copia.getRutas().get(index1).get(index3).setRuta(firstPath);
            copia.getRutas().get(index2).get(index4).setRuta(secondPath);
            copia.getRutas().get(index1).get(index3).setCosto(costo1);
            copia.getRutas().get(index2).get(index4).setCosto(costo2);
            copia.getRutas().get(index1).get(index3).setCarga(
                    punto2.getCantidad() + copia.getRutas().get(index1).get(index3).getCarga() - punto1.getCantidad());
            copia.getRutas().get(index2).get(index4).setCarga(
                    punto1.getCantidad() + copia.getRutas().get(index2).get(index4).getCarga() - punto2.getCantidad());
            //copia.getRutas().get(index1).get(index3).setPesoBruto(peso2Anterior);
            //copia.getRutas().get(index2).get(index4).setPesoBruto(peso1Anterior);
            //copia.getRutas().get(index1).get(index3).setTipoCodigo(tipo2Anterior);
            //copia.getRutas().get(index2).get(index4).setTipoCodigo(tipo1Anterior);
            copia.getRutas().get(index1).get(index3).setPesoTotal(peso1Anterior +
                    ((double) (copia.getRutas().get(index1).get(index3).getCarga()) / 2));
            copia.getRutas().get(index2).get(index4).setPesoTotal(peso2Anterior +
                    ((double) (copia.getRutas().get(index2).get(index4).getCarga()) / 2));
            //SETEAMOS HORAS
            setearHorasCisterna(distancias,copia.getRutas().get(index1).get(index3));
            setearHorasCisterna(distancias,copia.getRutas().get(index2).get(index4));
            copia.setCosto(nuevoCosto);
            return nuevoCosto;
        }
        else return -1;
    }

    public void hallarMejorSolucion(ArrayList<ArrayList<Double>> distancias,Map<Integer,Pedido> coordinatesAndOrders,
                                    HashMap<Integer, Cisternas> cisternas,BestSolution nuevaSolucion){
         int numIteraciones = 50000,cantidadCisternas = cisternas.size();
         double nuevoCosto;
         BestSolution copiaSolucion = new BestSolution (nuevaSolucion.getCosto(),nuevaSolucion.getRutas());
         for (int i=0;i<=numIteraciones;i++) {

            nuevoCosto = mutacionGlobal(copiaSolucion,cantidadCisternas,coordinatesAndOrders,distancias);
            if(nuevoCosto < this.nuevaSolucion.getCosto() && (nuevoCosto!=-1)) {
                //System.out.println("SOLUCION ANTIGUA = " + this.nuevaSolucion.getCosto());
                //this.nuevaSolucion = new BestSolution(copiaSolucion.getCosto(), copiaSolucion.getRutas());
                this.nuevaSolucion.setCosto(copiaSolucion.getCosto());
                this.nuevaSolucion.setRutas(copiaSolucion.getRutas());
                System.out.println("MTUACION GLOBAL TRIGGERED: NUEVO COSTO = " + nuevoCosto);
                //System.out.println("SOLUCION NUEVA = " + this.nuevaSolucion.getCosto());
            }
            //BestSolution copiaSolucion2 = new BestSolution (nuevaSolucion.getCosto(),nuevaSolucion.getRutas());
            nuevoCosto = mutacionLocal(copiaSolucion,cantidadCisternas,coordinatesAndOrders,distancias);
             if(nuevoCosto < this.nuevaSolucion.getCosto() && (nuevoCosto!=-1)) {
                 //System.out.println("SOLUCION ANTIGUA = " + this.nuevaSolucion.getCosto());
                 //this.nuevaSolucion = new BestSolution(copiaSolucion.getCosto(), copiaSolucion.getRutas());
                 this.nuevaSolucion.setCosto(copiaSolucion.getCosto());
                 this.nuevaSolucion.setRutas(copiaSolucion.getRutas());
                 System.out.println("MTUACION LOCAL TRIGGERED: NUEVO COSTO = " + nuevoCosto);
                 //System.out.println("SOLUCION NUEVA = " + this.nuevaSolucion.getCosto());
             }
         }

    }
//    public BestSolution hallarMejorVecino(ArrayList<ArrayList<Double>> distancias, ArrayList<ArrayList<Integer>> vecinos) {
//        double mejorDistancia = hallarDistancia(distancias, vecinos.get(0));
//        ArrayList<Integer> mejorVecino = vecinos.get(0);
//        for (ArrayList<Integer> vecino : vecinos) {
//            double distanciaActual = hallarDistancia(distancias, vecino);
//            if (distanciaActual < mejorDistancia) {
//                mejorDistancia = distanciaActual;
//                mejorVecino = vecino;
//            }
//        }
//        return new BestSolution(mejorVecino, mejorDistancia);
//    }

    public void impresionPedidos(Map<Integer, Pedido> coordinatesAndOrders) {
        for (int i=1;i<coordinatesAndOrders.size();i++) {
            if(coordinatesAndOrders.get(i).getHoraEntrega()==null) break;
            System.out.println("PEDIDO NUM: "+i);
            System.out.println("HORA DE RECEPCION: "+coordinatesAndOrders.get(i).getHoraRecepcion());
            System.out.println("HORA DE ENTREFA FINAL: "+coordinatesAndOrders.get(i).getHoraEntrega());
        }
    }

    public void habilitarCisternas(BestSolution nuevaSolucion){
        int comparacion,tamaño;
        double costoTotal=0;
        if (nuevaSolucion.getRutas()==null) return;
        for (int i=1;i<=cisternas.size();i++){
            if (nuevaSolucion.getRutas().containsKey(i)){
                for (Iterator<Map.Entry<Integer,Cisterna>> it=nuevaSolucion.getRutas().get(i).entrySet().iterator();it.hasNext();){
                    Map.Entry<Integer,Cisterna> entry = it.next();
                        comparacion = entry.getValue().getHoraLlegada()
                                .compareTo(horaLocal);
                        if (comparacion<=0){
                            System.out.println("LA HORA LOCAL ES: "+this.horaLocal);
                            System.out.println("SOY EL CAMION: "+entry.getKey()+" DE TIPO: "+ entry.getValue().getTipoCodigo()+
                                    " Y MI HORA DE LLEGADA FUE: "+entry.getValue().getHoraLlegada() );
                            costoTotal+= entry.getValue().getCosto();
//                            ArrayList<PuntoEntrega> puntitos = new ArrayList<>();
//                            PuntoEntrega puntito = new PuntoEntrega();
//                            puntito.setPunto(0);
//                            puntito.setCantidad(-1);
//                            puntitos.add(puntito);
//                            nuevaSolucion.getRutas().get(i).get(j).setRuta(puntitos);
//                            nuevaSolucion.getRutas().get(i).get(j).setDisponible(true);
//                            nuevaSolucion.getRutas().get(i).get(j).setCosto(0);
//                            nuevaSolucion.getRutas().get(i).get(j).setCarga(0);
//                            nuevaSolucion.getRutas().get(i).get(j)
//                                    .setPesoTotal(nuevaSolucion.getRutas().get(i).get(j).getPesoBruto());
                            it.remove();
                            this.costoTotal += costoTotal;
                        }
                }
                tamaño = nuevaSolucion.getRutas().get(i).size();
                if (nuevaSolucion.getRutas().get(i).size()==0) nuevaSolucion.getRutas().remove(i);
            }
        }
    }


    public void runAlgorithm() throws FileNotFoundException {
        int noHayFlota;
        int intervalo = 5,contador=1,totalPedido=1;
        int pedidoInf=-1,pedidoSup=-1;
        boolean nuevoDia=false;
        //LEYENDO DATOS
        createMatrix();
        readVehicles();
        //DIA INICIAL
        this.diaLocal = 1;
        //HILL CLIMBING
        while(coordinatesAndOrders.containsKey(contador)){
            pedidoInf = contador;
            for (int i=1;i<intervalo;i++){
                if (!coordinatesAndOrders.containsKey(contador+i)) break;
                pedidoSup = contador+i;
                if (coordinatesAndOrders.get(contador+i).getDiaRecepcion()!=this.diaLocal){
                    nuevoDia = true;
                    break;
                }
            }
            if (nuevoDia) contador = pedidoSup;
            else contador+=(intervalo+1);
            //SETEAMOS LA HORA DEL SISTEMA
            this.horaLocal = coordinatesAndOrders.get(pedidoSup).getHoraRecepcion();
            this.diaLocal = coordinatesAndOrders.get(pedidoSup).getDiaRecepcion();
            //LAS CISTERNAS QUE LLEGARON A LA CENTRAL ESTAN DISPONIBLES
            habilitarCisternas(nuevaSolucion);
            //CREAMOS SOLUCION RANDOM
            noHayFlota = crearSolucionRandom(distancias,coordinatesAndOrders,cisternas,pedidoInf,pedidoSup);
            //UTILIZAMOS LAS MUTACIONES LOCALES Y GLOBALES
            if(this.nuevaSolucion.getRutas()!=null){
                //BUCLE INFINITO DENTRO?
                hallarMejorSolucion(distancias,coordinatesAndOrders,cisternas,nuevaSolucion);
                //IMPRIMIMOS RUTAS Y SETEAMOS LAS CISTERNAS A NO DISPONIBLE
                impresionRutas(cisternas,nuevaSolucion);
            }
            //LLENAMOS EL MAP DE PEDIDOS CON LAS HORAS DE ENTREGA
            llenarPedidoHoraEntrega(coordinatesAndOrders,nuevaSolucion);
            //NUEVO DIA, HABILITAMOS SISTERMAS PARA EVITAR FALLAS
            if (nuevoDia){
                DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                this.horaLocal = LocalTime.parse("23:59:59",parseFormat);
                habilitarCisternas(nuevaSolucion);
            }
            nuevoDia = false;
            //SI NO SE PUEDE METER UN NUEVO CAMION POR COMBUSTIBLE O FALTA DE FLOTA
            if (noHayFlota!=-1) {
                System.out.println("EL PEDIDO NUMERO "+noHayFlota+" NO HA PODIDO SER ASIGNADO POR INSUFICIENCIA DE FLOTA O FALTA" +
                        "DE COMBUSTIBLE");
                break;
            }
        }
        impresionPedidos(coordinatesAndOrders);
        System.out.println("EL COSTO TOTAL DE LA OPERACION ES: "+this.costoTotal);



        // = crearSolucionRandom(distancias,coordinatesAndOrders,cisternas);






//        double distanciaActual = hallarDistancia(distancias,solucionActual);
//        ArrayList<ArrayList<Integer>> vecinos = obtenerVecinos(solucionActual);
//
//        nuevaSolucion = hallarMejorVecino(distancias,vecinos);
//
//        while( nuevaSolucion.getDistancia() < distanciaActual) {
//            solucionActual = nuevaSolucion.getRuta();
//            distanciaActual = nuevaSolucion.getDistancia();
//            vecinos = obtenerVecinos(solucionActual);
//            nuevaSolucion = hallarMejorVecino(distancias,vecinos);
//        }
//
//        nuevaSolucion.addFinalPoint();
//        System.out.println(nuevaSolucion.getRuta());
//        System.out.println(nuevaSolucion.getDistancia());
//
    }
}