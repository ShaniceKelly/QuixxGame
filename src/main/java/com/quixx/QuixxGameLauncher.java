import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.*;

public class QuixxGameLauncher {
    private static JFrame gameFrame = null;  // Store game frame reference
    private static JButton returnToGameButton;  // Store return button reference
    private static JFrame launcherFrame;
    private static GameState loadedGameState = null; // Store loaded game state

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowLauncher());
    }

    public static void createAndShowLauncher() {
        launcherFrame = new JFrame("Quixx Setup");
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Quixx");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    
        // Start New button
        JButton newGameButton = new JButton("Start New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(e -> {
            launcherFrame.setVisible(false);
            if (gameFrame != null) {
                // Dispose old game if starting new
                gameFrame.dispose();
                gameFrame = null;
            }
            createAndShowGame();
        });

        // Return to Game button
        returnToGameButton = new JButton("Return to Game");
        returnToGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnToGameButton.setEnabled(false);  // Initially disabled
        returnToGameButton.addActionListener(e -> {
            if (gameFrame != null) {
                launcherFrame.setVisible(false);
                gameFrame.setVisible(true);
            }
        });

         // Load Game button
         JButton loadGameButton = new JButton("Load Game");
         loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(launcherFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    loadedGameState = (GameState) ois.readObject();
                    launcherFrame.setVisible(false);
                    if (gameFrame != null) {
                        gameFrame.dispose();
                        gameFrame = null;
                    }
                    createAndShowGame();
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(launcherFrame, 
                        "Error loading game: " + ex.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
 
         // Exit button
         JButton exitButton = new JButton("Exit");
         exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
         exitButton.addActionListener(e -> System.exit(0));

        

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(newGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(loadGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(returnToGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(exitButton);

        launcherFrame.add(mainPanel);
        launcherFrame.pack();
        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setVisible(true);
    }

    public static void handleBackToMenu() {
        if (gameFrame != null) {
            gameFrame.setVisible(false);
            returnToGameButton.setEnabled(true);
            launcherFrame.setVisible(true);
        }
    }

    private static void createAndShowGame() {
        if (gameFrame == null) {
            gameFrame = new JFrame("Quixx");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Quixx game = new Quixx(gameFrame, loadedGameState); // Pass loaded state
            gameFrame.add(game);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            
        }
        
        gameFrame.setVisible(true);
        returnToGameButton.setEnabled(true);
    }
}