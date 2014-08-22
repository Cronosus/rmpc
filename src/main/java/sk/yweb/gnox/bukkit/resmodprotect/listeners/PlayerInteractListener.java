package sk.yweb.gnox.bukkit.resmodprotect.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import sk.yweb.gnox.bukkit.resmodprotect.Config;
import sk.yweb.gnox.bukkit.resmodprotect.ResModProtect;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerInteractListener implements Listener {

    public static final Logger logger = ResModProtect.logger;
    public ResidenceManager resManager = Residence.getResidenceManager();
    public Config cfg = ResModProtect.getConfigManager();

    public PlayerInteractListener() {
    }



    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player p = e.getPlayer();
        p.sendMessage("Test!");
        Block clickedBlock = e.getClickedBlock();
        Location l = clickedBlock.getLocation();
        ClaimedResidence res = resManager.getByLoc(l);

        if (res == null || p.isOp()) {
            return;
        }

        //<editor-fold defaultstate="collapsed" desc="debug testing">
//        if (p.getItemInHand().getTypeId() == 371) {
//            p.sendMessage(Integer.toString(e.getClickedBlock().getTypeId()));
//        }
        //</editor-fold>

        ResidencePermissions resPerms = res.getPermissions();

        boolean hasMEPerms = resPerms.playerHas(p.getName(), "me", true);

        if (!hasMEPerms && cfg.getAEProtectedIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "You cannot open this block!");
            return;
        }

        boolean hasChestPerms = resPerms.playerHas(p.getName(), "modchests", true);

        if (!hasChestPerms && cfg.getProtectedChestIds().contains(clickedBlock.getTypeId())) {
            e.setCancelled(true);
            p.closeInventory();
            p.sendMessage(ChatColor.RED + "You cannot open this chest!");
            return;
        }

        boolean hasWrenchPerms = resPerms.playerHas(p.getName(), "wrench", true);

        if (!hasWrenchPerms && e.getItem() != null && cfg.getWrenchIds().contains(e.getItem().getTypeId())) {
            e.setCancelled(true);
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
