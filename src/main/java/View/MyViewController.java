package View;

import ViewModel.MyViewModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class MyViewController implements IView, Initializable {

    @FXML private VBox mainMenuPanel;
    @FXML private VBox bottomPanel;
    @FXML private AnchorPane centerPane;
    @FXML private Pane confettiLayer;
    @FXML private Button generateMazeButton, startButton;
    @FXML private TextField rowsField, colsField;
    @FXML private GameBoard gameBoard;
    @FXML private Button defaultButton;

    //@FXML private RadioButton manualInputRadio;
    //@FXML private RadioButton defaultInputRadio;
    //private ToggleGroup inputModeGroup;
    private Properties appProperties;

    private boolean isMissionMode = false;

    private MyViewModel viewModel;
    private int startR, startC;
    private int currentR = -1, currentC = -1;
    private int[][] mazeGrid;
    private final List<ConfettiParticle> particles = new ArrayList<>();
    private AnimationTimer confettiTimer;
    private final BackgroundMusicPlayer musicPlayer = new BackgroundMusicPlayer();
    private int missionLevel = 0;
    private final int maxMissionLevels = 5;

    private List<enemy> enemies = new ArrayList<>();
    private final Random rand = new Random();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // הסתרת הפאנל המרכזי בתחילת הדרך
        centerPane.setVisible(false);
        centerPane.setManaged(false);

        // קישור גודל GameBoard לגודל המרכז
        gameBoard.prefWidthProperty().bind(centerPane.widthProperty());
        gameBoard.prefHeightProperty().bind(centerPane.heightProperty());

        // הגדרת תמונות למבוך
        MazeDisplayer displayer = gameBoard.getMazeDisplayer();
        displayer.setWallImage(new Image(getClass().getResourceAsStream("/images/wall.png")));
        displayer.setFloorImage(new Image(getClass().getResourceAsStream("/images/floor.png")));
        displayer.setGoalImage(new Image(getClass().getResourceAsStream("/images/end_point.png")));
        displayer.setCharacterImage(new Image(getClass().getResourceAsStream("/images/character.png")));
        displayer.setStartImage(new Image(getClass().getResourceAsStream("/images/start_point.png")));
        displayer.setEnemyImage(new Image(getClass().getResourceAsStream("/images/enemy.png")));

        // לאפשר פוקוס ולקבל קלט מקלדת
        displayer.setFocusTraversable(true);
        displayer.setOnMouseClicked(e -> displayer.requestFocus());
        displayer.addEventHandler(KeyEvent.KEY_PRESSED, this::keyPressed); // ⭐ חשוב מאוד

        // טעינת קובץ properties
        appProperties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                appProperties.load(is);
            } else {
                System.err.println("⚠️ config.properties not found");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load config.properties: " + e.getMessage());
        }

        // אנימציית Fade לפאנל התחתון
        // אנימציית Fade לפאנל התחתון
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), bottomPanel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

// אתחול confettiTimer ✅
        confettiTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (ConfettiParticle p : particles) {
                    p.update();
                }
            }
        };
    }


    private void spawnEnemies(int count) {
        Maze maze = viewModel.mazeProperty().get();
        int[][] grid = maze.getGrid();
        Position goal = maze.getGoalPosition();

        enemies.clear();
        double cellW = gameBoard.getWidth() / grid[0].length;
        double cellH = gameBoard.getHeight() / grid.length;

        while (enemies.size() < count) {
            int r = rand.nextInt(grid.length);
            int c = rand.nextInt(grid[0].length);

            boolean valid =
                    grid[r][c] == 0 &&
                            !(r == startR && c == startC) &&
                            !(r == goal.getRowIndex() && c == goal.getColumnIndex()) &&
                            enemies.stream().noneMatch(e -> e.getRow() == r && e.getCol() == c);

            if (valid) {
                boolean horizontal = rand.nextBoolean();
                enemy e = new enemy(r, c, horizontal, cellW, cellH);
                enemies.add(e);
            }
        }

        gameBoard.getMazeDisplayer().setEnemies(enemies);
    }

    @FXML
    private void handleStart() {
        currentR = startR;
        currentC = startC;

        gameBoard.setShowStartImage(false); // הסתרת תמונת התחלה
        gameBoard.setCharacterPosition(currentR, currentC);

        // הבאת פוקוס ל־MazeDisplayer כדי שיקבל אירועי מקשים
        MazeDisplayer displayer = gameBoard.getMazeDisplayer();
        displayer.setFocusTraversable(true);

        // הקפד לשים את זה בתוך runLater כדי לוודא שה־FXML נטען
        Platform.runLater(displayer::requestFocus);

        startButton.setVisible(false);
        startButton.setManaged(false);
        musicPlayer.playMusic();
    }



    @FXML
    public void keyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP, NUMPAD8, DIGIT8 -> moveCharacter(-1, 0);
            case DOWN, NUMPAD2, DIGIT2 -> moveCharacter(1, 0);
            case LEFT, NUMPAD4, DIGIT4 -> moveCharacter(0, -1);
            case RIGHT, NUMPAD6, DIGIT6 -> moveCharacter(0, 1);
            default -> { return; }
        }
        event.consume();
    }

    private void moveCharacter(int dr, int dc) {
        if (mazeGrid == null || currentR < 0) return;

        int newR = currentR + dr, newC = currentC + dc;

        if (newR >= 0 && newR < mazeGrid.length &&
                newC >= 0 && newC < mazeGrid[0].length &&
                mazeGrid[newR][newC] == 0) {

            currentR = newR;
            currentC = newC;
            gameBoard.setCharacterPosition(currentR, currentC);

            double cellW = gameBoard.getWidth() / mazeGrid[0].length;
            double cellH = gameBoard.getHeight() / mazeGrid.length;

            // הזזת אויבים בתזוזה אחת
            for (enemy e : enemies) {
                e.moveOneStep(mazeGrid);
            }

            gameBoard.getMazeDisplayer().setEnemies(enemies);

            // בדיקת התנגשות עם אויבים
            for (enemy e : enemies) {
                if (e.getRow() == currentR && e.getCol() == currentC) {
                    showInfoDialog("Defeat", "The enemy caught you! Try again.");
                    resetToWelcome();
                    return;
                }
            }

            Maze maze = viewModel.mazeProperty().get();
            if (currentR == maze.getGoalPosition().getRowIndex() &&
                    currentC == maze.getGoalPosition().getColumnIndex()) {
                startConfetti();
            }
        }
    }


    private Position moveEnemyOneStep(Position enemy) {
        int rows = mazeGrid.length;
        int cols = mazeGrid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<Position> queue = new LinkedList<>();
        Map<Position, Position> cameFrom = new HashMap<>();

        queue.add(enemy);
        visited[enemy.getRowIndex()][enemy.getColumnIndex()] = true;

        Position target = new Position(currentR, currentC);
        Position firstStep = null;

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            if (current.equals(target)) {
                break;
            }

            for (int[] d : new int[][]{{0,1}, {1,0}, {0,-1}, {-1,0}}) {
                int nr = current.getRowIndex() + d[0];
                int nc = current.getColumnIndex() + d[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols &&
                        mazeGrid[nr][nc] == 0 && !visited[nr][nc]) {

                    Position next = new Position(nr, nc);
                    visited[nr][nc] = true;
                    queue.add(next);
                    cameFrom.put(next, current);
                }
            }
        }

        // שחזור הדרך חזרה מהשחקן לאויב
        Position step = target;
        while (cameFrom.containsKey(step) && !cameFrom.get(step).equals(enemy)) {
            step = cameFrom.get(step);
        }

        // זהו הצעד הבא
        return cameFrom.containsKey(step) ? step : enemy;
    }



    private void startConfetti() {
        musicPlayer.stopMusic();
        playVictorySound();
        particles.clear();
        confettiLayer.getChildren().clear();

        double cellW = gameBoard.getWidth() / mazeGrid[0].length;
        double cellH = gameBoard.getHeight() / mazeGrid.length;
        double centerX = currentC * cellW + cellW / 2;
        double centerY = currentR * cellH + cellH / 2;

        for (int i = 0; i < 200; i++) {
            ConfettiParticle p = new ConfettiParticle(centerX, centerY);
            particles.add(p);
            confettiLayer.getChildren().add(p);
        }
        confettiTimer.start();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> Platform.runLater(() -> {
            if (missionLevel > 0) {
                showInfoDialog("Maze Completed!", "🎉 You completed level " + missionLevel + "!");
                missionLevel++;
                generateMissionMaze();
            } else {
                showInfoDialog("Maze Completed!", "🎉 You solved the maze! Well done!");
                resetToWelcome();
            }
        }));

        pause.play();
    }

    private void playVictorySound() {
        try {
            String path = getClass().getResource("/music/finish-level-sfx.mp3").toExternalForm();
            Media media = new Media(path);
            MediaPlayer player = new MediaPlayer(media);
            player.setVolume(0.6);
            player.play();
        } catch (Exception e) {
            System.err.println("⚠️ Failed to play victory sound: " + e.getMessage());
        }
    }

    private void resetToWelcome() {
        musicPlayer.stopMusic();
        confettiTimer.stop();
        particles.clear();
        confettiLayer.getChildren().clear();
        gameBoard.setMaze(null);
        centerPane.setVisible(false);
        centerPane.setManaged(false);
        bottomPanel.setVisible(false);
        bottomPanel.setManaged(false);
        mainMenuPanel.setVisible(true);
        mainMenuPanel.setManaged(true);
        isMissionMode = false;
        missionLevel = 0;
        rowsField.clear();
        colsField.clear();
        startButton.setVisible(false);
        startButton.setManaged(false);
        ((BorderPane) gameBoard.getScene().getRoot()).setStyle(
                "-fx-background-image: url('/images/welcome_background.png');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;");
    }

    private void generateMissionMaze() {
        if (missionLevel > maxMissionLevels) {
            showInfoDialog("Mission Complete!", "🎉 You've completed all mission levels!");
            resetToWelcome();
            return;
        }

        int size = 10 + (missionLevel - 1) * 5;
        viewModel.generateMaze(size, size);

        bottomPanel.setVisible(false);
        bottomPanel.setManaged(false);
        centerPane.setVisible(true);
        centerPane.setManaged(true);

        // ❗ אחרי יצירת המבוך – נציב אויבים ונעדכן רדיוס ראייה
        Platform.runLater(() -> {
            enemies.clear();
            if (missionLevel >= 3) {
                int count = missionLevel - 2; // שלב 3 → אויב 1, שלב 4 → 2, שלב 5 → 3
                spawnEnemies(count);
            }

            // קביעת רדיוס ראייה לפי רמת משימה
            int visibility;
            if (missionLevel >= 5) visibility = 1;
            else if (missionLevel == 4) visibility = 2;
            else if (missionLevel == 3) visibility = 3;
            else visibility = Integer.MAX_VALUE;

            gameBoard.getMazeDisplayer().setVisibilityRadius(visibility);
        });
    }

    private void showInfoDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //@Override public void setViewModel(MyViewModel vm) { this.viewModel = vm; }
    @Override
    public void setViewModel(MyViewModel vm) {
        this.viewModel = vm;

        viewModel.mazeProperty().addListener((obs, oldMaze, newMaze) -> {
            if (newMaze != null) {
                startR = newMaze.getStartPosition().getRowIndex();
                startC = newMaze.getStartPosition().getColumnIndex();
                currentR = currentC = -1;

                this.mazeGrid = newMaze.getGrid(); // ✅ חובה!

                gameBoard.setMaze(mazeGrid);
                gameBoard.setStartPosition(startR, startC);
                gameBoard.setGoalPosition(
                        newMaze.getGoalPosition().getRowIndex(),
                        newMaze.getGoalPosition().getColumnIndex()
                );

                gameBoard.setCharacterPosition(-1, -1);
                gameBoard.setShowStartImage(true);

                centerPane.setVisible(true);
                centerPane.setManaged(true);
                startButton.setVisible(true);
                startButton.setManaged(true);
                ((BorderPane) gameBoard.getScene().getRoot()).setStyle("");
            }
        });

    }





    public void mouseDragged(MouseEvent e) {}

    @FXML private void handleRegularMaze() {
        isMissionMode = false;
        mainMenuPanel.setVisible(false);
        mainMenuPanel.setManaged(false);
        bottomPanel.setVisible(true);
        gameBoard.getMazeDisplayer().setVisibilityRadius(Integer.MAX_VALUE);
        bottomPanel.setManaged(true);
    }

    @FXML private void handleMissionMaze() {
        mainMenuPanel.setVisible(false);
        mainMenuPanel.setManaged(false);
        centerPane.setVisible(true);
        centerPane.setManaged(true);
        startMissionMode();
    }

    @FXML private void handleNewMaze() { resetToWelcome(); }
    @FXML private void handleSaveMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showSaveDialog(gameBoard.getScene().getWindow());
        if (file != null) viewModel.saveMazeToFile(file);
    }
    @FXML private void handleLoadMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Maze");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        File file = fileChooser.showOpenDialog(gameBoard.getScene().getWindow());
        if (file != null) {
            viewModel.loadMazeFromFile(file);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        }
    }
    @FXML
    private void handleProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in resources");
            }

            Properties props = new Properties();
            props.load(input);

            StringBuilder sb = new StringBuilder();
            for (String key : props.stringPropertyNames()) {
                sb.append(key).append(" = ").append(props.getProperty(key)).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Application Properties");
            alert.setHeaderText("Current Configuration:");
            alert.setContentText(sb.toString());
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load properties");
            alert.setContentText("Make sure config.properties exists in resources.\n\n" + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML private void handleExit()       { System.exit(0); }
    @FXML private void handleHelp()       { showInfoDialog("Help", "Use the arrow keys to move Skye through the maze.\nReach the cute dog at the end to win!"); }
    @FXML private void handleAbout()      { showInfoDialog("About", """
Maze Game v1.0

Developed by:
- Ido Assaf
- Hadar Ofer

Technologies & Algorithms:
- JavaFX for GUI
- MVVM architecture
- Depth-First Search (DFS) for maze generation
- Best-First Search for solving the maze
- Custom animation and design

In Mission Mode, the player must complete 5 increasingly difficult mazes with moving enemies. Good luck!
"""); }
    @FXML private void handleShowSolution() {
        List<Position> solution = viewModel.getMazeSolution();
        if (solution != null) {
            gameBoard.setSolution(solution);
        } else {
            showInfoDialog("No Solution", "Maze not available or unsolvable.");
        }
    }
    @FXML private void handleHideSolution() {
        gameBoard.clearSolution();
    }

    @FXML
    private void handleGenerateMaze() {
        try {
            int rows = Integer.parseInt(rowsField.getText());
            int cols = Integer.parseInt(colsField.getText());
            viewModel.generateMaze(rows, cols);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        } catch (NumberFormatException ex) {
            showInfoDialog("Invalid Input", "Please enter valid maze dimensions.");
        }
    }

    // שיטה חדשה ל־Default Maze
    @FXML
    private void handleDefaultMaze() {
        int rows = Integer.parseInt(appProperties.getProperty("maze.defaultRows", "10"));
        int cols = Integer.parseInt(appProperties.getProperty("maze.defaultCols", "10"));
        viewModel.generateMaze(rows, cols);

        if (isMissionMode) {
            missionLevel = 1;
        } else {
            enemies.clear();
            gameBoard.getMazeDisplayer().setEnemies(Collections.emptyList());
        }

        bottomPanel.setVisible(false);
        bottomPanel.setManaged(false);
    }

    private void startMissionMode() {
        isMissionMode = true;
        missionLevel = 1;
        generateMissionMaze();
    }
}
