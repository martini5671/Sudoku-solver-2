import de.sfuhrm.sudoku.GameMatrix;
import de.sfuhrm.sudoku.Riddle;
import org.example.SudokuSolver;
import org.example.SudokuStorage;
import org.example.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
public class ValidatorTests {

    private SudokuStorage sudokuStorage;
    private int[][] unsolvedSudoku;

    private final String SUDOKUID = "787b3ab0-cb18-4fde-9d27-4b4ba270c531";
    private int[][] solvedSudoku;
    @BeforeEach
    public void init() {
        this.sudokuStorage = new SudokuStorage();
        List<int[][]> sudokus = sudokuStorage.retrieveSudokusById(SUDOKUID);
        this.unsolvedSudoku = sudokus.getFirst();
        this.solvedSudoku = sudokus.getLast();
    }

    @Test
    @DisplayName("check if only block detection works: false")
    public void checkBlockValidation() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        // 2 in 2,1
        Assertions.assertFalse(Validator.isNumberValid(2, 2, 1, unsolvedSudoku));
    }

    @Test
    @DisplayName("check if only block detection works: true")
    public void checkBlockValidation2() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        // 1 in 0,2
        Assertions.assertTrue(Validator.isNumberValid(1, 0, 2, unsolvedSudoku));
    }

    @Test
    @DisplayName("check if row validation works")
    public void checkRowValidation() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        Assertions.assertFalse(Validator.isValidRow(7, 0, 2, unsolvedSudoku));
    }

    @Test
    @DisplayName("check if row validation works")
    public void checkRowValidation2() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        Assertions.assertTrue(Validator.isValidRow(5, 0, 2, unsolvedSudoku));
    }

    @Test
    @DisplayName("check if column validation works")
    public void checkColumnValidation() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        Assertions.assertTrue(Validator.isValidColumn(1, 0, 2, unsolvedSudoku));
    }

    @Test
    @DisplayName("check if column validation works")
    public void checkColumnValidation2() {
        SudokuSolver.prettyPrintSudoku(unsolvedSudoku);
        Assertions.assertFalse(Validator.isValidColumn(7, 0, 2, unsolvedSudoku));
    }

    @DisplayName("check if Validator works as a hole")
    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8}) // six numbers
    void checkDiagonal(int number) {
        Assertions.assertTrue(Validator.isNumberValid(
                solvedSudoku[number][number],
                number, number, unsolvedSudoku));
    }
}
