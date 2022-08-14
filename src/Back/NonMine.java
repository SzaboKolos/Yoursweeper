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

    /**
     *
     */
    public void setColor(){
        switch (this.getState()){
            default -> this.setFieldColor(Color.black);
            case 1 -> this.setFieldColor(Color.blue);
            case 2 -> this.setFieldColor(Color.green);
            case 3 -> this.setFieldColor(Color.red);
            case 4 -> this.setFieldColor(Color.getColor("#0000AA"));
            case 5 -> this.setFieldColor(Color.getColor("#AA0000"));
            case 6 -> this.setFieldColor(Color.getColor("#00AAAA"));
            case 7 -> this.setFieldColor(Color.getColor("#AA00AA"));
            case 8 -> this.setFieldColor(Color.getColor("#FFAA00"));
        }
    }
}