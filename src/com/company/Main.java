package com.company;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Checking args length
        if (args.length < 1) {
            System.out.println("Usage : files to solve");
            System.exit(-1);
        }
        long timerTotal = System.currentTimeMillis();

        long timer;

        for (int i = 0; i < args.length; ++i)
        {
            BacktrackingSolver solver = new BacktrackingSolver(args[i]);

            solver.Solve();
            solver.Print();

            /*System.out.println(args[i]);

            timer = System.currentTimeMillis();

            if(solver.Solve()) {
                timer = System.currentTimeMillis() - timer;

                solver.Print();

                System.out.println(solver.isValid());
            } else {
                timer = System.currentTimeMillis() - timer;

                solver.Print();

                System.out.println("No Solution!");
            }*/

            //System.out.println("Time: " + timer + " ms");

            //System.out.println("---------------");
        }
        //timerTotal = System.currentTimeMillis() - timerTotal;
        //System.out.println("Total time: " + timerTotal + " ms");
    }
}
