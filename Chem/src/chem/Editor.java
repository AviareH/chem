package chem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Editor extends javax.swing.JFrame {
    private List<Point[]> lines = new ArrayList<>();
    private Point firstClickedPoint = null;
    private Point secondClickedPoint = null;
    private int buttonsHit = 0;

    public Editor() {
        initComponents();
        generateGrid();
        setupFrame();
    }

    private void setupFrame() {
        setLayout(null);
        addClearButton();
        Panel.setBounds(0, 40, getWidth(), getHeight() - 40);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                Panel.requestFocusInWindow();
            }
        });
    }

    private void addClearButton() {
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(getWidth() - 120, 5, 80, 30);
        clearButton.setBackground(Color.RED);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> confirmClear());
        add(clearButton);
    }

    private void confirmClear() {
        JFrame quitConfirm = new JFrame();
        int response = JOptionPane.showConfirmDialog(quitConfirm, "Are you sure you want to clear?", "CLEAR", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            lines.clear();
            Panel.repaint();
            resetPoints();
        }
    }

    private void resetPoints() {
        firstClickedPoint = null;
        secondClickedPoint = null;
    }

    private void generateGrid() {
        int rows = 10;
        int columns = 25;
        int spacing = 96;
        int horizontalPadding = 20;
        int verticalPadding = 150;

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                JButton button = createGridButton(i, j, spacing, horizontalPadding, verticalPadding);
                Panel.add(button);
            }
        }
    }

    private JButton createGridButton(int i, int j, int spacing, int horizontalPadding, int verticalPadding) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getModel().isArmed() ? Color.LIGHT_GRAY : getBackground());
                g.fillOval(0, 0, getWidth(), getHeight());
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(getForeground());
                g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
            }

            @Override
            public boolean contains(int x, int y) {
                int radius = getWidth() / 2;
                return Math.pow(x - radius, 2) + Math.pow(y - radius, 2) <= Math.pow(radius, 2);
            }
        };

        button.setBounds(spacing * i + horizontalPadding, spacing * j + verticalPadding, 20, 20);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(Color.GRAY);
        button.addActionListener(e -> handleButtonClick(button));

        return button;
    }

    private void handleButtonClick(JButton button) {
        buttonsHit++;
        if (buttonsHit == 1) {
            firstClickedPoint = new Point(button.getX() + button.getWidth() / 2, button.getY() + button.getHeight() / 2);
        } else if (buttonsHit == 2) {
            secondClickedPoint = new Point(button.getX() + button.getWidth() / 2, button.getY() + button.getHeight() / 2);
            lines.add(new Point[]{firstClickedPoint, secondClickedPoint});
            firstClickedPoint = secondClickedPoint;
            secondClickedPoint = null;
            buttonsHit = 1;
        }
        Panel.repaint();
    }

    private void undoLastLine() {
        if (!lines.isEmpty()) {
            lines.remove(lines.size() - 1);
            Panel.repaint();
        }
    }

    class MyPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawButtons(g);
            drawLines(g);
        }

        private void drawButtons(Graphics g) {
            for (int i = 0; i < Panel.getComponentCount(); i++) {
                JButton button = (JButton) Panel.getComponent(i);
                button.paint(g);
            }
        }

        private void drawLines(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(8));
            g2d.setColor(Color.BLACK);
            for (Point[] line : lines) {
                if (line[0] != null && line[1] != null) {
                    g2d.drawLine(line[0].x, line[0].y, line[1].x, line[1].y);
                }
            }
            g2d.setStroke(originalStroke);
        }
    }

    private void initComponents() {
        Panel = new MyPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseAndKeyListeners();
        setupPanelLayout();
    }

    private void addMouseAndKeyListeners() {
        Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    resetPoints();
                    Panel.repaint();
                }
            }
        });

        Panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    undoLastLine();
                }
            }
        });

        Panel.setFocusable(true);
        Panel.requestFocusInWindow();
    }

    private void setupPanelLayout() {
        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1923, Short.MAX_VALUE)
        );
        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1082, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1923, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1082, Short.MAX_VALUE)
        );

        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Editor().setVisible(true));
    }

    private javax.swing.JPanel Panel;
}
