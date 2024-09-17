package org.example;

public class Validator {

    public static boolean isNumberValid(int value, int x, int y, int[][] board) {
        return Validator.isValidBlock(value,x, y, board) &
                Validator.isValidColumn(value, x, y, board) &
                Validator.isValidRow(value, x, y, board);
    }

    public static boolean isValidRow(int value, int x, int y, int[][] board) {
        for (int i = 0; i < board.length; i++) {
            if (y != i) {
                if (board[x][i] == value) {
                    //System.out.println("row violation");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidColumn(int value, int x, int y, int[][] board) {
        for (int i = 0; i < board.length; i++) {
            if (x != i) {
                if (board[i][y] == value) {
                    //System.out.println("column violation");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidBlock(int value, int x, int y, int[][] board) {
        // get the sub block
        int xBound = (int) (Math.floor((double) x / 3) * 3);
        int yBound = (int) (Math.floor((double) y / 3) * 3);
        // iterate
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[xBound + i][yBound + j] == value
                        && xBound + i != x
                        && yBound + j != y) {
                    //System.out.println("block violation");
                    return false;
                }
            }
        }
        return true;
    }
}

