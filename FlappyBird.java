import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;


public class FlappyBird extends JPanel implements ActionListener,KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    private static final String HIGHSCORE_FILE = "highscore.txt";

    private Clip backgroundClip;
    
    //Images
    Image backgroundImg;
    Image birdImg;
    Image bottompipeImg;
    Image toppipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

   

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img ;

        Bird(Image img){
            this.img = img;
        }
    }
    
    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scaled by 1/6
    int pipeHeight = 512;
    
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocitX = -4;
    int velocitY = 0;
    int gravity = 1;

    ArrayList<Pipe>pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placepipertTimer;

    boolean gameOver = false;
    double score = 0;

    int highScore;


    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        // setBackground(Color.red);
        setFocusable(true);
        addKeyListener(this);

        prepareSounds();

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        toppipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);

        //pipe
        pipes = new ArrayList<Pipe>();

        loadHighScore();
        playBackgroundMusic();
        

        //game timer
        gameLoop = new Timer(1000/60, this); 
        gameLoop.start();

        //place pipes timer
        placepipertTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placepipertTimer.start();
    }

    private void prepareSounds(){
        backgroundClip = loadClip("./background.wav");
    }

   

    private Clip loadClip(String string) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(string);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*pipeHeight/2);
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(toppipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        
        Pipe bottomPipe = new Pipe(bottompipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
       
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth,boardHeight,null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        
        //pipe
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.black);
        g.setFont(new Font("Garamond",Font.PLAIN,32));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf((int)score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

        g.drawString("HighScore: " + highScore, 10,65) ;
    }

    public void move(){
        //bird
        velocitY += gravity;
        bird.y += velocitY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocitX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }

            if(collision(bird, pipe)){
                backgroundClip.stop();
                gameOver = true;
            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }

    }

    public boolean collision(Bird a, Pipe b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y; 
    
        
    }

    
    
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                highScore = Integer.parseInt(line);
                System.out.println("High score loaded: " + highScore);
            } else {
                System.out.println("No high score found in the file.");
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
            System.out.println("High score saved: " + highScore);
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placepipertTimer.stop();
            gameLoop.stop();
        }

        if(gameOver && score > highScore){
            highScore = (int)score;
            saveHighScore();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocitY = -9;
            if(gameOver){
                //restart the game by reseting the cositions
                backgroundClip.start();
                bird.y = birdY;
                velocitY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placepipertTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

   
}
