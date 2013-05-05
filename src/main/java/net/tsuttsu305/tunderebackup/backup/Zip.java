/**
 * TundereBackup - Package: net.tsuttsu305.tunderebackup.backup
 * Created: 2013/05/05 23:22:12
 */
package net.tsuttsu305.tunderebackup.backup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * Zip (Zip.java)
 * @author tsuttsu305
 */
public class Zip {
    public static void createZip(File srcDir, File destZip) throws Exception{
        File[] files = { srcDir };
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZip));
        zos.setEncoding("MS932");
        try {
            encode(zos, files);
        } finally {
            zos.close();
        }
    }
    
    static byte[] buf = new byte[1024];

    static void encode(ZipOutputStream zos, File[] files) throws Exception {
        for (File f : files) {
            if (f.isDirectory()) {
                encode(zos, f.listFiles());
            } else {
                ZipEntry ze = new ZipEntry(f.getPath().replace('\\', '/'));
                zos.putNextEntry(ze);
                InputStream is = new BufferedInputStream(new FileInputStream(f));
                for (;;) {
                    int len = is.read(buf);
                    if (len < 0) break;
                    zos.write(buf, 0, len);
                }
                is.close();
            }
        }
    }
}
