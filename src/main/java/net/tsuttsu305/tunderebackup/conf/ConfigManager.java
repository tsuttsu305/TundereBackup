/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.conf
 * Created: 2013/05/05 17:09:22
 */
package net.tsuttsu305.tunderebackup.conf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.tsuttsu305.tunderebackup.TundereBackup;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * ConfigManager (ConfigManager.java)
 * @author tsuttsu305
 */
public class ConfigManager {
    private TundereBackup plugin = null;
    
    private static FileConfiguration conf = null;
    private File pluginDir;
    private File jarFile;
    private File confFile;
    private final int ConfVer = 1;
    
    public ConfigManager(TundereBackup plugin, File jarFile) {
        this.plugin = plugin;
        this.pluginDir = plugin.getDataFolder();
        this.jarFile = jarFile;
    }
    
    public void load(){
        if (!pluginDir.exists()){
            pluginDir.mkdir();
        }
        
        confFile = new File(pluginDir, "config.yml");
        if (!confFile.exists()){
            FileUtil.copyFileFromJar(jarFile, new File(pluginDir, "config.yml"), "config.yml", false);
        }
        
        conf = YamlConfiguration.loadConfiguration(confFile);
        
        chkVer();
    }
    
    private void chkVer(){
        int v = conf.getInt("ConfVer");
        
        if (v != ConfVer){
            TundereBackup.getLog().info("config.yml VerError. Please delete old config.yml");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
    
    public FileConfiguration getConfig(){
        return conf;
    }
    
    public void save(){
        try {
            conf.save(confFile);
        } catch (IOException e) {
            TundereBackup.getLog().warning("Can not save config.yml!");
        }
    }
    
    /*Getter*/
    public boolean isAutoSave(){
        return conf.getBoolean("AutoSave", true);
    }
    public boolean isAutoBackup(){
        return conf.getBoolean("AutoBackup", true);
    }
    public int getSaveInterval(){
        return conf.getInt("SaveInterval", 30);
    }
    public int getAutoBackupInterval(){
        return conf.getInt("AutoBackupInterval", 3);
    }
    public int getMaxBackupFiles(){
        return conf.getInt("MaxBackupFiles", 100);
    }
    public List<String> getExcludeWorld(){
        return conf.getStringList("ExcludeWorld");
    }
    public boolean isBackupPlugins(){
        return conf.getBoolean("BackupPlugins", true);
    }
    public List<String> getExcludePlugin(){
        return conf.getStringList("ExcludePlugin");
    }
    public boolean isZipBackup(){
        return conf.getBoolean("ZipBackup", true);
    }
    public String getNameFormat(){
        return conf.getString("NameFormat", "yy-MM-dd-HH-mm");
    }
    public String getBackupDir(){
        return conf.getString("BackupDir");
    }
}
