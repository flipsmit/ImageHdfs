package com.uerj.util;



/**
 * ImageFilter is an abstract Class to implement an linear or nonlinear filter.
 * The class provides a method to get a subpicture regarding to the filtersize
 * around the position of the picture to be calculated.
 * Borderhandling can be defined.
 * @author Mürzl Harald
 *
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
         * applyFilter applies the filter on the MMTImage img.
         * It returns a new MMTImage.
         * @param img MMTImage
         * @return MMTImage
         */
        public abstract MMTImage applyFilter(MMTImage img);

        /**
         * getPart returns the subpicture of img regarding to the filter width.
         * The pixel specified by x and y is the center of the subpicture.
         * The size depends on the specified filter width and borderhandling.
         * 
         * @param x int, the x coordinate of the pixel to be calculated.
         * @param y int, the y coordinate of the pixel to be calculated.
         * @param img MMTImage, the Image from where to get the subpicture.
         * @return MMTImage the subpicture.
         */
        protected MMTImage getPart(int x, int y, MMTImage img) {
                
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
                                MMTImage nim = new MMTImage(this.width, this.width);
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
                                
                                MMTImage nim = new MMTImage(nwidth, nheight);
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