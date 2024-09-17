package org.example;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SudokuStorage sudokuStorage = new SudokuStorage();
        List<int[][]> sudoku = sudokuStorage.retrieveSudokusById("787b3ab0-cb18-4fde-9d27-4b4ba270c531");
        SudokuSolver sudokuSolver = new SudokuSolver(sudoku.getFirst());
        sudokuSolver.solve();
    }
}