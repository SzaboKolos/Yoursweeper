package Front;

import Back.Game;

import java.io.*;
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
        ArrayList<Score> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            result = ((Scores)ois.readObject()).getTopList();
        } catch (IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
        return result;
    }
}
