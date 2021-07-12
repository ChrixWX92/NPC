package idk.plugin.npc.commands;

import cn.nukkit.Player;
import idk.plugin.npc.Loader;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class UpdateCsv {

    public static String repDialogue;
    public static String dkChange;

    /**
     *Add dialogue to the appropriate CSV.
     *
     * @param diaKey Talk text save name
     * @param diaValue Dialogue content
     * @param user Player object to determine world name to append to the filepath
     * @return Boolean - has it worked?
     */
    public static boolean updateDialogue(String diaKey, String diaValue, Player user) {
        try{
            String worldName = user.level.getName();
            String filepath = (Loader.getPath("dialogue") + "\\"+worldName+".csv");
            FileWriter fw = new FileWriter(filepath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            dkChange = diaKey.replaceAll("[ ,¬\n\r¦]","");

            repDialogue = diaValue.replace(",","¬");

            pw.println(dkChange+","+repDialogue);
            pw.flush();
            pw.close();
            bw.close();
            fw.close();

            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during data write! Please ensure the relevant CSV is not currently in use by another application.");
            return false;
        }
    }

    /**
     *Search for a dialogue entry based on the title it's stored under. Returning its value is optional.
     *
     * @param diaKey Talk text save name
     * @param user Player object to determine world name to append to the filepath
     * @param give True = Returns the text value associated with diaKey, False = Returns a keyword to confirm whether the entry exists or not
     * @return A string, either the dialogue found, or a keyword representing the search result, to be checked by Talk
     */
    public static String findDialogue(String diaKey, Player user, boolean give) {

        boolean found = false;
        boolean rtn = give;
        String searchKey;
        String rtnDialogue = "";
        String dk = diaKey.replaceAll("[ ,¬\n\r¦]","");;

        try {

            String worldName = user.level.getName();
            String filepath = (Loader.getPath("dialogue") + "\\" + worldName + ".csv");

            Scanner scanner = new Scanner(new File(filepath));
            scanner.useDelimiter("[,\n]");

            while (scanner.hasNext() && !found) {
                searchKey = scanner.next();
                rtnDialogue = scanner.next();

                if (searchKey.equals(dk)) {
                    found = true;
                    if (!rtn)
                    {
                        String confirm = "$AEE$";
                        scanner.close();
                        return confirm;
                    }
                    }
                }

            if (found) {
                String dV = rtnDialogue.replace("¬", ",");
                String dVFlat = dV.replace("\n","");
                String diaValue = dVFlat.replace("¦", "\n");
                scanner.close();
                return diaValue;
            } else {
                rtnDialogue = "*NO TEXT FOUND UNDER NAME \"" + dk + " \"*";
                if (!rtn)
                {
                    String confirm = "$NEE$";
                    scanner.close();
                    return confirm;
                }
                return rtnDialogue;
            }

        } catch (IOException e) {
            e.printStackTrace();
            rtnDialogue = "*ERROR OCCURRED WHILE SEARCHING FOR TEXT WITH NAME \"" + dk + " \" PLEASE OVERWRITE THE ENTRY OR CHECK THE RELEVANT CSV*";
            if (!rtn)
            {
                String confirm = "$EEE$";
                return confirm;
            }
            return rtnDialogue;
        }
    }

    /**
     *Used for deleting CSV entries, but ultimately only called when overwriting.
     *
     * @param diaKey Talk text save name
     * @param user Player object to determine world name to append to the filepath
     * @return Boolean - has it worked?
     */
    public static boolean deleteDialogue(String diaKey, Player user)
    {
        String worldName = user.level.getName();
        String filepath = (Loader.getPath("dialogue") + "\\" + worldName + ".csv");
        String searchKey;
        String rtnDialogue;
        String tbd = diaKey;
        String tempFile = (Loader.getPath("dialogue") + "\\" + worldName + "temp.csv");

        File oldFile = new File(filepath);
        File newFile = new File(tempFile);

        try
        {
            FileWriter fw = new FileWriter(tempFile,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            Scanner scanner = new Scanner(new File(filepath));
            scanner.useDelimiter("[,\n]");

            while (scanner.hasNext())
            {
                searchKey = scanner.next();
                rtnDialogue = scanner.next();

                if (!searchKey.equals(tbd))
                {
                    pw.println(searchKey+","+rtnDialogue);
                }

            }
            scanner.close();
            pw.flush();
            pw.close();
            bw.close();
            fw.close();
            oldFile.delete();
            File dump = new File(filepath);
            newFile.renameTo(dump);

            return true;

        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Error during overwrite! Please ensure the relevant CSV is not currently in use by another application.");
            return false;
        }

    }

}
