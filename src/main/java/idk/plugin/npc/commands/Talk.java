package idk.plugin.npc.commands;


import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
//import idk.plugin.npc.Loader;
import ru.nukkitx.forms.elements.SimpleForm;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static idk.plugin.npc.listeners.entity.EntityDamageListener.entName;

public class Talk extends Command {

    public Talk() {
        super("talk");
        this.setDescription("Assigns dialogue to a dialogue box.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(TextFormat.RED + "Cannot execute from the console.");
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(TextFormat.RED + "Please specify the title of your stored dialogue.");
            return false;
        } else {
            //String dialogue = String.join(" ",args);
            Player p = ((Player) sender).getPlayer();
            String talkText = UpdateCsv.findDialogue(args[0], p, false); //Loader.setTalk.get(args[0]);
            boolean textFound = false;
            if (talkText != null) {
                switch (talkText) {
                    case "$EEE$":
                        sender.sendMessage(TextFormat.RED + "Fatal error while searching for dialogue. Please reload the server and try again.");
                        return false;
                    case "$NEE$":
                        sender.sendMessage(TextFormat.RED + "No dialogue found under title \"" + args[0] + "\"" + ".  Remember that titles are case-sensitive!");
                        talkText = "";
                        textFound = false;
                        break;
                    case "$AEE$":
                        textFound = true;
                        break;
                    default:
                        talkText = "";
                        sender.sendMessage(TextFormat.RED + "Unknown exception occurred while searching for dialogue with title \"" + args[0] + "\"" + ".  Please try again with a different title.");
                }

                if (textFound) {talkText = UpdateCsv.findDialogue(args[0], p, true);}
                String cleanText = talkText.replaceAll("\r", ""); // What an absolute nightmare this line was to troubleshoot!
                sender.sendMessage(talkText);

                StringSelection selection = new StringSelection(talkText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                SimpleForm simpleForm = new SimpleForm("§l§8" + entName)
                .setContent("§f" + cleanText)
                .addButton("OK");

                simpleForm.send(p, (target, form, data) -> {
                    if (data == -1) return; });
                return true;

            } else {
                return false;
            }


    }

}
}