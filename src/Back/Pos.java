package Back;
/**
 * Position of Fields in a Table.
 */
public class Pos{
    /**
     * Column of the position.
     */
    private final int c;
    /**
     * Row of the position.
     */
    private final int r;

    /**
     * Returns column of the position.
     * @return c int
     */
    public Integer getC(){
        return c;
    }
    /**
     * Returns row of the position.
     * @return r int
     */
    public Integer getR(){
        return r;
    }

    /**
     * Constructs instance of Pos.
     * @param r New position's row
     * @param c New position's column
     */
    public Pos(int r,int c){
        this.r = r;
        this.c = c;
    }

    /**
     * Overrides built-in toString() method.
     * @return Position in "[row][column]" format
     */
    public String toString(){
        return "["+ r +"]["+ c +"];";
    }
}