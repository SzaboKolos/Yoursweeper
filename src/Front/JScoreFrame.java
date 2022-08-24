package Front;

import javax.swing.*;

public class JScoreFrame extends JFrame {
    private String[][] scores;

    public JScoreFrame(Scores s){
        getScoreBoard(s);
        String[] header = {"Name", "Time"};
        JTable scoreTable = new JTable(scores, header);
        this.setSize(200,300);
        this.setLocationRelativeTo(null);
        scoreTable.setEnabled(false);

        this.add(scoreTable);
    }
    private void getScoreBoard(Scores s){
        this.scores = s.get2DArray();
    }

}
