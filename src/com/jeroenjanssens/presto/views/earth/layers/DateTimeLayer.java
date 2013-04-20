/*
Copyright (C) 2001, 2007 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
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

package com.jeroenjanssens.presto.views.earth.layers;

import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.OrderedRenderable;
import gov.nasa.worldwind.util.Logging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;


import com.jeroenjanssens.presto.views.earth.EarthView;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Renders a scalebar graphic in a screen corner.
 * 
 * @author Patrick Murris
 * @version $Id: DateTimeLayer.java 5178 2008-04-25 21:51:20Z patrickmurris $
 */


/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class DateTimeLayer extends RenderableLayer {

	// Positionning constants
    public final static String NORTHWEST = "gov.nasa.worldwind.ScalebarLayer.NorthWest";
    public final static String SOUTHWEST = "gov.nasa.worldwind.ScalebarLayer.SouthWest";
    public final static String NORTHEAST = "gov.nasa.worldwind.ScalebarLayer.NorthEast";
    public final static String SOUTHEAST = "gov.nasa.worldwind.ScalebarLayer.SouthEast";
	// Stretching behavior constants
	public final static String RESIZE_STRETCH = "gov.nasa.worldwind.ScalebarLayer.Stretch";
	public final static String RESIZE_SHRINK_ONLY = "gov.nasa.worldwind.ScalebarLayer.ShrinkOnly";
	public final static String RESIZE_KEEP_FIXED_SIZE = "gov.nasa.worldwind.ScalebarLayer.FixedSize";
	// Units constants
	public final static String UNIT_METRIC = "gov.nasa.worldwind.ScalebarLayer.Metric";
	public final static String UNIT_IMPERIAL = "gov.nasa.worldwind.ScalebarLayer.Imperial";

	private Dimension size = new Dimension(150, 10);
	private Color color = Color.white;
	private int borderWidth = 20;
	private String position = SOUTHWEST;
	private String resizeBehavior = RESIZE_SHRINK_ONLY;
	private String unit = UNIT_METRIC;
	private Font defaultFont = Font.decode("Arial-PLAIN-14");
	private double toViewportScale = 0.2;

	private TextRenderer textRenderer = null;

	private EarthView earthView;
	
    private OrderedIcon orderedImage = new OrderedIcon();

    private class OrderedIcon implements OrderedRenderable
    {
        public double getDistanceFromEye()
        {
            return 0;
        }

        public void pick(DrawContext dc, Point pickPoint)
        {
        }

        public void render(DrawContext dc)
        {
            DateTimeLayer.this.draw(dc);
        }
    }

    /**
     * Renders a scalebar graphic in a screen corner
     */
	public DateTimeLayer(EarthView earthView) {
		this.earthView = earthView;
		this.setName("Date Time");
		//this.setName(Logging.getMessage("layers.Earth.DateTimeLayer.Name"));
	}

	// Public properties

    /**
     * Get the scalebar graphic Dimension (in pixels)
     * @return the scalebar graphic Dimension
     */
    public Dimension getSize()
    {
		return this.size;
	}

    /**
     * Set the scalebar graphic Dimenion (in pixels)
     * @param size the scalebar graphic Dimension
     */
    public void setSize(Dimension size)
    {
        if (size == null)
        {
            String message = Logging.getMessage("nullValue.DimensionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
		this.size = size;
	}

    /**
     * Get the scalebar color
     * @return  the scalebar Color
     */
    public Color getColor()
    {
		return this.color;
	}

    /**
     * Set the scalbar Color
     * @param color the scalebar Color
     */
    public void setColor(Color color)
    {
        if (color == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
		this.color = color;
	}

	/** Returns the scalebar-to-viewport scale factor.
	 *
	 * @return the scalebar-to-viewport scale factor
	 */
	public double getToViewportScale()
	{
		return toViewportScale;
	}

	/**
	 * Sets the scale factor applied to the viewport size to determine the displayed size of the scalebar. This
	 * scale factor is used only when the layer's resize behavior is {@link #RESIZE_STRETCH} or {@link
	 * #RESIZE_SHRINK_ONLY}. The scalebar's width is adjusted to occupy the proportion of the viewport's width indicated by
	 * this factor. The scalebar's height is adjusted to maintain the scalebar's Dimension aspect ratio.
	 *
	 * @param toViewportScale the scalebar to viewport scale factor
	 */
	public void setToViewportScale(double toViewportScale)
	{
		this.toViewportScale = toViewportScale;
	}

	public String getPosition()
    {
		return this.position;
	}

    /**
     * Sets the relative viewport location to display the scalebar. Can be one of {@link #NORTHEAST} (the default),
     * {@link #NORTHWEST}, {@link #SOUTHEAST}, or {@link #SOUTHWEST}. These indicate the corner of the viewport.
     *
     * @param position the desired scalebar position
     */
	public void setPosition(String position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
		this.position = position;
	}

    /**
     * Returns the layer's resize behavior.
     *
     * @return the layer's resize behavior
     */
	public String getResizeBehavior()
	{
		return resizeBehavior;
	}

    /**
     * Sets the behavior the layer uses to size the scalebar when the viewport size changes, typically when the
     * World Wind window is resized. If the value is {@link #RESIZE_KEEP_FIXED_SIZE}, the scalebar size is kept to the size
     * specified in its Dimension scaled by the layer's current icon scale. If the value is {@link #RESIZE_STRETCH},
     * the scalebar is resized to have a constant size relative to the current viewport size. If the viewport shrinks the
     * scalebar size decreases; if it expands then the scalebar enlarges. If the value is
     * {@link #RESIZE_SHRINK_ONLY} (the default), scalebar sizing behaves as for {@link #RESIZE_STRETCH} but it will
     * not grow larger than the size specified in its Dimension.
     *
     * @param resizeBehavior the desired resize behavior
     */
	public void setResizeBehavior(String resizeBehavior)
	{
		this.resizeBehavior = resizeBehavior;
	}

	public int getBorderWidth()
	{
		return borderWidth;
	}

    /**
     * Sets the scalebar offset from the viewport border.
     *
     * @param borderWidth the number of pixels to offset the scalebar from the borders indicated by {@link
     * #setPosition(String)}.
     */
	public void setBorderWidth(int borderWidth)
	{
		this.borderWidth = borderWidth;
	}

	public String getUnit()
    {
		return this.unit;
	}

    /**
     * Sets the unit the scalebar uses to display distances.
     * Can be one of {@link #UNIT_METRIC} (the default),
     * or {@link #UNIT_IMPERIAL}.
     *
     * @param unit the desired unit
     */
	public void setUnit(String unit)
    {
		this.unit = unit;
	}

    /**
     * Get the scalebar legend Fon
     * @return the scalebar legend Font
     */
    public Font getFont()
    {
		return this.defaultFont;
	}

    /**
     * Set the scalebar legend Fon
     * @param font the scalebar legend Font
     */
    public void setFont(Font font)
    {
        if (font == null)
        {
            String msg = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
		this.defaultFont = font;
	}

    // Rendering
    @Override
    public void doRender(DrawContext dc)
    {
        dc.addOrderedRenderable(this.orderedImage);
        
    }

	// Rendering
	public void draw(DrawContext dc)
	{
		if(earthView.getCurrentScenarioEditor() == null) return;
		if(earthView.getCurrentScenarioEditor().getScenario() == null) return;
		
		
		
		
		GL gl = dc.getGL();

		boolean attribsPushed = false;
		boolean modelviewPushed = false;
		boolean projectionPushed = false;

		try
		{
			gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT
					| GL.GL_COLOR_BUFFER_BIT
					| GL.GL_ENABLE_BIT
					| GL.GL_TEXTURE_BIT
					| GL.GL_TRANSFORM_BIT
					| GL.GL_VIEWPORT_BIT
					| GL.GL_CURRENT_BIT);
			attribsPushed = true;

			gl.glDisable(GL.GL_TEXTURE_2D);		// no textures

			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDisable(GL.GL_DEPTH_TEST);

			double width = this.size.width;
			double height = this.size.height;

			// Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
			// into the GL projection matrix.
			java.awt.Rectangle viewport = dc.getView().getViewport();
			gl.glMatrixMode(javax.media.opengl.GL.GL_PROJECTION);
			gl.glPushMatrix();
			projectionPushed = true;
			gl.glLoadIdentity();
			double maxwh = width > height ? width : height;
			gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			modelviewPushed = true;
			gl.glLoadIdentity();

			// Scale to a width x height space
			// located at the proper position on screen
			double scale = this.computeScale(viewport);
			
			Vec4 locationSW = new Vec4(0d + viewport.getWidth() / 2, viewport.getHeight() - 30, 0);
			
			gl.glTranslated(locationSW.x(), locationSW.y(), locationSW.z());
			gl.glScaled(scale, scale, 1);

			gl.glLoadIdentity();
            gl.glDisable(GL.GL_CULL_FACE);
            drawLabel(earthView.getCurrentScenarioEditor().getTimeLine().getDateTimeLabel(), locationSW);
 
        }
		finally
		{
			if (projectionPushed)
			{
				gl.glMatrixMode(GL.GL_PROJECTION);
				gl.glPopMatrix();
			}
			if (modelviewPushed)
			{
				gl.glMatrixMode(GL.GL_MODELVIEW);
				gl.glPopMatrix();
			}
			if (attribsPushed)
				gl.glPopAttrib();
		}
	}

    // Draw the scale label
	private void drawLabel(String text, Vec4 screenPoint)
    {
		if (this.textRenderer == null) {
			this.textRenderer =  new TextRenderer(this.defaultFont, true, true);
		}

		Rectangle2D nameBound = this.textRenderer.getBounds(text);
		int x = (int) (screenPoint.x() - nameBound.getWidth() / 2d);
		//int x = (int) screenPoint.x();
		int y = (int) screenPoint.y();

		this.textRenderer.begin3DRendering();

        this.textRenderer.setColor(this.getBackgroundColor(this.color));
        this.textRenderer.draw(text, x + 1, y - 1);
		this.textRenderer.setColor(this.color);
		this.textRenderer.draw(text, x, y);

		this.textRenderer.end3DRendering();

	}

	private final float[] compArray = new float[4];    
    // Compute background color for best contrast
    private Color getBackgroundColor(Color color)
    {
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), compArray);
        if (compArray[2] > 0.5)
            return new Color(0, 0, 0, 0.7f);
        else
            return new Color(1, 1, 1, 0.7f);
    }

	private double computeScale(java.awt.Rectangle viewport)
	{
		if (this.resizeBehavior.equals(RESIZE_SHRINK_ONLY))
		{
			return Math.min(1d, (this.toViewportScale) * viewport.width / this.size.width);
		}
		else if (this.resizeBehavior.equals(RESIZE_STRETCH))
		{
			return (this.toViewportScale) * viewport.width / this.size.width;
		}
		else if (this.resizeBehavior.equals(RESIZE_KEEP_FIXED_SIZE))
		{
			return 1d;
		}
		else
		{
			return 1d;
		}
	}

    @Override
	public void dispose()
    {
        if (this.textRenderer != null)
        {
            this.textRenderer.dispose();
            this.textRenderer = null;
        }
    }

	@Override
	public String toString()
	{
		return this.getName();
	}

}	
