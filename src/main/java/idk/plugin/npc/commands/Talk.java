package idk.plugin.npc.commands;


import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import idk.plugin.npc.Loader;
import ru.nukkitx.forms.elements.SimpleForm;

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
            String talkText = Loader.setTalk.get(args[0]);
            if (talkText != null) {
                Player p = ((Player) sender).getPlayer();
                SimpleForm simpleForm = new SimpleForm("§l§8" + entName)
                        .setContent("§f" + talkText)
                        .addButton("OK");

                simpleForm.send(p, (target, form, data) -> {
                    if (data == -1) return; });
                return true;
            } else {
                sender.sendMessage(TextFormat.RED + "No dialogue found under the title specified. Remember that titles are case-sensitive!");
                return false;
            }


    }

}
}