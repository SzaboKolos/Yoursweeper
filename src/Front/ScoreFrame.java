package Front;

import javax.swing.*;

public class ScoreFrame extends JFrame {
    private final String[] header = {"Name","Time"};
    private String[][] scores;

    public ScoreFrame(Scores s){
        getScoreBoard(s);
        JFrame scoreFrame = new JFrame();
        JTable scoreTable = new JTable(scores,header);
        scoreFrame.setSize(200,300);
        scoreFrame.setLocationRelativeTo(null);
        //scoreTable.setSize(200,300);
        scoreFrame.add(scoreTable);
    }
    private void getScoreBoard(Scores s){
        this.scores = s.get2DArray();
    }

}
