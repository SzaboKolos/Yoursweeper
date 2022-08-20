package Front;

import Back.Game;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileOperations {


    public static void save(Scores s, String filename){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(s);
            out.close();
            fileOut.close();
            System.out.println("Scores are saved in " + filename);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    public static ArrayList<Score> load(String filename){
        return new ArrayList<Score>();
    }
}
