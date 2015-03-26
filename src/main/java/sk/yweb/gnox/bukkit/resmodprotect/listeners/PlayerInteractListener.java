package sk.yweb.gnox.bukkit.resmodprotect.listeners;

import net.t00thpick1.residence.api.ResidenceAPI;
import net.t00thpick1.residence.api.ResidenceManager;
import net.t00thpick1.residence.api.areas.PermissionsArea;
import net.t00thpick1.residence.api.areas.ResidenceArea;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import sk.yweb.gnox.bukkit.resmodprotect.Config;
import sk.yweb.gnox.bukkit.resmodprotect.ResModProtect;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerInteractListener implements Listener {

    public static final Logger logger = ResModProtect.logger;
    public ResidenceManager resManager = ResidenceAPI.getResidenceManager();
    public Config cfg = ResModProtect.getConfigManager();
    private ResModProtect plugin = ResModProtect.plugin;


    public PlayerInteractListener() {
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("residence.admin")) {
            return;
        }

        Player p = e.getPlayer();
        Location l = e.getRightClicked().getLocation();
        PermissionsArea perms = ResidenceAPI.getPermissionsAreaByLocation(l);

        if (perms == null) {
            return;
        }

        boolean hasEntityPerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_ENTITY));

        if (!hasEntityPerms) {
            e.setCancelled(true);

            ResidenceArea res = resManager.getByLocation(l);
            String message = ("Player: " + p.getName() + " tried to right click entity " + e.getRightClicked().getType() + " in Residence: " + res.getName() + ". Coordinates X: " + e.getRightClicked().getLocation().getX() + " Y: " + e.getRightClicked().getLocation().getY() + " Z: " + e.getRightClicked().getLocation().getZ() + ". World: " + e.getRightClicked().getLocation().getWorld().getName());
            plugin.logToFile(message);

            ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to right click entity {1} in Residence: {2}.", new Object[]{p.getName(), e.getRightClicked().getType(), res.getName()});

            p.sendMessage(ChatColor.RED + "You cannot right-click this entity.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("residence.admin")) {
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {

                Player p = e.getPlayer();
                Block clickedBlock = p.getTargetBlock(null, 15);
                Location l = clickedBlock.getLocation();
                PermissionsArea perms = ResidenceAPI.getPermissionsAreaByLocation(l);

                ResidenceArea res = resManager.getByLocation(l);


                if (res == null) {
                    return;
                }

                boolean hasWrenchPerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_WRENCH));

                if (!hasWrenchPerms && e.getItem() != null && cfg.getWrenchIds().contains(e.getItem().getTypeId())) {
                    e.setCancelled(true);
                    e.setUseInteractedBlock(Event.Result.DENY);
                    e.setUseItemInHand(Event.Result.DENY);

                    String message = ("Player: " + p.getName() + " tried to use wrench " + e.getItem().getTypeId() + " onn item ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + " Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
                    plugin.logToFile(message);

                    ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to use wrench {1} on item ID: {2} in Residence: {3}.", new Object[]{p.getName(), e.getItem().getTypeId(), clickedBlock.getTypeId(), res.getName()});

                    p.sendMessage(ChatColor.RED + "You cannot use this Item.");
                    return;
                }
                return;
            } else {
                return;
            }
        }

        Player p = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();
        Location l = clickedBlock.getLocation();
        ResidenceArea res = resManager.getByLocation(l);

        if (res == null || p.isOp() || p.hasPermission("residence.admin")) {
            return;
        }

        PermissionsArea perms = ResidenceAPI.getPermissionsAreaByLocation(l);

        if (p.getDisplayName().startsWith("[") && p.getDisplayName().endsWith("]") &&
                !perms.allowAction(Config.getFlag(Config.FLAG_FAKEPLAYER))) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            return;
        }

        boolean hasMEPerms = perms.allowAction(p.getDisplayName(), Config.getFlag(Config.FLAG_ME));

        if (!hasMEPerms && cfg.getAEProtectedIds().contains(clickedBlock.getType().getId())) {
            p.closeInventory();
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            String message = ("Player: " + p.getDisplayName() + " tried to open ME - ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + "Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
            plugin.logToFile(message);
            p.sendMessage(ChatColor.RED + "You cannot open this block!");
            return;
        }

        boolean hasChestPerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_MODCHESTS));

        if (!hasChestPerms && cfg.getProtectedChestIds().contains(clickedBlock.getTypeId())) {
            p.closeInventory();
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            String message = ("Player: " + p.getName() + " tried open chest - ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + "Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
            plugin.logToFile(message);
            p.sendMessage(ChatColor.RED + "You cannot open this chest!");
            return;
        }

        boolean hasWrenchPerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_WRENCH));

        if (!hasWrenchPerms && e.getItem() != null && cfg.getWrenchIds().contains(e.getItem().getTypeId())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);

            String message = ("Player: " + p.getName() + " tried to use wrench " + e.getItem().getTypeId() + " on item ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + "Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
            plugin.logToFile(message);

            ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to use wrench {1} on item ID: {2} in Residence: {3}.", new Object[]{p.getName(), e.getItem().getTypeId(), clickedBlock.getTypeId(), res.getName()});

            p.sendMessage(ChatColor.RED + "You cannot use this Item.");
            return;
        }

        boolean hasMachinePerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_MACHINE));

        if (!hasMachinePerms && cfg.getMachineIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);

            p.closeInventory();
            String message = ("Player: " + p.getName() + " tried to open machine - ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + "Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
            plugin.logToFile(message);
            p.sendMessage(ChatColor.RED + "You cannot open this machine!");
            return;
        }

        boolean hasDecorPerms = perms.allowAction(p.getName(), Config.getFlag(Config.FLAG_DECOR));

        if (!hasDecorPerms && cfg.getDecorIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            p.closeInventory();

            String message = ("Player: " + p.getName() + " tried to open decor - ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ". Coordinates X: " + clickedBlock.getX() + " Y: " + clickedBlock.getY() + "Z: " + clickedBlock.getZ()) + ". World: " + clickedBlock.getWorld().getName();
            plugin.logToFile(message);
            p.sendMessage(ChatColor.RED + "You are not allowed to use this!");
        }

    }


}
