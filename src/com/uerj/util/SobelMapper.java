package com.uerj.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

public class SobelMapper extends Mapper<LongWritable, BufferedImage, LongWritable, BufferedImage>{

	public void map(LongWritable key, BufferedImage value, Context context) 
			throws IOException, InterruptedException {
		
		
		System.out.println("Map Phase iniciada");
		
		BorderHandling bh = BorderHandling.PARTIAL;

		MMTImage img = FileImageReader.read("lena.jpeg",value);

		SobelOperator so = new SobelOperator(bh);
		MMTImage gradient = so.applyFilter(img);

		// Limita a figura de 0 a 255
		gradient.setData(gradient.getLimited_0_255());
		// Salva a nova figura/ parte da figura
		FileImageWriter.write(gradient, "GradientImage_2.png",context,key);
		bh = null;
		img =null;
		so = null;
		gradient=null;
		
		}
}
