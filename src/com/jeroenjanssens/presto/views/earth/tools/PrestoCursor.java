/**
 * Copyright 2009 Tilburg University. All rights reserved.
 * 
 * This file is part of Presto.
 *
 * Presto is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Presto is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Presto.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jeroenjanssens.presto.views.earth.tools;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import com.jeroenjanssens.presto.Activator;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class PrestoCursor extends ArrayList<Cursor> {

	private static final long serialVersionUID = -1473421836452662509L;
	
	public static final int CURSOR_DEFAULT = 0;
	public static final int CURSOR_NAVIGATION_UP = 1;
	public static final int CURSOR_NAVIGATION_DOWN = 2;
	public static final int CURSOR_HOVER_TRACK = 3;
	public static final int CURSOR_HOVER_WAYPOINT = 4;
	
	public PrestoCursor() {
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		this.add(toolkit.createCustomCursor(convertToAWT(Activator.getImageDescriptor("icons/cursor_default.gif").createImage(Activator.getDefault().getWorkbench().getDisplay())), new Point(0,0), "Default"));
		this.add(toolkit.createCustomCursor(convertToAWT(Activator.getImageDescriptor("icons/cursor_navigation_up.gif").createImage(Activator.getDefault().getWorkbench().getDisplay())), new Point(0,0), "Default"));
		this.add(toolkit.createCustomCursor(convertToAWT(Activator.getImageDescriptor("icons/cursor_navigation_down.gif").createImage(Activator.getDefault().getWorkbench().getDisplay())), new Point(0,0), "Default"));
		this.add(toolkit.createCustomCursor(convertToAWT(Activator.getImageDescriptor("icons/cursor_hover_track.gif").createImage(Activator.getDefault().getWorkbench().getDisplay())), new Point(0,0), "Default"));
		this.add(toolkit.createCustomCursor(convertToAWT(Activator.getImageDescriptor("icons/cursor_hover_waypoint.gif").createImage(Activator.getDefault().getWorkbench().getDisplay())), new Point(0,0), "Default"));
	}
	
	private BufferedImage convertToAWT(Image swtImage) {
		
		ImageData data = swtImage.getImageData();
		
	    ColorModel colorModel = null;
	    PaletteData palette = data.palette;
	    if (palette.isDirect) {
	      colorModel = new DirectColorModel(data.depth, palette.redMask,
	          palette.greenMask, palette.blueMask);
	      BufferedImage bufferedImage = new BufferedImage(colorModel,
	          colorModel.createCompatibleWritableRaster(data.width,
	              data.height), false, null);
	      WritableRaster raster = bufferedImage.getRaster();
	      int[] pixelArray = new int[3];
	      for (int y = 0; y < data.height; y++) {
	        for (int x = 0; x < data.width; x++) {
	          int pixel = data.getPixel(x, y);
	          RGB rgb = palette.getRGB(pixel);
	          pixelArray[0] = rgb.red;
	          pixelArray[1] = rgb.green;
	          pixelArray[2] = rgb.blue;
	          raster.setPixels(x, y, 1, 1, pixelArray);
	        }
	      }
	      return bufferedImage;
	    } else {
	      RGB[] rgbs = palette.getRGBs();
	      byte[] red = new byte[rgbs.length];
	      byte[] green = new byte[rgbs.length];
	      byte[] blue = new byte[rgbs.length];
	      for (int i = 0; i < rgbs.length; i++) {
	        RGB rgb = rgbs[i];
	        red[i] = (byte) rgb.red;
	        green[i] = (byte) rgb.green;
	        blue[i] = (byte) rgb.blue;
	      }
	      if (data.transparentPixel != -1) {
	        colorModel = new IndexColorModel(data.depth, rgbs.length, red,
	            green, blue, data.transparentPixel);
	      } else {
	        colorModel = new IndexColorModel(data.depth, rgbs.length, red,
	            green, blue);
	      }
	      BufferedImage bufferedImage = new BufferedImage(colorModel,
	          colorModel.createCompatibleWritableRaster(data.width,
	              data.height), false, null);
	      WritableRaster raster = bufferedImage.getRaster();
	      int[] pixelArray = new int[1];
	      for (int y = 0; y < data.height; y++) {
	        for (int x = 0; x < data.width; x++) {
	          int pixel = data.getPixel(x, y);
	          pixelArray[0] = pixel;
	          raster.setPixel(x, y, pixelArray);
	        }
	      }
	      
	      swtImage.dispose();
	      return bufferedImage;
	    }
	  }
	
}
