package graph;

import graph.scc.SCCTarjan;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPaths;
import metrics.Metrics;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Basic deterministic tests: SCC, Topological order, DAG shortest paths.
 */
public class BasicTests {

    @Test
    public void testSCCSimple() {
        // Build graph: 0->1,1->2,2->0 (cycle), 3 isolated
        int n = 4;
        List<List<int[]>> g = new ArrayList<>();
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        g.get(0).add(new int[]{1,1});
        g.get(1).add(new int[]{2,1});
        g.get(2).add(new int[]{0,1});
        // node 3 has no edges

        Metrics m = new Metrics();
        SCCTarjan tarjan = new SCCTarjan(n, g, m);
        List<List<Integer>> comps = tarjan.run();
        // Expect 2 components: one size 3 and one size 1
        Assert.assertEquals(2, comps.size());
        boolean foundSize3 = comps.stream().anyMatch(c -> c.size() == 3);
        boolean foundSize1 = comps.stream().anyMatch(c -> c.size() == 1);
        Assert.assertTrue(foundSize3 && foundSize1);
    }

    @Test
    public void testTopologicalSimple() {
        // DAG: 0->1->2
        int n = 3;
        List<List<Integer>> g = new ArrayList<>();
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        g.get(0).add(1);
        g.get(1).add(2);

        Metrics m = new Metrics();
        TopologicalSort topo = new TopologicalSort(n, g, m);
        List<Integer> order = topo.kahnOrder();
        // Only valid topo order is [0,1,2]
        Assert.assertEquals(3, order.size());
        Assert.assertEquals(Integer.valueOf(0), order.get(0));
        Assert.assertEquals(Integer.valueOf(1), order.get(1));
        Assert.assertEquals(Integer.valueOf(2), order.get(2));
    }

    @Test
    public void testDAGShortestPaths() {
        // DAG with weights:
        // 0 -> 1 (2), 0 -> 2 (6), 1 -> 2 (3)
        int n = 3;
        List<List<long[]>> g = new ArrayList<>();
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        g.get(0).add(new long[]{1, 2});
        g.get(0).add(new long[]{2, 6});
        g.get(1).add(new long[]{2, 3});

        Metrics m = new Metrics();
        DAGShortestPaths dsp = new DAGShortestPaths(n, g, m);
        long[] dist = dsp.shortestFrom(0);
        // distances: 0->0 = 0, 0->1 = 2, 0->2 = 5 (via 1)
        Assert.assertEquals(0L, dist[0]);
        Assert.assertEquals(2L, dist[1]);
        Assert.assertEquals(5L, dist[2]);
    }
}
