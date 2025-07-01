package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.List;

public class MyViewModel {

    private final IModel model;
    private final SimpleObjectProperty<Maze> mazeProperty = new SimpleObjectProperty<>();

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generateMaze(int rows, int cols) {
        model.generateMaze(rows, cols);
        Maze generated = ((MyModel)model).getGeneratedMaze();
        mazeProperty.set(generated);
    }

    public SimpleObjectProperty<Maze> mazeProperty() {
        return mazeProperty;
    }

    public void saveMazeToFile(File file) {
        model.saveMazeToFile(file);
    }

    public void loadMazeFromFile(File file) {
        Maze loaded = model.loadMazeFromFile(file);
        if (loaded != null) {
            mazeProperty.set(loaded);
        }
    }

    // ✅ בקשת פתרון מהמודל
    public List<Position> getMazeSolution() {
        return ((MyModel)model).solveCurrentMaze();
    }
}
