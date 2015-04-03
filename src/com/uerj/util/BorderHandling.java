package com.uerj.util;

/**
 * lists the methods for handling the borders of the images, when filters are applied.
 * 
 * LIMITING...the border of the image is cut off, so the new image is smaller.
 * PARTIAL....only the part of the image which overlaps with the coefficients is processed.
 * PADDING....the pixels outside the image are set to 0 before the filter is applied.
 * 
 * CAUTION: when LIMITING is set as BorderHandling, then the filterclass has to cut off the border.
 *          internally LIMITING is treated as PARTIAL.
 * @author Mürzl Harald
 *
 */
public enum BorderHandling {

        LIMITING,
        PARTIAL,
        PADDING
}

