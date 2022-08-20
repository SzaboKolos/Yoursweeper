package Front;

import java.io.Serializable;

public class Score implements Serializable {
    private final int time;
    private final String name;
    private int number;
    public Score(int t,String n){
        time = t;
        name = n;
    }
    public String getName(){
        return name;
    }
    public int getTime(){
        return time;
    }
    public int getNumber(){
        return number;
    }
    public void setNumber(int n){
        number = n;
    }

}