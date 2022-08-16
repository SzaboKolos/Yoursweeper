package Front;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.*;
import java.awt.*;

import Back.Field;
import Back.NonMine;
import Back.Game;

/**
 * Frontend of the project, everything that can be seen is here.
 */
public class GUI {
    /**
     * Attribute of the Game to be played.
     */
    private Game game;
    /**
     * The main window, contains the playfield and information fields.
     */
    private JFrame mainFrame;
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

    private final TimeManager timeManager = new TimeManager();
    private Timer timer;
    /**
     * Creates instance of GUI.
     * @param g Inintial game
     */
    public GUI(Game g){
        game = g;
        initFrame();
    }
    /**
     * Initializes mainFrame and diffChangerFrame.
     */
    private void initFrame(){
        this.mainFrame = new JFrame("Yoursweeper");

        mainFrame.setSize((20*game.colNum()+10),20*game.rowNum()+30);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel tablePane = new JPanel();
        this.playField = initTable();
        tablePane.add(playField);
        mainFrame.getContentPane().add(tablePane, BorderLayout.NORTH);
        mainFrame.getContentPane().add(initMenuPanel(), BorderLayout.SOUTH);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        JPanel chooser = new JPanel();
        chooser.add(newDiffButton(0));
        chooser.add(newDiffButton(1));
        chooser.add(newDiffButton(2));
        diffChangerFrame.add(chooser);
        diffChangerFrame.setLocationRelativeTo(mainFrame);
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
                        if (thisField.isFlagged()) {
                            thisField.unflag();
                        }
                        else {
                            thisField.flag();
                        }
                    colorField(thisField);
                    }
                    playField.setValueAt(thisField, row, col);
                    // (debug)
                    System.out.println(thisField.getClass().getSimpleName()+ thisField.getPos()+" H:"+ thisField.isHidden() +"; F:"+ thisField.isFlagged());
                }
            }
        };
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
        timer.stop();
    }

    /**
     *
     */
    private void win(){
        //TODO if all mines are flagged and all nonmines are revealed
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

        if (!f.isFlagged()){
            if (game.isNonMine(fS[0]) && fS[0].isHidden()){
                fS[0].reveal();
                playField.setValueAt(fS[0],thisR-1,thisC);
            }
            if (game.isNonMine(fS[1]) && fS[1].isHidden()) {
                fS[1].reveal();
                playField.setValueAt(fS[1],thisR,thisC+1);
            }
            if (game.isNonMine(fS[2]) && fS[2].isHidden()){
                fS[2].reveal();
                playField.setValueAt(fS[2],thisR+1,thisC);
            }
            if (game.isNonMine(fS[3]) && fS[3].isHidden()){
                fS[3].reveal();
                playField.setValueAt(fS[3],thisR,thisC-1);
            }
            if (game.isNonMine(fS[4]) && ((NonMine)fS[4]).getState()!=0 && fS[4].isHidden()){
                fS[4].reveal();
                playField.setValueAt(fS[4],thisR-1,thisC-1);
            }
            if (game.isNonMine(fS[5]) && ((NonMine)fS[5]).getState()!=0 && fS[5].isHidden()){
                fS[5].reveal();
                playField.setValueAt(fS[5],thisR-1,thisC+1);
            }
            if (game.isNonMine(fS[6]) && ((NonMine)fS[6]).getState()!=0 && fS[6].isHidden()){
                fS[6].reveal();
                playField.setValueAt(fS[6],thisR+1,thisC-1);
            }
            if (game.isNonMine(fS[7]) && ((NonMine)fS[7]).getState()!=0 && fS[7].isHidden()){
                fS[7].reveal();
                playField.setValueAt(fS[7],thisR+1,thisC+1);
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
     * @param field Actual field to be discovered
     */
    private void recursiveThingy(NonMine field){
        int thisR = field.getR();
        int thisC = field.getC();
        field.reveal();
        playField.setValueAt(field, thisR, thisC);

        Field[] fS = getNeighbors(thisR, thisC);

        if (game.isNonMine(fS[0]) && fS[0].isHidden()){
            if (((NonMine)fS[0]).getState()==0)
                recursiveThingy((NonMine)fS[0]);
            else {
                fS[0].reveal();
                colorField(fS[0]);
                playField.setValueAt(fS[0],thisR-1,thisC);
            }
        }
        if (game.isNonMine(fS[1]) && fS[1].isHidden()) {
            if (((NonMine)fS[1]).getState()==0)
                recursiveThingy((NonMine)fS[1]);
            else {
                fS[1].reveal();
                colorField(fS[1]);
                playField.setValueAt(fS[1],thisR,thisC+1);
            }
        }
        if (game.isNonMine(fS[2]) && fS[2].isHidden()){
            if (((NonMine)fS[2]).getState()==0)
                recursiveThingy((NonMine)fS[2]);
            else {
                fS[2].reveal();
                colorField(fS[2]);
                playField.setValueAt(fS[2],thisR+1,thisC);
            }
        }
        if (game.isNonMine(fS[3]) && fS[3].isHidden()){
            if (((NonMine)fS[3]).getState()==0)
                recursiveThingy((NonMine)fS[3]);
            else {
                fS[3].reveal();
                colorField(fS[3]);
                playField.setValueAt(fS[3],thisR,thisC-1);
            }
        }
        if (game.isNonMine(fS[4]) && fS[4].isHidden()){
            if (((NonMine)fS[4]).getState()==0)
                recursiveThingy((NonMine)fS[4]);
            else {
                fS[4].reveal();
                colorField(fS[4]);
                playField.setValueAt(fS[4],thisR-1,thisC-1);
            }
        }
        if (game.isNonMine(fS[5]) && fS[5].isHidden()){
            if (((NonMine)fS[5]).getState()==0)
                recursiveThingy((NonMine)fS[5]);
            else {
                fS[5].reveal();
                colorField(fS[5]);
                playField.setValueAt(fS[5],thisR-1,thisC+1);
            }
        }
        if (game.isNonMine(fS[6])&& fS[6].isHidden()){
            if (((NonMine)fS[6]).getState()==0 )
                recursiveThingy((NonMine)fS[6]);
            else{
                fS[6].reveal();
                colorField(fS[6]);
                playField.setValueAt(fS[6],thisR+1,thisC-1);
            }
        }
        if (game.isNonMine(fS[7]) && fS[7].isHidden()) {
            if (((NonMine) fS[7]).getState() == 0)
                recursiveThingy((NonMine) fS[7]);
            else {
                fS[7].reveal();
                colorField(fS[7]);
                playField.setValueAt(fS[7], thisR + 1, thisC + 1);
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
        //table.setEnabled(false);
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
            for (int r=0;r < game.rowNum(); r++){
                fields[r][c] = game.getField()[r][c];
            }
        }

        return( new DefaultTableModel(fields,colNames) {public boolean isCellEditable(int row, int column) { return false; }});
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
            for (int c=0; c < game.colNum();c++) {
                table.setValueAt(game.getField()[r][c],r,c);
            }
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
        JButton btnThis = new JButton(text);
        btnThis.setSize(100,25);
        btnThis.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                diffChangerFrame.setVisible(false);
                replantTable(diff);
                mainFrame.setSize((20*game.colNum()+26),20*game.rowNum()+74);
                mainFrame.setLocationRelativeTo(null);
                timeManager.resetTime();
            }
        });
        return btnThis;
    }

    /**
     * Initializes menu panel's and changer frame's appearance and layout.
     * @return JPanel with buttons and information of the current game.
     */
    private JPanel initMenuPanel(){
        diffChangerFrame = new JFrame();
        diffChangerFrame.setSize(new Dimension(250,75));
        diffChangerFrame.setResizable(false);
        JButton btnNew = new JButton("New Game");
        JPanel panel = new JPanel();
        Label labelTime = new Label("0");
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
        timer.start();

        btnNew.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                diffChangerFrame.setVisible(true);
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