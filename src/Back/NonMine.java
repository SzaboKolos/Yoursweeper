package Back;

import java.awt.*;

/**
 * Descendant of Field
 */
public class NonMine extends Field {
    /**
     * Number of neighboring Mines. (0-8)
     */
    private int state = 0; // how many bombs are around the field

    /**
     * Constructs an instance of NonMine
     * @param p Pos, Field's position
     */
    public NonMine(Pos p) {
        position = p;
        icon = ' ';
    }

    /**
     * Returns state of this NonMine.
     * @return state int
     */
    public int getState() {
        return state;
    }
    /**
     * If flagged returns the flag icon, if hidden the hidden icon,
     * else returns the Field's icon.
     * @return icon char
     */
    @Override
    public char getIcon(){
        if (isFlagged)
            return flag;
        else if (isHidden)
            return hiddenIcon;
        else if (state != 0)
            return Integer.toString(state).charAt(0);
        return icon;
    }

    /**
     * Increments value of state by one.
     */
    public void plusState(){
        this.state = this.state+1;
    }
}