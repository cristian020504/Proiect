import javax.swing.*;
public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 340;
        int boardHeight = 650;

        JFrame frame = new JFrame("Flappy Bird - Munteanu Cristian");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}