package Front;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.*;
import java.awt.*;
import java.util.Arrays;

import Back.Field;
import Back.NonMine;
import Back.Game;

/**
 * Frontend of the project, everything that can be seen is here.
 */
public class JMainFrame extends JFrame {
    /**
     * Attribute of the Game to be played.
     */
    private Game game;
    /**
     * JTable, contains the actual instance of Game, stylized.
     */
    private JTable playField;
    /**
     * Model that playField relies on.
     */
    private DefaultTableModel model;
    /**
     * Window with the game difficulty options.
     */
    private JFrame diffChangerFrame;
    /**
     * The game's own time manager.
     */
    private final TimeManager timeManager;
    /**
     * The timer which updates the label, using the timeManager's methods.
     */
    private Timer timer;
    /**
     * Label which shows the player the time of the game.
     */
    private final Label labelTime = new Label("0");
    /**
     * Scores instance to store and serialize scores.
     */
    private final Scores scores;

    public JMainFrame(Game g, TimeManager tm, Scores s){
        game = g;
        timeManager = tm;
        scores = s;
        initFrame();
    }
    /**
     * Initializes mainFrame and diffChangerFrame.
     */
    private void initFrame(){
        this.setTitle("YourSweeper");

        this.setSize((20*game.colNum()+10),20*game.rowNum()+30);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel tablePane = new JPanel();
        this.playField = initTable();
        tablePane.add(playField);
        this.getContentPane().add(tablePane, BorderLayout.NORTH);
        this.getContentPane().add(initMenuPanel(), BorderLayout.SOUTH);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        diffChangerFrame = new JDiffFrame(newDiffButton(0),newDiffButton(1),newDiffButton(2));
    }

    /**
     * Contains events for left and right mouse release,
     * In case of left release it reveals the field, if it's a Mine,
     * the player loses and can choose the difficulty for the next game.
     * In case of right release it flags or unflags the field.
     * @return MouseAdapter that can be put on fields individually
     */
    private MouseAdapter cellActionAdapter(){
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                int row = playField.rowAtPoint(evt.getPoint());
                int col = playField.columnAtPoint(evt.getPoint());
                if (!timer.isRunning())
                    timer.start();
                Field thisField = game.getFieldAt(row, col);
                if (row >= 0 && col >= 0 && row < game.rowNum() && col < game.colNum()) {
                    if (SwingUtilities.isLeftMouseButton(evt) && !thisField.isFlagged() && thisField.isHidden()) {
                        if (thisField.getClass().getSimpleName().equals("NonMine") && ((NonMine) thisField).getState() == 0)
                            recursiveThingy((NonMine) thisField);
                        else if (thisField.getClass().getSimpleName().equals("Mine"))
                            lose();
                        else {
                            game.revealField(row, col);
                            colorField(game.getField()[row][col]);
                        }
                    }
                    else if (SwingUtilities.isLeftMouseButton(evt) && !thisField.isFlagged() && !thisField.isHidden()
                            && thisField.getClass().getSimpleName().equals("NonMine") && ((NonMine) thisField).getState() == game.neighborFlags(row,col))
                        revealNeighborFields(thisField);
                    else if (SwingUtilities.isRightMouseButton(evt) && thisField.isHidden()) {
                        if (thisField.isFlagged())
                            thisField.unflag();
                        else
                            thisField.flag();
                    colorField(thisField);
                    }
                    playField.setValueAt(thisField, row, col);
                    // (debug)
                    System.out.println(thisField.getClass().getSimpleName()+ thisField.getPos()+" H:"+ thisField.isHidden() +"; F:"+ thisField.isFlagged());
                }
                if (isObjectiveMet())
                    win();
            }
        };
    }

    /**
     * Checks if you have won.
     * @return True if all the objectives have been met, false if not.
     */
    private boolean isObjectiveMet(){
        return (Arrays.stream(game.getField()).allMatch(x -> Arrays.stream(x).allMatch(y -> (game.isNonMine(y) && !y.isHidden()) || (game.isMine(y) && y.isFlagged()  && y.isHidden()))));
    }
    /**
     * Sets renderer of the cell which the Field parameter's in to modify its color.
     * @param f Field to get its coordinate
     */
    private void colorField(Field f) {
        TableColumn tc = playField.getColumnModel().getColumn(f.getC());
        tc.setCellRenderer(new CellColorRenderer());
    }
    /**
     * If triggered reveals all fields and displays the difficulty changer window.
     */
    private void lose(){
        revealAllFields();
        diffChangerFrame.setVisible(true);
    }

    /**
     * If all the conditions are met, you win.
     */
    private void win(){
        String fileName;
        switch (game.getDifficulty()){
            default -> fileName = "Easy";
            case 1 -> fileName = "Normal";
            case 2 -> fileName = "Hard";
        }
        if (scores.getTopList().size() != 0)
            scores.saveScores("./"+ fileName +"Scores.txt");
        scores.loadScores("./"+ fileName +"Scores.txt");
        timer.stop();
        scores.addScore(new Score(Integer.parseInt(timeManager.getTime()),JOptionPane.showInputDialog(null, "Who.. Are you?")));
        scores.saveScores("./"+ fileName +"Scores.txt");
        JFrame scoreFrame = new JScoreFrame(scores);
        scoreFrame.setVisible(true);
        System.out.println("Szer <3");
    }
    /**
     * If clicked on non-hidden field and the neighboring flags match the number of Mines next to this field
     * reveals all fields next to this field.
     * @param f Field the action is triggered on.
     */
    private void revealNeighborFields(Field f) {
        int thisR = f.getR();
        int thisC = f.getC();

        Field[] fS = getNeighbors(thisR,thisC);
        if (Arrays.stream(fS).anyMatch(x -> game.isMine(x) && !x.isFlagged())){
            lose();
        } else
            for (Field neighbor: fS){
                if (neighbor != null && !neighbor.isFlagged()){
                    if (game.isNonMine(neighbor) && ((NonMine)neighbor).getState() == 0){
                        recursiveThingy((NonMine) neighbor);}
                    else if (game.isNonMine(neighbor))
                        neighbor.reveal();
                    playField.setValueAt(neighbor,neighbor.getR(),neighbor.getC());
                    colorField(neighbor);
                }
            }
    }

    /**
     * Reveals all fields on the table.
     */
    private void revealAllFields(){
        game.revealFields();
        for (int r=0; r < game.rowNum();r++) {
            for (int c=0; c < game.colNum();c++) {
                playField.setValueAt(game.getField()[r][c].getIcon(),r,c);
                colorField(game.getField()[r][c]);
            }
        }
    }

    /**
     * Only called on fields that has no Mine neighbor, discovers all fields like this in a cluster
     * reveals them, and gets called again recursively. Also reveals the NonMines on the cluster's rim.
     * (Could be named better)
     * @param field Actual field to be discovered
     */
    private void recursiveThingy(NonMine field){
        int thisR = field.getR();
        int thisC = field.getC();
        field.reveal();
        playField.setValueAt(field, thisR, thisC);

        Field[] fS = getNeighbors(thisR, thisC);
        for (Field neighbor: fS) {
            if (game.isNonMine(neighbor) && neighbor.isHidden()){
                if (((NonMine)neighbor).getState()==0)
                    recursiveThingy((NonMine)neighbor);
                else {
                    neighbor.reveal();
                    colorField(neighbor);
                    playField.setValueAt(neighbor,neighbor.getR(),neighbor.getC());
                }
            }
        }
    }

    /**
     * Returns a field's neighbors in a Field array.
     * @param thisR Field's row id.
     * @param thisC Field's column id.
     * @return Array of neighbor Fields.
     */
    private Field[] getNeighbors(int thisR, int thisC){
        Game fTable = game;

        Field SF = fTable.getFieldAt(thisR-1,thisC);
        Field EF = fTable.getFieldAt(thisR,thisC+1);
        Field NF = fTable.getFieldAt(thisR+1,thisC);
        Field WF = fTable.getFieldAt(thisR,thisC-1);

        Field NWF = fTable.getFieldAt(thisR-1,thisC-1);
        Field NEF = fTable.getFieldAt(thisR-1,thisC+1);
        Field SWF = fTable.getFieldAt(thisR+1,thisC-1);
        Field SEF = fTable.getFieldAt(thisR+1,thisC+1);
        return (new Field[]{SF, EF, NF, WF, NWF, NEF, SWF, SEF});
    }

    /**
     * Sets starting state of the JTable the playfield is on.
     * @return JTable with initial values.
     */
    private JTable initTable(){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        centerRenderer.setVerticalAlignment( JLabel.CENTER );
        model = makeModel();
        JTable table = new JTable(model);
        setTableDesign(table);
        table.addMouseListener(cellActionAdapter());
        return table;
    }

    /**
     * Creates the model the playfield's JTable is set on.
     * @return DefaultTableModel with size matching the difficulty setting.
     */
    private DefaultTableModel makeModel(){
        Object[][] fields = new Object[game.rowNum()][game.colNum()];
        String[] colNames = new String[game.colNum()];
        for (int k = 0; k < game.colNum(); k++){
            colNames[k] = (k+1)+"";
        }
        for (int c=0;c < game.colNum(); c++){
            for (int r=0;r < game.rowNum(); r++)
                fields[r][c] = game.getField()[r][c];
        }

        return ( new DefaultTableModel(fields,colNames) { public boolean isCellEditable(int row, int column) { return false; }});
    }

    /**
     * Sets visual options of given JTable and its cells.
     * @param table JTable to be styled
     */
    private void setTableDesign(JTable table){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.setFont(new Font("Arial Unicode", Font.BOLD, 12));
        table.getColumnModel().setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setBorder(BorderFactory.createLineBorder(Color.black));
        for (int w =0;w< game.colNum(); w++) {
            TableColumn tabCol = table.getColumnModel().getColumn(w);
            tabCol.setPreferredWidth(20);
            tabCol.setMinWidth(20);
            tabCol.setMaxWidth(20);
            tabCol.setCellRenderer(centerRenderer);
            tabCol.setResizable(false);
        }
        for (int r=0; r < game.rowNum();r++) {
            for (int c=0; c < game.colNum();c++)
                table.setValueAt(game.getField()[r][c],r,c);
        }
        table.setRowHeight(20);
    }

    /**
     * Creates new instance of JButton with the listener to change difficulty based on diff parameter.
     * @param diff Difficulty selector
     * @return New JButton with the text of difficulty
     */
    private JButton newDiffButton(int diff){
        String text = switch (diff) {
            case 0 -> "Easy";
            case 1 -> "Normal";
            case 2 -> "Hard";
            default -> "Custom";
        };
        JFrame mainFrame = this;
        JButton btnThis = new JButton(text);
        btnThis.setSize(100,25);
        btnThis.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                diffChangerFrame.setVisible(false);
                replantTable(diff);
                mainFrame.setSize((20*game.colNum()+26),20*game.rowNum()+74);
                mainFrame.setLocationRelativeTo(null);
                timeManager.resetTime();
                labelTime.setText("0");
            }
        });
        return btnThis;
    }

    /**
     * Initializes menu panel's and changer frame's appearance and layout.
     * @return JPanel with buttons and information of the current game.
     */
    private JPanel initMenuPanel(){
        JButton btnNew = new JButton("New Game");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel.add(btnNew);
        panel.add(labelTime);
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeManager.incrTime(1);
                labelTime.setText(timeManager.getTime());
            }
        });

        btnNew.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                diffChangerFrame.setVisible(true);
                timer.stop();
            }
        });
        return panel;
    }

    /**
     * Resets the minefield to a fresh start.
     * @param diff Selector for the difficulty of the new game
     */
    private void replantTable(int diff){
        game = new Game(diff);
        model.setRowCount(game.rowNum());
        model.setColumnCount(game.colNum());
        setTableDesign(playField);
        for (int r=0; r < game.rowNum();r++) {
            for (int c=0; c < game.colNum();c++) {
                playField.setValueAt(game.getField()[r][c],r,c);
            }
        }
    }

    /**
     * Renderer to change text of a single cell at a time.
     */
    class CellColorRenderer extends DefaultTableCellRenderer {
        /**
         * Bocsi nem hagyhattam ki :P
         */
        Color kolor;
        /**
         * Creates a new instance of CellColorRenderer.
         */
        public CellColorRenderer() {
            super();
        }
        /**
         * Gets a cell modifies it, then returns it.
         * @param table Table
         * @param value Object in cell
         * @param isSelected Always false, cells can't be selected
         * @param hasFocus Always false
         * @param row Row the cell is in
         * @param column Column the cell is in
         * @return The modified (colored) cell
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (game.getFieldAt(row, column).isHidden())
                kolor = (Color.black);
            else {
                if (game.isNonMine(game.getFieldAt(row, column))) {
                    switch (((NonMine) game.getFieldAt(row, column)).getState()) {
                        default -> kolor = (Color.black);
                        case 1 -> kolor = (Color.blue);
                        case 2 -> kolor = (Color.decode("#00AA00")); //Dark Green
                        case 3 -> kolor = (Color.decode("#FF9934")); //Orange
                        case 4 -> kolor = (Color.decode("#0000AA")); //Dark Blue
                        case 5 -> kolor = (Color.decode("#AA0000")); //Dark Red
                        case 6 -> kolor = (Color.decode("#00AAAA")); //Dark Aqua
                        case 7 -> kolor = (Color.decode("#AA00AA")); //Dark Purple
                        case 8 -> kolor = (Color.decode("#FFAA00")); //Gold
                    }
                }
                else
                    kolor = (Color.black);
            }
            if (game.getFieldAt(row, column).isFlagged())
                kolor = (Color.red);
            cell.setForeground(kolor);
            this.setFont(new Font("Arial Unicode", Font.BOLD, 12));
            this.setHorizontalAlignment(JLabel.CENTER);
            return cell;
            }
    }
}