package idk.plugin.npc.dialogue;

import cn.nukkit.Player;
import idk.plugin.npc.Loader;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UpdateCsv {

    public static String dkChange;

    public static String cleanStr(String s) {
        return s;//.replaceAll("[,¬\n\r¦]", "");
    }

    public static HashMap<String, String> loadDialogueFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            HashMap<String, String> dlgs = new HashMap<>();
            String line = br.readLine();

            while (line != null) {
                if (!line.isEmpty()) {
                    String[] attrs = line.split(",");
                    if (attrs.length == 2) {
                        dlgs.put(cleanStr(attrs[0]), cleanStr(attrs[1]));
                    }
                }

                line = br.readLine();
            }

            return dlgs;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public static void saveDialogueFile(String filename, HashMap<String, String> dlgs) {
        try (FileWriter fw = new FileWriter(filename)) {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (Map.Entry<String, String> entry : dlgs.entrySet()) {
                pw.println(String.format("%s,%s", entry.getKey(), entry.getValue()));
            }

            bw.close();
            pw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     *Add dialogue to the appropriate CSV.
     *
     * @param diaKey Talk text save name
     * @param diaValue Dialogue content
     * @param user Player object to determine world name to append to the filepath
     * @return Boolean - has it worked?
     */
    public static boolean updateDialogue(String diaKey, String diaValue, Player user) {
        try {
            String worldName = user.level.getName();
            File filepath = new File(Loader.getPath("dialogue"), worldName+".csv");
            HashMap<String, String> dlgs = loadDialogueFile(filepath.toString());

            dlgs.put(cleanStr(diaKey), cleanStr(diaValue));
            saveDialogueFile(filepath.toString(), dlgs);

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
     * @return A string, either the dialogue found, or a keyword representing the search result, to be checked by Talk
     */
    public static String findDialogue(String diaKey, Player user) throws IOException {
        String dk = cleanStr(diaKey);

        String worldName = user.level.getName();
        File filepath = new File(Loader.getPath("dialogue"), worldName + ".csv");
        HashMap<String, String> dlgs = loadDialogueFile(filepath.toString());

        if (dlgs.containsKey(dk))
            return dlgs.get(dk);

        return null;
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
        File filepath = new File(Loader.getPath("dialogue"), worldName + ".csv");
        String searchKey;
        String rtnDialogue;
        File tempFile = new File(Loader.getPath("dialogue"), worldName + "temp.csv");

        try
        {
            FileWriter fw = new FileWriter(tempFile,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            Scanner scanner = new Scanner(filepath);
            scanner.useDelimiter("[,\n]");

            while (scanner.hasNext())
            {
                searchKey = scanner.next();
                rtnDialogue = scanner.next();

                if (!searchKey.equals(diaKey))
                {
                    pw.println(searchKey+","+rtnDialogue);
                }

            }
            scanner.close();
            pw.flush();
            pw.close();
            bw.close();
            fw.close();
            filepath.delete();
            tempFile.renameTo(filepath);

            return true;

        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, "Error during overwrite! Please ensure the relevant CSV is not currently in use by another application.");
            return false;
        }

    }

}
