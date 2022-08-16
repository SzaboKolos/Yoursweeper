import Back.Game;
import Front.Graphics;
/**
 * @author      Kolos Szabó <szabo.kolos.01 @ gmail.com>
 * @version     4.11
 * @see         "https://en.wikipedia.org/wiki/Minesweeper_(video_game)"
 */
class Main {
    public static void main(String[] args){
        Game game = new Game(0);
        new Graphics(game);
    }
}