package org.example;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SudokuSolver {

    int[][] board;

    public int updateCounter = 0;

    HashMap<Point, ArrayList<Integer>> currentOptions = new HashMap<Point, ArrayList<Integer>>();

    public SudokuSolver(int[][] board) {
        this.board = board;
    }

    public int[][] solve() throws ExecutionException, InterruptedException {
        while (true)
        {
            boolean outcome = simpleElimination();
            if(!outcome)
            {
                outcome = blockElimination();
                if(!outcome)
                {
                    break;
                }
            }
        }
        return board;
    }

    public boolean simpleElimination() {
        try (ExecutorService executorService = Executors.newFixedThreadPool(board.length)) {
            ArrayList<Callable<HashMap<Point,ArrayList<Integer>>>> callableArrayList = new ArrayList<>();

            // define tasks and add them to array list:
            for (int i = 0; i < 9; i++) {
                int rowIndex = i;
                Callable<HashMap<Point, ArrayList<Integer>>> callableTask = () -> {
                    // scan row
                    return getPossibleCandidatesForRow(rowIndex);
                };
                callableArrayList.add(callableTask);
            }
            List<Future<HashMap<Point, ArrayList<Integer>>>> futureResults = executorService.invokeAll(callableArrayList);

            // now stack all hashmaps together:
            HashMap<Point, ArrayList<Integer>> result = new HashMap<>();
            futureResults.forEach(hashMapFuture -> {
                try {
                    result.putAll(hashMapFuture.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            // update board
            boolean wasBoardUpdated = updateBoard(result);
            // update possible options if the update operation is false
            updateCurrentOptions(result);
            return wasBoardUpdated;
            
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<Point, ArrayList<Integer>> getPossibleCandidatesForRow(int x) {
        HashMap<Point, ArrayList<Integer>> pointArrayListHashMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            // this will merge multiple maps into one final map for row
            pointArrayListHashMap.putAll(getPossibleCandidatesForCell(x, i));
        }
        return pointArrayListHashMap;
    }

    private HashMap<Point, ArrayList<Integer>> getPossibleCandidatesForCell(int x, int y) {
        // this will return old points
        ArrayList<Integer> candidates = new ArrayList<>();
        HashMap<Point, ArrayList<Integer>> hashMap = new HashMap<>();
        // check if the cell is not already filled:
        if (board[x][y] != 0) {
            candidates.add((-1 * (board[x][y])));
            hashMap.put(new Point(x, y), candidates);
            return hashMap;
        }
        // if its not filled than execute this:
        for (int i = 1; i < 10; i++) {
            boolean isValid = Validator.isNumberValid(i, x, y, board);
            if (isValid) {
                candidates.add(i);
            }
        }
        hashMap.put(new Point(x, y), candidates);
        return hashMap;
    }
    /// block elimination:
    public boolean blockElimination() throws InterruptedException, ExecutionException {
        // hashmap to return:
        HashMap<Point, ArrayList<Integer>> result = new HashMap<>();

        // generate blocks with all possible options
        HashMap<Point, ArrayList<Integer>> blocks = groupCellsIntoBlocks(currentOptions);

        // create an executor service
        ExecutorService executorService = Executors.newFixedThreadPool(board.length);

        // now search for unique values within each block:
        ArrayList<Callable<Optional<HashMap<Point, ArrayList<Integer>>>>> callables =
                new ArrayList<>();

        for (Map.Entry<Point, ArrayList<Integer>> entry: blocks.entrySet()) {
            Callable<Optional<HashMap<Point, ArrayList<Integer>>>> blockScanner =
                    () -> {
                        // find distinct value
                        List<Integer> distinctValues = entry.getValue()
                                .stream()
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                                .entrySet()
                                .stream()
                                .filter(e -> e.getValue() == 1)
                                .map(Map.Entry::getKey)
                                .toList();
                        if (!distinctValues.isEmpty()) {
                            Point blockPoint = entry.getKey();
                            Point pointForReduction = getMatchingPointFromBlock(blockPoint, distinctValues.getFirst(), currentOptions);
                            result.put(pointForReduction, new ArrayList<>(List.of(distinctValues.getFirst())));
                        }
                        return Optional.of(result);
                    };
            callables.add(blockScanner);
        }
        // submit to executor service
        List<Future<Optional<HashMap<Point, ArrayList<Integer>>>>> results = executorService.invokeAll(callables);

        // collect results
        for (Future<Optional<HashMap<Point, ArrayList<Integer>>>> future : results) {
            Optional<HashMap<Point, ArrayList<Integer>>> optionalResult = future.get();
            optionalResult.ifPresent(result::putAll);
        }

        // shutdown the executor service
        executorService.shutdown();

        // merge result of reduction with current options
        currentOptions.putAll(result);
        updateCurrentOptions(currentOptions);

        // update board
        return updateBoard(result);
    }
    private HashMap<Point, ArrayList<Integer>> groupCellsIntoBlocks(HashMap<Point, ArrayList<Integer>> unsolvedCells){
        // Creating a 2D array of HashMap<Point, ArrayList<Integer>>
        HashMap<Point, ArrayList<Integer>> blocks = new HashMap<>();
        // block split:
        for (Map.Entry<Point, ArrayList<Integer>> entry: unsolvedCells.entrySet())
        {
            Point point = entry.getKey();
            int xBlock = (int) (Math.floor((double) point.x / 3) *3); // get row for block
            int yBlock = (int) (( Math.floor((double) point.y / 3) *3)); // get column for block
            if(!blocks.containsKey(new Point(xBlock, yBlock)))
            {
                // clone array list:
                ArrayList<Integer> byteArrayList = new ArrayList<>(entry.getValue());
                blocks.put(new Point(xBlock,yBlock), byteArrayList);
            }
            else {
                // retrieve array to update
                Point point1 = new Point(xBlock,yBlock);
                ArrayList<Integer> currentArray = blocks.get(point1);
                //merge with new values
                currentArray.addAll(entry.getValue());
            }
        }
        return blocks;
    }


    private Point getMatchingPointFromBlock(Point blockPoint, int lookupValue, HashMap<Point, ArrayList<Integer>> unsolvedCells) {
        for (byte i = 0; i < 3; i++) {
            for (byte j = 0; j < 3; j++) {
                byte xVal = (byte) (blockPoint.x + i);
                byte yVal = (byte) (blockPoint.y + j);
                Point searchedPoint = new Point(xVal, yVal);
                try {
                    ArrayList<Integer> array = unsolvedCells.get(searchedPoint);
                    Integer lookup = array.stream().filter(aByte -> aByte == lookupValue).findFirst().orElse( -1);
                    if (lookup != -1) {
                        return searchedPoint;
                    }
                } catch (NullPointerException ignored) {

                }

            }
        }
        return null;
    }
    
    /// block elimination end
    public void updateCurrentOptions(HashMap<Point, ArrayList<Integer>> options) {
        currentOptions.clear();
        for (Map.Entry<Point, ArrayList<Integer>> entry : options.entrySet()) {
            if (entry.getValue().size() > 1)
            {
                currentOptions.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean updateBoard(HashMap<Point, ArrayList<Integer>> hashMap){
        boolean isUpdated = false;
        for (Map.Entry<Point, ArrayList<Integer>> entry: hashMap.entrySet())
        {
            if(entry.getValue().size() == 1 && entry.getValue().getFirst() > 0)
            {
                updateCounter ++;
                board[entry.getKey().x][entry.getKey().y] = entry.getValue().getFirst();
                isUpdated = true;
            }
        }
        return isUpdated;
    }


    public static void prettyPrintSudoku(int[][] array) {
        for (int[] x : array) {
            for (int y : x) {
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }
    public static void showCurrentOptions(HashMap<Point, ArrayList<Integer>> output) {
        // Iterate over the entries of the map
        for (Map.Entry<Point, ArrayList<Integer>> entry : output.entrySet()) {
            Point point = entry.getKey();
            ArrayList<Integer> candidates = entry.getValue();

            // Print the point coordinates
            System.out.print("Point: (" + point.x + ", " + point.y + ") -> ");

            // Print the candidates
            if (candidates.isEmpty()) {
                System.out.println("None");
            } else {
                System.out.println(candidates);
            }
        }
    }
}
