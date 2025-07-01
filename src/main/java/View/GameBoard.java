package View;

import algorithms.mazeGenerators.Position;
import javafx.scene.layout.StackPane;

import java.util.List;

public class GameBoard extends StackPane {
    private final MazeDisplayer mazeDisplayer = new MazeDisplayer();

    public GameBoard() {
        getChildren().add(mazeDisplayer);
        mazeDisplayer.setFocusTraversable(true);
        mazeDisplayer.widthProperty().bind(widthProperty());
        mazeDisplayer.heightProperty().bind(heightProperty());
    }

    // --- גישה למתודות מתוך MazeDisplayer ---
    public void setMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
    }

    public void setStartPosition(int r, int c) {
        mazeDisplayer.setStartPosition(r, c);
    }

    public void setGoalPosition(int r, int c) {
        mazeDisplayer.setGoalPosition(r, c);
    }

    public void setCharacterPosition(int r, int c) {
        mazeDisplayer.setCharacterPosition(r, c);
    }

    public void setSolution(List<Position> solution) {
        mazeDisplayer.setSolution(solution);
    }

    public void clearSolution() {
        mazeDisplayer.clearSolution();
    }

    public MazeDisplayer getMazeDisplayer() {
        return mazeDisplayer;
    }

    public void setWallImage(javafx.scene.image.Image img) {
        mazeDisplayer.setWallImage(img);
    }

    public void setFloorImage(javafx.scene.image.Image img) {
        mazeDisplayer.setFloorImage(img);
    }

    public void setCharacterImage(javafx.scene.image.Image img) {
        mazeDisplayer.setCharacterImage(img);
    }

    public void setGoalImage(javafx.scene.image.Image img) {
        mazeDisplayer.setGoalImage(img);
    }

    public void setStartImage(javafx.scene.image.Image img) {
        mazeDisplayer.setStartImage(img);
    }

    public void setShowStartImage(boolean show) {
        mazeDisplayer.setShowStartImage(show);
    }
}
