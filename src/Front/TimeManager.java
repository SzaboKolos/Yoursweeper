package Front;

/**
 * Stores and handles operations with time.
 */
public class TimeManager {
    /**
     * Passed time in seconds.
     */
    private int time = 0;

    /**
     * Increments time attribute by delay.
     * @param delay Incrementation value in seconds
     */
    public void incrTime(int delay){
        time = time+delay;
    }

    /**
     * Returns the time passed from the start.
     * @return time attribute in as String
     */
    public String getTime(){
        return time+"";
    }

    /**
     * Sets time attribute to zero.
     */
    public void resetTime(){
        time = 0;
    }
}
