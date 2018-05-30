package com.company;

public class Main {

    public static void main(String[] args) {

        // Checking args length
        if (args.length < 1) {
            System.out.println("Usage : files to solve");
            System.exit(-1);
        }

        for (int i = 0; i < args.length; ++i)
        {
            BacktrackingSolver solver = new BacktrackingSolver(args[i]);

            System.out.println(args[i]);

            long timer = System.currentTimeMillis();

            if(solver.Solve()) {
                timer = System.currentTimeMillis() - timer;

                solver.Print();

                System.out.println(solver.isValid());
            } else {
                timer = System.currentTimeMillis() - timer;

                solver.Print();

                System.out.println("No Solution!");
            }

            System.out.println("Time: " + timer + " ms");

            System.out.println("---------------");
        }


    }
}
