package com.company;

import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BacktrackingSolver {

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 9;
    private final int UNASSIGNED_VALUE = 0;

    private List<List<Integer>> grid = new ArrayList<>();
    private int numberOfCall = 0;
    private List<List<List<Integer>>> possibilities = new ArrayList<>();

    public BacktrackingSolver(String filePath) {
        try {
            for(String line : Files.readAllLines(Paths.get(filePath))) {
                List<Integer> numbers = new ArrayList<>();
                for (String number : line.split("(?!^)")) {
                    numbers.add(Integer.parseInt(number));
                }
                grid.add(numbers);
            }

            for (int i = 0; i < 9; ++i)
            {
                possibilities.add(new ArrayList<>());

                for (int j = 0; j < 9; ++j)
                {
                    possibilities.get(i).add(new ArrayList<>());

                    if (grid.get(i).get(j) == UNASSIGNED_VALUE)
                    {
                        for (int k = 1; k <= 9; ++k)
                        {
                            if (isValid(i, j, k))
                            {
                                possibilities.get(i).get(j).add(k);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean Solve() {
        numberOfCall++;

        List<Pair<Pair<Integer, Integer>, Integer>> modifications = new ArrayList<>();
        List<Pair<Integer, Integer>> numbersAdded = new ArrayList<>();
        boolean numberPlaced = true;

        while (numberPlaced)
        {
            numberPlaced = false;

            for (int i = 0; i < 9; ++i)
            {
                for (int j = 0; j < 9; ++j)
                {
                    if (grid.get(i).get(j) == UNASSIGNED_VALUE)
                    {
                        List<Integer> elementsToRemove = new ArrayList<>();

                        for (int k : possibilities.get(i).get(j))
                        {
                            if (!isValid(i, j, k))
                            {
                                elementsToRemove.add(k);
                                modifications.add(new Pair<>(new Pair<>(i, j), k));
                            }
                        }

                        for (int element : elementsToRemove)
                        {
                            possibilities.get(i).get(j).remove(new Integer(element));
                        }

                        if (grid.get(i).get(j) == UNASSIGNED_VALUE && possibilities.get(i).get(j).size() == 1)
                        {
                            grid.get(i).set(j, possibilities.get(i).get(j).get(0));
                            modifications.add(new Pair<>(new Pair<>(i, j), possibilities.get(i).get(j).get(0)));
                            possibilities.get(i).get(j).remove(0);
                            numbersAdded.add(new Pair<>(i, j));
                            numberPlaced = true;
                        }
                    }
                }
            }
        }

        Pair<Integer, Integer> unassignedCase = findNextUnassignedLocation();

        if(unassignedCase == null)
            return true;

        List<Integer> nbToTry = new ArrayList<>(possibilities.get(unassignedCase.getKey()).get(unassignedCase.getValue()));

        possibilities.get(unassignedCase.getKey()).get(unassignedCase.getValue()).clear();

        for(int number : nbToTry) {
            grid.get(unassignedCase.getKey()).set(unassignedCase.getValue(), number);

            if(Solve()) {
                return true;
            }
        }

        grid.get(unassignedCase.getKey()).set(unassignedCase.getValue(), UNASSIGNED_VALUE);
        possibilities.get(unassignedCase.getKey()).set(unassignedCase.getValue(), new ArrayList<>(nbToTry));


        for (Pair<Pair<Integer, Integer>, Integer> modif : modifications)
        {
            possibilities.get(modif.getKey().getKey()).get(modif.getKey().getValue()).add(modif.getValue());
        }

        for (Pair<Integer, Integer> nbAdded : numbersAdded)
        {
            grid.get(nbAdded.getKey()).set(nbAdded.getValue(), UNASSIGNED_VALUE);
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
            List<Integer> rowList = grid.get(i + boxRow);
            for(int j = 0; j < 3; j++) {
                if(rowList.get(j + boxCol) == value) {
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

    //Fonctione qui vérifier que la grille complète est valide.
    //À utiliser pour des tests unitaires seulement.
    //NE PAS TOUCHER
    boolean isValid()
    {
        List<Integer> validList = new ArrayList<>();
        for (int i = 1; i <= 9; ++i)
        {
            validList.add(i);
        }

        List<List<Integer>> lists = new ArrayList<>();

        for (List<Integer> l : grid)
        {
            if (l.contains(0))
            {
                return false;
            }

            List newLine = new ArrayList(l);
            lists.add(newLine);
        }


        List<List<Integer>> squares = new ArrayList<>();

        for (int i = 0; i < 9; ++i)
        {
            squares.add(new ArrayList<>());
        }

        for (int i = 0; i < 9; ++i)
        {
            List<Integer> column = new ArrayList<>();

            for (int j = 0; j < 9; ++j)
            {
                squares.get(i/3*3+j/3).add(grid.get(i).get(j));
                column.add(grid.get(j).get(i));
            }

            lists.add(column);
        }

        lists.addAll(squares);

        for (List<Integer> list : lists)
        {
            Collections.sort(list);

            if (!validList.equals(list))
            {
                return false;
            }
        }

        return true;
    }
}
