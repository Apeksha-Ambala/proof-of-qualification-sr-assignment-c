package com.example;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * This is a supporting class for K Mean Cluster algorithm program
 *
 * @author Apeksha Ambala
 * @since 1.0
 * @version 1.0
 */
@Getter
@Setter
public class KMeanCluster {
  private List<Double[]> data;
  private HashMap<String, List<Double[]>> clusters;
  private HashMap<String, Double[]> centroidValues;
  private int k; // number of cluster
  private int n; // N-dimensional vectors
  private Double d; // small constant
  private Double[] nanObject;
  private static int numberOfRuns = 50;
  private static double clusterDistanceError;
  private int[] newRow;

  public KMeanCluster(List<Double[]> data, int k, int n, Double d) {
    this.data = data;
    this.k = k;
    this.n = n;
    this.d = d;
    this.clusters = new HashMap<>();
  }

  /** This Method is used to create an initial centroid by randomly selecting k vectors */
  private void initialCentroid() {
    List<Double[]> tmpData = new ArrayList<>(data);
    centroidValues = new HashMap<>();

    for (int i = 0; i < k; i++) {
      int randomIndex = getRandomIndex(i, data.size());
      Double[] initialValues = tmpData.get(randomIndex);
      tmpData.set(randomIndex, tmpData.get(data.size() - i - 1));

      String clusterId = String.valueOf(i);
      centroidValues.put(clusterId, initialValues);
    }
  }

  /**
   * This Method is used to get a random index using minimum and maximum values
   *
   * @param min - minimum value
   * @param max - maximum value
   * @return integer with random index
   */
  public int getRandomIndex(int min, int max) {
    return new Random().nextInt(max - min);
  }

  /** This Method is used to create a cluster using k mean algorithm */
  public void buildCluster() {
    HashMap<String, Double[]> properCentroid = new HashMap<>();
    HashMap<String, List<Double[]>> properCluster = new HashMap<>();
    int[] properNewRow = new int[0];
    double acceptedClusterDistance = Double.MAX_VALUE;
    int counter = numberOfRuns;

    while (counter > 0) {
      initialCentroid();

      clusterDistanceError = Double.MAX_VALUE;
      reorganiseCluster();

      if (clusterDistanceError < acceptedClusterDistance) {
        acceptedClusterDistance = clusterDistanceError;
        properCentroid = centroidValues;
        properNewRow = newRow;
        properCluster = new HashMap<>(clusters);
      }

      counter--;
    }

    clusterDistanceError = acceptedClusterDistance;
    centroidValues = properCentroid;
    newRow = properNewRow;
    clusters = new HashMap<>(properCluster);
  }

  /** This Method is used to reorganize the cluster as many times it is required */
  private void reorganiseCluster() {
    double prevDistError;
    HashMap<String, List<Double[]>> tmpClusters = new HashMap<>();

    do {
      Double tmpDistance, minVal, tmpDistError = 0D;
      int minCluster, newRowVal = 0;
      newRow = new int[data.size()];

      // for each vector v from the set
      for (int v = 0; v < data.size(); v++) {
        minCluster = 0;
        minVal = Double.MAX_VALUE;
        Double[] key = data.get(v);

        // for each centroid ci
        for (int i = 0; i < k; i++) {
          // compute D( v, ci )
          tmpDistance = calculateDistance(key, centroidValues.get(String.valueOf(i)));
          if (tmpDistance < minVal) {
            minVal = tmpDistance;
            minCluster = i;
          }
        }
        newRow[v] = minCluster;
      }

      reassignCentroid();

      prevDistError = clusterDistanceError;

      // Calculate Distance Error
      for (int i = 0; i < data.size(); i++) {
        newRowVal = newRow[i];
        tmpDistError +=
            calculateDistance(data.get(i), centroidValues.get(String.valueOf(newRowVal)));
      }
      clusterDistanceError = tmpDistError;
    } while (!checkIfContinueLoop(prevDistError));
  }

  /**
   * This Method is used to calculate the sum of two array of type Double
   *
   * @param arr1 - array 1
   * @param arr2 - array 2
   * @return the sum of two array
   */
  public Double[] sumArray(Double[] arr1, Double[] arr2) {
    Double[] arr3 = new Double[arr2.length];
    int counter = 0;
    for (Double num1 : arr1) {
      arr3[counter] = num1 + arr2[counter];
      counter++;
    }
    return arr3;
  }

  /** This Method is used to reassign centroid */
  private void reassignCentroid() {
    int[] clusterSize = new int[k];
    HashSet<Integer> unassignedCentroid = new HashSet<>();
    centroidValues.clear();
    centroidValues = new HashMap<>();
    Double[] arr1 = new Double[n];
    Arrays.fill(arr1, 0D);
    for (int i = 0; i < k; i++) {
      centroidValues.put(String.valueOf(i), arr1);
    }

    clusters.clear();

    int counter = 0;

    // for each class i
    for (int i = 0; i < data.size(); i++) {
      clusterSize[newRow[i]]++;

      List<Double[]> tmpList = new ArrayList<>();
      if (clusters.get(String.valueOf(newRow[i])) != null) {
        tmpList.addAll(clusters.get(String.valueOf(newRow[i])));
      }
      tmpList.add(data.get(i));
      clusters.put(String.valueOf(newRow[i]), tmpList);
      centroidValues.put(
          String.valueOf(newRow[i]),
          sumArray(centroidValues.get(String.valueOf(newRow[i])), data.get(i)));

      counter++;
    }

    for (int i = 0; i < k; i++) {
      if (clusterSize[i] == 0) {
        unassignedCentroid.add(i);
      } else {
        centroidValues.put(
            String.valueOf(i), divideArray(centroidValues.get(String.valueOf(i)), clusterSize[i]));
      }
    }

    if (unassignedCentroid.size() != 0) {
      HashSet<Double[]> assignedCentroid = new HashSet<Double[]>(k - unassignedCentroid.size());
      for (int i = 0; i < k; i++) {
        if (!unassignedCentroid.contains(i)) {
          assignedCentroid.add(centroidValues.get(String.valueOf(i)));
        }
      }

      for (int i : unassignedCentroid) {
        while (true) {
          int randomIndex = new Random().nextInt(data.size());
          Double[] dataRandom = data.get(randomIndex);
          if (!assignedCentroid.contains(dataRandom)) {
            assignedCentroid.add(dataRandom);
            centroidValues.put(String.valueOf(i), dataRandom);
            break;
          }
        }
      }
    }
  }

  /**
   * This Method is used to get the array divided with a number
   *
   * @param doubles - array of type Double
   * @param number - number to divide with
   * @return the array after division with number
   */
  private Double[] divideArray(Double[] doubles, int number) {
    Double[] newArray = new Double[doubles.length];
    for (int i = 0; i < doubles.length; i++) {
      newArray[i] = doubles[i] / number;
    }
    return newArray;
  }

  /**
   * This Method is used to calculate the distance
   *
   * @param key - array of type Double
   * @param doubles - array of type Doubles
   * @return the calculated distance
   */
  private Double calculateDistance(Double[] key, Double[] doubles) {
    double distance = 0D;
    for (int i = 0; i < key.length; i++) {
      distance += Math.abs((key[i] - doubles[i]) * (key[i] - doubles[i]));
    }
    return distance;
  }

  /**
   * This Method is used to check if the loop should keep reorganizing the cluster or not
   *
   * @param prevDistError - old distance error
   * @return the boolean value
   */
  private boolean checkIfContinueLoop(double prevDistError) {
    return d > (1 - (clusterDistanceError / prevDistError));
  }
}
