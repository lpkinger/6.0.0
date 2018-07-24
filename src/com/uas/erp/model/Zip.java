package com.uas.erp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 压缩文件夹
 * 
 * @example
 * 		Zip zip = new Zip();
		try {
			zip.zip("D:\\Program Files\\Doc");
		} catch (Exception e) {
			e.printStackTrace();
		}
 */
public class Zip {
    /**
     * @param inputFileName 输入一个文件夹
     * @return 压缩文件的path
     */
     public String zip(String inputFileName){
         File f = new File(inputFileName);
         try{
        	 String path = f.getAbsolutePath() + ".zip";
             zip(path, f);
             return path;
         } catch (Exception e){
        	 System.err.println("压缩失败");
        	 return null;
         }
     }
     private void zip(String zipFileName, File inputFile) throws Exception {
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
         out.setEncoding("gbk");
         zip(out, inputFile, "");
         out.close();
     }
     private void zip(ZipOutputStream out, File f, String base) throws Exception {
         if (f.isDirectory()) {
            File[] fl = f.listFiles();
            ZipEntry entry = new ZipEntry(base + File.separator);
            entry.setUnixMode(755);
            out.putNextEntry(entry);
            base = base.length() == 0 ? "" : base + File.separator;
            for (int i = 0; i < fl.length; i++) {
            zip(out, fl[i], base + fl[i].getName());
          }
         }else {
        	ZipEntry entry = new ZipEntry(base);
            entry.setUnixMode(644);
            out.putNextEntry(entry);
            FileInputStream in = new FileInputStream(f);
            int b;
            while ( (b = in.read()) != -1) {
            	out.write(b);
            }
            in.close();
        }
     }
}
