/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup
 * Created: 2013/05/05 16:21:34
 */
package net.tsuttsu305.tunderebackup;

import java.util.logging.Logger;

import net.tsuttsu305.tunderebackup.backup.AutoBackup;
import net.tsuttsu305.tunderebackup.backup.Backup;
import net.tsuttsu305.tunderebackup.conf.ConfigManager;
import net.tsuttsu305.tunderebackup.save.AutoSave;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TundereBackup (TundereBackup.java)
 * @author tsuttsu305
 */
public class TundereBackup extends JavaPlugin {
    private static Logger logger;
    private static TundereBackup plugin;
    
    private static AutoBackup backup = null;
    private static AutoSave save = null;
    private static ConfigManager conf = null;
    @SuppressWarnings("unused")
    private int saveTask;
    @SuppressWarnings("unused")
    private int backupTask;
    
    @Override
    public void onEnable() {
        TundereBackup.plugin = this;
        TundereBackup.logger = this.getLogger();
        
        //初期化処理
        init();
        
    }
    
    /**
     * 初期化処理
     */
    private void init(){
        //初期化
        backup = null;
        save = null;
        conf = null;
        
        //configロード
        conf = new ConfigManager(this, getFile());
        conf.load();
        
        //AutoSave
        if (conf.isAutoSave()){
            save = new AutoSave(plugin);
            int saveDelay = conf.getSaveInterval() * 20 * 60;//20tick = 1秒
            saveTask = getServer().getScheduler().scheduleSyncRepeatingTask(plugin, save, saveDelay, saveDelay);
        }
        
        //AutoBackup
        if (conf.isAutoBackup()){
            backup = new AutoBackup();
            int backupDelay = conf.getAutoBackupInterval() *20 *60 *60;
            backupTask = getServer().getScheduler().scheduleSyncRepeatingTask(plugin, backup, backupDelay, backupDelay);
        }
    }
    
    @Override
    public void onDisable() {
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("backup")){
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("tunderebackup.backup")) {
                        Backup back = new Backup(plugin);
                        back.startBackup();
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "権限がありません!");
                        return true;
                    }
                } else {
                    Backup back = new Backup(plugin);
                    back.startBackup();
                    return true;
                }
            }else{
                if (args[0].equalsIgnoreCase("debug")){
                    if (!(sender instanceof Player)){
                        for(World world : getServer().getWorlds()){
                            logger.info(world.getName() + ": " + world.isAutoSave());
                        }
                    }
                }else if (args[0].equalsIgnoreCase("reload")){
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (player.hasPermission("tunderebackup.reload")) {
                            conf.load();
                            player.sendMessage(ChatColor.GREEN + "[TundereBackup] Reload Config SUCCESS");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "権限がありません!");
                            return true;
                        }
                    } else {
                        conf.load();
                        return true;
                    }
                }
            }
            return true;
        }
        
        return false;
    }
    
    /*Getter*/
    public static Logger getLog() {
        return logger;
    }
    public static TundereBackup getInstance() {
        return plugin;
    }
    public static ConfigManager getConf() {
        return conf;
    }
}
