package View;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class NewMazeDialog extends Dialog<int[]> {

    private final TextField rowsField = new TextField();
    private final TextField colsField = new TextField();

    public NewMazeDialog() {
        setTitle("New Maze");
        setHeaderText("Enter dimensions for the new maze:");

        // Set the button types
        ButtonType generateButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        rowsField.setPromptText("Rows");
        colsField.setPromptText("Columns");

        grid.add(new Label("Rows:"), 0, 0);
        grid.add(rowsField, 1, 0);
        grid.add(new Label("Columns:"), 0, 1);
        grid.add(colsField, 1, 1);

        getDialogPane().setContent(grid);

        // Convert result to int[] when Generate is clicked
        setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType) {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int cols = Integer.parseInt(colsField.getText());

                    if (rows < 2 || cols < 2)
                        throw new NumberFormatException("Too small");

                    return new int[]{rows, cols};
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Please enter valid numbers greater than 1");
                    alert.showAndWait();
                }
            }
            return null;
        });
    }
}
