package sk.yweb.gnox.bukkit.resmodprotect;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{

	protected List<Integer> MEProtectedIds;
	protected List<Integer> protectedChestIds;
	protected List<Integer> wrenchIds;
	protected List<Integer> machineIds;
	protected List<Integer> decorIds;

	protected FileConfiguration config;
	private File cfgFile;
	private final File datafolder;

	public Config(File datafolder)
	{

		this.datafolder = datafolder;
		this.cfgFile = new File(datafolder, "config.yml");
		config = YamlConfiguration.loadConfiguration(cfgFile);
		this.load();
	}

	private void load()
	{
		MEProtectedIds = config.getIntegerList("Flags.me");
		protectedChestIds = config.getIntegerList("Flags.modchests");
		wrenchIds = config.getIntegerList("Flags.wrench");
		machineIds = config.getIntegerList("Flags.machine");
		decorIds = config.getIntegerList("Flags.decor");
	}

	// <editor-fold defaultstate="collapsed" desc="not used save function">
	// private void save() {
	// try {
	// config.save(cfgFile);
	// Logger.getLogger("Minecraft").log(Level.CONFIG,
	// "[ResModProtect] File successfuly saved!");
	// } catch (IOException ex) {
	// Logger.getLogger(Config.class.getName()).log(Level.WARNING,
	// "[ResModProtect] This file could not be saved.", ex);
	// }
	// }
	// </editor-fold>

	public void reload()
	{
		if(cfgFile == null)
		{
			cfgFile = new File(datafolder, "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(cfgFile);
		load();
	}

	public List<Integer> getAEProtectedIds()
	{
		return MEProtectedIds;
	}

	public List<Integer> getProtectedChestIds()
	{
		return protectedChestIds;
	}

	public List<Integer> getWrenchIds()
	{
		return wrenchIds;
	}

	public List<Integer> getMachineIds()
	{
		return machineIds;
	}

	public List<Integer> getDecorIds()
	{
		return decorIds;
	}

}
