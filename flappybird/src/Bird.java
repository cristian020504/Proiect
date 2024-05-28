import java.awt.*;

class Bird {
    int boardWidth = 360;
    int boardHeight = 640;
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 54;
    int birdHeight = 34;
    int x = birdX;
    int y = birdY;
    int width = birdWidth;
    int height = birdHeight;
    Image img;

    Bird(Image img) {
        this.img = img;
    }
}