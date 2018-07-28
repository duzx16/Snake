/*
This file is part of Volantis Mobility Server.  
 
Volantis Mobility Server is free software: you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation, either version 3 of the License, or 
(at your option) any later version. 
 
Volantis Mobility Server is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License for more details. 
 
You should have received a copy of the GNU General Public License 
along with Volantis Mobility Server.  If not, see <http://www.gnu.org/licenses/>.  
*/
/* ----------------------------------------------------------------------------
 * (c) Volantis Systems Ltd 2005.
 * ----------------------------------------------------------------------------
 */
package imageio;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * SVGImageReader is responsible for reading SVG files and converting them into
 * a BufferedImage. <p> This class provides a reader plugin for ImageIO and is
 * configured in META-INF/services/javax.imageio.spi.ImageReaderSpi.
 */
public class SVGImageReader{


    /**
     * Converts the svg graphic contained in the supplied inputStream into a
     * <code> BufferedImage </code>.
     *
     * @param inputStream stream containing the svg image to be converted.
     * @return a BufferedImage representation of the original svg contained in
     * the input stream.
     */
    public static BufferedImage svgToBufferedImage(ImageInputStream inputStream)
            throws IOException {

        // The input to an ImageReader is an ImageInputStream but 
        // the TranscoderInput constructor expects a standard ouput stream, 
        // which is not compatible with ImageInputStream.  A work around to 
        // this problem is to use a Wrapper class to encapsulate the 
        // ImageInputStream in a class that extends InputStream. 
        ImageInputStreamAdaptor imageInputStreamAdaptor =
                new ImageInputStreamAdaptor(inputStream);

        // create a servlet 
        ImageTranscoder bufferedImgTranscoder =
                new BufferedImageTranscoder();

        TranscoderInput transcoderInput =
                new TranscoderInput(imageInputStreamAdaptor);

        // create the servlet output 
        BufferedImageTranscoderOutput output =
                new BufferedImageTranscoderOutput();

        // perform the transcoding 
        try {
            bufferedImgTranscoder.transcode(transcoderInput, output);
        } catch (TranscoderException e) {
            String imagePath = transcoderInput.getURI();
            throw new IOException(e.getLocalizedMessage());
        }
        catch (org.w3c.dom.DOMException e){
        }
        return output.getBufferedImage();
    }
}


 
/*
 =========================================================================== 
 Change History 
 =========================================================================== 
 $Log$ 
 
 16-Mar-05  311/5   rgreenall   VBM:2005012701 Error message passed to logger in SVGImageReader is now referenced from Messages.properties. 
 
 16-Mar-05  311/3   rgreenall   VBM:2005012701 Calling writeImage in concrete implementations of AbstractImageWriter no longer results in the image being converted twice. 
 
 21-Feb-05  311/1   rgreenall   VBM:2005012701 Resolved conflicts 
 
 =========================================================================== 
*/