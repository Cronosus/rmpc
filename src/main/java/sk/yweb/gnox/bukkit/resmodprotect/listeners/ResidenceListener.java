package sk.yweb.gnox.bukkit.resmodprotect.listeners;

import net.t00thpick1.residence.api.ResidenceAPI;
import net.t00thpick1.residence.api.areas.ResidenceArea;
import net.t00thpick1.residence.api.flags.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import sk.yweb.gnox.bukkit.resmodprotect.ResidenceCounter;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class ResidenceListener implements Listener {
    protected Map<String, Long> lastUpdate = new HashMap<String, Long>();
    protected Map<String, Location> entryLocation = new HashMap<String, Location>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player == null || player.isOp())
            return;

        Long last = lastUpdate.get(player.toString());
        long now = System.currentTimeMillis();

        if (last != null && now - last < 50)
            return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.distance(to) <= 0)
            return;

        ResidenceArea res = ResidenceAPI.getResidenceManager().getByLocation(to);
        if (res == null) {
            entryLocation.put(player.toString(), player.getLocation());
            return;
        }

        lastUpdate.put(player.toString(), now);

        if (!ResidenceAPI.getPermissionsAreaByLocation(event.getTo()).allowAction(player.getName(),
                new Flag("move", Flag.FlagType.ANY, null, ""))
                && !ResidenceAPI.getResidenceManager().getByLocation(event.getTo()).getOwner().equals(player.getName())
                && !player.hasPermission("residence.admin.move")) {
            ResidenceCounter counter = ResidenceCounter.get(player, res, true);

            counter.increment();

            if (counter.hasOverflown()) {
                if (!entryLocation.containsKey(player.toString())) {
                    entryLocation.put(player.toString(), getServer().getWorld("world").getSpawnLocation());
                }
                if (player.isInsideVehicle()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.eject();
                    vehicle.teleport(res.getOutsideFreeLoc(entryLocation.get(player.toString())));
                }
                player.teleport(entryLocation.get(player.toString()));
                System.out.println("Hrac " + player.getName() + " byl teleportovan mimo residenci z duvodu opakovaneho vstupovani do residence " + res.getName() + " bez prav.");
            }
        }
    }
}

