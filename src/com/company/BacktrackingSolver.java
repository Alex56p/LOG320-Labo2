package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BacktrackingSolver {

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 9;
    private final int NON_ASSIGNED_VALUE = 0;

    private List<int[]> grid = new ArrayList<>();

    public BacktrackingSolver(String filePath) {
        try {
            for(String line : Files.readAllLines(Paths.get(filePath))) {
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Solve(List<String[]> grid) {

    }

    public boolean isValid(int i, int j, int value) {

    }
}
