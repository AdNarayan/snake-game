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
            SoundManager.playEat();
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
            SoundManager.playGameOver();
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                gameOver = true;
                SoundManager.playGameOver();
                break;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (running || gameOver) {
            g2d.setColor(new Color(30, 30, 30));
            for (int x = 0; x <= BOARD_WIDTH; x++) {
                g2d.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, WINDOW_HEIGHT);
            }
            for (int y = 0; y <= BOARD_HEIGHT; y++) {
                g2d.drawLine(0, y * TILE_SIZE, WINDOW_WIDTH, y * TILE_SIZE);
            }

            int cx = food.x * TILE_SIZE + TILE_SIZE / 2;
            int cy = food.y * TILE_SIZE + TILE_SIZE / 2;
            for (int r = 3; r >= 0; r--) {
                g2d.setColor(new Color(255, 50, 50, 60 - r * 15));
                g2d.fillOval(cx - (TILE_SIZE / 2 + r * 3), cy - (TILE_SIZE / 2 + r * 3),
                             TILE_SIZE + r * 6, TILE_SIZE + r * 6);
            }
            g2d.setColor(Color.RED);
            g2d.fillOval(food.x * TILE_SIZE + 2, food.y * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4);

            int size = snake.size();
            for (int i = 0; i < size; i++) {
                Point segment = snake.get(i);
                float ratio = (float) i / Math.max(size - 1, 1);
                int red = (int) (30 * ratio);
                int green = (int) (255 - 150 * ratio);
                int blue = (int) (80 * ratio);
                g2d.setColor(new Color(red, green, blue));
                if (i == 0) {
                    g2d.fillRoundRect(segment.x * TILE_SIZE + 1, segment.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);
                } else {
                    g2d.fillRect(segment.x * TILE_SIZE + 1, segment.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                }
            }

            g2d.setColor(new Color(0, 0, 0, 160));
            g2d.fillRect(0, 0, WINDOW_WIDTH, 35);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2d.drawString("Score: " + score, 12, 25);

            if (gameOver) {
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 44));
                FontMetrics metrics = getFontMetrics(g2d.getFont());
                String gameOverText = "Game Over";
                String restartText = "Press SPACE to restart";
                g2d.drawString(gameOverText, (WINDOW_WIDTH - metrics.stringWidth(gameOverText)) / 2, WINDOW_HEIGHT / 2 - 20);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 22));
                metrics = getFontMetrics(g2d.getFont());
                g2d.drawString(restartText, (WINDOW_WIDTH - metrics.stringWidth(restartText)) / 2, WINDOW_HEIGHT / 2 + 24);
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
