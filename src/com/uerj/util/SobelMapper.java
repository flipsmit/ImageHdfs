package com.uerj.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * SobelMapper - Hadoop Class used to apply Sobel Filter.
 * @author Felipe Batista
 *
 */
public class SobelMapper extends Mapper<LongWritable, BufferedImage, LongWritable, BufferedImage>{

	public void map(LongWritable key, BufferedImage value, Context context) 
			throws IOException, InterruptedException {
		
		
		System.out.println("Map Phase started");
		
		BorderHandling bh = BorderHandling.PARTIAL;

		WImage img = FileImageReader.read("lena.jpeg",value);

		SobelOperator so = new SobelOperator(bh);
		WImage gradient = so.applyFilter(img);

		// Limit the picture from 0 to 255
		gradient.setData(gradient.getLimited_0_255());
		// Save/Split a new picture / slice picture.
		FileImageWriter.write(gradient, "GradientImage_2.png",context,key);
		bh = null;
		img =null;
		so = null;
		gradient=null;
		
		}
}
