/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.backup
 * Created: 2013/05/05 23:22:01
 */
package net.tsuttsu305.tunderebackup.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsuttsu305.tunderebackup.TundereBackup;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Backup (Backup.java)
 * @author tsuttsu305
 */
public class Backup {
    private TundereBackup plugin;
    
    public static boolean isBackupNow = false;
    
    public Backup(TundereBackup plugin) {
        this.plugin = plugin;
    }
    
    public void startBackup(){
        if (isBackupNow){
            return;
        }
        BackupTask bt = new BackupTask(plugin);
        bt.start();
    }
}
class BackupTask extends Thread{
    private TundereBackup plugin;
    
    public BackupTask(TundereBackup plugin) {
        this.plugin = plugin;
        setName("BackupTask");
    }
    
    @Override
    public void run() {
        TundereBackup.getLog().info("Start backup");
        plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[AutoBackup] Backupを開始します。ラグに注意してください");
        Backup.isBackupNow = true;
        
        ConsoleCommandSender con = plugin.getServer().getConsoleSender();
        
        
        List<World> targetWorlds = saveAllWorlds(con);
        
        //Backup保存先Directory
        File backupDir = new File(TundereBackup.getConf().getBackupDir());
        if (!backupDir.exists()){
            backupDir.mkdir();
        }
        
        //TempDirectory
        File temp = new File(backupDir.getAbsolutePath(), "temp");
        if (!temp.exists()){
            temp.mkdir();
        }else{
            try {
                FileUtils.deleteDirectory(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            temp.mkdir();
        }
        
        //最大数制限のやつ
        if (backupDir.list().length > TundereBackup.getConf().getMaxBackupFiles()){
            chkOldBackupFiles(backupDir);
        }
        
        
        //WorldCopy
        copyWorlds(targetWorlds, temp);
        
        if (TundereBackup.getConf().isBackupPlugins()){
            backupPlugins(temp);
        }
        
        //Result File Name
        String result = getResultFileName();
        File re = new File(backupDir, result);
        temp.renameTo(re);
        
        //Zip
        if (TundereBackup.getConf().isZipBackup()){
            try {
                //Zip.createZip(temp, new File(backupDir, result + ".zip"));
                ZipUtil.archiveZip(re, new File(backupDir, re.getName() + ".zip"));
                FileUtils.deleteDirectory(re);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //Worldをもとに戻す
        for (World world : plugin.getServer().getWorlds()) {
            world.setAutoSave(true);
        }
        plugin.getServer().dispatchCommand(con, "save-on");
        
        plugin.getServer().broadcastMessage(ChatColor.GREEN + "[AutoBackup] Backupが完了しました。");
        TundereBackup.getLog().info("Backup completed");
        
        Backup.isBackupNow = false;
    }

    private void copyWorlds(List<World> targetWorlds, File temp) {
        for (World world : targetWorlds) {
            File worldFile = new File(System.getProperty("user.dir"), world.getName());
            try {
                FileUtils.copyDirectory(worldFile, new File(temp.getAbsolutePath(), world.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getResultFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat(TundereBackup.getConf().getNameFormat());
        Date date = new Date();
        String result = sdf.format(date);
        return result;
    }

    private List<World> saveAllWorlds(ConsoleCommandSender con) {
        plugin.getServer().dispatchCommand(con, "save-all");
        plugin.getServer().dispatchCommand(con, "save-off");
        
        //除外World Listを取得
        List<String> ecWorlds = TundereBackup.getConf().getExcludeWorld();
        
        //除外World以外セーブし自動セーブを解除 targetに追加
        List<World> targetWorlds = new ArrayList<>();
        
        for (World world : plugin.getServer().getWorlds()) {
            if (ecWorlds.contains(world.getName()) == false){
                targetWorlds.add(world);
            }
            world.setAutoSave(false);
        }
        plugin.getServer().savePlayers();
        return targetWorlds;
    }

    private void chkOldBackupFiles(File backupDir) {
        long d = Long.MAX_VALUE;
        File del = null;
        for(File f:backupDir.listFiles()){
            if (f.getName().equalsIgnoreCase("temp"))continue;
            
            if (f.lastModified() < d){
                d = f.lastModified();
                del = f;
            }
        }
        if (del == null){
            
        }else if (del.isDirectory()){
            try {
                FileUtils.deleteDirectory(del);
                TundereBackup.getLog().info("Delete old Backup: " + del.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            del.delete();
            TundereBackup.getLog().info("Delete old Backup: " + del.getAbsolutePath());
        }
    }

    private void backupPlugins(File temp) {
        List<String> ecPlugin = TundereBackup.getConf().getExcludePlugin();
        
        File plugins = new File(System.getProperty("user.dir"), "plugins");
        fname: for (String fName : plugins.list()) {
            String f = fName.replace(".jar", "");
            for (String string : ecPlugin) {
                if (string.toLowerCase().equalsIgnoreCase(f)){
                    continue fname;
                }
            }
            
            File file = new File(plugins.getAbsolutePath(), fName);
            if (file.isDirectory()){
                try {
                    FileUtils.copyDirectory(file, new File(temp.getAbsolutePath() + "/plugins", fName));
                } catch (IOException e) {
                    TundereBackup.getLog().info(file.getAbsoluteFile() + " is not backup! " + e.getMessage());
                }
            }else {
                try {
                    FileUtils.copyFile(file, new File(temp.getAbsolutePath() + "/plugins", fName));
                } catch (IOException e) {
                    TundereBackup.getLog().info(file.getAbsoluteFile() + " is not backup! " + e.getMessage());
                }
            }
        }
    }
}
