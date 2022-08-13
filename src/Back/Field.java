package Back;

/**
 * Abstract ancestor of Mine and NonMine types.
 *
 */
public abstract class Field{
    protected Pos position;
    protected char icon;
    protected char hiddenIcon = '-';
    protected char flag = '₱';
    protected boolean isHidden = true;
    protected boolean isFlagged = false;

    /**
     * Returns the value of given Field's position
     * @return Position of given Field
     */
    public Pos getPos(){
        return position;
    }
    /**
     * Returns the row value of given Field's position
     * @return Value of position's row variable
     */
    public int getR(){
        return position.getR();
    }
    /**
     * Returns the column value of given Field's position
     * @return Value of position's col variable
     */
    public int getC(){
        return position.getC();
    }

    /**
     * Returns the value of this Fields icon.
     * (Must be overwritten)
     * @return icon char
     */
    abstract public char getIcon();
    /**
     * Sets isHidden value to true.
     */
    public void hide(){
        isHidden = true;
    }
    /**
     * Sets isHidden value to false.
     */
    public void reveal(){
        isHidden = false;
    }
    /**
     * Returns private variable isHidden's value.
     * @return isHidden boolean
     */
    public boolean isHidden(){
        return isHidden;
    }
    /**
     * Overrides built-in toString() method.
     * @return Value of given Field's getIcon() method
     */
    public String toString(){
        return getIcon()+"";
    }

    /**
     * Sets isFlagged value to true.
     */
    public void flag(){
        isFlagged = true;
    }
    /**
     * Sets isFlagged value to false.
     */
    public void unflag(){
        isFlagged = false;
    }
    /**
     * Returns private variable isFlagged's value.
     * @return isFlagged boolean
     */
    public boolean isFlagged() {
        return isFlagged;
    }
}