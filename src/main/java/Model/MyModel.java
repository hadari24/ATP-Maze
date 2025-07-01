package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;

import java.io.*;
import java.util.List;

public class MyModel implements IModel {
    private Maze currentMaze;

    @Override
    public void generateMaze(int rows, int cols) {
        this.currentMaze = new algorithms.mazeGenerators.MyMazeGenerator().generate(rows, cols);
    }

    public void saveMazeToFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] mazeBytes = currentMaze.toByteArray();
            fos.write(mazeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Maze loadMazeFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = fis.readAllBytes();
            this.currentMaze = new Maze(data);
            return currentMaze;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Maze getGeneratedMaze() {
        return currentMaze;
    }

    public Maze getCurrentMaze() {
        return currentMaze;
    }

    // ✅ פתרון המבוך בעזרת BFS (או DFS לפי בחירה)
    public List<Position> solveCurrentMaze() {
        if (currentMaze == null) return null;

        ISearchable searchableMaze = new SearchableMaze(currentMaze);
        ISearchingAlgorithm searcher = new BreadthFirstSearch(); // אפשר גם DFS

        Solution solution = searcher.solve(searchableMaze);
        return solution.getSolutionPath().stream()
                .map(state -> new Position(state.getRow(), state.getCol()))
                .toList();
    }

}
