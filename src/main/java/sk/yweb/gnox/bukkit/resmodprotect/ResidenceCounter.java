package sk.yweb.gnox.bukkit.resmodprotect;

import net.t00thpick1.residence.api.areas.ResidenceArea;
import org.bukkit.entity.Player;

import java.util.HashSet;

//import org.apache.commons.lang3.Validate;

public class ResidenceCounter
{
	public static final int COUNT_LIMIT = 10;
	public static final HashSet<ResidenceCounter> VALUES = new HashSet<ResidenceCounter>();
	private final Player player;
	private final ResidenceArea residence;
	private int count;
	
	public ResidenceCounter(Player player, ResidenceArea residence)
	{
		//Validate.notNull(player);
		//Validate.notNull(residence);
		
		this.player = player;
		this.residence = residence;
	}
	
	public static ResidenceCounter get(Player player, ResidenceArea residence, boolean create)
	{
		for(ResidenceCounter counter : VALUES)
			if(counter.matches(player, residence))
				return counter;
		
		if(!create)
			return null;
		
		ResidenceCounter counter = new ResidenceCounter(player, residence);
		
		VALUES.add(counter);
		
		return counter;
	}
	
	public boolean matches(Player player, ResidenceArea residence)
	{
		return this.player.equals(player) && this.residence.equals(residence);
	}
	
	public int increment()
	{
		return ++count;
	}
	
	public boolean hasOverflown()
	{
		return count >= COUNT_LIMIT;
	}

	public ResidenceArea getResidence()
	{
		return residence;
	}

	public Player getPlayer()
	{
		return player;
	}
}
