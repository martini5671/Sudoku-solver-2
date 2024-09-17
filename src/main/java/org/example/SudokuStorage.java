package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class SudokuStorage {

    private MongoClient mongoClient;
    private MongoDatabase database;

    private final String CONNECTION_URL = "mongodb://localhost:27017";
    private MongoCollection<Document> unsolvedCollection;
    private MongoCollection<Document> solvedCollection;

    public SudokuStorage() {

        // Initialize MongoDB connection
        mongoClient = MongoClients.create(CONNECTION_URL); // Adjust according to your MongoDB setup
        database = mongoClient.getDatabase("sudokuDB");
        unsolvedCollection = database.getCollection("unsolvedSudoku");
        solvedCollection = database.getCollection("solvedSudoku");
    }

    public String saveSudokus(int[][] unsolvedSudoku, int[][] solvedSudoku) {
        // check if valid
        if (!containsZeros(unsolvedSudoku)) {
            throw new IllegalArgumentException("You provided solved sudoku instead of unsolved one" +
                    " as a first argument in the method. Please provide unsolved sudoku as first argument.");
        }
        if (containsZeros(solvedSudoku)) {
            throw new IllegalArgumentException("You provided unsolved sudoku instead of solved one" +
                    " as a second argument in the method. Please provide solved sudoku as second argument.");
        }

        HashMap<String, Integer> unsolvedMap = convertArrayToHashMap(unsolvedSudoku);
        HashMap<String, Integer> solvedMap = convertArrayToHashMap(solvedSudoku);
        // id of sudoku + documents
        String uniqueID = UUID.randomUUID().toString();
        Document document = new Document(unsolvedMap);
        document.append("id", uniqueID);
        Document document1 = new Document(solvedMap);
        document1.append("id", uniqueID);
        // insertion
        unsolvedCollection.insertOne(document);
        solvedCollection.insertOne(document1);
        return uniqueID;
    }

    public List<int[][]> retrieveSudokusById(String documentId) {
        /**
         * Retrieves both unsolved and solved Sudoku puzzles from the database based on the given document ID.
         * @param documentId The unique identifier for the document representing the Sudoku puzzles.
         * @return A list containing two elements: the first element is the unsolved Sudoku (as a int array),
         *         and the second element is the solved Sudoku (as a int array). Each element may be null if not found.
         */
        List<int[][]> sudokus = new ArrayList<>();
        sudokus.add(retrieveUnsolvedSudoku(documentId));
        sudokus.add(retrieveSolvedSudoku(documentId));
        return sudokus; // Return list containing both unsolved and solved Sudokus
    }

    public int[][] retrieveUnsolvedSudoku(String documentId) {
        /**
         * Retrieves the unsolved Sudoku puzzle from the database based on the given document ID.
         * @param documentId The unique identifier for the document representing the unsolved Sudoku.
         * @return An int array representing the unsolved Sudoku, or null if not found.
         */

        Document unsolvedDoc = unsolvedCollection.find(eq("id", documentId)).first();
        if (unsolvedDoc != null) {
            return convertDocumentToArray(unsolvedDoc);
        } else {
            return null; // Return null if the unsolved Sudoku is not found
        }
    }

    public int[][] retrieveSolvedSudoku(String documentId) {
        /**
         * Retrieves the solved Sudoku puzzle from the database based on the given document ID.
         * @param documentId The unique identifier for the document representing the solved Sudoku.
         * @return An int array representing the solved Sudoku, or null if not found.
         */

        Document solvedDoc = solvedCollection.find(eq("id", documentId)).first();
        if (solvedDoc != null) {
            return convertDocumentToArray(solvedDoc);
        } else {
            return null; // Return null if the solved Sudoku is not found
        }
    }

    public void deleteSolvedUnsolvedSudoku(String id) {
        deleteSolvedSudoku(id);
        deleteUnsolvedSudoku(id);
    }

    public void deleteUnsolvedSudoku(String id) {
        Bson query = eq("id", id);
        try {
            // Deletes the first document that has a "title" value of "The Garbage Pail Kids Movie"
            DeleteResult result = unsolvedCollection.deleteOne(query);
            System.out.println(STR."Deleted document count: \{result.getDeletedCount()}");
            // Prints a message if any exceptions occur during the operation
        } catch (MongoException me) {
            System.err.println(STR."Unable to delete due to an error: \{me}");
        }
    }

    public void deleteSolvedSudoku(String id) {
        Bson query = eq("id", id);
        try {
            // Deletes the first document that has a "title" value of "The Garbage Pail Kids Movie"
            DeleteResult result = solvedCollection.deleteOne(query);
            System.out.println(STR."Deleted document count: \{result.getDeletedCount()}");
            // Prints a message if any exceptions occur during the operation
        } catch (MongoException me) {
            System.err.println(STR."Unable to delete due to an error: \{me}");
        }
    }


    private HashMap<String, Integer> convertArrayToHashMap(int[][] sudoku) {
        // for upload
        HashMap<String, Integer> stringintHashMap = new HashMap<>();
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku.length; j++) {
                String s1 = String.valueOf(i);
                String s2 = String.valueOf(j);
                stringintHashMap.put(s1 + s2, sudoku[i][j]);
            }
        }
        return stringintHashMap;
    }

    private int[][] convertDocumentToArray(Document mongoSudoku) {
        // for download
        int[][] result = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String s1 = String.valueOf(i);
                String s2 = String.valueOf(j);
                int value = mongoSudoku.getInteger(s1 + s2);
                result[i][j] = value;
            }
        }

        return result;
    }

    private boolean containsZeros(int[][] sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
