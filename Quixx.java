
import java.awt.*;
import javax.swing.*;

public class Quixx extends JPanel {  // Changed from JFrame to JPanel

    private final GameFunction gameFunction;
    private final DiceRollerGUI diceRoller;
    // Add reference to parent frame

    public Quixx(JFrame parentFrame, GameState loadedState) {
        // Set up the main panel with BorderLayout
        setLayout(new BorderLayout());

        // Initialize components
        gameFunction = new GameFunction(parentFrame, loadedState);  // Pass the parent frame
        diceRoller = new DiceRollerGUI();

        // Create separate panels for each component
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(gameFunction);

        JPanel dicePanel = new JPanel();
        dicePanel.add(diceRoller);

        // Add components to the main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(dicePanel, BorderLayout.CENTER);
    }

    // Getter methods for accessing components
    public GameFunction getButtonGame() {
        return gameFunction;
    }

    public DiceRollerGUI getDiceRoller() {
        return diceRoller;
    }

    // Method to handle communication between components
    public void synchronizeComponents() {
        //To Do:
        // Add code here to handle any interaction between GameFunction and DiceRollerGUI
    }

    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
        });
    }
}
