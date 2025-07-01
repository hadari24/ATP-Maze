package View;

import algorithms.mazeGenerators.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class MazeDisplayer extends Canvas {

    private int[][] maze;
    private int characterRow = -1, characterCol = -1;
    private int goalRow = 0, goalCol = 0;
    private int startRow = 0, startCol = 0;

    private List<enemy> enemies = new ArrayList<>();
    private List<Position> solutionPath = null;

    private Image wallImage, floorImage, characterImage, goalImage, startImage, enemyImage;

    private boolean showStartImage = true;
    private double scale = 1.0;
    private double lastMouseX = 0, lastMouseY = 0;

    private int visibilityRadius = Integer.MAX_VALUE; // ברירת מחדל – הכל גלוי

    public MazeDisplayer() {
        widthProperty().addListener(o -> draw());
        heightProperty().addListener(o -> draw());

        addEventFilter(ScrollEvent.SCROLL, this::handleZoom);
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });
        addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            double dx = e.getSceneX() - lastMouseX;
            double dy = e.getSceneY() - lastMouseY;
            setTranslateX(getTranslateX() + dx);
            setTranslateY(getTranslateY() + dy);
            lastMouseX = e.getSceneX();
            lastMouseY = e.getSceneY();
        });
    }

    private void handleZoom(ScrollEvent event) {
        if (event.isControlDown()) {
            double delta = 0.1;
            scale = (event.getDeltaY() < 0) ? Math.max(0.5, scale - delta) : Math.min(3.0, scale + delta);
            setScaleX(scale);
            setScaleY(scale);
            event.consume();
        }
    }

    public void setMaze(int[][] maze) {
        this.maze = maze;
        this.solutionPath = null;
        draw();
    }

    public void setStartPosition(int r, int c) {
        this.startRow = r;
        this.startCol = c;
        draw();
    }

    public void setGoalPosition(int r, int c) {
        this.goalRow = r;
        this.goalCol = c;
        draw();
    }

    public void setCharacterPosition(int r, int c) {
        this.characterRow = r;
        this.characterCol = c;
        draw();
    }

    public void setSolution(List<Position> solution) {
        this.solutionPath = solution;
        draw();
    }

    public void clearSolution() {
        this.solutionPath = null;
        draw();
    }

    public void setWallImage(Image i)      { this.wallImage = i; }
    public void setFloorImage(Image i)     { this.floorImage = i; }
    public void setCharacterImage(Image i) { this.characterImage = i; }
    public void setGoalImage(Image i)      { this.goalImage = i; }
    public void setStartImage(Image i)     { this.startImage = i; }
    public void setEnemyImage(Image i)     { this.enemyImage = i; }

    public void setEnemies(List<enemy> enemies) {
        this.enemies = enemies;
        draw();
    }

    public void setShowStartImage(boolean show) {
        this.showStartImage = show;
        draw();
    }

    public void setVisibilityRadius(int radius) {
        this.visibilityRadius = radius;
        draw();
    }

    private void draw() {
        double w = getWidth(), h = getHeight();
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        if (maze == null) return;

        double cellW = w / maze[0].length;
        double cellH = h / maze.length;

        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {

                // חשיפה לפי רדיוס
                if (Math.abs(r - characterRow) > visibilityRadius || Math.abs(c - characterCol) > visibilityRadius) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(c * cellW, r * cellH, cellW, cellH);
                    continue;
                }

                double x = c * cellW, y = r * cellH;

                if (maze[r][c] == 1) {
                    if (wallImage != null) gc.drawImage(wallImage, x, y, cellW, cellH);
                } else {
                    if (floorImage != null) gc.drawImage(floorImage, x, y, cellW, cellH);
                }

                if (r == startRow && c == startCol) {
                    if (characterRow == startRow && characterCol == startCol && characterImage != null) {
                        gc.drawImage(characterImage, x, y, cellW, cellH);
                    } else if (showStartImage && startImage != null) {
                        gc.drawImage(startImage, x, y, cellW, cellH);
                    }
                }

                if (r == goalRow && c == goalCol && goalImage != null) {
                    gc.drawImage(goalImage, x, y, cellW, cellH);
                }

                if (!(r == startRow && c == startCol)
                        && r == characterRow && c == characterCol
                        && characterImage != null) {
                    gc.drawImage(characterImage, x, y, cellW, cellH);
                }
            }
        }

        // ✅ ציור אויבים
        if (enemyImage != null && enemies != null) {
            for (enemy e : enemies) {
                int r = e.getRow(), c = e.getCol();

                if (Math.abs(r - characterRow) > visibilityRadius || Math.abs(c - characterCol) > visibilityRadius)
                    continue;

                double x = c * cellW, y = r * cellH;
                gc.drawImage(enemyImage, x, y, cellW, cellH);
            }
        }

        // ✅ פתרון מבוך
        if (solutionPath != null) {
            gc.setFill(Color.rgb(0, 0, 255, 0.4));
            for (Position pos : solutionPath) {
                int r = pos.getRowIndex(), c = pos.getColumnIndex();

                if (Math.abs(r - characterRow) > visibilityRadius || Math.abs(c - characterCol) > visibilityRadius)
                    continue;

                double x = c * cellW, y = r * cellH;
                gc.fillRect(x, y, cellW, cellH);
            }
        }
    }
}
