import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image topRedPipe;
    Image botRedPipe;
    Image topBluPipe;
    Image botBluPipe;
    Image topBestPipe;
    Image botBestPipe;


    //bird class
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 54;
    int birdHeight = 34;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //pipe class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; //move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    double bestscore = -1;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./bg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./bird2.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        topRedPipe = new ImageIcon(getClass().getResource("./topredpipe.png")).getImage();
        botRedPipe = new ImageIcon(getClass().getResource("./botredpipe.png")).getImage();
        topBluPipe = new ImageIcon(getClass().getResource("./topblupipe.png")).getImage();
        botBluPipe = new ImageIcon(getClass().getResource("./botblupipe.png")).getImage();

        //best score pipes
        topBestPipe = new ImageIcon(getClass().getResource("./topbestpipe.png")).getImage();
        botBestPipe = new ImageIcon(getClass().getResource("./botbestpipe.png")).getImage();



        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();




            int delay_pipe = 3500; //  modificare timp spawn pipe (milisecunde)
            placePipeTimer = new Timer(delay_pipe, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Code to be executed
                    placePipes();
                }
            });
        placePipeTimer.start();


        //game timer
        gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();
    }

    void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        if(score == bestscore) { // Verificăm dacă scorul actual este egal cu cel mai mare scor
            Pipe topPipe = new Pipe(topBestPipe);
            topPipe.y = randomPipeY;
            pipes.add(topPipe);

            Pipe bottomPipe = new Pipe(botBestPipe);
            bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
            pipes.add(bottomPipe);
        } else if(score <= 8) {
            Pipe topPipe = new Pipe(topRedPipe);
            topPipe.y = randomPipeY;                    // PIPE-UL DE SUS DUPA SCORUL DE 8
            pipes.add(topPipe);

            Pipe bottomPipe = new Pipe(botRedPipe);
            bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;    // PIPE-UL DE JOS DUPA SCORUL DE 8
            pipes.add(bottomPipe);
        } else {
            Pipe topPipe = new Pipe(topBluPipe);
            topPipe.y = randomPipeY;
            pipes.add(topPipe);

            Pipe bottomPipe = new Pipe(botBluPipe);
            bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
            pipes.add(bottomPipe);
        }
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //apply gravity to current bird.y, limit the bird.y to top of the canvas

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; //0.5 because there are 2 pipes! so 0.5*2 = 1, 1 for each set of pipes
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
                a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        } else {
            adjustPipeTimer();
        }
    }


    void adjustPipeTimer() {
        if (score >= 2 && score <= 8) {
            placePipeTimer.setDelay(2500); // Delay when score is above 15
        }
        if (score >= 8) {
            placePipeTimer.setDelay(1500); // Delay when score is between 11 and 15
        }
    }



    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            velocityY = -9;

            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                if(score > bestscore){
                    bestscore = score;
                }
                score = 0;
                gameLoop.start();
                placePipeTimer.setDelay(3000);
                placePipeTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}