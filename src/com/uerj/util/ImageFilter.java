package com.uerj.util;



/**
 * ImageFilter - Abstract Class to implement an linear filter.
 * The class have a method to get a subpicture regarding to the filtersize
 * @author Felipe Batista
 */


public abstract class ImageFilter {

        protected int width;
        protected BorderHandling border;
        
        // Constructors
        public ImageFilter() {}
        public ImageFilter(BorderHandling borderHandling) {
                this.border = borderHandling;
        }
        public ImageFilter(BorderHandling borderHandling, int width) {
                this.border = borderHandling;
                this.width = width;
        }

        /**
         * applyFilter applies the filter on the WImage img.
         * It returns a new WImage.
         * @param img WImage
         * @return WImage
         */
        public abstract WImage applyFilter(WImage img);

        /**
         * getPart returns the subpicture of img regarding to the filter width.
         * The pixel specified by x and y is the center of the subpicture.
         * The size depends on the specified filter width and borderhandling.
         * 
         * @param x int, the x coordinate of the pixel to be calculated.
         * @param y int, the y coordinate of the pixel to be calculated.
         * @param img WImage, the Image from where to get the subpicture.
         * @return WImage the subpicture.
         */
        protected WImage getPart(int x, int y, WImage img) {
                
                int imw = img.getWidth();       //width of image
                int imh = img.getHeight();      //height of image
                int wh = (this.width-1)/2;      //nr of pixels around act pixel
                // Startpoint
                int xs = x-wh;
                int ys = y-wh;
                // Endpoint
                int xe = x+wh;
                int ye = y+wh;
                
                // inside the picture (borderhandling not nescessary)
                if ((xs >= 0) && (ys >= 0) && (xe < imw) && (ye < imh)) {
                        return img.getSubPicture(xs, ys, this.width, this.width);
                }
                else {
                        // borderhandling PADDING (if pixel outside --> pixel = 0)
                        if (border == BorderHandling.PADDING) {
                                WImage nim = new WImage(this.width, this.width);
                                for (int xhelp=xs; xhelp<=xe; xhelp++) {
                                        for (int yhelp=ys; yhelp<=ye; yhelp++) {
                                                int val = 0;
                                                if ((xhelp >= 0) && (yhelp >= 0) && (xhelp < imw) && (yhelp < imh)) {
                                                        val = img.getPixel(xhelp, yhelp);
                                                }
                                                nim.setPixel(xhelp-xs, yhelp-ys, val);
                                        }
                                }
                                return nim;
                        }
                        // borderhandling PARTIAL (filtersize reduced on border)
                        else if ((border == BorderHandling.PARTIAL) || (border == BorderHandling.LIMITING)){ 
                                // how many pixels outside at each side
                                int left = (xs<0) ? Math.abs(xs) : 0;
                                int right = (xe>=imw) ? xe-imw+1 : 0;
                                int up = (ys<0) ? Math.abs(ys) : 0;
                                int down = (ye>=imh) ? ye-imh+1 : 0;
                                int nwidth = this.width-left-right;
                                int nheight = this.width-up-down;
                                
                                WImage nim = new WImage(nwidth, nheight);
                                for (int xhelp=0; xhelp<nwidth; xhelp++) {
                                        for (int yhelp=0; yhelp<nheight; yhelp++) {
                                                int val = img.getPixel(xhelp+left+xs, yhelp+up+ys);
                                                nim.setPixel(xhelp, yhelp, val);
                                        }
                                }
                                return nim;
                        }
                        else return null;
                }
        }
}