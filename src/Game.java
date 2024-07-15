import processing.core.PApplet;


public class Game extends PApplet {
    // TODO: declare game variables
    Board gameBoard;
    int clickX, clickY;

    public void settings() {
        size(800, 800);   // set the window size

    }

    public void setup() {
        // TODO: initialize game variables
        frameRate(60);
        gameBoard = new Board(this);
    }

    /***
     * Draws each frame to the screen.  Runs automatically in a loop at frameRate frames a second.
     * tick each object (have it update itself), and draw each object
     */
    public void draw() {
        background(255);    // paint screen white
        fill(0,255,0);          // load green paint color
        drawBoard();
        gameBoard.drawPieces(this);
        drawLetters();
        drawNumbers();
        gameBoard.updateBoard(this);
    }
    public void drawBoard() {
        for(int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y+=2) {
                if (x % 2 == 0) {
                    fill(34,139,34);
                } else fill(245,245,245);
                rect(x * 100, y * 100, 100, 100);
            }
        }
        for(int x = 0; x < 8; x++) {
            for (int y = 1; y < 8; y+=2) {
                if (x % 2 == 0) {
                    fill(245,245,245);
                } else fill(34,139,34);
                rect(x * 100, y * 100, 100, 100);
            }
        }
    }
    public void drawLetters() {
        String letters = "abcdefgh";
        int count = 0;
        textSize(20);
        fill(0,0,255);
        for (int x = 80; x < 800; x+=100) {
            text(letters.substring(count, count+1), x, 790);
            count++;
        }
    }
    public void drawNumbers() {
        int count = 1;
        for (int y = 730; y > 0; y-= 100) {
            text(count, 10, y);
            count++;
        }
    }
    public void mousePressed() {
        //updates click coords when you press w/ mouse
        clickX = mouseX;
        clickY = mouseY;
    }
    public int getClickX() {
        return clickX;
    }
    public int getClickY() {
        return clickY;
    }

    public static void main(String[] args) {
        PApplet.main("Game");
    }
}

