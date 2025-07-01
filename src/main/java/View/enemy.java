package View;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class enemy extends ImageView {
    private int row, col;
    private int dirRow, dirCol;
    private final double cellWidth, cellHeight;

    public enemy(int startRow, int startCol, boolean horizontal, double cellWidth, double cellHeight) {
        super(new Image(enemy.class.getResourceAsStream("/images/enemy.png")));
        this.row = startRow;
        this.col = startCol;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

        if (horizontal) {
            dirRow = 0;
            dirCol = 1;
        } else {
            dirRow = 1;
            dirCol = 0;
        }

        setFitWidth(cellWidth);
        setFitHeight(cellHeight);
        relocateToCell();
    }

    public void moveOneStep(int[][] maze) {
        int newRow = row + dirRow;
        int newCol = col + dirCol;

        // בדיקת תנועה בכיוון הנוכחי
        if (isValidMove(maze, newRow, newCol)) {
            row = newRow;
            col = newCol;
            relocateToCell();
            return;
        }

        // היפוך כיוון ונסיון מחדש
        dirRow *= -1;
        dirCol *= -1;
        newRow = row + dirRow;
        newCol = col + dirCol;
        if (isValidMove(maze, newRow, newCol)) {
            row = newRow;
            col = newCol;
            relocateToCell();
            return;
        }

        // ניסיון לעבור לכיוון אחר (מעלה/מטה/ימין/שמאל)
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : directions) {
            int tryRow = row + d[0];
            int tryCol = col + d[1];
            if (isValidMove(maze, tryRow, tryCol)) {
                dirRow = d[0];
                dirCol = d[1];
                row = tryRow;
                col = tryCol;
                relocateToCell();
                return;
            }
        }

        // אם אין תנועה חוקית - האויב נשאר במקומו
    }
    private boolean isValidMove(int[][] maze, int r, int c) {
        return r >= 0 && r < maze.length &&
                c >= 0 && c < maze[0].length &&
                maze[r][c] == 0;
    }


    private void relocateToCell() {
        setLayoutX(col * cellWidth);
        setLayoutY(row * cellHeight);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
