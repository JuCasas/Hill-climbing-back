package com.dp1.route4d;

import com.dp1.route4d.algorithm.HillClimbing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class Route4D_HillClimbing {

	public static void main(String[] args) throws FileNotFoundException {
		SpringApplication.run(Route4D_HillClimbing.class, args);
		FileWriter myWriter = null;
		try {
			File myObj = new File("src/main/resources/resultados2.txt");
			if (myObj.createNewFile()) {
				myWriter = new FileWriter(myObj);

			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		long startTime;
		long endTime;
		int iteraciones = 100;
		HillClimbing hill = new HillClimbing();

		for (int i = 0; i < iteraciones; i++) {
			startTime = System.currentTimeMillis();
			hill.runAlgorithm();
			endTime = System.currentTimeMillis() - startTime;
			try {
				myWriter.write(i + "," + hill.getCostoTotal() + ", " + endTime + "," + 0 + "\n");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
			hill.setCostoTotal(0);
		}
		try {
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}
}
