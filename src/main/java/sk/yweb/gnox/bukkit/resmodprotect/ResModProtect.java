package sk.yweb.gnox.bukkit.resmodprotect;

import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import sk.yweb.gnox.bukkit.resmodprotect.listeners.PlayerInteractListener;
import sk.yweb.gnox.bukkit.resmodprotect.listeners.ResidenceListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResModProtect extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");
    private static Config cfgManager;

    @Override
    public void onEnable() {

        ResidenceCounter.VALUES.clear();

        saveDefaultConfig();
        cfgManager = new Config(getDataFolder());

        PluginManager pm = getServer().getPluginManager();
        Plugin p = pm.getPlugin("Residence");

        if (p != null) {
            if (!p.isEnabled()) {
                System.out
                        .println("ResModProtect - Manually Enabling Residence!");
                pm.enablePlugin(p);
            }

        } else {
            System.out
                    .println("ResModProtect - Residence NOT Installed, DISABLED!");
            this.setEnabled(false);
        }


        logger.info("[ResModProtect] Added 6 flags and 1 group to Residence!");

        FlagPermissions.addFlag("me");
        FlagPermissions.addFlag("modchests");
        FlagPermissions.addFlag("wrench");
        FlagPermissions.addFlag("machine");
        FlagPermissions.addFlag("decor");
        FlagPermissions.addFlag("entity");

        FlagPermissions.addFlagToFlagGroup("mods", "me");
        FlagPermissions.addFlagToFlagGroup("mods", "modchests");
        FlagPermissions.addFlagToFlagGroup("mods", "wrench");
        FlagPermissions.addFlagToFlagGroup("mods", "machine");
        FlagPermissions.addFlagToFlagGroup("mods", "decor");
        FlagPermissions.addFlagToFlagGroup("mods", "entity");

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new ResidenceListener(), this);

    }

    @Override
    public void onDisable() {
        cfgManager = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rmp")
                || command.getName().equalsIgnoreCase("resmodprotect")) {
            if (args.length != 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!(sender instanceof Player)) {
                        cfgManager.reload();
                        sender.sendMessage("[ResModProtect] Config has been reloaded!");
                        return true;
                    } else {
                        Player p = (Player) sender;
                        if (p.isOp()) {
                            cfgManager.reload();
                            sender.sendMessage("[ResModProtect] Config has been reloaded!");
                            logger.info("[ResModProtect] Config has been reloaded!");
                            return true;
                        } else {
                            p.sendMessage(ChatColor.RED
                                    + "You are not allowed to use this command!");
                            return true;
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("help")) {
                    if (args.length == 2 && args[1].equalsIgnoreCase("flags")) {
                        sender.sendMessage(ChatColor.RED
                                + "List of available  flags:");
                        sender.sendMessage(ChatColor.DARK_GREEN
                                + "- me : "
                                + ChatColor.WHITE
                                + "ME stuff cannot be accessed without this flag");
                        sender.sendMessage(ChatColor.DARK_GREEN
                                + "- modchests : " + ChatColor.WHITE
                                + "chests from mods cannot be opened");
                        sender.sendMessage(ChatColor.DARK_GREEN
                                + "- wrench : "
                                + ChatColor.WHITE
                                + "wrenches cannot be used in residence with this flag disabled");
                        sender.sendMessage(ChatColor.DARK_GREEN
                                + "- machine : " + ChatColor.WHITE
                                + "machines cannot be opened");
                        sender.sendMessage(ChatColor.DARK_GREEN
                                + "- decor : "
                                + ChatColor.WHITE
                                + "decorative blocks (ie Bibliocraft armor stands) cannot be opened");
                        sender.sendMessage(ChatColor.AQUA
                                + "- mods : "
                                + ChatColor.WHITE
                                + "this flag gives every flag from this list to a player");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + "Content:");
                        sender.sendMessage(ChatColor.GREEN + "/rmp help flags");
                        return true;
                    }
                }
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Content:");
                sender.sendMessage(ChatColor.GREEN + "/rmp help flags");
                return true;
            }
//            if (args[0].equalsIgnoreCase("d")) {
//                debugCommand((Player) sender, args[1]);
//                return true;
//            }
        }
        if (command.getName().equalsIgnoreCase("rmpreload")) {
            if (!(sender instanceof Player)) {
                logger.log(Level.INFO, "Plugin reloaded by {0}!",
                        sender.getName());
                reloadPlugin();
                return true;
            } else {
                Player p = (Player) sender;
                if (p.isOp()) {
                    p.sendMessage("Plugin reload commiting!");
                    logger.log(Level.INFO, "Plugin reloaded by {0}!",
                            sender.getName());
                    reloadConfig();
                    return true;
                } else {
                    p.sendMessage("You are not allowed to use this command.");
                    return true;
                }

            }
        }

        return false;

    }

    public static Config getConfigManager() {
        return cfgManager;
    }

    public void reloadPlugin() {
        this.onDisable();
        this.onEnable();

    }

    public void logToFile(String message) {
        try {
            File dataFolder = Bukkit.getServer().getPluginManager().getPlugin("ResModProtect").getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File saveTo = new File(dataFolder, "log.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }


            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            Calendar cal = Calendar.getInstance();
            cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss");

            String logText = "[" + sdf.format(cal.getTime()) + "] " + message;
            pw.println(logText);
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void debugCommand(Player p, String id) {
        p.getWorld().getBlockAt(p.getTargetBlock(null, 10).getLocation())
                .setType(Material.getMaterial(Integer.parseInt(id)));

    }
}
