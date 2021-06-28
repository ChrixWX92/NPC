package idk.plugin.npc;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.TextFormat;
import idk.plugin.npc.commands.NpcCommand;
import idk.plugin.npc.listeners.entity.*;
import idk.plugin.npc.listeners.entity.player.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Loader extends PluginBase {

    public static Loader plugin;

    public String getPath() {
        String PATH = "\\skins";
        Path currentRelativePath = Paths.get("");
        String imgPath = currentRelativePath.toAbsolutePath().toString();
        return imgPath.concat(PATH);
    }

    public File directory = new File(getPath());

    @Override
    public void onEnable() {
        plugin = this;
        NPC.registerEntity();
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        getServer().getConsoleSender().sendMessage(TextFormat.BLUE+"NPC plugin enabled");

        if (! directory.exists()){
            directory.mkdir();
            getServer().getConsoleSender().sendMessage(TextFormat.GOLD + getPath() + " created for custom NPC skins.");
        }
        else {getServer().getConsoleSender().sendMessage(TextFormat.GREEN + "Skins folder found.");}

        if(pluginManager.getPlugin("FormAPI") == null) {
            getLogger().alert("§cRequired component not found (FormAPI Plugin)");
            pluginManager.disablePlugin(this);
            return;
        }

        this.registerListeners();
        this.registerCommands();
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








