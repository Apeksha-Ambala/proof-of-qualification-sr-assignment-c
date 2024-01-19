package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Main Class for K Mean Algorithm Program
 *
 * @author Apeksha Ambala
 * @since 1.0
 * @version 1.0
 */

/** Takes Input from file and process it to apply k-mean algorithm */
public class KMeansAlgorithm {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  /**
   * This Method is main method of this program
   *
   * @param args - arguments passed in cmd
   */
  public static void main(String[] args) {
    BufferedReader reader;
    Pattern pattern = Pattern.compile("[\\s]+");
    boolean isFirstLine = true;
    int m = 0, n = 0, k = 0;
    double d = 0;
    List<Double[]> vectorSet = new ArrayList<>();
    Double[] vector;
    int iterationCount = 0;

    // Read data from file
    try {
      String path = Paths.get("").toAbsolutePath().toString();
      String filePath = path + "/src/main/resources/input.txt";
      reader = new BufferedReader(new FileReader(filePath));
      String line = reader.readLine();

      while (line != null) {
        iterationCount++;
        // read next line
        line = line.trim().replaceAll("\\s{2,}", " ");

        if (isFirstLine) {
          // The first line of the file contains the values of M, N, K and d,
          // represented as three integer values and one floating-point value,
          // separated by one or more blanks
          String[] splitLine = line.split("\\s+");
          m = Integer.parseInt(splitLine[0]);
          n = Integer.parseInt(splitLine[1]);
          k = Integer.parseInt(splitLine[2]);
          d = Double.parseDouble(splitLine[3]);

          isFirstLine = false;
          line = reader.readLine();
          continue;
        }

        // The next M lines contain uniform values of vectors from the set,
        // represented as N floating-point values, separated by one or more blanks
        vector = pattern.splitAsStream(line).map(Double::parseDouble).toArray(Double[]::new);
        vectorSet.add(vector);

        line = reader.readLine();
      }
      reader.close();
    } catch (Exception e) {
      System.out.println("Exception occurred. Check your input. Aborting.");
      System.exit(0);
      // e.printStackTrace();
    }

    // The program should terminate if the algorithm does not converge for more than
    // pre-defined number of iterations, represented as a constant in code.
    if (iterationCount - 1 != m) { // after removing 1st header line
      System.out.println(
          "Insufficient input provided. Program expects "
              + m
              + " vector sets, and user provided "
              + (iterationCount - 1)
              + " vector sets. Aborting");
      System.exit(0);
    }

    // Create cluster
    try {
      KMeanCluster kMeanCluster = new KMeanCluster(vectorSet, k, n, d);
      HashMap<String, Double[]> clusterCentroidValues = kMeanCluster.buildCluster();

      // Program outputs:
      //
      // 1.  K lines with one integer and N floating-point values, representing the cluster index
      //      and the centroid vector
      //      Example:
      //      0 0.28 0.33 1.14 3.85
      //      1 3.66 9.21 5.39 0.02
      clusterCentroidValues.forEach(
          (key, value) -> {
            System.out.print(key);
            for (double dValue : value) {
              System.out.print(" " + df.format(dValue));
            }
            System.out.println();
          });

      // 2.  a blank line
      System.out.println();

      // 3. M lines with one integer and N floating-point values, representing the cluster index of
      // a vector and the vector from the initial set.
      HashMap<String, List<Double[]>> clusters = kMeanCluster.getClusters();

      for (Double[] doubles : vectorSet) {
        for (Map.Entry<String, List<Double[]>> entry : clusters.entrySet()) {
          if (entry.getValue().contains(doubles)) {
            System.out.print(entry.getKey());
            for (double dValue : doubles) {
              System.out.print(" " + dValue);
            }
            System.out.println();
            break;
          }
        }
      }

    } catch (Exception e) {
      System.out.println("Exception occurred. Aborting.");
      System.exit(0);
      // e.printStackTrace();
    }
  }
}
