package com.uerj.image;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner; /*This class is responsible for running map reduce job*/

import com.uerj.util.ImgRecordReader;
import com.uerj.util.InputFormatImg;
import com.uerj.util.SobelMapper;

public class SobelFilter extends Configured implements Tool {
	public int run(String[] args) throws Exception {
		long chrono = System.currentTimeMillis();
		Configuration conf = new Configuration();
		// conf.addResource("/usr/local/hadoop/conf/core-site.xml");
		// conf.addResource("/usr/local/hadoop/conf/hdfs-site.xml");
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: SobelFilter <in> <out>");
			System.exit(2);
		}
		FileSystem dfs = FileSystem.get(conf);
		Path dir = new Path(otherArgs[0]);
		FileStatus[] files = dfs.listStatus(dir);
		conf.setInt("overlapPixel", 64);
		int overlapPixel = ImgRecordReader.overlapPixel;
		System.out.println(overlapPixel);
		Path fpath = null;
		for (FileStatus file : files) {
			if (file.isDir())
				continue;
			fpath = file.getPath();

			System.out.println(fpath);
		}

		Path outdir = new Path(otherArgs[1]);
		if (dfs.exists(outdir))
			dfs.delete(outdir, true);



		Path workdir = dfs.getWorkingDirectory();
		BufferedImage img = null;


		// Instantiate a Hadoop Job
		
		Job job = new Job(conf, "Sobel Edge detection");
		job.setJarByClass(SobelFilter.class);
		job.setMapperClass(SobelMapper.class);
		job.setInputFormatClass(InputFormatImg.class);

		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(LongWritable.class);

		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		boolean ret = job.waitForCompletion(true);

		String s = job.getTrackingURL();
		Path tmpdir = new Path(workdir, s.substring(s.indexOf("jobid") + 6));
		
		//Merge all splitted images
		mergeImages(img, tmpdir, dfs, fpath, outdir);

		System.out.println("Tempo (s): "
				+ ((System.currentTimeMillis() - chrono) / 60));

		s=null;
		otherArgs = null;
		dfs = null;
		dir = null;
		fpath = null;
		img = null;
		job = null;
		tmpdir = null;
		return ret ? 0 : 1;
	}

	public void mergeImages(BufferedImage img, Path tmpdir, FileSystem dfs,
			Path fpath, Path outdir) {

		int i = 0;
		Path iPath = new Path(tmpdir, "" + i);
		int currX = 0, currY = 0;
		int sizePixel = ImgRecordReader.sizePixel;
		int border = 16;

		try {
			FSDataInputStream fs = null;
			MemoryCacheImageInputStream image = new MemoryCacheImageInputStream(
					dfs.open(fpath));
			Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
			ImageReader reader = (ImageReader) readers.next();
			reader.setInput(image);
			int imgwidth = 0, imgheight = 0;
			imgwidth = reader.getWidth(0);
			imgheight = reader.getHeight(0);
			System.out.println(imgwidth);
			System.out.println(imgheight);

			img = new BufferedImage(imgwidth, imgheight,
					BufferedImage.TYPE_INT_RGB);

			if (imgwidth * imgheight <= sizePixel * sizePixel) {
				fs = dfs.open(iPath);
				img = ImageIO.read(fs);
				iPath = null;
			}
			// iPath = null;
			while (iPath != null && dfs.exists(iPath)) {
				int x = currX, y = currY;
				currX += sizePixel;
				if (currX >= imgwidth) {
					currX = 0;
					currY += sizePixel;
				}

				fs = dfs.open(iPath);
				BufferedImage window = ImageIO.read(fs);
				int width = window.getWidth() - border * 2;
				int height = window.getHeight() - border * 2;

				img.setRGB(x + border, y + border, width, height, window
						.getRGB(border, border, width, height, null, 0, width),
						0, width);
				fs.close();
				i++;
				iPath = new Path(tmpdir, "" + i);
			}
			Path newimgpath = new Path(outdir, fpath.getName());

			if (dfs.exists(newimgpath))
				dfs.delete(newimgpath, false);
			dfs.createNewFile(newimgpath);
			FSDataOutputStream ofs = dfs.create(newimgpath);
			ImageIO.write(img, "JPG", ofs);

			ofs.close();
			dfs = null;
			fpath = null;
			img = null;
			tmpdir = null;
			image = null;
			fs = null;
			iPath = null;
			newimgpath = null;

		} catch (Exception e) {
		}
	}

//	public void printSplittedImages(BufferedImage img, Path tmpdir,
//			FileSystem dfs, Path fpath, Path outdir) {
//
//		int i = 0;
//		Path iPath = new Path(tmpdir, "" + i);
//		int currX = 0, currY = 0;
//		int sizePixel = ImgRecordReader.sizePixel;
//		int border = 16;
//
//		try {
//			FSDataInputStream fs = null;
//			MemoryCacheImageInputStream image = new MemoryCacheImageInputStream(
//					dfs.open(fpath));
//			Iterator<ImageReader> readers = ImageIO.getImageReaders(image);
//			ImageReader reader = (ImageReader) readers.next();
//			reader.setInput(image);
//			int imgwidth = 0, imgheight = 0;
//			imgwidth = reader.getWidth(0);
//			imgheight = reader.getHeight(0);
//			System.out.println(imgwidth);
//			System.out.println(imgheight);
//
//			// img = new BufferedImage(imgwidth, imgheight,
//			// BufferedImage.TYPE_INT_RGB);
//
//			while (iPath != null && dfs.exists(iPath)) {
//				int x = currX, y = currY;
//				currX += sizePixel;
//				if (currX >= imgwidth) {
//					currX = 0;
//					currY += sizePixel;
//				}
//
//				fs = dfs.open(iPath);
//				BufferedImage window = ImageIO.read(fs);
//				int width = window.getWidth() - border * 2;
//				int height = window.getHeight() - border * 2;
//
//				Path newimgpath = new Path(outdir, fpath.getName());
//				img.setRGB(x + border, y + border, width, height, window
//						.getRGB(border, border, width, height, null, 0, width),
//						0, width);
//
//				dfs.createNewFile(newimgpath);
//				FSDataOutputStream ofs = dfs.create(newimgpath);
//				ImageIO.write(img, "JPG", ofs);
//
//				ofs.close();
//
//				fs.close();
//
//				i++;
//				iPath = new Path(tmpdir, "" + i);
//			}
//			Path newimgpath = new Path(outdir, fpath.getName());
//
//			if (dfs.exists(newimgpath))
//				dfs.delete(newimgpath, false);
//			dfs.createNewFile(newimgpath);
//			FSDataOutputStream ofs = dfs.create(newimgpath);
//			ImageIO.write(img, "JPG", ofs);
//
//			ofs.close();
//			dfs = null;
//			fpath = null;
//			img = null;
//			tmpdir = null;
//			image = null;
//			fs = null;
//			iPath = null;
//			newimgpath = null;
//
//		} catch (Exception e) {
//		}
//	}

	public static void main(String[] args) throws Exception {
		SobelFilter driver = new SobelFilter();
		int exitCode = ToolRunner.run(driver, args);
		System.exit(exitCode);
	}
}
