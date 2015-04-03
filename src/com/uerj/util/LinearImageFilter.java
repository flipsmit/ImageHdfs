package com.uerj.util;

/**
 * LinearImageFilter - Class provides a method to get the subcoefficients regarding to the borderhandling.
 * The method is needed if borderhandling is set to PARTIAL, because you will not need the coefficients,
 * which are currently outside the picture when applying the filter.
 * There is also a method for computing the sum of coefficients
 * @author Felipe Batista
 *
 */
public abstract class LinearImageFilter extends ImageFilter {

        private WImage coefficients;
        
        // constructors
        public LinearImageFilter() {
                super();
        }
        public LinearImageFilter(BorderHandling borderHandling) {
                super(borderHandling);
        }
        public LinearImageFilter(BorderHandling borderHandling, int width) {
                super(borderHandling, width);
        }
        public LinearImageFilter(BorderHandling borderHandling, WImage coefficients) {
                super(borderHandling, coefficients.getWidth());
                this.coefficients = coefficients;
        }

        public abstract WImage applyFilter(WImage img);

        protected WImage getCoefficients() {
                return coefficients;
        }

        protected void setCoefficients(WImage coefficients) {
                this.coefficients = coefficients;
                this.width = coefficients.getWidth();
        }
        protected long getSumOfCoefficients(WImage coefficients) {
                long coefsum = 0;
                for (int val : coefficients.getData()) {
                        coefsum += val;
                }
                return coefsum;
        }
        protected WImage getSubCoeffs(int x, int y, WImage img) {
                
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
                        return this.coefficients;
                }
                else {
                        // borderhandling PADDING (if pixel outside --> pixel = 0)
                        // coefficients stay the same
                        if (border == BorderHandling.PADDING) {
                                return this.coefficients;
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
                                                int val = coefficients.getPixel(xhelp+left, yhelp+up);
                                                nim.setPixel(xhelp, yhelp, val);
                                        }
                                }
                                return nim;
                        }
                        else return null;
                }
        }

}
