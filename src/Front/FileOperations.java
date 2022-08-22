package Front;

import java.io.*;
import java.util.ArrayList;

public class FileOperations {
    public static void save(Scores s, String fileName){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(s);
            out.close();
            fileOut.close();
            System.out.println("Scores are saved in " + fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    public static ArrayList<Score> load(String fileName){
        ArrayList<Score> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            result = ((Scores)ois.readObject()).getTopList();
        } catch (IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
        return result;
    }
}
