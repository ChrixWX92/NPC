package idk.plugin.npc;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.TextFormat;
import idk.plugin.npc.commands.NpcCommand;
import idk.plugin.npc.commands.SetTalk;
import idk.plugin.npc.commands.Talk;
import idk.plugin.npc.listeners.entity.*;
import idk.plugin.npc.listeners.entity.player.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;

public class Loader extends PluginBase {

    public static Loader plugin;

    public static File getPath(String folder) {
        File folderDir = new File(plugin.getServer().getDataPath(), folder);
        return folderDir;
    }

    public File skinsDirectory;
    public File talkDirectory;
    public static Hashtable<String, String> setTalk = new Hashtable<>();

    @Override
    public void onEnable() {
        plugin = this;

        skinsDirectory = getPath("skins");
        talkDirectory = getPath("dialogue");

        NPC.registerEntity();
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        getServer().getConsoleSender().sendMessage(TextFormat.BLUE+"NPC plugin enabled");


        if (! skinsDirectory.exists()){
            skinsDirectory.mkdir();
            getServer().getConsoleSender().sendMessage(TextFormat.GOLD + getPath("skins").toString() + " created for custom NPC skins.");
        }
        else {getServer().getConsoleSender().sendMessage(TextFormat.GREEN + "Skins folder found.");}

        if (! talkDirectory.exists()){
            talkDirectory.mkdir();
            getServer().getConsoleSender().sendMessage(TextFormat.GOLD + getPath("dialogue").toString() + " created for NPC dialogue CSVs.");
        }
        else {getServer().getConsoleSender().sendMessage(TextFormat.GREEN + "Dialogue folder found.");}

        if(pluginManager.getPlugin("FormAPI") == null) {
            getLogger().alert("§cRequired component not found (FormAPI Plugin)");
            pluginManager.disablePlugin(this);
            return;
        }

        this.registerListeners();
        this.registerCommands();

        getServer().getCommandMap().register("talk", new Talk());
        getServer().getCommandMap().register("settalk", new SetTalk());
    }

    private void registerListeners() {
        Arrays.asList(
                new EntityDamageListener(),
                new EntityVehicleEnterListener(),
                new PlayerQuitListener(),
                new PlayerMoveListener()
        ).forEach(listener -> Server.getInstance().getPluginManager().registerEvents(listener, this));
    }

    private void registerCommands() {
        Server.getInstance().getCommandMap().register("", new NpcCommand());
    }
}








