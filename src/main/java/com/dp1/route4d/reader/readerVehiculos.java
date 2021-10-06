package com.dp1.route4d.reader;

import com.dp1.route4d.model.Cisternas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class readerVehiculos {
    public HashMap<Integer, Cisternas> dataCisternas = createMapDataCisternas();

    public readerVehiculos() throws FileNotFoundException {}

    public HashMap<Integer,Cisternas> createMapDataCisternas() throws FileNotFoundException {
        HashMap <Integer,Cisternas> dataCisternas = new HashMap<>();
        File file = new File("src/main/resources/cisternas.txt");
        Scanner scan = new Scanner(file);
        int typeNumber=1,capacity,quantity;
        double pesoBruto;
        String type;
        //LEEMOS ENCABEZADO
        scan.nextLine();
        while(scan.hasNextLine()){
            String content = scan.nextLine();
            if (content.length() <1) break;
            else {
                Cisternas cisternas = new Cisternas();
                type = content.substring(0,2);
                pesoBruto = Double.parseDouble(content.substring(3,6));
                capacity = Integer.parseInt(content.substring(7,9));
                quantity = Integer.parseInt(content.substring(10,12));
                cisternas.setCapacidad(capacity);
                cisternas.setCantidad(quantity);
                cisternas.setPesoBruto(pesoBruto);
                cisternas.setTipo(type);
                dataCisternas.put(typeNumber,cisternas);
                typeNumber+=1;
            }
        }
        scan.close();
        return dataCisternas;
    }

    public HashMap<Integer, Cisternas> getDataCisternas() {
        return dataCisternas;
    }


    public void setDataCisternas(HashMap<Integer, Cisternas> dataCisternas) {
        this.dataCisternas = dataCisternas;
    }
}
