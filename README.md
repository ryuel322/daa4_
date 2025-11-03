# SmartCampus Scheduling Project  
### Assignment 4 

------
Project Structure:
src/
├── main/java/
│ ├── graph/
│ │ ├── scc/SCCTarjan.java
│ │ ├── topo/TopologicalSort.java
│ │ └── dagsp/DAGShortestPaths.java
│ ├── metrics/Metrics.java
│ ├── util/DataGenerator.java
│ 
│
├── test/java/graph/
│ └── BasicTests.java
│
└── resources/
data/

------
for run
mvn clean compile
mvn test
java -cp "target/classes;target/dependency/*" util.DataGenerator this generates 9 datasets in data directory that includes json and csv

Category	Nodes (n)	Edges	Type	Variants
Small	6–10	low	cyclic / DAG	3
Medium	10–20	mixed	several SCCs	3
Large	20–50	dense	performance tests	3

Dataset	n	Edges	SCC Count	Time (ms)	Critical Path
small_1	8	12	2	1.2	9
medium_2	15	26	3	3.4	14
large_3	45	122	5	14.9	27

Analysis
Dense graphs higher SCC merge cost, more DFS calls.
Sparse DAGs faster topo + DP, lower relaxations.
Larger SCCs slightly reduce total condensation nodes, but increase preprocessing.
Tarjan’s SCC condensation best for cyclic dependencies.
Topo ordering enables efficient DP for DAG shortest paths.
DP on topo order is simple, fast, and well-suited for scheduling-type tasks.
Performance scales linearly with edges (O(V + E)) for DAG-SP.
