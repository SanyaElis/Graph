package ru.vsu.cs.course1.graph.graphic;

public class Edge {
    public int vertex1;
    public int vertex2;
    public int weight;

    public Edge(int vertex1, int vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (vertex1 != edge.vertex1) return false;
        return vertex2 == edge.vertex2;
    }

    @Override
    public int hashCode() {
        int result = vertex1;
        result = 31 * result + vertex2;
        return result;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "vertex1=" + vertex1 +
                ", vertex2=" + vertex2 +
                '}';
    }
}
