/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.backup
 * Created: 2013/05/06 9:00:43
 */
package net.tsuttsu305.tunderebackup.backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * ZipUtil (ZipUtil.java)
 * @author tsuttsu305
 */
public class ZipUtil {
    private ZipUtil() {}
    
    /**
     * ディレクトリの中をzipに圧縮する。<br>
     * ディレクトリ内のFileがzipを開くとrootにある
     * @param srcDir 対象ディレクトリ
     * @param dest 出力先zipファイル
     */
    public static void archiveZip(File srcDir, File dest){
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(dest);
            zos.setEncoding("MS932");
            
            addEntry(zos, srcDir.listFiles(), srcDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    
                }
            }
        }
    }
    
    private static void addEntry(ZipOutputStream zos, File[] files, String rootPath){
        for (File file : files) {
            //ディレクトリの場合再帰処理する
            if (file.isDirectory()){
                addEntry(zos, file.listFiles(), rootPath);
            }else{
                BufferedInputStream input = null;
                try {
                    input = new BufferedInputStream(new FileInputStream(file));
                    
                    //Entryの名称
                    String entryName = file.getAbsolutePath().replace(rootPath, "").substring(1);
                    
                    zos.putNextEntry(new ZipEntry(entryName));
                    
                    //書き込み
                    byte[] buf = new byte[1024];
                    for (;;) {
                        int len = input.read(buf);
                        if (len < 0) break;
                        zos.write(buf, 0, len);
                    }
                    
                    zos.closeEntry();
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if (input != null){
                        try {
                            input.close();
                        } catch (IOException e) {
                            
                        }
                    }
                }
                
            }
        }
    }

}
