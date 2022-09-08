package ru.vsu.cs.course1.graph.demo;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Permutations {
    private static boolean contains(int[] arr, int el, int ind) {
        for (int i = 0; i <= ind; i++) {
            if (arr[i] == el){
                return true;
            }
        }
        return false;
    }

    private static void recursion(int[] arr, int[] answer, int index, int k, Consumer<int[]> addToList) {
        if (index == k) {
            addToList.accept(answer);
            return;
        }

        for (int j : arr) {
            if (!contains(answer, j, index - 1)) {
                answer[index] = j;
                recursion(arr, answer, index + 1, k, addToList);
            }
        }
    }

    private static void solution(int[] arr, int k, Consumer<int[]> addToList) {
        int[] answer = new int[k];

        recursion(arr, answer, 0, k, addToList);
    }

    public static ArrayList<int[]> permutationK(int[] vertex, int k){
        ArrayList<int[]> ways = new ArrayList<>();
        Consumer<int[]> addToList = (int[] answer) -> {
            ways.add(answer.clone());
        };
        solution(vertex, k, addToList);
        System.out.println(ways.size());
        return ways;
    }
}
