package org.example;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create sudoku storage instance
        SudokuStorage sudokuStorage = new SudokuStorage();

        // retrieve 2 sudoku by id (solved and unsolved one)
        List<int[][]> sudoku = sudokuStorage.retrieveSudokusById("787b3ab0-cb18-4fde-9d27-4b4ba270c531");

        // the first sudoku in the list is the unsolved one.
        SudokuSolver sudokuSolver = new SudokuSolver(sudoku.getFirst());

        // solve sudoku
        int[][] board = sudokuSolver.solve();
        System.out.println("solved board:");
        SudokuSolver.prettyPrintSudoku(board);
        System.out.println("original solved board:");
        SudokuSolver.prettyPrintSudoku(sudoku.getLast());
    }
}