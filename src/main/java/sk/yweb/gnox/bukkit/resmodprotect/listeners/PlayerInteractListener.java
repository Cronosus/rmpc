package sk.yweb.gnox.bukkit.resmodprotect.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
    public ResidenceManager resManager = Residence.getResidenceManager();
    public Config cfg = ResModProtect.getConfigManager();
    private ResModProtect plugin = new ResModProtect();


    public PlayerInteractListener() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {

        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("residence.admin")) {
            return;
        }

        Player p = e.getPlayer();
        Location l = e.getRightClicked().getLocation();
        ClaimedResidence res = resManager.getByLoc(l);

        if (res == null) {
            return;
        }

        ResidencePermissions resPerms = res.getPermissions();
        boolean hasEntityPerms = resPerms.playerHas(p.getName(), "entity", true);

        if (!hasEntityPerms) {
            e.setCancelled(true);
            String message = ("Player: " + p.getName() + " tried to right click entity " + e.getRightClicked().getType() + " in Residence: " + res.getName() + ".");
            plugin.logToFile(message);

            ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to right click entity {1} in Residence: {2}.", new Object[]{p.getName(), e.getRightClicked().getType(), res.getName()});

            p.sendMessage(ChatColor.RED + "You cannot right-click this entity.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlyerInteract(PlayerInteractEvent e) {

        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("residence.admin")) {
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR) {

                Player p = e.getPlayer();
                Block clickedBlock = p.getTargetBlock(null, 15);
                Location l = clickedBlock.getLocation();

                ClaimedResidence res = resManager.getByLoc(l);

                if (res == null) {
                    return;
                }

                ResidencePermissions resPerms = res.getPermissions();
                boolean hasWrenchPerms = resPerms.playerHas(p.getName(), "wrench", true);

                if (!hasWrenchPerms && e.getItem() != null && cfg.getWrenchIds().contains(e.getItem().getTypeId())) {
                    e.setCancelled(true);

                    String message = ("Player: " + p.getName() + " tried to use wrench on item ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ".");
                    plugin.logToFile(message);

                    ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to use wrench on item ID: {1} in Residence: {2}.", new Object[]{p.getName(), clickedBlock.getTypeId(), res.getName()});

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
        ClaimedResidence res = resManager.getByLoc(l);

        if (res == null || p.isOp()) {
            return;
        }

        ResidencePermissions resPerms = res.getPermissions();

        boolean hasMEPerms = resPerms.playerHas(p.getName(), "me", true);

        if (!hasMEPerms && cfg.getAEProtectedIds().contains(clickedBlock.getTypeId())) {
            p.closeInventory();
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You cannot open this block!");
            return;
        }

        boolean hasChestPerms = resPerms.playerHas(p.getName(), "modchests", true);

        if (!hasChestPerms && cfg.getProtectedChestIds().contains(clickedBlock.getTypeId())) {
            p.closeInventory();
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You cannot open this chest!");
            return;
        }

        boolean hasWrenchPerms = resPerms.playerHas(p.getName(), "wrench", true);

        if (!hasWrenchPerms && e.getItem() != null && cfg.getWrenchIds().contains(e.getItem().getTypeId())) {
            e.setCancelled(true);

            String message = ("Player: " + p.getName() + " tried to use wrench on item ID " + clickedBlock.getTypeId() + " in Residence: " + res.getName() + ".");
            plugin.logToFile(message);

            ResModProtect.logger.log(Level.WARNING, "Player: {0} tried to use wrench on item ID: {1} in Residence: {2}.", new Object[]{p.getName(), clickedBlock.getTypeId(), res.getName()});

            p.sendMessage(ChatColor.RED + "You cannot use this Item.");
            return;
        }

        boolean hasMachinePerms = resPerms.playerHas(p.getName(), "machine", true);

        if (!hasMachinePerms && cfg.getMachineIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "You cannot open this machine!");
            return;
        }

        boolean hasDecorPerms = resPerms.playerHas(p.getName(), "decor", true);

        if (!hasDecorPerms && cfg.getDecorIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "You are not allowed to use this!");
        }

    }


}
