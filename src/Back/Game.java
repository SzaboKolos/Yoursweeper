package Back;
import java.util.ArrayList;

/**
 * This class manages the making of the game's field and operation.
 */
public class Game {
    /**
     * Number of rows.
     */
    private int row;
    /**
     * Number of columns.
     */
    private int col;
    /**
     * Number of mines on the table.
     */
    private int mines;
    /**
     * List of all Fields.
     */
    private final ArrayList<Field> fieldList = new ArrayList<>();
    /**
     * 2D array of all Fields.
     */
    private Field[][] minefield;

    /**
     * Creates new instance of a Game using a difficulty parameter to set the new size.
     * @param difficulty Game's new difficulty
     */
    public Game(int difficulty){
        switch (difficulty) {
            case 0 -> newGame(10, 10, 15) ;
            case 1 -> newGame(20, 20, 60);
            case 2 -> newGame(30, 30, 135);
            default -> newGame(0, 0, 0);
        }
    }

    /**
     * Sets the fields dimensions.
     * @param rows Number of rows of the field
     * @param columns Number of columns of the field
     * @param mines Number of mines to be scattered on the field
     */
    public void newGame(int rows, int columns, int mines){
        this.row = rows;
        this.col = columns;
        this.mines = mines;
        this.minefield = new Field[rows][columns];

        plantField();
    }

    /**
     * Random generates the position of mines, puts them in the list and fills the rest with NonMine fields.
     */
    public void plantField(){
        ArrayList<Field> mineList = new ArrayList<>();
        while (mineList.size() < mines){
            int newMineCol = getRandomNumber(colNum());
            int newMineRow = getRandomNumber(rowNum());
            if (mineList.stream().noneMatch(x -> x.getPos().getC() == newMineCol
                    && x.getPos().getR() == newMineRow)){
                mineList.add(new Mine(new Pos(newMineRow,newMineCol)));
            }
        }
        fieldList.addAll(mineList);
        fillMineFieldFromList();
        for (int c=0;c < colNum(); c++){
            for (int r=0;r < rowNum(); r++) {
                if (minefield[r][c] == null) {
                    addField(new NonMine(new Pos(r,c)));
                }
            }
        }
        initStates();
    }

    /**
     * Returns a random number between 0 and the max parameter.
     * @param max The upper limit of the random generator
     * @return random integer
     */
    private int getRandomNumber(int max) {
        return (int) (Math.random() * (max));
    }

    /**
     * Goes through fieldList attribute and initializes the minefield 2D array using the fields' positions.
     */
    public void fillMineFieldFromList(){
        for (Field field : fieldList) {
            int r = field.getPos().getR();
            int c = field.getPos().getC();
            minefield[r][c] = field;
        }
    }

    /**
     * Returns private minefield attribute.
     * @return 2D Field array of Mines and NonMines
     */
    public Field[][] getField(){
        return minefield;
    }

    /**
     * Returns Field at given row and column.
     * @param r Needed Field's row number
     * @param c Needed Field's column number
     * @return Field with r row and c column
     */
    public Field getFieldAt(int r, int c){
        return (r < 0 || r >= row || c < 0 || c >= col) ? null : minefield[r][c];
    }

    /**
     * Determines if a Field is non-null and has Mine type.
     * @param f Field that needs to be identified
     * @return True if the Field is a Mine, False if it's null or NonMine
     */
    public boolean isMine(Field f){
        return f != null && f.getClass().getSimpleName().equals("Mine");
    }
    /**
     * Determines if a Field is non-null and has NonMine type.
     * @param f Field that needs to be identified
     * @return True if the Field is a NonMine, False if it's null or Mine
     */
    public boolean isNonMine(Field f){
        return f != null && f.getClass().getSimpleName().equals("NonMine");
    }

    /**
     * (debug) Prints the minefield 2D array to standard output.
     */
    public void printFields(){
        Field[][] mf = getField().clone();
        for (int r=0;r < rowNum(); r++){
            for (int c=0;c < colNum(); c++){
                //mf[r][c].reveal();
                System.out.print(mf[r][c].getIcon() + " ");
            }
            System.out.println();
        }
    }

    /**
     * Initializes values NonMine fields' state attributes.
     */
    public void initStates(){
        fillMineFieldFromList();
        for (int c=0;c < colNum(); c++) {
            for (int r = 0; r < rowNum(); r++) {
                if (!isMine(minefield[r][c])) {
                    NonMine nm = (NonMine) minefield[r][c];
                    if (nm.getState() < 8) {
                        if(c != 0 && isMine(minefield[r][c-1]))
                            nm.plusState();
                        if (c != colNum()-1 && isMine(minefield[r][c+1]))
                            nm.plusState();
                        if (r != 0 && isMine(minefield[r-1][c]))
                            nm.plusState();
                        if (r != colNum()-1 && isMine(minefield[r+1][c]))
                            nm.plusState();
                        if (r != rowNum()-1 && c != colNum()-1 && isMine(minefield[r+1][c+1]))
                            nm.plusState();
                        if (r != rowNum()-1 && c != 0 && isMine(minefield[r+1][c-1]))
                            nm.plusState();
                        if (r != 0 && c != colNum()-1 && isMine(minefield[r-1][c+1]))
                            nm.plusState();
                        if (r != 0 && c != 0 && isMine(minefield[r-1][c-1]))
                            nm.plusState();
                    }
                }
            }
        }
    }

    /**
     * Sets all Fields' isHidden attribute to false.
     */
    public void revealFields(){
        for (int c=0;c < colNum(); c++){
            for (int r=0;r < rowNum(); r++){
                getFieldAt(r,c).unflag();
                revealField(r,c);
            }
        }
    }

    /**
     * Sets one Field's isHidden attribute to false.
     * @param r Field's row number.
     * @param c Field's column number.
     */
    public void revealField(int r, int c){
        minefield[r][c].reveal();
    }

    /**
     * Returns the table's row number.
     * @return row attribute
     */
    public int rowNum(){
        return row;
    }

    /**
     * Returns the table's column number.
     * @return col attribute
     */
    public int colNum(){
        return col;
    }

    /**
     * Adds a field to fieldList attribute.
     * @param f New Field
     */
    public void addField(Field f){
        fieldList.add(f);
    }

    /**
     * Returns number of flagged Fields neighboring selected Field.
     * @param r Field's row number.
     * @param c Field's column number.
     * @return flagged Fields number neighboring this Field
     */
    public int neighborFlags(int r,int c){
        int flagNum = 0;
        if(c != 0 && minefield[r][c-1].isFlagged())
            flagNum++;
        if (c != colNum()-1 && minefield[r][c+1].isFlagged())
            flagNum++;
        if (r != 0 && minefield[r-1][c].isFlagged())
            flagNum++;
        if (r != colNum()-1 && minefield[r+1][c].isFlagged())
            flagNum++;
        if (r != rowNum()-1 && c != colNum()-1 && minefield[r+1][c+1].isFlagged())
            flagNum++;
        if (r != rowNum()-1 && c != 0 && minefield[r+1][c-1].isFlagged())
            flagNum++;
        if (r != 0 && c != colNum()-1 && minefield[r-1][c+1].isFlagged())
            flagNum++;
        if (r != 0 && c != 0 && minefield[r-1][c-1].isFlagged())
            flagNum++;
        return flagNum;
    }
}