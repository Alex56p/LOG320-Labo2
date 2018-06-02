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
    private final int UNASSIGNED_VALUE = 0;

    private List<List<Integer>> grid = new ArrayList<>();
    List<List<List<Integer>>> possibilities = new ArrayList<>();
    private int numberOfCall = 0;

    public BacktrackingSolver(String filePath) {
        try {
            for(String line : Files.readAllLines(Paths.get(filePath))) {
                List<Integer> numbers = new ArrayList<>(9);
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
        CheckWithPossibilities();
        Pair<Integer, Integer> unassignedCase = findNextUnassignedLocation();
        if(unassignedCase == null)
            return true;

        final int x = unassignedCase.getKey();
        final int y = unassignedCase.getValue();

        for(int i : possibilities.get(x).get(y)) {
            if(isValid(x, y, i)) {
                grid.get(x).set(y, i);

                if(Solve()) {
                    return true;
                }

                grid.get(x).set(y, UNASSIGNED_VALUE);
            }
        }
        return false;
    }

    private void CheckWithPossibilities(){
        for(int i = 0; i < MAX_VALUE; i++) {
            for (int j = 0; j < MAX_VALUE; j++) {
                if(possibilities.get(i).get(j).size() == 1){
                    final int possibility = possibilities.get(i).get(j).get(0);
                    grid.get(i).set(j, possibility);
                    RemovePossibility(i,j, possibility);
                }
                else {
                    CheckLinePossibility(i);
                    CheckColumnPossibility(j);
                    CheckSectionPossibility(i, j);
                }
            }
        }
    }

    private void CheckLinePossibility(int x) {
        final List<List<Integer>> CurrentRow = possibilities.get(x);
        List<Integer> CheckedNumbers = new ArrayList<>();
        List<Pair<Pair<Integer, Integer>, Integer>> toRemove = new ArrayList<>();
        for(int i = 0; i < MAX_VALUE; i++){
            for (int k : CurrentRow.get(i)) {
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
        for(int i = 0; i < MAX_VALUE; i++){
            for (int k : possibilities.get(i).get(y)) {
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
        int boxRow = (x / 3) * 3;
        int boxCol = (y / 3) * 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                for (int k : possibilities.get(i + boxRow).get(j + boxCol)) {
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
        int boxRow = (x / 3) * 3;
        int boxCol = (y / 3) * 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if (i == x && j == y)
                    continue;
                for (int k : possibilities.get(i + boxRow).get(j + boxCol)) {
                    if(k == possibility) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isOnlyLinePossibility(int x, int excludedCase, int possibility){
        for(int i = 0; i < MAX_VALUE; i++) {
            if(excludedCase == i)
                continue;
            for(int n : possibilities.get(x).get(i)){
                if(n == possibility)
                    return false;
            }
        }
        return true;
    }
    private boolean isOnlyColumnPossibility(int y, int excludedCase, int possibility){
        for(int i = 0; i < MAX_VALUE; i++) {
            if(excludedCase == i)
                continue;
            for(int n : possibilities.get(i).get(y)){
                if(n == possibility)
                    return false;
            }
        }
        return true;
    }

    private void RemovePossibility(int x, int y, int possibility){
        RemovePossibilityFromCase(x,y);
        RemovePossibilityFromLine(x, possibility);
        RemovePossibilityFromColumn(y, possibility);
        RemovePossibilityFromSection(x, y, possibility);
    }

    private void RemovePossibilityFromCase(int x, int y){
        Iterator<Integer> iter = possibilities.get(x).get(y).iterator();
        while(iter.hasNext()){
            int possibility = iter.next();
            iter.remove();
        }
    }

    private void RemovePossibilityFromLine(int x, int possibility){
        for(int i = 0; i < MAX_VALUE; i++){
            possibilities.get(x).get(i).removeIf(n -> n == possibility);
        }
    }

    private void RemovePossibilityFromColumn(int y, int possibility){
        for(int i = 0; i < MAX_VALUE; i++){
            possibilities.get(i).get(y).removeIf(n -> n == possibility);
        }
    }

    private void RemovePossibilityFromSection(int x, int y, int possibility){
        int boxRow = (x / 3) * 3;
        int boxCol = (y / 3) * 3;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                possibilities.get(i+boxRow).get(j+boxCol).removeIf(n -> n == possibility);
            }
        }}
    /**
     * Returns the next unassigned case in the grid. Returns null if all the cases are assigned.
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

    public void PrintPossibilities() {
        for(List<List<Integer>> row : possibilities) {
            StringBuilder stringRow = new StringBuilder();
            for(List<Integer> col : row) {
                stringRow.append(' ');
                if(col.size() == 0)
                    stringRow.append('X');
                for(int number : col) {
                    stringRow.append(number);
                }
            }

            System.out.println(stringRow);
        }
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
