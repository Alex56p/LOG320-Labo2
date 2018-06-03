package com.company;

import javafx.util.Pair;
import org.omg.CORBA.Current;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BacktrackingSolver {

    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 9;
    private final int BOX_SIZE = 3;
    private final int UNASSIGNED_VALUE = 0;

    private List<List<Integer>> grid = new ArrayList<>();
    private List<List<List<Integer>>> possibilities = new ArrayList<>();
    private int numberOfCall = 0;

    public BacktrackingSolver(String filePath) {
        try {
            for (final String line : Files.readAllLines(Paths.get(filePath))) {
                List<Integer> numbers = new ArrayList<>(9);
                for (final String number : line.split("(?!^)")) {
                    numbers.add(Integer.parseInt(number));
                }
                grid.add(numbers);
            }

            for (int i = 0; i < MAX_VALUE; ++i) {
                possibilities.add(new ArrayList<>());
                final List<List<Integer>> row = possibilities.get(i);
                for (int j = 0; j < MAX_VALUE; ++j) {
                    row.add(new ArrayList<>());
                    final List<Integer> col = possibilities.get(i).get(j);
                    if (grid.get(i).get(j) == UNASSIGNED_VALUE) {
                        for (int k = MIN_VALUE; k <= MAX_VALUE; ++k) {
                            if (isValid(i, j, k)) {
                                col.add(k);
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
        CheckWithPossibilities();
        Pair<Integer, Integer> unassignedCase = findNextUnassignedLocation();
        if (unassignedCase == null)
            return true;

        final int x = unassignedCase.getKey();
        final int y = unassignedCase.getValue();
        final List<Integer> casePossibilities = possibilities.get(x).get(y);

        for (int i = 0; i < casePossibilities.size(); i++) {
            int possibility = casePossibilities.get(i);
            if(isValid(x,y, possibility)) {
                grid.get(x).set(y, possibility);

                if (Solve()) {
                    return true;
                }
                grid.get(x).set(y, UNASSIGNED_VALUE);
            }
        }

        return false;
    }


    private void CheckWithPossibilities() {
        List<Pair<Pair<Integer, Integer>, Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < MAX_VALUE; i++) {
            final List<List<Integer>> row = possibilities.get(i);
            for (int j = 0; j < MAX_VALUE; j++) {
                final List<Integer> col = row.get(j);
                if (col.size() == 1) {
                    final int possibility = col.get(0);
                    grid.get(i).set(j, possibility);
                    toRemove.add(new Pair<>(new Pair<>(i, j), possibility));
                }
                else {
                    CheckLinePossibility(i);
                    CheckColumnPossibility(j);
                    CheckSectionPossibility(i, j);
                }
                for (Pair<Pair<Integer, Integer>, Integer> aToRemove : toRemove) {
                    RemovePossibility(aToRemove.getKey().getKey(), aToRemove.getKey().getValue(), aToRemove.getValue());
                }
            }
        }
    }

    private void CheckLinePossibility(int x) {
        final List<List<Integer>> CurrentRow = possibilities.get(x);
        List<Integer> CheckedNumbers = new ArrayList<>();
        List<Pair<Pair<Integer, Integer>, Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < MAX_VALUE; i++) {
            for (final int k : CurrentRow.get(i)) {
                if (!CheckedNumbers.contains(k) && isOnlyLinePossibility(x, i, k)) {
                    grid.get(x).set(i, k);
                    toRemove.add(new Pair<>(new Pair<>(x, i), k));
                }
                CheckedNumbers.add(k);
            }
            for (Pair<Pair<Integer, Integer>, Integer> aToRemove : toRemove) {
                RemovePossibility(aToRemove.getKey().getKey(), aToRemove.getKey().getValue(), aToRemove.getValue());
            }
        }
    }

    private void CheckColumnPossibility(int y) {
        List<Integer> CheckedNumbers = new ArrayList<>();
        List<Pair<Pair<Integer, Integer>, Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < MAX_VALUE; i++) {
            for (final int k : possibilities.get(i).get(y)) {
                if (!CheckedNumbers.contains(k) && isOnlyColumnPossibility(y, i, k)) {
                    grid.get(i).set(y, k);
                    toRemove.add(new Pair<>(new Pair<>(i, y), k));
                }
                CheckedNumbers.add(k);
            }
            for (Pair<Pair<Integer, Integer>, Integer> aToRemove : toRemove) {
                RemovePossibility(aToRemove.getKey().getKey(), aToRemove.getKey().getValue(), aToRemove.getValue());
            }
        }
    }

    private void CheckSectionPossibility(int x, int y) {
        List<Integer> CheckedNumbers = new ArrayList<>();
        List<Pair<Pair<Integer, Integer>, Integer>> toRemove = new ArrayList<>();
        final int boxRow = (x / 3) * 3;
        final int boxCol = (y / 3) * 3;
        for (int i = 0; i < 3; i++) {
            final List<List<Integer>> row = possibilities.get(i + boxRow);
            for (int j = 0; j < 3; j++) {
                final List<Integer> col = row.get(j + boxCol);
                for (final int k : col) {
                    if (!CheckedNumbers.contains(k) && isOnlySectionPossibility(x, y, k)) {
                        grid.get(i + boxRow).set(j + boxCol, k);
                        toRemove.add(new Pair<>(new Pair<>(i, y), k));
                    }
                    CheckedNumbers.add(k);
                }
                for (Pair<Pair<Integer, Integer>, Integer> aToRemove : toRemove) {
                    RemovePossibility(aToRemove.getKey().getKey(), aToRemove.getKey().getValue(), aToRemove.getValue());
                }
            }
        }
    }

    private boolean isOnlySectionPossibility(int x, int y, int possibility) {
        final int boxRow = (x / BOX_SIZE) * BOX_SIZE;
        final int boxCol = (y / BOX_SIZE) * BOX_SIZE;
        for (int i = 0; i < BOX_SIZE; i++) {
            final List<List<Integer>> row = possibilities.get(i + boxRow);
            for (int j = 0; j < BOX_SIZE; j++) {
                final List<Integer> col = row.get(j + boxCol);
                if (i == x && j == y)
                    continue;
                for (final int k : col) {
                    if (k == possibility) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isOnlyLinePossibility(int x, int excludedCase, int possibility) {
        final List<List<Integer>> row = possibilities.get(x);
        for (int i = 0; i < MAX_VALUE; i++) {
            if (excludedCase == i)
                continue;
            final List<Integer> col = row.get(i);
            for (final int n : col) {
                if (n == possibility)
                    return false;
            }
        }
        return true;
    }

    private boolean isOnlyColumnPossibility(int y, int excludedCase, int possibility) {
        for (int i = 0; i < MAX_VALUE; i++) {
            final List<List<Integer>> row = possibilities.get(i);
            if (excludedCase == i)
                continue;
            for (final int n : row.get(y)) {
                if (n == possibility)
                    return false;
            }
        }
        return true;
    }

    private void RemovePossibility(int x, int y, int possibility) {
        RemovePossibilityFromCase(x, y);
        RemovePossibilityFromLine(x, possibility);
        RemovePossibilityFromColumn(y, possibility);
        RemovePossibilityFromSection(x, y, possibility);
    }

    private void RemovePossibilityFromCase(int x, int y) {
        Iterator<Integer> iter = possibilities.get(x).get(y).iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    private void RemovePossibilityFromLine(int x, int possibility) {
        for (int i = 0; i < MAX_VALUE; i++) {
            possibilities.get(x).get(i).removeIf(n -> n == possibility);
        }
    }

    private void RemovePossibilityFromColumn(int y, int possibility) {
        for (int i = 0; i < MAX_VALUE; i++) {
            possibilities.get(i).get(y).removeIf(n -> n == possibility);
        }
    }

    private void RemovePossibilityFromSection(int x, int y, int possibility) {
        final int boxRow = (x / BOX_SIZE) * BOX_SIZE;
        final int boxCol = (y / BOX_SIZE) * BOX_SIZE;
        for (int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                possibilities.get(i + boxRow).get(j + boxCol).removeIf(n -> n == possibility);
            }
        }
    }

    /**
     * Returns the next unassigned case in the grid. Returns null if all the cases are assigned.
     *
     * @return
     */
    public Pair<Integer, Integer> findNextUnassignedLocation() {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.get(i).size(); j++) {
                if (grid.get(i).get(j) == UNASSIGNED_VALUE) {
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
        for (final List<Integer> rows : grid) {
            if (rows.get(col) == value)
                return false;
        }

        // Col
        List<Integer> targetRow = grid.get(row);
        for (final int i : targetRow) {
            if (i == value) {
                return false;
            }
        }

        // Box
        final int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        final int boxCol = (col / BOX_SIZE) * BOX_SIZE;
        for ( int i = 0; i < BOX_SIZE; i++) {
            for (int j = 0; j < BOX_SIZE; j++) {
                if (grid.get(i + boxRow).get(j + boxCol) == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public void Print() {
        for (final List<Integer> row : grid) {
            StringBuilder stringRow = new StringBuilder();
            for (final int number : row) {
                stringRow.append(number);
            }
            System.out.println(stringRow);
        }
        System.out.println(numberOfCall);
    }

    public void PrintPossibilities() {
        for (final List<List<Integer>> row : possibilities) {
            StringBuilder stringRow = new StringBuilder();
            for (final List<Integer> col : row) {
                stringRow.append(' ');
                if (col.size() == 0)
                    stringRow.append('X');
                for (int number : col) {
                    stringRow.append(number);
                }
            }

            System.out.println(stringRow);
        }
    }

    //Fonctione qui vérifier que la grille complète est valide.
    //À utiliser pour des tests unitaires seulement.
    //NE PAS TOUCHER
    boolean isValid() {
        List<Integer> validList = new ArrayList<>();
        for (int i = 1; i <= 9; ++i) {
            validList.add(i);
        }

        List<List<Integer>> lists = new ArrayList<>();

        for (final List<Integer> l : grid) {
            if (l.contains(0)) {
                return false;
            }

            List newLine = new ArrayList(l);
            lists.add(newLine);
        }


        List<List<Integer>> squares = new ArrayList<>();

        for (int i = 0; i < MAX_VALUE; ++i) {
            squares.add(new ArrayList<>());
        }

        for (int i = 0; i < MAX_VALUE; ++i) {
            List<Integer> column = new ArrayList<>();

            for (int j = 0; j < MAX_VALUE; ++j) {
                squares.get(i / BOX_SIZE * BOX_SIZE + j / BOX_SIZE).add(grid.get(i).get(j));
                column.add(grid.get(j).get(i));
            }

            lists.add(column);
        }

        lists.addAll(squares);

        for (final List<Integer> list : lists) {
            Collections.sort(list);

            if (!validList.equals(list)) {
                return false;
            }
        }

        return true;
    }

}
