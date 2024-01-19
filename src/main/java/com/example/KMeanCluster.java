package com.example;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

  public KMeanCluster(List<Double[]> data, int k, int n, Double d) {
    this.data = data;
    this.k = k;
    this.n = n;
    this.d = d;
    this.initialCentroid();
  }

  /** This Method is used to create an initial centroid by randomly selecting k vectors */
  private void initialCentroid() {
    clusters = new HashMap<>();
    nanObject = new Double[n];
    Arrays.fill(nanObject, Double.NaN);
    centroidValues = new HashMap<>();
    Double[] previousValue = new Double[n];
    for (int i = 0; i < k; i++) {
      List<Double[]> list = new ArrayList<>();
      int randomIndex = getRandomIndex(0, data.size());
      Double[] initialValues = data.get(randomIndex);

      if (i != 0) {
        if (Arrays.equals(previousValue, initialValues)) {
          do {
            // System.out.println("Same index, change");
            initialValues = data.get(getRandomIndex(0, data.size()));
          } while (Arrays.equals(previousValue, initialValues));
        }
      }

      previousValue = Arrays.copyOf(initialValues, initialValues.length);
      String clusterId = String.valueOf(i);
      centroidValues.put(clusterId, initialValues);

      clusters.put(clusterId, list);
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
    return new Random().nextInt(max - min) + min;
  }

  /**
   * This Method is used to create a cluster using k mean algorithm
   *
   * @return HashMap containing cluster index in String and the centroid vector in Double []
   */
  public HashMap<String, Double[]> buildCluster() {
    createFirstCluster(data);
    return reorganiseCluster();
  }

  /**
   * This Method is used to reorganize the cluster as many times it is required
   *
   * @return HashMap containing cluster index in String and the centroid vector in Double []
   */
  private HashMap<String, Double[]> reorganiseCluster() {
    boolean breakLoop = false;
    do {
      HashMap<String, Double[]> oldCentroidValues = new HashMap<>(centroidValues);

      for (String key : clusters.keySet()) {
        List<Double[]> tmpData = clusters.get(key);

        Double[] clusterValues = centroidValues.get(key);
        Double[] newCentriodValues = new Double[clusterValues.length];
        Arrays.fill(newCentriodValues, 0.0d);
        for (Double[] data1 : tmpData) {
          for (int i = 0; i < newCentriodValues.length; i++) {
            newCentriodValues[i] = newCentriodValues[i] + data1[i];
          }
        }

        for (int i = 0; i < newCentriodValues.length; i++) {
          newCentriodValues[i] = newCentriodValues[i] / tmpData.size();
          if (newCentriodValues[i].equals(Double.NaN)) {
            // System.out.println("It is nan object, assign it as zero.");
            newCentriodValues[i] = 0.0;
          }
        }

        // update the value in centroids
        centroidValues.put(key, newCentriodValues);
        reassignCluster(tmpData, key);
      }
      // Check if loop needs to continue
      breakLoop = checkIfContinueLoop(oldCentroidValues, centroidValues);
      oldCentroidValues = new HashMap<>(centroidValues);
    } while (!breakLoop);

    return centroidValues;
  }

  /**
   * This Method is used to reassign the cluster
   *
   * @param tmpData - list of vectors
   * @param oldKey - old index / key
   */
  private void reassignCluster(List<Double[]> tmpData, String oldKey) {
    List<Integer> indexToBeRemoved = new ArrayList<>();

    for (int i = 0; i < tmpData.size(); i++) {
      Double[] dataValues = tmpData.get(i);
      Double finalClusterDistance = 0d;
      String finalCluster = getFinalCluster(dataValues, finalClusterDistance);

      if (!finalCluster.equalsIgnoreCase(oldKey) && finalClusterDistance > d) {
        List<Double[]> organizedCluster = clusters.get(finalCluster);

        if (organizedCluster == null) {
          organizedCluster = new ArrayList<>();
        }

        organizedCluster.add(dataValues);
        indexToBeRemoved.add(i);
      }
    }

    for (int i = 0; i < indexToBeRemoved.size(); i++) {
      tmpData.remove(i);
    }
  }

  /**
   * This Method is used to check if the loop should keep reorganizing the cluster or not
   *
   * @param clusterValues - old centroid values
   * @param newCentroidValues - old index / key
   * @return the boolean value
   */
  private boolean checkIfContinueLoop(
      HashMap<String, Double[]> clusterValues, HashMap<String, Double[]> newCentroidValues) {
    AtomicBoolean returnVal = new AtomicBoolean(true);

    newCentroidValues.forEach(
        (key, value) -> {
          if (Arrays.equals(nanObject, value)) {
            // System.out.println("It is nanObject. - it will continue the loop");
            returnVal.set(false);
          }
        });

    if (!returnVal.get()) {
      return false;
    }

    if (clusterValues.entrySet().stream()
            .allMatch(e -> Arrays.equals(e.getValue(), newCentroidValues.get(e.getKey())))
        && !newCentroidValues.containsValue(nanObject)) {
      // System.out.println("Returning true - it will break the loop");
      return true;
    }
    //  System.out.println("Returning false - it will continue the loop");
    return false;
  }

  /**
   * This Method is used to create first cluster
   *
   * @param tmpData - list of vectors
   */
  private void createFirstCluster(List<Double[]> tmpData) {
    for (Double[] dataValues : tmpData) {
      Double finalClusterDistance = 0d;
      String finalCluster = getFinalCluster(dataValues, finalClusterDistance);
      List<Double[]> organizedData = clusters.get(finalCluster);
      if (organizedData == null) {
        organizedData = new ArrayList<>();
      }
      organizedData.add(dataValues);
    }
  }

  /**
   * This Method is used to check Euclidean Distance and create a final cluster
   *
   * @param dataValues - data values
   * @param finalClusterDistance - use to store the distance
   * @return the final cluster
   */
  private String getFinalCluster(Double[] dataValues, Double finalClusterDistance) {
    Double distance = Double.MAX_VALUE;
    /*
    Euclidean Distance:
    the square root of the sum of the squared differences between the two vectors
    */
    String finalCluster = "";
    for (String clusterIndex : centroidValues.keySet()) {
      Double[] centroids = centroidValues.get(clusterIndex);
      double sSD = 0.00d;
      for (int k = 0; k < dataValues.length; k++) {
        double tmpDistance = (centroids[k] - dataValues[k]);
        double distanceSquare = Math.pow(tmpDistance, 2);
        sSD = sSD + distanceSquare;
      }

      finalClusterDistance = Math.sqrt(sSD);

      if (finalClusterDistance.compareTo(distance) < 0) {
        distance = finalClusterDistance;
        finalCluster = clusterIndex;
      }
    }

    // To return this value to check with value of d:
    finalClusterDistance = distance;

    return finalCluster;
  }
}
