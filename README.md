
# Sudoku Solver 2
This is my small project in which I wanted to explore concurency in Java by creating a Sudoku solving algorithm. I also used MongoDB to store and retrieve both solved and unsolved sudokus. Sudoku generator was implemented by using Stephan Fuhrmann's library "sudoku" v. 4.0.0. https://github.com/sfuhrm/sudoku







## Features

- Simple Elimination: The algorithm attempts to generate all possible numbers for a given cell which do not violate the rules of Sudoku. It uses multi-threading to scan through each row concurrently, improving efficiency.

- Block Elimination: The algorithm groups unfilled cells into 3x3 blocks and looks for unique candidates within these blocks. If there exists a unique number among all candidates within the same block, the given cell is filled with that number. This part also uses multi-threading by implementing ExecutorService.

- Brute Force: If the simple and block eliminations do not lead to a solution, the algorithm falls back on a brute force method to exhaustively try all possible combinations until the Sudoku board is solved or identified as unsolvable.


## Installation

You can clone the project.
    
## Usage/Examples

```java
public static void main(String[] args) throws ExecutionException, InterruptedException {
        // create sudoku storage instance
        SudokuStorage sudokuStorage = new SudokuStorage();
        
        // retrieve 2 sudoku by id (solved and unsolved one)
        // the id which is provided here may differ from yours. 
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
```

