import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GameControlPanel extends JPanel {
    private JButton redoButton;
    private final GameFunction game;  // Reference to the main game

    public GameControlPanel(GameFunction game, JFrame parentFrame) {
        super(new FlowLayout());
        this.game = game;
        initializeControls();
    }

    private void initializeControls() {
        JButton skipButton = createControlButton("Skip Turn", e -> game.handleSkip());
        JButton undoButton = createControlButton("Undo", e -> game.undoMove());
        redoButton = createControlButton("Redo", e -> game.redoMove());
        JButton resetButton = createControlButton("Reset Board", e -> game.resetBoard());
        JButton saveButton = createControlButton("Save Game", e -> game.saveGame());
        JButton mainMenuButton = createControlButton("Back to Main Menu", e -> returnToMainMenu());

        // Initially disable redo button
        redoButton.setEnabled(false);

        // Add all buttons to the panel
        add(skipButton);
        add(undoButton);
        add(redoButton);
        add(resetButton);
        add(saveButton);
        add(mainMenuButton);
    }

    private void returnToMainMenu() {
        QuixxGameLauncher.handleBackToMenu();
    }

    private JButton createControlButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    // Method to enable/disable redo button
    public void setRedoEnabled(boolean enabled) {
        redoButton.setEnabled(enabled);
    }

    // Method to get the redo button
    public JButton getRedoButton() {
        return redoButton;
    }
}