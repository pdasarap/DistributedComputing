import java.io.*;
//writing edge to output stram heance serializable,
public class Edge implements Comparable<Edge>, Serializable {

	int weight;
	int vertex1;
	int vertex2;
	EdgeStatus edgeStatus;
	String edgeHostname;
	int edgePort;

	//basic edge initially
	public Edge() {
		this.edgeStatus = EdgeStatus.BASIC;
	}

	//edge between 2 vertices
	public Edge(int weight, int vertex1, int vertex2) {
		this.weight = weight;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.edgeStatus = EdgeStatus.BASIC;
	}

	//edge created from reading file
	public Edge(int weight, int vertex1, int vertex2, String host, int port) {
		this.weight = weight;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.edgeStatus = EdgeStatus.BASIC;
		this.edgeHostname = host;
		this.edgePort = port;
	}
/*setters and getters */
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getVertex1() {
		return vertex1;
	}

	public void setVertex1(int vertex1) {
		this.vertex1 = vertex1;
	}

	public int getVertex2() {
		return vertex2;
	}

	public void setVertex2(int vertex2) {
		this.vertex2 = vertex2;
	}

	public EdgeStatus getEdgeStatus() {
		return edgeStatus;
	}

	public void setEdgeStatus(EdgeStatus edgeStatus) {
		this.edgeStatus = edgeStatus;
	}

	public String getEdgeHostname() {
		return edgeHostname;
	}

	public void setEdgeHostname(String edgeHostname) {
		this.edgeHostname = edgeHostname;
	}

	public int getEdgePort() {
		return edgePort;
	}

	public void setEdgePort(int edgePort) {
		this.edgePort = edgePort;
	}

	@Override
	public String toString() {
		return "Edge [ vertex1=" + vertex1 + ", vertex2=" + vertex2 + ", weight=" + weight +"]";
	}
	@Override
	public int compareTo(Edge e) {
		int diff = Integer.compare(weight, e.weight);
		if (diff == 0) {
			diff = Integer.compare(getVertex1(), e.getVertex1());
			return diff == 0 ? Integer.compare(getVertex2(), e.getVertex2()) : diff;
		} else {
			return diff;
		}
	}

}
