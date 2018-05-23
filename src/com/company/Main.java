package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Checking args length
        if (args.length != 1) {
            System.out.println("Usage : GridFile");
            System.exit(-1);
        }

        List<String[]> grid = new ArrayList<>();

        try {
            for(String line : Files.readAllLines(Paths.get(args[0]))) {
                grid.add(line.split(""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
