package Model;

import algorithms.mazeGenerators.Maze;

import java.io.File;

public interface IModel {
    void generateMaze(int rows, int cols);

    void saveMazeToFile(File file);       // נוספה
    Maze loadMazeFromFile(File file);     // נוספה
}
