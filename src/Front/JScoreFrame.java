package Front;

import javax.swing.*;
import java.awt.*;

public class JScoreFrame extends JFrame {
    private String[][] scores;

    public JScoreFrame(Scores s,Point p){
        getScoreBoard(s);
        String[] header = {"Name", "Time"};
        JTable scoreTable = new JTable(scores, header);
        this.setSize(200,220);
        this.setLocation(p.x+210,p.y);
        scoreTable.setEnabled(false);

        this.add(scoreTable);
    }
    private void getScoreBoard(Scores s){
        this.scores = s.get2DArray();
    }

}
