package idk.plugin.npc.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import idk.plugin.npc.Loader;
import io.netty.util.internal.ThreadLocalRandom;
import ru.nukkitx.forms.elements.CustomForm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static idk.plugin.npc.NPC.*;

public class NpcCommand extends Command {

    public NpcCommand() {
        super("npc", "", "/npc");
        this.getCommandParameters().put("default",
                new CommandParameter[]{
                        new CommandParameter("create | getId | list | teleport | edit", CommandParamType.TEXT, false)
                });
        this.setPermission("npc.use");
    }
/*
    public static void sendSetNPCSkinPacket(Entity npc, Player player, String username) { // The username is the name for the player that has the skin.
        removeNPCPacket(npc, player);

        GameProfile profile = new GameProfile(uuid, null);

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", username)).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);

                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                String skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                String signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));

                profile.getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                player.getServer().broadcastMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The client settings.
        DataWatcher watcher = npc.getDataWatcher();
        watcher.set(new DataWatcherObject<>(15, DataWatcherRegistry.a), (byte)127);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(npc.getId(), watcher, true);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);

        addNPCPacket(npc, player);
    }
*/
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cUse command in game!");
            return false;
        }

        Player player = (Player) sender;
        UUID playerUniqueId = player.getUniqueId();

        if (args.length < 1) {
            player.sendMessage(
                    "Available commands: \n" +
                            " /npc spawn - Create npc entity \n" +
                            " /npc getID - Get ID entity \n" +
                            " /npc list - Get npc entity list \n" +
                            " /npc teleport - Teleport entity to you \n" +
                            " /npc edit - Open entity setup menu"
            );

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn":
            case "create":
                CustomForm customForm = new CustomForm("§l§8Create NPC")
                        .addDropDown("§l§7Entity Type", entityList, 16)
                        .addInput("§l§7Entity Name")
                        .addInput("§l§7Custom Skin")
                        .addSlider("Size",1,100,1,10)
                        .addToggle("§l§fRotаtion", true)
                        .addToggle("§l§fNametag visibilitу", true)
                        .addInput("§l§7Commands (Across ,)", "cmd1, cmd2, cmd3")
                        .addToggle("§l§fExecute by playеr", true)
                        .addLabel("\n§l§7If the npc is a Human:")
                        .addToggle("§l§fUsе itеms on you", false);

                customForm.send(player, (target, form, data) -> {
                    if (data == null) return;

                    String entityType = (String) data.get(0);
                    String entityName = (String) data.get(1);
                    String customSkin = (String) data.get(2);
                    Float scale = (Float) data.get(3);
                    boolean isRotation = (Boolean) data.get(4);
                    boolean visibleTag = (Boolean) data.get(5);
                    String[] commands = ((String) data.get(6)).split(", ");
                    boolean isPlayer = (Boolean) data.get(7);
                    boolean hasUseItem = entityType.equals("Human") ? (Boolean) data.get(9) : false;

                    Skin nsStatic = new Skin();
                    BufferedImage skinFile = null;
                    String filePath;
                    Skin oldSkin = new Skin ();

                    if (customSkin.length() < 1) {
                        int prots = ThreadLocalRandom.current().nextInt(0, 2); //TODO: Working?
                        if (prots == 1) {
                            InputStream skinSteve = getClass().getResourceAsStream("/steve.png");
                            try {
                                skinFile = ImageIO.read(skinSteve);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        else{
                            InputStream skinAlex = getClass().getResourceAsStream("/alex.png");
                            try {skinFile = ImageIO.read(skinAlex);}
                            catch (IOException ioException) {ioException.printStackTrace();}


                            oldSkin.setSkinData(skinFile);
                            player.setSkin(oldSkin);
                            player.getServer().updatePlayerListData(playerUniqueId, player.getId(), player.getName(), oldSkin);
                            nsStatic = oldSkin;
                        }
                    }
                    else {
                        String nameError = new StringBuilder().append(TextFormat.RED + "No " + args[0] + "file found in " + Loader.plugin.directory.toString()).append(". Be sure to include the file extension when specifying a skin (e.g. 'Herobrine.png', rather than 'Herobrine''.)").toString();
                        try {
                            filePath = Loader.plugin.directory.toString().concat("\\").concat(customSkin);
                            File skinPath = (new File(filePath));
                            if (skinPath.exists()) {skinFile = ImageIO.read(skinPath);}
                            else{sender.sendMessage(nameError);}
                        } catch (IOException ioException) {
                            sender.sendMessage(nameError);
                        }

                        Skin newSkin = new Skin();

                        newSkin.setSkinData(skinFile);

                        player.setSkin(newSkin);
                        player.getServer().updatePlayerListData(playerUniqueId, player.getId(), player.getName(), newSkin);

                        nsStatic = newSkin;
                    }


                    CompoundTag compoundTag = nbt(player, entityType, commands, isPlayer, isRotation);

                    Entity entity = Entity.createEntity(entityType + "NPC", player.chunk, compoundTag);
                    if (!entityName.replace(" ", "").equals("")) {
                        entity.setNameTag(entityName);
                    }

                    entity.setNameTagVisible(visibleTag);
                    entity.setNameTagAlwaysVisible(visibleTag);

                    entity.setScale((scale / 10));

                    if (entityType.equals("Human")) {
                        EntityHuman human = (EntityHuman) entity;

                        if (hasUseItem) {
                            PlayerInventory inventory = player.getInventory();
                            PlayerInventory humanInventory = human.getInventory();

                            humanInventory.setContents(inventory.getContents());
                        }
                    }

                    sender.sendMessage("§fNPC §aspawned§f with ID §e" + entity.getId() + " §fand the name §b\"§f" + entity.getName() + "§b\"");

                    player.getServer().updatePlayerListData(playerUniqueId, player.getId(), player.getName(), nsStatic);

                    player.hidePlayer(player);
                    player.showPlayer(player);

                    entity.spawnToAll();

                    entity.recalculateBoundingBox();

                });

                break;

            case "getid":
            case "id":
                if (npcEditorsList.contains(playerUniqueId)) {
                    player.sendMessage("§cYou are in entity edit mode");
                    break;
                }

                idRecipientList.add(playerUniqueId);
                player.sendMessage("§aID MODE - click an entity to get the ID");
                break;

            case "list":
            case "entities":
                sender.sendMessage("§aAvailable entities: §3" + entityList.toString());
                break;

            case "tphere":
            case "teleport":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /npc teleport <ID>");
                    return true;
                }

                try {
                    Entity entity = player.getLevel().getEntity(Integer.parseInt(args[1]));

                    if (entity.namedTag.getBoolean("npc")) {
                        entity.teleport(player);
                        entity.respawnToAll();
                        player.sendMessage("§aEntity teleported");
                        return true;
                    }
                } catch (Exception exception) {
                    player.sendMessage("§cUsage: /npc teleport <ID>");
                }
                break;

            case "edit":
                if (idRecipientList.contains(playerUniqueId)) {
                    player.sendMessage("§cYou are in entity get id mode");
                    break;
                }

                npcEditorsList.add(playerUniqueId);
                player.sendMessage("§aEDIT MODE - click an entity to edit it");
                break;
        }
        return false;
    }
}
