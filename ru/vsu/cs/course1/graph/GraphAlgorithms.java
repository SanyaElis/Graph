package ru.vsu.cs.course1.graph;

import ru.vsu.cs.course1.graph.demo.Permutations;

import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Consumer;

public class GraphAlgorithms {

    /**
     * Поиск в глубину, реализованный рекурсивно
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void dfsRecursion(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];

        class Inner {
            void visit(Integer curr) {
                visitor.accept(curr);
                visited[curr] = true;
                for (Integer v : graph.adjacency(curr)) {
                    if (!visited[v]) {
                        visit(v);
                    }
                }
            }
        }
        new Inner().visit(from);
    }

    /**
     * Поиск в глубину, реализованный с помощью стека
     * (не совсем "правильный"/классический, т.к. "в глубину" реализуется только "план" обхода, а не сам обход)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void dfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(from);
        visited[from] = true;
        while (!stack.empty()) {
            Integer curr = stack.pop();
            visitor.accept(curr);
            for (Integer v : graph.adjacency(curr)) {
                if (!visited[v]) {
                    stack.push(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * Поиск в ширину, реализованный с помощью очереди
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @param visitor Посетитель
     */
    public static void bfs(Graph graph, int from, Consumer<Integer> visitor) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(from);
        visited[from] = true;
        while (queue.size() > 0) {
            Integer curr = queue.remove();
            visitor.accept(curr);
            for (Integer v : graph.adjacency(curr)) {
                if (!visited[v]) {
                    queue.add(v);
                    visited[v] = true;
                }
            }
        }
    }

    /**
     * Поиск в глубину в виде итератора
     * (начальная вершина также включена)
     * @param graph граф
     * @param from Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> dfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Stack<Integer> stack = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                stack = new Stack<>();
                stack.push(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! stack.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = stack.pop();
                        for (Integer adj : graph.adjacency(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                stack.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    /**
     * Поиск в ширину в виде итератора
     * (начальная вершина также включена)
     * @param from Вершина, с которой начинается поиск
     * @return Итератор
     */
    public static Iterable<Integer> bfs(Graph graph, int from) {
        return new Iterable<Integer>() {
            private Queue<Integer> queue = null;
            private boolean[] visited = null;

            @Override
            public Iterator<Integer> iterator() {
                queue = new LinkedList<>();
                queue.add(from);
                visited = new boolean[graph.vertexCount()];
                visited[from] = true;

                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return ! queue.isEmpty();
                    }

                    @Override
                    public Integer next() {
                        Integer result = queue.remove();
                        for (Integer adj : graph.adjacency(result)) {
                            if (!visited[adj]) {
                                visited[adj] = true;
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }
    public static class Result {
        public int[] way;
        public Double weight;

        public Result(int[] way, Double weight) {
            this.way = way;
            this.weight = weight;
        }
        public Double getWeight() {
            return weight;
        }

    }

    public static List<Result> findWayAB(WeightedGraph graph, int from, int to){
        ArrayList<Integer> way = new ArrayList<>();
        List<Result> ways = new ArrayList<>();
        class Inner {
            void visit(Integer curr, ArrayList<Integer> way) {
                way.add(curr);
                for (WeightedGraph.WeightedEdgeTo v : graph.adjacencyWithWeights(curr)) {
                    if (v.to() != to && !way.contains(v.to())) {
                        visit(v.to(), (ArrayList<Integer>) way.clone());
                    } else if (v.to() == to){
                        int[] currWay = new int[way.size() + 1];
                        currWay[way.size()] = to;
                        int i = 0;
                        for (Integer vertex: way) {
                            currWay[i] = vertex;
                            i++;
                        }
                        double weight = 0;
                        for (int j = 0; j < currWay.length - 1; j++) {
                            weight += graph.getWeight(currWay[j], currWay[j+1]);
                        }
                        ways.add(new Result(currWay, weight));
                    }
                }
            }
        }
        new Inner().visit(from, way);
        ways.sort(Comparator.comparing(Result::getWeight));
        return ways;
    }


    public static List<Result> solution(WeightedGraph graph, int from, int to){
        List<Result> answer = new ArrayList<>();
        int n = graph.vertexCount();

        if (graph.getWeight(from, to) != null){
            answer.add(new Result(null, graph.getWeight(from, to)));
        }
        if (n - 2 != 0){
            int[] ver = new int[n - 2];
            int k = 0;
            for (int i = 0; i < n; i++) {
                if (i == from || i  == to ){
                    continue;
                }
                ver[k] = i;
                k++;
            }
            System.out.println("============================================");
            System.out.println(Arrays.toString(ver));
            System.out.println("============================================");

            for (int i = 1; i <= n - 2; i++) {
                ArrayList<int[]> ways = Permutations.permutationK(ver, i);
                for (int[] way: ways){
                    Double weight = Double.MAX_VALUE;
                    boolean adjacent = true;
                    if (graph.getWeight(from, way[0]) != null && graph.getWeight(way[i - 1], to) != null){
                        weight = graph.getWeight(from, way[0]) + graph.getWeight(way[i - 1], to);
                    } else {
                        continue;
                    }
                    if (way.length > 1){
                        for (int j = 0; j < way.length - 1; j++) {
                            if (graph.getWeight(way[j], way[j+1]) != null){
                                weight += graph.getWeight(way[j], way[j+1]);
                            } else {
                                adjacent = false;
                                break;
                            }
                        }
                    }
                    if (adjacent){
                        answer.add(new Result(way, weight));
                    }
                }
            }
        }
        answer.sort(Comparator.comparing(Result::getWeight));
        for (Result way: answer) {
            System.out.print(from + " -> ");
            if (way.way != null){
                for (int num: way.way) {
                    System.out.print(num + " -> ");
                }
            }
            System.out.print(to + "\n");
            System.out.println("Длина пути = " + way.weight);
        }
        return answer;
    }

    public static class MeetingPlace {
        public int[] point;
        public double from;
        double time;

        public MeetingPlace(int[] point, double from, double time) {
            this.point = point;
            this.from = from;
            this.time = time;
        }
    }
    public static boolean canRobotCome(WeightedGraph graph, MeetingPlace place, int from, int speed){
        if (place.point.length == 1){
            List<Result> ways = findWayAB(graph, from, place.point[0]);
            for (Result way: ways) {
                if (way.weight / speed == place.time){
                    System.out.println("Третий робот в позиции " + from);
                    System.out.println("За " + place.time + " секунд(ы)");
                    System.out.println(Arrays.toString(way.way));
                    return true;
                }
            }
        }
        if (place.point.length == 2){
            List<Result> ways1 = findWayAB(graph, from, place.point[0]);
            List<Result> ways2 = findWayAB(graph, from, place.point[1]);
            for (Result way: ways1) {
                if (way.weight + place.from / speed == place.time){
                    System.out.println("Третий робот в позиции " + from);
                    System.out.println("За " + place.time + " секунд(ы)");
                    System.out.println(Arrays.toString(way.way));
                    return true;
                }
            }
            for (Result way: ways2) {
                if (way.weight + graph.getWeight(place.point[0], place.point[1]) - place.from / speed == place.time){
                    System.out.println("Третий робот в позиции " + from);
                    System.out.println("За " + place.time + " секунд(ы)");
                    System.out.println(Arrays.toString(way.way));
                    return true;
                }
            }
        }
        return false;
    }
    public static MeetingPlace findPoint(WeightedGraph graph, Result result, int firstSpeed, int secondSpeed){
        double time = result.weight / (firstSpeed + secondSpeed);
        double length = time * firstSpeed;
        int[] way = result.way;
        int k = 0;
        while (length > 0){
           length -= graph.getWeight(way[k], way[k + 1]);
           if (length == 0){
               return new MeetingPlace(new int[] {way[k + 1]}, 0, time);
           }
           k++;
        }
        return new MeetingPlace(new int[] {way[k- 1], way[k]}, length + graph.getWeight(way[k-1], way[k]), time);
    }

    public static int[] findPlace(WeightedGraph graph, int[] speed, int[] place) {
        if (place.length == 2) {
            List<Result> ways = findWayAB(graph, place[0], place[1]);
            if (ways.size() == 0) {
                System.out.println("Роботы не могут встретится :( :( :( :(");
            } else {
                Result answer = ways.get(0);
                MeetingPlace where = findPoint(graph, answer, speed[0], speed[1]);
                System.out.println(where.from);
                return where.point;
            }
        }
        if (place.length == 3) {
            List<Result> ways12 = findWayAB(graph, place[0], place[1]);
            List<Result> ways23 = findWayAB(graph, place[1], place[2]);
            List<Result> ways13 = findWayAB(graph, place[0], place[2]);
            if (ways12.size() == 0 && ways23.size() == 0 || ways13.size() == 0 && ways23.size() == 0 || ways12.size() == 0 && ways13.size() == 0) {
                System.out.println("Роботы не могут встретится :( :( :( :(");
                return new int[]{};
            }
            for (Result way : ways12) {
                MeetingPlace where = findPoint(graph, way, speed[0], speed[1]);
                if (canRobotCome(graph, where, place[2], speed[2])) {
                    System.out.println(Arrays.toString(way.way));
                    System.out.println(where.from);
                    return where.point;
                }
            }
            for (Result way : ways23) {
                MeetingPlace where = findPoint(graph, way, speed[1], speed[2]);
                if (canRobotCome(graph, where, place[0], speed[0])) {
                    System.out.println(Arrays.toString(way.way));
                    System.out.println(where.from);
                    return where.point;
                }
            }
            for (Result way : ways13) {
                MeetingPlace where = findPoint(graph, way, speed[0], speed[2]);
                if (canRobotCome(graph, where, place[1], speed[1])) {
                    System.out.println(Arrays.toString(way.way));
                    System.out.println(where.from);
                    return where.point;
                }
            }
        }
        return new int[]{};
    }
}
