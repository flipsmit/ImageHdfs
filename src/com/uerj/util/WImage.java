package com.uerj.util;

/**
 * WImage - class used to work with grayscale image.
 * @author Felipe Batista
 *
 */
public class WImage {

        /** image data 
         */
        private int[] data;

        /** image width*/
        private int w;

        /** image height*/
        private int h;

        /** image name*/
        private String name;

        /**
         * creates an image and allocates memory
         * @param width
         *             image width 
         * @param height 
         *             image height 
         */
        public WImage(int width, int height) {
                this.data = new int[width*height];
                this.h = height;
                this.w = width;
        }

        /**
         * get pixel at specified position, 2D
         * 
         * @param x
         * @param y
         * @return pixel at (x,y)
         */
        public int getPixel(int x, int y) {
                return this.data[x+this.w*y];
        }
        
        /**
         * get pixel at specified position, 1D
         * 
         * @param i
         * @return pixel at (i)
         */
        public int getPixel(int i) {
                return this.data[i];
        }

        /** image height*/
        public int getHeight() {
                return this.h;
        }

        /** image width*/
        public int getWidth() {
                return this.w;
        }

        /** image name*/
        public String getName() {
                return this.name;
        }
                
        /**
         * set pixel at position (x,y) to gray value val, 2D
         * 
         * @param x
         * @param y
         * @param val
         */
        public void setPixel(int x, int y, int val) {
                this.data[x+this.w*y] = val;
        }

        /**
         * set pixel at position (i) to gray value val, 1D
         * 
         * @param i
         * @param val
         */
        public void setPixel(int i, int val) {
                this.data[i] = val;
        }

        /**
         * 
         * @return int[], the array with the image data
         */
        public int[] getData() {
                return data;
        }

        /**
         * change imagedata array
         * caution: width and height must also be changed.
         * @param data int[]
         */
        public void setData(int[] data) {
                this.data = data;
        }

        /**
         * sets the name of the image
         * @param name String
         */
        public void setName(String name) {
                this.name = name;
        }

        /**
         * returns the Subimage from xstart, ystart to width and height
         * @param xstart int startpoint x
         * @param ystart int startpoint y
         * @param width int width of subpicture
         * @param height int height of subpicture
         * @return WImage the subpicture
         */
        public WImage getSubPicture(int xstart, int ystart, int width, int height) {

                if ((xstart < 0) || (ystart < 0) || (width < 0) || (height < 0)) {
                        throw new IllegalArgumentException("Negative values are not allowed!");
                }
                if (((xstart + width) > this.w) || (ystart + height) > this.h) {
                        throw new IllegalArgumentException("Area of subpicture is outside the original image.");
                }

                WImage im = new WImage(width, height);
                
                for (int x=0; x<width; x++) {
                        for (int y=0; y<height; y++) {
                                im.setPixel(x, y, this.getPixel(x+xstart, y+ystart));
                        }
                }
                return im;
        }

        public int[] getAbs() {
                int[] absdata = new int[this.data.length];
                for(int i=0; i<this.data.length; i++) {
                        absdata[i] = Math.abs(this.data[i]);
                }
                return absdata;
        }
        
        public int[] getLimited_0_255() {
                int[] limdata = new int[this.data.length];
                for(int i=0; i<this.data.length; i++) {
                        limdata[i] = (this.data[i] < 0) ? 0 : this.data[i];
                        limdata[i] = (this.data[i] > 255) ? 255 : this.data[i];
                }
                return limdata;
        }
}
