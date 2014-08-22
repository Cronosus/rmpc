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
/*    
    @EventHandler
	public void onResidenceChanged(ResidenceChangedEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();
		ClaimedResidence to = event.getTo();
		
		if(to == null)
			return;
		
		ResidencePermissions perms = to.getPermissions();
		boolean canMove = perms.playerHas(playerName, "move", true);
		
		if(canMove)
			return;
		
		ResidenceCounter counter = ResidenceCounter.get(player, to, true);
		
		counter.increment();
		
		if(counter.hasOverflown())
		{
			World world = player.getWorld();
			Location spawn = world.getSpawnLocation();
			
			player.teleport(spawn);
		}
		
		player.sendMessage(ChatColor.RED + "Sem nepatrís!");
	}
	*/

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player == null)
            return;

        String playerName = player.getName();
        Long last = lastUpdate.get(playerName);
        long now = System.currentTimeMillis();

        if (last != null && now - last < Residence.getConfigManager().getMinMoveUpdateInterval())
            return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.distance(to) <= 0)
            return;

        ClaimedResidence res = Residence.getResidenceManager().getByLoc(to);
        if (res == null)
            return;

        lastUpdate.put(playerName, now);

        if (!res.getPermissions().playerHas(playerName, "move", true) && !Residence.isResAdminOn(player) && !player.hasPermission("residence.admin.move")) {
            ResidenceCounter counter = ResidenceCounter.get(player, res, true);

            counter.increment();


            if (counter.hasOverflown()) {
                if (player.isInsideVehicle()) {
                    Entity vehicle = player.getVehicle();
                    vehicle.eject();
                    vehicle.teleport(entryLocation.get(playerName));
                }
                player.teleport(res.getOutsideFreeLoc(entryLocation.get(playerName)));
                System.out.println("Hrác " + playerName + " byl teleportován na spawn z důvodu opakovaného vstupování do residence bez práv.");
            }

            player.sendMessage(ChatColor.RED + "Tady nemás, co delat.");
        }
    }

    @EventHandler
    public void onResidenceEnterEvent(ResidenceEnterEvent e) {
        entryLocation.put(e.getPlayer().getDisplayName(), e.getPlayer().getLocation());
    }
}
