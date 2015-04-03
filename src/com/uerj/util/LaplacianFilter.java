package com.uerj.util;

/**
 * applies an laplacian filter.
 * therefore the sum of the coefficients has to be 0!
 * after applying the filter, the absolut values are used for the resulting image.
 * the values are also limited from 0 to 255.
 * @author muetze
 *
 */
public class LaplacianFilter extends LinearImageFilter {

        public LaplacianFilter(BorderHandling borderHandling, MMTImage coefficients) {
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
        public MMTImage applyFilter(MMTImage img) {
                MMTImage nim = new MMTImage(img.getWidth(), img.getHeight());
                
                for (int x=0; x<img.getWidth(); x++) {
                        for (int y=0; y<img.getHeight(); y++) {
                                MMTImage cf = this.getSubCoeffs(x, y, img);     //coefficients
                                MMTImage sim = this.getPart(x, y, img);         //subimage
                                
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