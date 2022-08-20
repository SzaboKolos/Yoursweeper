package Front;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Scores{
    private ArrayList<Score> topList = new ArrayList<>(10);

    public Scores(){

    }
    public void addScore(Score s){
        List<Score> sortedScores = new ArrayList<>();
        sortedScores.add(0,s);
        sortedScores.addAll(topList);
        sortedScores = sortedScores.stream().sorted(new Comparator<Score>() {
            @Override
            public int compare(Score o1, Score o2) {
                return Integer.compare(o1.getTime(), o2.getTime());
            }
        }).toList();

        for (int k = 0; k < sortedScores.size(); k++)
            sortedScores.get(k).setNumber(k);
        topList = new ArrayList<>(10);
        topList.addAll(sortedScores);
    }
    public String toString(){
        StringBuilder a = new StringBuilder();
        for (Score sc:topList) {
            a.append(sc.getName()).append(":").append(sc.getTime()).append("; ");
        }
        return a.toString();
    }
    public void loadScores(){
        //TODO file operation implement
        topList = FileOperations.load();
    }
}
