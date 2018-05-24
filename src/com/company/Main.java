package com.company;

public class Main {

    public static void main(String[] args) {

        // Checking args length
        if (args.length != 1) {
            System.out.println("Usage : GridFile");
            System.exit(-1);
        }

        BacktrackingSolver solver = new BacktrackingSolver(args[0]);
        if(solver.Solve()) {
            solver.Print();
        } else {
            System.out.println("No Solution!");
        }
    }
}
