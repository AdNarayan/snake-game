import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 20;
    private static final int BOARD_WIDTH = 30;
    private static final int BOARD_HEIGHT = 30;
    private static final int WINDOW_WIDTH = TILE_SIZE * BOARD_WIDTH;
    private static final int WINDOW_HEIGHT = TILE_SIZE * BOARD_HEIGHT;
    private static final int INITIAL_DELAY = 140;

    private final ArrayList<Point> snake;
    private Point food;
    private char direction = 'R';
    private boolean running = false;
    private boolean gameOver = false;
    private final Timer timer;
    private final Random random;
    private int score;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        random = new Random();
        snake = new ArrayList<>();
        timer = new Timer(INITIAL_DELAY, this);
        startGame();
    }

    public void startGame() {
        snake.clear();
        snake.add(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2));
        snake.add(new Point(BOARD_WIDTH / 2 - 1, BOARD_HEIGHT / 2));
        snake.add(new Point(BOARD_WIDTH / 2 - 2, BOARD_HEIGHT / 2));
        direction = 'R';
        score = 0;
        gameOver = false;
        spawnFood();
        running = true;
        timer.start();
    }

    public void spawnFood() {
        Point newFood;
        do {
            newFood = new Point(random.nextInt(BOARD_WIDTH), random.nextInt(BOARD_HEIGHT));
        } while (snake.contains(newFood));
        food = newFood;
    }

    public void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 'U': newHead.translate(0, -1); break;
            case 'D': newHead.translate(0, 1); break;
            case 'L': newHead.translate(-1, 0); break;
            case 'R': newHead.translate(1, 0); break;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    public void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= BOARD_WIDTH || head.y < 0 || head.y >= BOARD_HEIGHT) {
            running = false;
            gameOver = true;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                gameOver = true;
                break;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running || gameOver) {
            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                Point segment = snake.get(i);
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(segment.x * TILE_SIZE, segment.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 10, 25);

            if (gameOver) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics metrics = getFontMetrics(g.getFont());
                String gameOverText = "Game Over";
                String restartText = "Press SPACE to restart";
                g.drawString(gameOverText, (WINDOW_WIDTH - metrics.stringWidth(gameOverText)) / 2, WINDOW_HEIGHT / 2 - 20);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                metrics = getFontMetrics(g.getFont());
                g.drawString(restartText, (WINDOW_WIDTH - metrics.stringWidth(restartText)) / 2, WINDOW_HEIGHT / 2 + 20);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_SPACE:
                if (gameOver) startGame();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
