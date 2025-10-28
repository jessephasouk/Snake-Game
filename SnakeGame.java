import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 25;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int DELAY = 100;
    
    private final LinkedList<Point> snake;
    private Point food;
    private char direction;
    private final Queue<Character> directionQueue;
    private boolean running;
    private boolean won;
    private Timer timer;
    private final Random random;
    private int score;
    
    public SnakeGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        snake = new LinkedList<>();
        directionQueue = new LinkedList<>();
        random = new Random();
    }
    
    public void init() {
        addKeyListener(this);
        startGame();
    }
    
    private void startGame() {
        snake.clear();
        snake.add(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2));
        direction = 'R';
        directionQueue.clear();
        score = 0;
        won = false;
        spawnFood();
        running = true;
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH);
            y = random.nextInt(BOARD_HEIGHT);
        } while (snake.contains(new Point(x, y)));
        
        food = new Point(x, y);
    }
    
    private void move() {
        if (!directionQueue.isEmpty()) {
            direction = directionQueue.poll();
        }
        
        Point head = snake.getFirst();
        Point newHead = new Point(head);
        
        switch (direction) {
            case 'U' -> newHead.y--;
            case 'D' -> newHead.y++;
            case 'L' -> newHead.x--;
            case 'R' -> newHead.x++;
        }
        
        // Check collision with walls
        if (newHead.x < 0 || newHead.x >= BOARD_WIDTH || 
            newHead.y < 0 || newHead.y >= BOARD_HEIGHT) {
            running = false;
            return;
        }
        
        // Check collision with self
        if (snake.contains(newHead)) {
            running = false;
            return;
        }
        
        snake.addFirst(newHead);
        
        // Check if food eaten
        if (newHead.equals(food)) {
            score++;
            
            // Check win condition
            if (snake.size() == BOARD_WIDTH * BOARD_HEIGHT) {
                running = false;
                won = true;
                return;
            }
            
            spawnFood();
        } else {
            snake.removeLast();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 45));
                }
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        } else {
            // Game over or win screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String msg = won ? "YOU WIN! Score: " + score : "Game Over! Score: " + score;
            g.drawString(msg, (BOARD_WIDTH * TILE_SIZE - metrics.stringWidth(msg)) / 2, 
                        BOARD_HEIGHT * TILE_SIZE / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String restart = "Press SPACE to restart";
            metrics = getFontMetrics(g.getFont());
            g.drawString(restart, (BOARD_WIDTH * TILE_SIZE - metrics.stringWidth(restart)) / 2, 
                        BOARD_HEIGHT * TILE_SIZE / 2 + 40);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (!running && key == KeyEvent.VK_SPACE) {
            startGame();
            return;
        }
        
        // Get the last direction in queue, or current direction if queue is empty
        Character lastDirObj = directionQueue.peek();
        char lastDirection = (lastDirObj != null) ? lastDirObj : direction;
        
        // Only allow perpendicular turns (no 180° reversals)
        if (key == KeyEvent.VK_UP && lastDirection != 'D' && lastDirection != 'U') {
            directionQueue.offer('U');
        } else if (key == KeyEvent.VK_DOWN && lastDirection != 'U' && lastDirection != 'D') {
            directionQueue.offer('D');
        } else if (key == KeyEvent.VK_LEFT && lastDirection != 'R' && lastDirection != 'L') {
            directionQueue.offer('L');
        } else if (key == KeyEvent.VK_RIGHT && lastDirection != 'L' && lastDirection != 'R') {
            directionQueue.offer('R');
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        game.init();
    }
}