package Back;

import java.awt.*;

/**
 * Descendant of Field.
 */
public class Mine extends Field{
    /**
     * Constructs an instance of Mine.
     * @param p Pos, Field's position
     */
    public Mine(Pos p){
        position = p;
        icon = '☼';
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
        return icon;
    }
}