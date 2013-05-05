/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.task
 * Created: 2013/05/05 22:45:07
 */
package net.tsuttsu305.tunderebackup.backup;

import net.tsuttsu305.tunderebackup.TundereBackup;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Backup (Backup.java)
 * @author tsuttsu305
 */
public class AutoBackup extends BukkitRunnable{

    @Override
    public void run() {
        Backup back = new Backup(TundereBackup.getInstance());
        back.startBackup();
    }
    
}
