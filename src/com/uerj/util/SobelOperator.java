package com.uerj.util;


public class SobelOperator extends ImageFilter {

        public SobelOperator(BorderHandling borderHandling) {
                super(borderHandling);
                this.width = 3;
        }


        @Override
        public MMTImage applyFilter(MMTImage img) {
                MMTImage nim = new MMTImage(img.getWidth(), img.getHeight());
                MMTImage xcoefs = new MMTImage(3, 3);
                MMTImage ycoefs = new MMTImage(3, 3);
                
                BorderHandling bordh = (this.border == BorderHandling.LIMITING) ? BorderHandling.PARTIAL : this.border;
                
                xcoefs.setData(new int[] {-1, 0, 1, -2, 0, 2, -1, 0, 1});
                ycoefs.setData(new int[] {-1, -2, -1, 0, 0, 0, 1, 2, 1});
                LaplacianFilter lfx = new LaplacianFilter(bordh, xcoefs);
                LaplacianFilter lfy = new LaplacianFilter(bordh, ycoefs);
                MMTImage xim = lfx.applyFilter(img);
                MMTImage yim = lfy.applyFilter(img);
                
                for (int x=0; x<img.getWidth(); x++) {
                        for (int y=0; y<img.getHeight(); y++) {
                                // Calcula Novo Pixel
                                double val = Math.sqrt(Math.pow(xim.getPixel(x, y), 2) + Math.pow(yim.getPixel(x, y), 2));
                                nim.setPixel(x, y, (int)val);
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
