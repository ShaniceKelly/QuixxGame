import java.awt.*;
import javax.swing.*;

public class DiePanel extends JPanel {
    private int value = 1;
    private final Color dieColor;

    public DiePanel(Color color) {
        dieColor = color;
        setPreferredSize(new Dimension(80, 80));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.WHITE);
    }

    public void setValue(int value) {
        this.value = value;
        repaint();
    }

    public int getValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw die background
        g2d.setColor(dieColor);
        g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 10, 10);

        // Draw dots
        g2d.setColor(dieColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        int dotSize = 8;
        int[][] dotPositions = getDotPositions(value);
        
        for (int[] pos : dotPositions) {
            g2d.fillOval(pos[0] - dotSize/2, pos[1] - dotSize/2, 
                        dotSize, dotSize);
        }
    }

    private int[][] getDotPositions(int value) {
        int w = getWidth();
        int h = getHeight();
        int[][] positions;
        
        positions = switch (value) {
            case 1 -> new int[][]{{w/2, h/2}};
            case 2 -> new int[][]{{w/4, h/4}, {3*w/4, 3*h/4}};
            case 3 -> new int[][]{{w/4, h/4}, {w/2, h/2}, {3*w/4, 3*h/4}};
            case 4 -> new int[][]{{w/4, h/4}, {3*w/4, h/4}, 
                {w/4, 3*h/4}, {3*w/4, 3*h/4}};
            case 5 -> new int[][]{{w/4, h/4}, {3*w/4, h/4}, 
                {w/2, h/2},
                {w/4, 3*h/4}, {3*w/4, 3*h/4}};
            case 6 -> new int[][]{{w/4, h/4}, {3*w/4, h/4}, 
                {w/4, h/2}, {3*w/4, h/2},
                {w/4, 3*h/4}, {3*w/4, 3*h/4}};
            default -> new int[0][0];
        };
        return positions;
    }
}  