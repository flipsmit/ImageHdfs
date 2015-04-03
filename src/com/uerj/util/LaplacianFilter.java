package com.uerj.util;

/**
 * LaplacianFilter - Class to apply an Laplacian Filter in a image.
 * The sum of the coefficients has to be 0!
 * after applying, the absolut values are used for the resulting image.
 * the values are also limited from 0 to 255.
 * @author Felipe Batista
 */
public class LaplacianFilter extends LinearImageFilter {

        public LaplacianFilter(BorderHandling borderHandling, WImage coefficients) {
                super(borderHandling, coefficients);

                // check coefficients
                if (this.getSumOfCoefficients(coefficients) != 0) {
                        throw new IllegalArgumentException("The sum of the laplacian filter coefficients has to be 0.");
                }
        }

        /**
         * applies the laplacian filter.
         */
        @Override
        public WImage applyFilter(WImage img) {
                WImage nim = new WImage(img.getWidth(), img.getHeight());
                
                for (int x=0; x<img.getWidth(); x++) {
                        for (int y=0; y<img.getHeight(); y++) {
                                WImage cf = this.getSubCoeffs(x, y, img);     //coefficients
                                WImage sim = this.getPart(x, y, img);         //subimage
                                
                                // calculate new pixel
                                int nval=0;
                                int pixels = cf.getHeight()*cf.getWidth();
                                for (int i=0; i<pixels; i++) {
                                        nval += cf.getPixel(i) * sim.getPixel(i);
                                }
                                
                                nim.setPixel(x, y, nval);
                        }
                }
                if (this.border == BorderHandling.LIMITING) {
                        int wh = (this.width - 1) / 2;
                        int nx = img.getWidth() - 2 * wh;
                        int ny = img.getHeight() - 2 * wh;
                        nim = nim.getSubPicture(wh, wh, nx, ny);
                }

                return nim;
        }

}