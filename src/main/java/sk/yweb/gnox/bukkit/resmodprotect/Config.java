package sk.yweb.gnox.bukkit.resmodprotect;

import net.t00thpick1.residence.api.flags.Flag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static List<Flag> flags;

    public static final int FLAG_WRENCH = 1;
    public static final int FLAG_MACHINE = 2;
    public static final int FLAG_DECOR = 3;
    public static final int FLAG_ENTITY = 4;
    public static final int FLAG_ME = 5;
    public static final int FLAG_MODCHESTS = 6;
    public static final int FLAG_FAKEPLAYER = 7;

    static {
        flags = new ArrayList<>();
        Flag parent = new Flag("mods", Flag.FlagType.AREA_ONLY, null, "Activates every mod flag");
        flags.add(parent);
        flags.add(new Flag("wrench", Flag.FlagType.AREA_ONLY, parent, "Wrench flag", true));
        flags.add(new Flag("machine", Flag.FlagType.AREA_ONLY, parent, "machine flag", true));
        flags.add(new Flag("decor", Flag.FlagType.AREA_ONLY, parent, "decoration blocks flag", true));
        flags.add(new Flag("entity", Flag.FlagType.AREA_ONLY, parent, "Entity flag", true));
        flags.add(new Flag("me", Flag.FlagType.AREA_ONLY, parent, "ME flag", true));
        flags.add(new Flag("modchests", Flag.FlagType.AREA_ONLY, parent, "Mod chests flag", true));
        flags.add(new Flag("fakeplayer", Flag.FlagType.AREA_ONLY, parent, "Fake player flag", true));
    }

    protected List<Integer> MEProtectedIds;
    protected List<Integer> protectedChestIds;
    protected List<Integer> wrenchIds;
    protected List<Integer> machineIds;
    protected List<Integer> decorIds;

    public static Flag getFlag(int flag) {
        return flags.get(flag);
    }

    protected FileConfiguration config;
    private File cfgFile;
    private final File datafolder;

    public Config(File datafolder) {

        this.datafolder = datafolder;
        this.cfgFile = new File(datafolder, "config.yml");
        config = YamlConfiguration.loadConfiguration(cfgFile);
        this.load();
    }

    private void load() {
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

    public void reload() {
        if (cfgFile == null) {
            cfgFile = new File(datafolder, "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(cfgFile);
        load();
    }

    public List<Integer> getAEProtectedIds() {
        return MEProtectedIds;
    }

    public List<Integer> getProtectedChestIds() {
        return protectedChestIds;
    }

    public List<Integer> getWrenchIds() {
        return wrenchIds;
    }

    public List<Integer> getMachineIds() {
        return machineIds;
    }

    public List<Integer> getDecorIds() {
        return decorIds;
    }

}
