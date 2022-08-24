package Front;

import javax.swing.*;
import java.awt.*;

public class JDiffFrame extends JFrame {
    public JDiffFrame(JButton easyButton, JButton normalButton, JButton hardButton){
        JPanel chooserPane = new JPanel();
        chooserPane.add(easyButton);
        chooserPane.add(normalButton);
        chooserPane.add(hardButton);
        this.add(chooserPane);
        this.setLocationRelativeTo(null);
        this.setSize(new Dimension(250,75));
        this.setResizable(false);
    }
}
