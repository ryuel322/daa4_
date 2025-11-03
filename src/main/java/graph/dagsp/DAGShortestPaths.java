package graph.dagsp;

import metrics.Metrics;

import java.util.*;

/**
 * DAG shortest/longest path utilities. Expects edge-weighted directed graph.
 */
public class DAGShortestPaths {
    private final int n;
    private final List<List<long[]>> g; // edge as [to, w]
    private final Metrics metrics;
    private static final long INF = Long.MAX_VALUE / 4;

    public DAGShortestPaths(int n, List<List<long[]>> g, Metrics metrics){ this.n=n; this.g=g; this.metrics=metrics; }

    private List<Integer> topoOrder(){
        int[] indeg = new int[n];
        for(int u=0;u<n;u++) for(long[] e: g.get(u)) indeg[(int)e[0]]++;
        Deque<Integer> q = new ArrayDeque<>();
        for(int i=0;i<n;i++) if(indeg[i]==0) q.add(i);
        List<Integer> topo = new ArrayList<>();
        while(!q.isEmpty()){
            int u=q.remove(); topo.add(u);
            for(long[] e: g.get(u)){
                indeg[(int)e[0]]--;
                if(indeg[(int)e[0]]==0) q.add((int)e[0]);
            }
        }
        return topo;
    }

    public long[] shortestFrom(int s){
        List<Integer> topo = topoOrder();
        long[] dist = new long[n]; Arrays.fill(dist, INF); dist[s]=0;
        int[] parent = new int[n]; Arrays.fill(parent, -1);
        for(int u: topo){
            if(dist[u]==INF) continue;
            for(long[] e: g.get(u)){
                int v = (int)e[0]; long w = e[1];
                metrics.increment("dagsp_relax_attempt");
                if(dist[v] > dist[u] + w){ dist[v] = dist[u] + w; parent[v]=u; metrics.increment("dagsp_relax_success"); }
            }
        }
        return dist;
    }

    public PathResult longestPath(){
        List<Integer> topo = topoOrder();
        long[] dp = new long[n]; Arrays.fill(dp, Long.MIN_VALUE/4);
        int[] parent = new int[n]; Arrays.fill(parent, -1);
        int[] indeg = new int[n]; for(int u=0;u<n;u++) for(long[] e: g.get(u)) indeg[(int)e[0]]++;
        for(int i=0;i<n;i++) if(indeg[i]==0) dp[i]=0;
        for(int u: topo){
            if(dp[u]==Long.MIN_VALUE/4) continue;
            for(long[] e: g.get(u)){
                int v=(int)e[0]; long w=e[1];
                metrics.increment("dag_long_relax");
                if(dp[v] < dp[u] + w){ dp[v] = dp[u] + w; parent[v]=u; }
            }
        }
        long max = Long.MIN_VALUE; int at = -1;
        for(int i=0;i<n;i++) if(dp[i]>max){ max=dp[i]; at=i; }
        List<Integer> path = new ArrayList<>();
        if(at!=-1){ while(at!=-1){ path.add(at); at=parent[at]; } Collections.reverse(path); }
        return new PathResult(max, path);
    }

    public static class PathResult{ public final long length; public final List<Integer> path; public PathResult(long l, List<Integer> p){this.length=l;this.path=p;} }
}
