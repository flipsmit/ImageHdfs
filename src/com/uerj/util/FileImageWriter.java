package com.uerj.util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * methods for writing an image file
 * @author Felipe Batista
 *
 */
public class FileImageWriter {

	/**
	 * writes the WImage to the file specified by fname the type of the
	 * imagefile is automatically set by the file extension (*.jpg, *.png,...).
	 * see doc of javax.imageio.ImageIO for further information.
	 * 
	 * @param img
	 *            WImage image to save
	 * @param fname
	 *            String name of the file
	 */
	public static void write(WImage img, String fname) {
		BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster r = bi.getRaster();

		r.setSamples(0, 0, img.getWidth(), img.getHeight(), 0, img.getData());

		File f = new File(fname);

		try {
			ImageIO.write(bi, fname.substring(fname.lastIndexOf('.') + 1), f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * writes the WImage to the file specified by fname the type of the
	 * imagefile is automatically set by the file extension (*.jpg, *.png,...).
	 * see doc of javax.imageio.ImageIO for further information.
	 * 
	 * @param img
	 *            WImage image to save
	 * @param fname
	 *            String name of the file
	 */
	public static void write(WImage img, String fname, Context context,
			LongWritable key) {
		BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster r = bi.getRaster();

		r.setSamples(0, 0, img.getWidth(), img.getHeight(), 0, img.getData());
		try {
			FileSystem dfs = FileSystem.get(context.getConfiguration());
			Path newimgpath = new Path(context.getWorkingDirectory(), context
					.getJobID().toString()
					+ "/" + key.get());
			dfs.createNewFile(newimgpath);
			FSDataOutputStream ofs = dfs.create(newimgpath);

			//ImageIO.write(bi, "jpg", ofs);
			ImageIO.write(bi, fname.substring(fname.lastIndexOf('.') + 1), ofs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
