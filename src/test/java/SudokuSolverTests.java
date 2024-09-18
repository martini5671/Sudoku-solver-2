import org.example.SudokuSolver;
import org.example.SudokuStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class SudokuSolverTests {

    private SudokuStorage sudokuStorage;
    private int[][] unsolvedSudoku;
    private final String SUDOKUID2 = "20964d68-3957-4e0f-9471-7d6b0fb4d708";
    private int[][] solvedSudoku;
    private SudokuSolver sudokuSolver;

    @BeforeEach
    public void init() {
        this.sudokuStorage = new SudokuStorage();
        List<int[][]> sudokus = sudokuStorage.retrieveSudokusById(SUDOKUID2);
        this.unsolvedSudoku = sudokus.getFirst();
        this.solvedSudoku = sudokus.getLast();
        this.sudokuSolver = new SudokuSolver(unsolvedSudoku);
    }
    @Test
    public void partialTest() throws ExecutionException, InterruptedException {
        int[][] partiallySolvedSudoku = sudokuSolver.solve();
        List<Boolean> booleans = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (partiallySolvedSudoku[i][j] != 0)
                {
                    booleans.add(partiallySolvedSudoku[i][j] == solvedSudoku[i][j]);
                }
            }
        }
        long falseCounter = booleans.stream().filter(aBoolean -> !aBoolean).count();
        System.out.println("Partially solved sudoku:");
        SudokuSolver.prettyPrintSudoku(partiallySolvedSudoku);
        System.out.println("Fully solved sudoku:");
        SudokuSolver.prettyPrintSudoku(solvedSudoku);
        System.out.println("Update counter:");
        System.out.println(sudokuSolver.updateCounter);
        Assertions.assertEquals(0, falseCounter);
    }
    @ParameterizedTest
    @DisplayName("solve 3 sudokus")
    @ValueSource(strings = {"20964d68-3957-4e0f-9471-7d6b0fb4d708", "5b624776-db6f-4c50-81dd-b65eee221c0a", "787b3ab0-cb18-4fde-9d27-4b4ba270c531"}) // six numbers
    public void solve3Sudoku(String string) throws ExecutionException, InterruptedException {
        List<int[][]> sudokus = sudokuStorage.retrieveSudokusById(string);
        this.unsolvedSudoku = sudokus.getFirst();
        this.solvedSudoku = sudokus.getLast();
        this.sudokuSolver = new SudokuSolver(unsolvedSudoku);
        int[][] solvedSudoku2 = sudokuSolver.solve();

        System.out.println("Solved sudoku: ");
        SudokuSolver.prettyPrintSudoku(solvedSudoku2);

        System.out.println("Solution: ");
        SudokuSolver.prettyPrintSudoku(solvedSudoku);

        System.out.println("Number of updates made with logic:");
        System.out.println(sudokuSolver.updateCounter);

        Assertions.assertArrayEquals(solvedSudoku, solvedSudoku2);
    }
}
