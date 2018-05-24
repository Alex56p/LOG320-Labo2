package com.company;

import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BacktrackingSolver {

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 9;
    private final int UNASSIGNED_VALUE = 0;

    private List<List<Integer>> grid = new ArrayList<>();
    private int numberOfCall = 0;

    public BacktrackingSolver(String filePath) {
        try {
            for(String line : Files.readAllLines(Paths.get(filePath))) {
                List<Integer> numbers = new ArrayList<>();
                for (String number : line.split("(?!^)")) {
                    numbers.add(Integer.parseInt(number));
                }
                grid.add(numbers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean Solve() {
        numberOfCall++;
        Pair<Integer, Integer> unassignedCase = findNextUnassignedLocation();

        if(unassignedCase == null)
            return true;

        for(int i = MIN_VALUE; i <= MAX_VALUE; i++) {
            if(isValid(unassignedCase.getKey(), unassignedCase.getValue(), i)) {
                grid.get(unassignedCase.getKey()).set(unassignedCase.getValue(), i);

                if(Solve()) {
                    return true;
                }

                grid.get(unassignedCase.getKey()).set(unassignedCase.getValue(), UNASSIGNED_VALUE);
            }
        }

        return false;
    }

    /**
     * Returns the next unassined case in the grid. Returns null if all the cases are assigned.
     * @return
     */
    public Pair<Integer, Integer> findNextUnassignedLocation() {
        for (int i = 0; i < grid.size(); i++) {
            for(int j = 0; j < grid.get(i).size(); j++) {
                if(grid.get(i).get(j) == UNASSIGNED_VALUE) {
                    return new Pair<>(i, j);
                }
            }
        }

        return null;
    }

    /**
     * Returns if the position is valid with the specified value
     *
     * @param row
     * @param col
     * @param value
     * @return
     */
    public boolean isValid(int row, int col, int value) {
        // Row
        for(List<Integer> rows : grid) {
            if(rows.get(col) == value)
                return false;
        }

        // Col
        List<Integer> targetRow = grid.get(row);
        for(int i : targetRow) {
            if(i == value) {
                return false;
            }
        }

        // Box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(grid.get(i + boxRow).get(j + boxCol) == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public void Print() {
        for(List<Integer> row : grid) {
            StringBuilder stringRow = new StringBuilder();
            for(int number : row) {
                stringRow.append(number);
            }

            System.out.println(stringRow);
        }

        System.out.println(numberOfCall);
    }
}
