/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.save
 * Created: 2013/05/05 22:55:35
 */
package net.tsuttsu305.tunderebackup.save;

import java.util.List;

import net.tsuttsu305.tunderebackup.TundereBackup;
import net.tsuttsu305.tunderebackup.backup.Backup;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Save (Save.java)
 * @author tsuttsu305
 */
public class AutoSave extends BukkitRunnable{
    private TundereBackup plugin;
    public AutoSave(TundereBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (Backup.isBackupNow)return;
        
        List<World> worlds = plugin.getServer().getWorlds();
        
        for (World world : worlds) {
            world.save();
        }
        
        plugin.getServer().savePlayers();
        TundereBackup.getLog().info("AutoSave Completed");
        plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AutoSave] 自動セーブ完了");
    }
}
