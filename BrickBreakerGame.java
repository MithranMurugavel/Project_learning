import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class BrickBreakerGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DELAY = 10;
    
    private Timer timer;
    private Paddle paddle;
    private Ball ball;
    private ArrayList<Brick> bricks;
    private ArrayList<PowerUp> powerUps;
    
    private boolean inGame = false;
    private boolean gameOver = false;
    private int score = 0;
    private int lives = 3;
    private int currentLevel = 1;
    
    // Random generator for power-ups
    private Random random = new Random();
    
    public BrickBreakerGame() {
        initGame();
    }
    
    private void initGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        paddle = new Paddle(WIDTH / 2, HEIGHT - 50);
        resetBall();
        
        bricks = new ArrayList<>();
        powerUps = new ArrayList<>();
        
        loadLevel(currentLevel);
        
        timer = new Timer(DELAY, this);
    }
    
    private void resetBall() {
        ball = new Ball(WIDTH / 2, HEIGHT - 70);
    }
    
    private void loadLevel(int level) {
        bricks.clear();
        
        int brickWidth = 70;
        int brickHeight = 30;
        int gap = 5;
        
        // Different layouts based on level
        switch (level) {
            case 1:
                // Basic grid pattern
                for (int y = 50; y < 250; y += brickHeight + gap) {
                    for (int x = 50; x < WIDTH - 50; x += brickWidth + gap) {
                        int strength = 1;
                        Color color = Color.RED;
                        bricks.add(new Brick(x, y, brickWidth, brickHeight, strength, color));
                    }
                }
                break;
                
            case 2:
                // Alternating pattern with some stronger bricks
                for (int y = 50; y < 300; y += brickHeight + gap) {
                    for (int x = 50; x < WIDTH - 50; x += brickWidth + gap) {
                        int strength = (y + x) % 3 + 1;
                        Color color;
                        switch (strength) {
                            case 1: color = Color.RED; break;
                            case 2: color = Color.ORANGE; break;
                            default: color = Color.YELLOW; break;
                        }
                        bricks.add(new Brick(x, y, brickWidth, brickHeight, strength, color));
                    }
                }
                break;
                
            case 3:
                // Diamond pattern
                int centerX = WIDTH / 2;
                int centerY = 200;
                for (int y = 50; y < 350; y += brickHeight + gap) {
                    for (int x = 50; x < WIDTH - 50; x += brickWidth + gap) {
                        double distance = Math.abs(x - centerX) / 10 + Math.abs(y - centerY) / 5;
                        if (distance < 25) {
                            int strength = 3 - (int)(distance / 10);
                            if (strength < 1) strength = 1;
                            
                            Color color;
                            switch (strength) {
                                case 1: color = Color.GREEN; break;
                                case 2: color = Color.BLUE; break;
                                default: color = Color.MAGENTA; break;
                            }
                            bricks.add(new Brick(x, y, brickWidth, brickHeight, strength, color));
                        }
                    }
                }
                break;
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!inGame) {
            drawIntro(g2d);
        } else if (gameOver) {
            drawGameOver(g2d);
        } else {
            drawGame(g2d);
        }
        
        Toolkit.getDefaultToolkit().sync();
    }
    
    private void drawIntro(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String message = "BRICK BREAKER";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2 - 50);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        message = "Press SPACE to start";
        fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2 + 20);
        
        message = "Use LEFT and RIGHT arrows to move the paddle";
        fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2 + 50);
    }
    
    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String message = gameWon() ? "LEVEL COMPLETE!" : "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2 - 50);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        message = "Score: " + score;
        fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameWon() && currentLevel < 3) {
            message = "Press SPACE to continue to level " + (currentLevel + 1);
        } else {
            message = "Press SPACE to play again";
        }
        fm = g2d.getFontMetrics();
        g2d.drawString(message, (WIDTH - fm.stringWidth(message)) / 2, HEIGHT / 2 + 50);
    }
    
    private void drawGame(Graphics2D g2d) {
        // Draw paddle
        paddle.draw(g2d);
        
        // Draw ball
        ball.draw(g2d);
        
        // Draw bricks
        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                brick.draw(g2d);
            }
        }
        
        // Draw power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g2d);
        }
        
        // Draw score and lives
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("Lives: " + lives, WIDTH - 100, 30);
        g2d.drawString("Level: " + currentLevel, WIDTH / 2 - 30, 30);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame && !gameOver) {
            updateGame();
        }
        
        repaint();
    }
    
    private void updateGame() {
        // Move paddle
        paddle.move();
        
        // Move ball
        ball.move();
        
        // Check collisions
        checkCollisions();
        
        // Move power-ups
        updatePowerUps();
        
        // Check if level is completed
        if (bricks.isEmpty()) {
            gameOver = true;
        }
    }
    
    private void updatePowerUps() {
        ArrayList<PowerUp> toRemove = new ArrayList<>();
        
        for (PowerUp powerUp : powerUps) {
            powerUp.move();
            
            // Check if power-up is caught
            if (powerUp.getBounds().intersects(paddle.getBounds())) {
                applyPowerUp(powerUp.getType());
                toRemove.add(powerUp);
            }
            
            // Remove if off-screen
            if (powerUp.getY() > HEIGHT) {
                toRemove.add(powerUp);
            }
        }
        
        powerUps.removeAll(toRemove);
    }
    
    private void applyPowerUp(PowerUpType type) {
        switch (type) {
            case EXPAND_PADDLE:
                paddle.expand();
                break;
            case SHRINK_PADDLE:
                paddle.shrink();
                break;
            case EXTRA_LIFE:
                lives++;
                break;
            case FAST_BALL:
                ball.speedUp();
                break;
            case SLOW_BALL:
                ball.slowDown();
                break;
        }
    }
    
    private void checkCollisions() {
        // Check ball-paddle collision
        if (ball.getBounds().intersects(paddle.getBounds())) {
            // Calculate the relative position of the ball on the paddle
            double relativeIntersectX = (ball.getX() + ball.getSize() / 2) - (paddle.getX() + paddle.getWidth() / 2);
            double normalizedRelativeIntersection = relativeIntersectX / (paddle.getWidth() / 2);
            
            // Calculate the bounce angle (-60 to 60 degrees)
            double bounceAngle = normalizedRelativeIntersection * Math.PI / 3;
            
            // Set the new velocity
            double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
            ball.setDx(speed * Math.sin(bounceAngle));
            ball.setDy(-speed * Math.cos(bounceAngle));
        }
        
        // Check ball-brick collisions
        ArrayList<Brick> bricksToRemove = new ArrayList<>();
        
        for (Brick brick : bricks) {
            if (brick.isVisible() && ball.getBounds().intersects(brick.getBounds())) {
                // Determine collision side and bounce accordingly
                Rectangle intersection = ball.getBounds().intersection(brick.getBounds());
                
                if (intersection.width <= intersection.height) {
                    // Horizontal collision (left or right)
                    ball.setDx(-ball.getDx());
                } else {
                    // Vertical collision (top or bottom)
                    ball.setDy(-ball.getDy());
                }
                
                // Damage the brick
                brick.hit();
                
                // If brick is destroyed, maybe drop a power-up
                if (!brick.isVisible()) {
                    if (random.nextDouble() < 0.3) { // 30% chance for power-up
                        PowerUpType[] types = PowerUpType.values();
                        PowerUpType randomType = types[random.nextInt(types.length)];
                        
                        powerUps.add(new PowerUp(
                            brick.getX() + brick.getWidth() / 2 - 15,
                            brick.getY() + brick.getHeight(),
                            randomType));
                    }
                    
                    score += 10 * brick.getInitialStrength();
                    bricksToRemove.add(brick);
                }
                
                // Only process one brick hit per update
                break;
            }
        }
        
        bricks.removeAll(bricksToRemove);
        
        // Check ball out of bounds
        if (ball.getY() > HEIGHT) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
            } else {
                resetBall();
                paddle = new Paddle(WIDTH / 2, HEIGHT - 50);
            }
        }
    }
    
    private boolean gameWon() {
        return bricks.isEmpty();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_SPACE) {
            if (!inGame) {
                inGame = true;
                gameOver = false;
                timer.start();
            } else if (gameOver) {
                if (gameWon() && currentLevel < 3) {
                    // Next level
                    currentLevel++;
                    resetGame(false);
                } else {
                    // Reset game completely
                    currentLevel = 1;
                    score = 0;
                    resetGame(true);
                }
            }
        }
        
        if (inGame && !gameOver) {
            if (key == KeyEvent.VK_LEFT) {
                paddle.setDx(-8);
            }
            
            if (key == KeyEvent.VK_RIGHT) {
                paddle.setDx(8);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            paddle.setDx(0);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    private void resetGame(boolean resetLives) {
        gameOver = false;
        if (resetLives) {
            lives = 3;
        }
        
        paddle = new Paddle(WIDTH / 2, HEIGHT - 50);
        resetBall();
        
        bricks.clear();
        powerUps.clear();
        loadLevel(currentLevel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Brick Breaker");
            BrickBreakerGame game = new BrickBreakerGame();
            frame.add(game);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    // Game objects
    
    // Ball class
    class Ball {
        private double x, y;
        private double dx, dy;
        private int size;
        private Color color;
        private double baseSpeed;
        
        public Ball(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = 15;
            this.color = Color.WHITE;
            this.baseSpeed = 5.0;
            
            // Initialize with an upward trajectory at an angle
            double angle = Math.toRadians(-60 + random.nextInt(120)); // -60 to 60 degrees
            this.dx = baseSpeed * Math.sin(angle);
            this.dy = -baseSpeed * Math.cos(angle);
        }
        
        public void move() {
            x += dx;
            y += dy;
            
            // Bounce off walls
            if (x <= 0 || x >= WIDTH - size) {
                dx = -dx;
            }
            
            // Bounce off ceiling
            if (y <= 0) {
                dy = -dy;
            }
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillOval((int)x, (int)y, size, size);
        }
        
        public Rectangle getBounds() {
            return new Rectangle((int)x, (int)y, size, size);
        }
        
        public void speedUp() {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            double factor = 1.3; // 30% faster
            dx = dx / currentSpeed * (currentSpeed * factor);
            dy = dy / currentSpeed * (currentSpeed * factor);
        }
        
        public void slowDown() {
            double currentSpeed = Math.sqrt(dx * dx + dy * dy);
            double factor = 0.7; // 30% slower
            dx = dx / currentSpeed * (currentSpeed * factor);
            dy = dy / currentSpeed * (currentSpeed * factor);
        }
        
        public double getX() {
            return x;
        }
        
        public double getY() {
            return y;
        }
        
        public int getSize() {
            return size;
        }
        
        public double getDx() {
            return dx;
        }
        
        public double getDy() {
            return dy;
        }
        
        public void setDx(double dx) {
            this.dx = dx;
        }
        
        public void setDy(double dy) {
            this.dy = dy;
        }
    }
    
    // Paddle class
    class Paddle {
        private int x, y;
        private int dx;
        private int width, height;
        private Color color;
        private int baseWidth;
        
        public Paddle(int x, int y) {
            this.x = x;
            this.y = y;
            this.dx = 0;
            this.baseWidth = 100;
            this.width = baseWidth;
            this.height = 20;
            this.color = new Color(50, 150, 250);
        }
        
        public void move() {
            x += dx;
            
            // Keep paddle within bounds
            if (x <= 0) {
                x = 0;
            }
            
            if (x >= WIDTH - width) {
                x = WIDTH - width;
            }
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillRoundRect(x, y, width, height, 15, 15);
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
        
        public void expand() {
            width = (int)(baseWidth * 1.5);
        }
        
        public void shrink() {
            width = (int)(baseWidth * 0.75);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setDx(int dx) {
            this.dx = dx;
        }
    }
    
    // Brick class
    class Brick {
        private int x, y;
        private int width, height;
        private int strength;
        private int initialStrength;
        private Color color;
        private boolean visible;
        
        public Brick(int x, int y, int width, int height, int strength, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.strength = strength;
            this.initialStrength = strength;
            this.color = color;
            this.visible = true;
        }
        
        public void draw(Graphics2D g2d) {
            if (!visible) return;
            
            // Determine color based on remaining strength
            float saturation = 0.7f - (0.2f * (initialStrength - strength));
            Color currentColor = new Color(
                (int)(color.getRed() * saturation),
                (int)(color.getGreen() * saturation),
                (int)(color.getBlue() * saturation)
            );
            
            g2d.setColor(currentColor);
            g2d.fillRoundRect(x, y, width, height, 5, 5);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x, y, width, height, 5, 5);
        }
        
        public void hit() {
            strength--;
            if (strength <= 0) {
                visible = false;
            }
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public int getInitialStrength() {
            return initialStrength;
        }
    }
    
    // PowerUp class
    enum PowerUpType {
        EXPAND_PADDLE,
        SHRINK_PADDLE,
        EXTRA_LIFE,
        FAST_BALL,
        SLOW_BALL
    }
    
    class PowerUp {
        private int x, y;
        private int width, height;
        private int speed;
        private PowerUpType type;
        private Color color;
        
        public PowerUp(int x, int y, PowerUpType type) {
            this.x = x;
            this.y = y;
            this.width = 30;
            this.height = 15;
            this.speed = 3;
            this.type = type;
            
            // Set color based on type
            switch (type) {
                case EXPAND_PADDLE:
                    this.color = Color.GREEN;
                    break;
                case SHRINK_PADDLE:
                    this.color = Color.RED;
                    break;
                case EXTRA_LIFE:
                    this.color = Color.PINK;
                    break;
                case FAST_BALL:
                    this.color = Color.YELLOW;
                    break;
                case SLOW_BALL:
                    this.color = Color.CYAN;
                    break;
                default:
                    this.color = Color.WHITE;
            }
        }
        
        public void move() {
            y += speed;
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillRoundRect(x, y, width, height, 10, 10);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x, y, width, height, 10, 10);
            
            // Draw symbol based on type
            g2d.setColor(Color.BLACK);
            switch (type) {
                case EXPAND_PADDLE:
                    g2d.drawString("+", x + width/2 - 4, y + height/2 + 4);
                    break;
                case SHRINK_PADDLE:
                    g2d.drawString("-", x + width/2 - 4, y + height/2 + 4);
                    break;
                case EXTRA_LIFE:
                    g2d.drawString("♥", x + width/2 - 5, y + height/2 + 5);
                    break;
                case FAST_BALL:
                    g2d.drawString("F", x + width/2 - 4, y + height/2 + 4);
                    break;
                case SLOW_BALL:
                    g2d.drawString("S", x + width/2 - 4, y + height/2 + 4);
                    break;
            }
        }
        
        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public PowerUpType getType() {
            return type;
        }
    }
}
