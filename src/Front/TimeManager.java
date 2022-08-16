package Front;

public class TimeManager {
    private int time = 0;

    public TimeManager(){

    }

    /**
     * Increments time attribute by delay.
     * @param delay Incrementation value in seconds.
     */
    public void incrTime(int delay){
        time = time+delay;
    }
    public String getTime(){
        return time+"";
    }
    public void resetTime(){
        time = 0;
    }
}
