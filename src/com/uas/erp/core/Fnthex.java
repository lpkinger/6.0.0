package com.uas.erp.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Fnthex {  
    
    public static BufferedImage source = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);  
    public static Graphics2D gs = source.createGraphics();  
      
    //  public static String getFontZpl(String content, int x, int y, int size, String fontName) {  
    //      return String.format("^FO%d,%d^A1N,%d,%d^FD%s^FS", x, y, size, size, content);  
    //  }  
      
    public static String getFontHexWithWidth(String content, int x, int y, int width,  
            int maxHeight, String fontName) {  
        if (content == null || "".equals(content))  
            return "";  
        Font f = null;  
        width = (width + 7) / 8 * 8;  
        int size = width / content.length();  
        int retryFlag = 1;  
        if (size > maxHeight) {  
            size = maxHeight;  
            if ("宋体".equals(fontName)) {  
                f = new Font("simsun", Font.PLAIN, size);  
            } else if ("黑体".equals(fontName)) {  
                f = new Font("simhei", Font.BOLD, size);  
            } else {  
                f = new Font("simsun", Font.PLAIN, size);  
            }  
        } else {  
            while (true) {  
                if ("宋体".equals(fontName)) {  
                    f = new Font("simsun", Font.PLAIN, size);  
                } else if ("黑体".equals(fontName)) {  
                    f = new Font("simhei", Font.BOLD, size);  
                } else {  
                    f = new Font("simsun", Font.PLAIN, size);  
                }  
                gs.setFont(f);  
                FontMetrics fontMetrics = gs.getFontMetrics();  
                Rectangle2D stringBounds = fontMetrics.getStringBounds(content, gs);  
                int nw = (int) stringBounds.getWidth();  
                  
                if (nw > width) {  
                    size--;  
                    if (retryFlag == 1) {  
                        break;  
                    }  
                    retryFlag = 0;  
                      
                } else {  
                    if (size >= maxHeight)  
                        break;  
                    size++;  
                    retryFlag = 1;  
                }  
            }  
        }  
        int height = size;  
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
        Graphics2D g2 = image.createGraphics();  
        g2.setFont(f);  
        g2.setColor(Color.BLACK);  
        g2.drawString(content, 1, (int) (height * 0.88));  
          
        g2.dispose();  
          
        StringBuilder zpl = new StringBuilder("^FO").append(x).append(",").append(y)  
                .append(getImage(image)).append("^FS");  
          
        return zpl.toString();  
          
    }  
      
    public static String getFontHex(String content, int x, int y, int size, String fontName) {  
        if (content == null || "".equals(content))  
            return "";  
        Font f = null;  
        if ("宋体".equals(fontName)) {  
            f = new Font("simsun", Font.PLAIN, size);  
        } else if ("黑体".equals(fontName)) {  
            f = new Font("simhei", Font.BOLD, size);  
        } else if("Times New Roman".equals(fontName)){  
        	f = new Font("Times New Roman", Font.PLAIN, size);  
        }else{
            f = new Font("simsun", Font.PLAIN, size);  
        }  
        gs.setFont(f);  
        FontMetrics fontMetrics = gs.getFontMetrics();  
        Rectangle2D stringBounds = fontMetrics.getStringBounds(content, gs);  
        int width = (int) stringBounds.getWidth();  
        int height = size;  
        width = (width + 7) / 8 * 8;  
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
        Graphics2D g2 = image.createGraphics();  
        g2.setFont(f);  
        g2.setColor(Color.BLACK);  
        g2.drawString(content, 1, (int) (height * 0.88));  
          
        g2.dispose();  
          
        StringBuilder zpl = new StringBuilder("^FO").append(x).append(",").append(y)  
                .append(getImage(image)).append("^FS");  
          
        return zpl.toString();  
          
    }  
      
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();  
      
    public static String printImage(BufferedImage image, int x, int y) {  
        if (image.getWidth() % 8 != 0) {  
            BufferedImage i = new BufferedImage(((image.getWidth() + 7) / 8) * 8,  
                    image.getHeight(), BufferedImage.TYPE_INT_ARGB);  
            Graphics2D g2 = i.createGraphics();  
            g2.drawImage(image, null, 0, 0);  
            g2.dispose();  
            image = i;  
        }  
        StringBuilder zpl = new StringBuilder("^FO").append(x).append(",").append(y)  
                .append(getImage(image)).append("^FS");  
        return zpl.toString();  
    }  
      
    private static String getImage(BufferedImage i) {  
        int w = i.getWidth();  
        int h = i.getHeight();  
        boolean black[] = getBlackPixels(i, w, h);  
        int hex[] = getHexValues(black);  
          
        String data = ints2Hex(hex);  
          
        int bytes = data.length() / 2;  
        int perRow = bytes / h;  
          
        return "^GFA," + bytes + "," + bytes + "," + perRow + "," + data;  
          
    }  
      
  
    private static String flipRows(String hex, int height) {  
        String flipped = "";  
        int width = hex.length() / height;  
          
        for (int i = 0; i < height; i++) {  
            flipped += new StringBuilder(hex.substring(i * width, (i + 1) * width)).reverse()  
                    .toString();  
        }  
        return flipped;  
    }  
      
    /** 
     * Returns an array of ones or zeros. boolean is used instead of int for memory considerations. 
     *  
     * @param o 
     * @return 
     */  
    private static boolean[] getBlackPixels(BufferedImage bi, int w, int h) {  
        int[] rgbPixels = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();  
        int i = 0;  
        boolean[] pixels = new boolean[rgbPixels.length];  
        for (int rgbpixel : rgbPixels) {  
            pixels[i++] = isBlack(rgbpixel);  
        }  
          
        return pixels;  
    }  
      
    private static boolean isBlack(int rgbPixel) {  
        int a = (rgbPixel & 0xFF000000) >>> 24;  
        if (a < 127) {  
            return false; // assume pixels that are less opaque than the luma threshold should be considered to be white  
        }  
        int r = (rgbPixel & 0xFF0000) >>> 16;  
        int g = (rgbPixel & 0xFF00) >>> 8;  
        int b = rgbPixel & 0xFF;  
        int luma = ((r * 299) + (g * 587) + (b * 114)) / 1000; //luma formula  
        return luma < 127;  
    }  
      
    private static int[] getHexValues(boolean[] black) {  
        int[] hex = new int[(int) (black.length / 8)];  
        // Convert every eight zero's to a full byte, in decimal  
        for (int i = 0; i < hex.length; i++) {  
            for (int k = 0; k < 8; k++) {  
                hex[i] += (black[8 * i + k] ? 1 : 0) << 7 - k;  
            }  
        }  
        return hex;  
    }  
      
    public static String getHexString(byte[] b) throws Exception {  
        String result = "";  
        for (int i = 0; i < b.length; i++) {  
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);  
        }  
        return result;  
    }  
      
    private static String ints2Hex(int[] ints) {  
        char[] hexChars = new char[ints.length * 2];  
        for (int i = 0; i < ints.length; ++i) {  
            hexChars[i * 2] = HEX[(ints[i] & 0xF0) >> 4];  
            hexChars[i * 2 + 1] = HEX[ints[i] & 0x0F];  
        }  
        return new String(hexChars);  
    }  
      
//    public static void main(String[] args) {  
//        System.out.println(getFontHex("你好", 10, 50, 80, "黑体"));  
//    }  
}  
