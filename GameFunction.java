
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public final class GameFunction extends JPanel {

    private final List<List<JButton>> rows;
    private static final int ROWS = 5;
    private static final int MIN_NUM = 2;
    private static final int MAX_NUM = 12;
    private final Stack<GameState> undoStack;
    private final Stack<GameState> redoStack;
    private int consecutiveSkips = 0;  // Track consecutive skips
    private final JLabel totalScoreLabel;
    private final JLabel[] rowScoreLabels;
    private GameControlPanel controlPanel;
    private final JFrame parentFrame; // Add reference to parent frame

    public GameFunction(JFrame parentFrame, GameState loadedState) {
        this.parentFrame = parentFrame;
        setBorder(BorderFactory.createTitledBorder("Score Card"));
        setLayout(new BorderLayout());

        rows = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        // Initialize score labels array
        rowScoreLabels = new JLabel[ROWS];

        // Create score panel with row scores and total
        JPanel scorePanel = new JPanel(new GridLayout(ROWS + 1, 1, 5, 5));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create individual row score labels
        for (int i = 0; i < ROWS; i++) {
            rowScoreLabels[i] = new JLabel("Row " + (i + 1) + ": 0 points", SwingConstants.LEFT);
            rowScoreLabels[i].setFont(new Font("Arial", Font.PLAIN, 14));
            rowScoreLabels[i].setOpaque(true);
            rowScoreLabels[i].setBackground(new Color(240, 240, 240));
            rowScoreLabels[i].setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            scorePanel.add(rowScoreLabels[i]);
        }

        // Create total score label
        totalScoreLabel = new JLabel("Total Score: 0", SwingConstants.CENTER);
        totalScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalScoreLabel.setOpaque(true);
        totalScoreLabel.setBackground(new Color(220, 220, 220));
        totalScoreLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        scorePanel.add(totalScoreLabel);

        add(scorePanel, BorderLayout.EAST);

        // Create main game panel
        JPanel gamePanel = new JPanel(new GridLayout(ROWS, 1, 5, 5));
        initializeButtons(gamePanel);
        add(gamePanel, BorderLayout.CENTER);

        // Create control panel
        initializeControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // If we have a loaded state, restore it
        if (loadedState != null) {
            restoreState(loadedState);
            // Clear undo/redo stacks when loading a saved game
            undoStack.clear();
            redoStack.clear();
        }
    }

    private void updateScore() {
        int totalScore = 0;

        // Calculate score for each regular row (rows 0-3)
        for (int i = 0; i < rows.size() - 1; i++) {
            List<JButton> row = rows.get(i);
            int xCount = 0;

            // Count X's in the row
            for (JButton button : row) {
                if (button.getText().equals("X")) {
                    xCount++;
                }
            }

            // Calculate points based on number of X's
            int rowScore = calculatePointsForMarks(xCount);
            totalScore += rowScore;

            // Update row score label with detailed information
            String rowName = "";
            switch (i) {
                case 0 ->
                    rowName = "Red Row";
                case 1 ->
                    rowName = "Yellow Row";
                case 2 ->
                    rowName = "Green Row";
                case 3 ->
                    rowName = "Blue Row";
            }
            rowScoreLabels[i].setText(String.format("%s: %d points (%d X's)", rowName, rowScore, xCount));
        }

        // Handle penalty row (row 5)
        List<JButton> penaltyRow = rows.get(rows.size() - 1);
        int penaltyCount = 0;
        for (JButton button : penaltyRow) {
            if (button.getText().equals("X")) {
                penaltyCount++;
            }
        }
        int penaltyScore = penaltyCount * 5;
        totalScore -= penaltyScore;
        rowScoreLabels[4].setText(String.format("Penalties: -%d points (%d X's)", penaltyScore, penaltyCount));

        // Update total score
        totalScoreLabel.setText(String.format("Total Score: %d", totalScore));
    }

    private int calculatePointsForMarks(int count) {
        return switch (count) {
            case 1 ->
                1; // 1 X = 1 point
            case 2 ->
                3; // 2 X = 3 points
            case 3 ->
                6; // 3 X = 6 points
            case 4 ->
                10; // 4 X = 10 points
            case 5 ->
                15; // 5 X = 15 points
            case 6 ->
                21; // 6 X = 21 points
            case 7 ->
                28; // 7 X = 28 points
            case 8 ->
                36; // 8 X = 36 points
            case 9 ->
                45; // 9 X = 45 points
            case 10 ->
                55; // 10 X = 55 points
            case 11 ->
                66; // 11 X = 66 points
            case 12 ->
                78; // 12 X = 78 points
            default ->
                0;
        };
    }

    private void initializeControlPanel() {
        controlPanel = new GameControlPanel(this, parentFrame); // Pass parent frame reference
    }

    public void handleSkip() {
        saveStateForUndo();
        consecutiveSkips++;

        if (consecutiveSkips == 1) {
            List<JButton> penaltyRow = rows.get(4);
            for (JButton button : penaltyRow) {
                if (button.isEnabled()) {
                    button.setText("X");
                    button.setEnabled(false);
                    break;
                }
            }
            consecutiveSkips = 0;

            boolean allPenaltyMarked = true;
            for (JButton button : penaltyRow) {
                if (button.isEnabled()) {
                    allPenaltyMarked = false;
                    break;
                }
            }

            if (allPenaltyMarked) {
                endGame();
            }
        }

        redoStack.clear();
        controlPanel.setRedoEnabled(false);
        updateScore();
    }

    public void restoreState(GameState state) {
        for (int i = 0; i < rows.size(); i++) {
            List<JButton> row = rows.get(i);
            List<ButtonState> rowState = state.getRows().get(i);
            for (int j = 0; j < row.size(); j++) {
                JButton button = row.get(j);
                ButtonState buttonState = rowState.get(j);
                button.setText(buttonState.getText());
                button.setEnabled(buttonState.isEnabled());
            }
        }
        updateScore(); // Update score when state is restored
    }

    private void handleButtonClick(JButton clickedButton) {
        consecutiveSkips = 0;

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<JButton> row = rows.get(rowIndex);
            int buttonIndex = row.indexOf(clickedButton);

            if (buttonIndex != -1) {
                clickedButton.setText("X");
                clickedButton.setEnabled(false);

                // Disable all buttons to the left, including the lock button if it's to the left
                for (int i = 0; i <= buttonIndex; i++) {
                    row.get(i).setEnabled(false);
                }
                updateScore();
                redoStack.clear();
                controlPanel.setRedoEnabled(false);
                break;
            }
        }
    }

    private void handleLockClick(List<JButton> rowButtons) {
        // Disable all buttons in the row
        for (JButton button : rowButtons) {
            if (button != rowButtons.get(rowButtons.size() - 1) && button.isEnabled()) { // Skip the lock button itself
                button.setEnabled(false);
            }
        }

        redoStack.clear();
        controlPanel.setRedoEnabled(false);
        updateScore();
    }

    public void undoMove() {
        if (!undoStack.isEmpty()) {
            GameState currentState = new GameState();
            for (List<JButton> row : rows) {
                List<ButtonState> rowState = new ArrayList<>();
                for (JButton button : row) {
                    rowState.add(new ButtonState(button.getText(), button.isEnabled()));
                }
                currentState.addRow(rowState);
            }
            redoStack.push(currentState);
            controlPanel.setRedoEnabled(true);

            GameState previousState = undoStack.pop();
            restoreState(previousState);
        }
    }

    public void redoMove() {
        if (!redoStack.isEmpty()) {
            saveStateForUndo();
            GameState redoState = redoStack.pop();
            restoreState(redoState);
            controlPanel.setRedoEnabled(!redoStack.isEmpty());
        }
    }

    public void resetBoard() {
        undoStack.clear();
        redoStack.clear();
        controlPanel.setRedoEnabled(false);
        consecutiveSkips = 0;

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<JButton> row = rows.get(rowIndex);
            for (int i = 0; i < row.size(); i++) {
                JButton button = row.get(i);

                // Handle different row types
                if (rowIndex < 4) {  // Regular rows (not penalty row)
                    if (i == row.size() - 1) {  // Lock button
                        button.setText("Lock");
                    } else if (rowIndex < 2) {  // Rows 1 and 2
                        button.setText(String.valueOf(i + MIN_NUM));
                    } else {  // Rows 3 and 4
                        button.setText(String.valueOf(MAX_NUM - i));
                    }
                } else {   // Penalty row
                    button.setText(" ");
                }
                button.setEnabled(true);
            }
        }
        updateScore();
    }

    private void endGame() {
        // Disable all buttons in the game
        for (List<JButton> row : rows) {
            for (JButton button : row) {
                button.setEnabled(false);
            }
        }

        // Show game over message
        JOptionPane.showMessageDialog(this,
                "Game Over! All penalty buttons have been marked.",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createLockButton(List<JButton> rowButtons, Color color) {
        JButton lockButton = new JButton("Lock");
        lockButton.setPreferredSize(new Dimension(70, 50));
        lockButton.setFont(new Font("Arial", Font.BOLD, 14));
        lockButton.setBackground(color);
        lockButton.setOpaque(true);

        // Add single-click lock button functionality
        lockButton.addActionListener(e -> {
            saveStateForUndo();
            // Mark with X and lock the row in one action
            lockButton.setText("X");
            lockButton.setEnabled(false);
            handleLockClick(rowButtons);
            redoStack.clear();
            controlPanel.setRedoEnabled(false);
            updateScore();
        });

        return lockButton;
    }

    private void initializeButtons(JPanel gamePanel) {
        Color[] rowColors = {
            Color.RED, // Row 1
            Color.YELLOW, // Row 2
            Color.GREEN, // Row 3
            Color.BLUE, // Row 4
            Color.WHITE // Row 5 (Penalty)
        };

        // Create rows 1 and 2 (2-12)
        for (int i = 0; i < 2; i++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            List<JButton> rowButtons = new ArrayList<>();

            for (int num = MIN_NUM; num <= MAX_NUM; num++) {
                JButton button = createButton(String.valueOf(num));
                button.setBackground(rowColors[i]);
                button.setOpaque(true);
                rowButtons.add(button);
                rowPanel.add(button);
            }

            // Add lock button
            JButton lockButton = createLockButton(rowButtons, rowColors[i]);
            rowButtons.add(lockButton);  // Add lock button to the row's button list
            rowPanel.add(lockButton);

            rows.add(rowButtons);
            gamePanel.add(rowPanel);
        }

        // Create rows 3 and 4 (12-2)
        for (int i = 0; i < 2; i++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            List<JButton> rowButtons = new ArrayList<>();

            for (int num = MAX_NUM; num >= MIN_NUM; num--) {
                JButton button = createButton(String.valueOf(num));
                button.setBackground(rowColors[i + 2]);
                button.setOpaque(true);
                rowButtons.add(button);
                rowPanel.add(button);
            }

            // Add lock button
            JButton lockButton = createLockButton(rowButtons, rowColors[i + 2]);
            rowButtons.add(lockButton);  // Add lock button to the row's button list
            rowPanel.add(lockButton);

            rows.add(rowButtons);
            gamePanel.add(rowPanel);
        }

        // Create penalty row (Row 5)
        JPanel penaltyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        List<JButton> penaltyButtons = new ArrayList<>();

        // Add label for penalty row
        JLabel penaltyLabel = new JLabel("Penalty:");
        penaltyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        penaltyPanel.add(penaltyLabel);

        // Add 4 unlabeled buttons
        for (int i = 0; i < 4; i++) {
            JButton button = createButton(" ");  // Empty space as label
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
            penaltyButtons.add(button);
            penaltyPanel.add(button);
        }

        rows.add(penaltyButtons);
        gamePanel.add(penaltyPanel);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(55, 55));
        button.setFont(new Font("Arial", Font.BOLD, 16));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStateForUndo();
                JButton clickedButton = (JButton) e.getSource();
                handleButtonClick(clickedButton);
                redoStack.clear();
                controlPanel.setRedoEnabled(true);;
            }
        });

        return button;
    }

    private void saveStateForUndo() {
        GameState currentState = new GameState();
        for (List<JButton> row : rows) {
            List<ButtonState> rowState = new ArrayList<>();
            for (JButton button : row) {
                rowState.add(new ButtonState(button.getText(), button.isEnabled()));
            }
            currentState.addRow(rowState);
        }
        undoStack.push(currentState);
    }

    public void saveGame() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    GameState currentState = new GameState();
                    for (List<JButton> row : rows) {
                        List<ButtonState> rowState = new ArrayList<>();
                        for (JButton button : row) {
                            rowState.add(new ButtonState(button.getText(), button.isEnabled()));
                        }
                        currentState.addRow(rowState);
                    }
                    oos.writeObject(currentState);
                }
                JOptionPane.showMessageDialog(this, "Game saved successfully!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

class GameState implements Serializable {

    private final List<List<ButtonState>> rows = new ArrayList<>();

    public void addRow(List<ButtonState> row) {
        rows.add(row);
    }

    public List<List<ButtonState>> getRows() {
        return rows;
    }
}

class ButtonState implements Serializable {

    private final String text;
    private final boolean enabled;

    public ButtonState(String text, boolean enabled) {
        this.text = text;
        this.enabled = enabled;
    }

    public String getText() {
        return text;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
