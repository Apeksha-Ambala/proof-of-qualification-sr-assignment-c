import com.example.KMeanCluster;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a test class for K Mean Cluster algorithm program
 *
 * @author Apeksha Ambala
 * @since 1.0
 * @version 1.0
 */
@PrepareForTest
public class TestKMeansAlgorithm {

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testMinimalWeightOfGraph() throws Exception {
    List<Double[]> vectorSet = new ArrayList<>();
    vectorSet.add(new Double[] {0.1, 0.55, 12.2, 1.55});
    vectorSet.add(new Double[] {2.3, 4.0, 1.8, 0.33});
    vectorSet.add(new Double[] {0.2, 0.56, 12.3, 1.56});
    vectorSet.add(new Double[] {3.1, 3.9, 1.6, 0.5});

    int m = 4, n = 4, k = 2;
    double d = 0.001;

    KMeanCluster kMeanCluster = new KMeanCluster(vectorSet, k, n, d);
    Whitebox.invokeMethod(kMeanCluster, "buildCluster");
    HashMap<String, Double[]> clusterCentroidValues =
        Whitebox.invokeMethod(kMeanCluster, "getCentroidValues");
    HashMap<String, List<Double[]>> clusters = Whitebox.invokeMethod(kMeanCluster, "getClusters");

    Assertions.assertNotNull(clusterCentroidValues);
    Assertions.assertNotNull(clusters);
  }
}
