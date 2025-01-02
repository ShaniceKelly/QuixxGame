import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class DiceRollerGUI extends JPanel {
    private DiePanel[] dicePanels;
    private JButton rollButton;
    private JLabel sumLabel;
    private final Random random;
    
    // Colors for dice
    private final Color[] diceColors = {
        Color.WHITE,    // Die 1 (White)
        Color.WHITE,    // Die 2 (White)
        Color.RED,      // Die 3
        Color.GREEN,    // Die 4
        Color.BLUE,     // Die 5
        Color.YELLOW    // Die 6
    };

    public DiceRollerGUI() {
        random = new Random();
        setupGUI();
    }

    private void setupGUI() {
        setBorder(BorderFactory.createTitledBorder("Dice"));
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create dice panel
        JPanel dicePanel = new JPanel(new GridLayout(2, 3, 10, 10));
        dicePanels = new DiePanel[6];
        
        // Initialize dice panels with colors
        for (int i = 0; i < 6; i++) {
            dicePanels[i] = new DiePanel(diceColors[i]);
            dicePanel.add(dicePanels[i]);
        }

        // Create control panel
        JPanel controlPanel = new JPanel();
        rollButton = new JButton("Roll Dice");
        rollButton.setFont(new Font("Arial", Font.BOLD, 14));
        sumLabel = new JLabel("Sum of White Dice: 0");
        sumLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add roll button action
        rollButton.addActionListener(e -> rollDice());

        controlPanel.add(rollButton);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(sumLabel);

        // Add components to main panel
        mainPanel.add(dicePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Set frame properties
        setSize(500, 400);
    }

    private void rollDice() {
        // Animate dice rolling
        Timer timer = new Timer(50, null);
        int[] rollCount = {0};
        
        timer.addActionListener(e -> {
            if (rollCount[0] < 20) {
                // Rolling animation
                for (DiePanel panel : dicePanels) {
                    panel.setValue(random.nextInt(6) + 1);
                }
                rollCount[0]++;
            } else {
                // Final values
                timer.stop();
                sumWhiteDice();
                rollButton.setEnabled(true);
            }
        });

        rollButton.setEnabled(false);
        timer.start();
    }

    private void sumWhiteDice() {
        // Only sum the first two dice (white dice)
        int sum = dicePanels[0].getValue() + dicePanels[1].getValue();
        sumLabel.setText("Sum of White Dice: " + sum);
    }

}