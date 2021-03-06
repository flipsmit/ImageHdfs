package com.uerj.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * methods to read an image file
 * @author Felipe Batista
 *
 */
public class FileImageReader {

        /**
         * Open and stores image data in Image
         * @param fname String
         * @return WImage
         * @throws IOException 
         */
        public static WImage read(String fname) throws IOException {
                File f = new File(fname);
                BufferedImage bi = ImageIO.read(f);
                Raster raster = bi.getData();
                String iname = fname.substring(0, fname.lastIndexOf('.'));
                int width = raster.getWidth();
                int height = raster.getHeight();
                
                WImage img = new WImage(width, height);
                img.setName(iname);
                img.setData(raster.getPixels(0, 0, width, height, img.getData()));
                f=null;
                bi=null;
                raster=null;
                iname=null;
                return img;
        }
        /**
         * Open and stores image data in Image
         * @param fname String
         * @return WImage
         * @throws IOException 
         */
        public static WImage read(String iname,BufferedImage bi) throws IOException {
        		BufferedImage oi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        		for (int x=0; x < bi.getWidth(); x++) {
                    for (int y=0; y < bi.getHeight(); y++) {
                        int pixelData = bi.getRGB(x,y);
                        oi.setRGB(x,y,pixelData);
                    }
                }
                Raster raster = oi.getData();
                int width = raster.getWidth();
                int height = raster.getHeight();
                
                WImage img = new WImage(width, height);
                img.setName(iname);
                int[] data=raster.getPixels(0, 0, width, height, img.getData());
                img.setData(data);
                oi=null;
                raster=null;
                iname=null;
                data=null;
                return img;
        }
}