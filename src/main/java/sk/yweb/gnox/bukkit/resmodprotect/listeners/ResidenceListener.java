package sk.yweb.gnox.bukkit.resmodprotect.listeners;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceEnterEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import sk.yweb.gnox.bukkit.resmodprotect.ResidenceCounter;

import java.util.HashMap;
import java.util.Map;

public class ResidenceListener implements Listener {
    protected Map<String, Long> lastUpdate = new HashMap<String, Long>();
    protected Map<String, Location> entryLocation = new HashMap<String, Location>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player == null || player.isOp())
            return;

        Long last = lastUpdate.get(player.toString());
        long now = System.currentTimeMillis();

        if (last != null && now - last < Residence.getConfigManager().getMinMoveUpdateInterval())
            return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.distance(to) <= 0)
            return;

        ClaimedResidence res = Residence.getResidenceManager().getByLoc(to);
        if (res == null){
            entryLocation.put(player.toString(), player.getLocation());
            return;
        }

        lastUpdate.put(player.toString(), now);

        if (!res.getPermissions().playerHas(player.toString(), "move", true) && !Residence.isResAdminOn(player) && !player.hasPermission("residence.admin.move")) {
            ResidenceCounter counter = ResidenceCounter.get(player, res, true);

            counter.increment();

            if (counter.hasOverflown()) {
                if (player.isInsideVehicle()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.eject();
                    vehicle.teleport(res.getOutsideFreeLoc(entryLocation.get(player.toString())));
                }

                player.teleport(entryLocation.get(player.toString()));
                System.out.println("Hrac " + player.toString() + " byl teleportovan mimo residenci z duvodu opakovaneho vstupovani do residence bez prav.");
            }
            player.sendMessage(ChatColor.RED + "Tady nem�s, co delat.");
        }
    }
}

