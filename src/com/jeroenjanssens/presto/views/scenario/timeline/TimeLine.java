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

package com.jeroenjanssens.presto.views.scenario.timeline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.TreeModel;
import com.jeroenjanssens.presto.model.Waypoint;
import com.jeroenjanssens.presto.sailing.TrackStatus;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;
import com.jeroenjanssens.presto.views.scenario.tree.TrackTreeViewer;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TimeLine extends Canvas {

	public static final long MS_MILLISECOND = 1; 
	public static final long MS_SECOND = 1000 * MS_MILLISECOND; 
	public static final long MS_MINUTE = 60 * MS_SECOND;
	public static final long MS_HOUR = 60 * MS_MINUTE; 
	public static final long MS_DAY = 24 * MS_HOUR; 

	public static final int UNIT_MILLISECOND = 0;
	public static final int UNIT_SECOND = 1;
	public static final int UNIT_MINUTE = 2;
	public static final int UNIT_HOUR = 3;
	public static final int UNIT_DAY = 4;
	public static final int UNIT_MONTH = 5;
	public static final int UNIT_YEAR = 6;

	private Locale locale;
	private DateFormat sdf;

	private boolean isLocked = false;
	private boolean isDragging = false;
	private int start_x;
	private int dif_x;
	private int current_x;	

	private long cursorTime;
	private int cursorOffset;

	private int canvasWidth;
	private int canvasHeight;
	private int itemHeight;
	private int headerHeight;
	private int propertyBoxWidth;

	private double zoomFactor = 1; //Number of milliseconds per pixel
	private int unitLevel = 0; 

	private Color colorWhite;
	private Color colorBlack;
	private Color colorGrid;
	private Color colorGridLight;
	private Color colorGridDark;
	private Color colorCursor;
	private Color colorArrow;


	private ArrayList<Long> gridTimes;
	private ArrayList<Integer> gridOffsets;

	private Display display;
	private TimeLine thisTimeLine;

	private Image headerImage;
	private TimeLinePickingSupport timeLinePickingSupport = new TimeLinePickingSupport();
	//private HashMap<Rectangle, Object> drawnObjects = new HashMap<Rectangle, Object>();

	private static ArrayList<Image> images = new ArrayList<Image>();

	private TrackTreeViewer trackTreeViewer; 
	private ScenarioEditor scenarioEditor;

	public static final int IMAGE_DOT = 0;	
	public static final int IMAGE_EDIT = 1;
	public static final int IMAGE_NO_EDIT = 2;
	public static final int IMAGE_LOCKED = 3;
	public static final int IMAGE_LOCKED_PARENT = 4;
	public static final int IMAGE_INVISIBLE = 5;
	public static final int IMAGE_INVISIBLE_PARENT = 6;
	public static final int IMAGE_FOLDER = 7;
	public static final int IMAGE_TRACK = 8;

	private ArrayList<TreeModel> treeObjects;

	public Date SelectionDateTime = new Date();

	private Image timeLineImage;
	private GC timeLineGC;

	public TimeLine(final ScenarioEditor scenarioEditor, final Composite parent, int style) {
		super(parent, style);

		this.scenarioEditor = scenarioEditor;
		this.trackTreeViewer = scenarioEditor.getTrackTreeViewer();

		itemHeight = trackTreeViewer.getTreeViewer().getTree().getItemHeight();
		headerHeight = 18;
		propertyBoxWidth = 76;

		treeObjects = new ArrayList<TreeModel>();

		thisTimeLine = this;

		cursorTime = new Date().getTime();
		locale = new Locale("en", "US");
		sdf = new SimpleDateFormat("EEEE, MMMM d yyyy  HH:mm:ss", locale);

		zoomFactor = TimeLine.MS_MINUTE;
		setAppropriateUnitLevel();

		display = parent.getDisplay();
		colorWhite = new Color(display, 255, 255, 255);
		colorBlack = new Color(display, 0, 0, 0);
		colorGrid = new Color(display, 200, 200, 240);
		colorGridLight = new Color(display, 230, 230, 255);
		colorGridDark = new Color(display, 150, 150, 200);
		colorCursor = new Color(display, 230,100,100);
		colorArrow = new Color(display, 50, 200, 0);
		headerImage = Activator.getImageDescriptor("icons/header.png").createImage();

		this.setBackground(colorWhite);
		isLocked = false;


		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				//System.out.println("Disposing header image of time line of " + thisTimeLine.getScenarioEditor().getScenario().getName());
				headerImage.dispose();
				timeLineImage.dispose();
				timeLineGC.dispose();
				colorWhite.dispose();
				colorBlack.dispose();
				colorGrid.dispose();
				colorGridLight.dispose();
				colorGridDark.dispose();
				colorCursor.dispose();
				colorArrow.dispose();
			}
		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {





				if(!isLocked) {
					if(e.x >= propertyBoxWidth) {
						thisTimeLine.setFocus();
						//start drag
						isDragging = true;
						scenarioEditor.getAnimator().startAnimate();
						start_x = e.x;
					} else {

						TreeModel item = trackTreeViewer.getItem(1, e.y-(headerHeight+1));
						if(item != null) {
							//System.out.println("clicked on: " + item.getName());
							if(e.x < 20) {


								if(e.button == 3) {
									if(item instanceof Track) {
										thisTimeLine.getScenarioEditor().getEarthView().getMenuManager().getTrackMenu().showMenu((Track) item, thisTimeLine.toDisplay(e.x, e.y));
									}
								}

								trackTreeViewer.getTreeViewer().setSelection(new StructuredSelection(item), true);

								/*
								Event ev = new Event();
								ev.type = SWT.MouseDown;
								ev.display = e.display;
								ev.x = e.x;
								ev.y = e.y - headerHeight;
								ev.button = e.button;
								ev.data = e.data;
								trackTreeViewer.getTreeViewer().getTree().notifyListeners(SWT.MouseDown, ev);
								ev.type = SWT.Selection;
								trackTreeViewer.getTreeViewer().getTree().notifyListeners(SWT.Selection, ev);
								 */
							} else if(e.x < 39) {
								item.setVisible(!item.isVisible());
								//System.out.println("toggle visible");
								scenarioEditor.setChanged(true);
								scenarioEditor.getEarthView().getWWD().redraw();
								scenarioEditor.getEarthView().getPropertiesView().getTrackComposite().restore();
							} else if(e.x < 57) {
								item.setLocked(!item.isLocked());
								//System.out.println("toggle locked");
								scenarioEditor.setChanged(true);
								scenarioEditor.getEarthView().getWWD().redraw();
								scenarioEditor.getEarthView().getPropertiesView().getTrackComposite().restore();
								if(item instanceof Track) {
									//((Track)item).shiftTime(-3600000L);
									//((Track)item).update();
									//scenarioEditor.getEarthView().getWWD().redraw();
								}

							} else {
								if(item instanceof Track) {
									System.out.println("change color");

									ColorDialog cd = new ColorDialog(parent.getShell());
									cd.setText("Select Track Color");
									cd.setRGB(item.getColor().getRGB());
									RGB newColor = cd.open();
									if (newColor == null) {
										return;
									}
									item.setColor(new Color(display, newColor));
									item.updatePropertyImage();
									scenarioEditor.getEarthView().getWWD().redraw();
									scenarioEditor.getEarthView().getPropertiesView().getTrackComposite().restore();
								}
							}	
						} else {
							//System.out.println("set null");
							trackTreeViewer.getTreeViewer().setSelection(null);
						}

						doRedraw();
					}
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				//stop drag;
				isDragging = false;
				scenarioEditor.getAnimator().stopAnimate();
			}

		});

		this.addMouseMoveListener(new MouseMoveListener() {

			public void mouseMove(MouseEvent e) {

				//Object o = timeLinePickingSupport.getTopPickedObject(new Point(e.x,e.y));
				
				if(isDragging && !isLocked) {
					current_x = e.x;
					dif_x = current_x - start_x;
					cursorTime -= (dif_x * zoomFactor);
					thisTimeLine.doRedraw();
					start_x = current_x;
				}
			}
		});


		this.addMouseWheelListener(new MouseWheelListener() {

			public void mouseScrolled(MouseEvent e) {

				if ((e.stateMask & SWT.CTRL) != 0) {

					cursorTime -= (e.count * 10 * zoomFactor);
					thisTimeLine.doRedraw();
					thisTimeLine.getScenarioEditor().getEarthView().getWWD().redraw();
				} else {
					
					if(e.count > 0) {
						decreaseZoomFactor();
					} else {
						increaseZoomFactor();
					}
				}
			}
		});


		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent event) {
				timeLinePickingSupport.start();
				canvasWidth = thisTimeLine.getBounds().width; 
				canvasHeight = thisTimeLine.getBounds().height;

				timeLinePickingSupport.addPickableObject(new Rectangle(0,0, canvasWidth, canvasHeight), this);

				itemHeight = trackTreeViewer.getTreeViewer().getTree().getItemHeight();

				if(timeLineImage != null) {
					timeLineImage.dispose();
					timeLineGC.dispose();
				}

				timeLineImage = new Image(event.display, thisTimeLine.getBounds());
				timeLineGC = new GC(timeLineImage);

				timeLineGC.setBackground(colorWhite);
				timeLineGC.fillRectangle(timeLineImage.getBounds());

				cursorOffset = (int) Math.floor(canvasWidth * 0.5);

				//Light grid
				if((unitLevel > 1) && (getUnitPixelWidth(cursorTime, unitLevel-1) > 4)) {
					timeLineGC.setForeground(colorGridLight);
					calculateGrid(unitLevel-1);
					for(Integer os : gridOffsets) {
						timeLineGC.drawLine(os.intValue(), 19, os.intValue(), canvasHeight);
					}
				}

				timeLineGC.setForeground(colorGrid);
				calculateGrid(unitLevel);

				//Normal grid
				for(Integer os : gridOffsets) {
					timeLineGC.drawLine(os.intValue(), 19, os.intValue(), canvasHeight);
				}

				//LabelBar
				timeLineGC.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				timeLineGC.fillRectangle(0, 0, canvasWidth, 18);

				//Labels
				timeLineGC.setForeground(colorBlack);
				for(int t = 0; t < gridTimes.size()-1;t++) {
					long time = gridTimes.get(t).longValue();
					int offset = gridOffsets.get(t).intValue();
					double unitWidth = getUnitPixelWidth(time, unitLevel);
					int labelOffset = offset + (int)Math.round(unitWidth / 2); 
					String label = getLabel(time, unitLevel);
					timeLineGC.drawText(label, labelOffset - (timeLineGC.textExtent(label).x/2), 3);
				}

				//Dark grid
				if(unitLevel < 6) {
					timeLineGC.setForeground(colorGridDark);
					calculateGrid(unitLevel+1);
					for(Integer os : gridOffsets) {
						timeLineGC.drawLine(os.intValue(), 19, os.intValue(), canvasHeight);
					}
				}

				//Iconrect
				timeLineGC.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				timeLineGC.fillRectangle(0, 0, propertyBoxWidth, 18);
				timeLineGC.setBackground(colorWhite);
				timeLineGC.fillRectangle(0, 19, propertyBoxWidth, canvasHeight);

				//Current Time




				//Shadow of labelbar
				timeLineGC.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				timeLineGC.drawLine(0, headerHeight, canvasWidth, headerHeight);

				List<TreeModel> selectedTreeModels = null;
				ISelection sel = trackTreeViewer.getTreeViewer().getSelection();
				if(!sel.isEmpty()) {
					if(sel instanceof IStructuredSelection) {
						selectedTreeModels = (((IStructuredSelection)sel).toList());
					}
				}

				timeLineGC.setForeground(colorBlack);
				int y = headerHeight;
				for(TreeModel t : treeObjects) {


					int old_wpx = 0;
					if(t instanceof Track) {
						Track track = (Track) t;

						if(track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES)) {
							timeLineGC.setForeground(colorBlack);						
						} else {
							timeLineGC.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
						}




						for(Waypoint w: track.getWaypoints()) {
							int new_wpx = getOffSetOfTime(w.getTime());
							if(new_wpx - old_wpx > 0) {

								if(old_wpx > 0) {
									timeLineGC.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
									timeLineGC.drawLine(old_wpx, y+7, new_wpx, y+7);
									timeLineGC.drawLine(old_wpx, y+10, new_wpx, y+10);
									if(track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES)) timeLineGC.setForeground(colorBlack);
									timeLineGC.drawLine(old_wpx, y+8, new_wpx, y+8);
									timeLineGC.drawLine(old_wpx, y+9, new_wpx, y+9);
									timeLinePickingSupport.addPickableObject(new Rectangle(old_wpx-1, y+2, (new_wpx-old_wpx)+1, 15), track);
								} else if(!w.isFirst()) {
									timeLineGC.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
									timeLineGC.drawLine(0, y+7, new_wpx, y+7);
									timeLineGC.drawLine(0, y+10, new_wpx, y+10);
									if(track.getStatus().equals(TrackStatus.CLEAN_VERTICES_AND_CLEAN_TIMES)) timeLineGC.setForeground(colorBlack);
									timeLineGC.drawLine(0, y+8, new_wpx, y+8);
									timeLineGC.drawLine(0, y+9, new_wpx, y+9);
									timeLinePickingSupport.addPickableObject(new Rectangle(0, y+2, new_wpx+1, 15), track);
								}
							}
							old_wpx = new_wpx + 1; 
							//gcImage.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
							timeLineGC.drawLine(new_wpx, y+4, new_wpx, y+13);
						}

						if(track.getWaypoints().size() > 1) {
							//timeLineGC.setForeground(colorArrow);
							timeLineGC.setBackground(colorArrow);
							if(getOffSetOfTime(track.getLastWaypoint().getTime()) < propertyBoxWidth) {
								//System.out.println("Track " + t.getName() + " is on the left");
								timeLineGC.fillPolygon(new int[] {propertyBoxWidth+10, y+14, propertyBoxWidth+4, y+9, propertyBoxWidth+10, y+4});
							}

							if(getOffSetOfTime(track.getTime()) > canvasWidth) {
								//System.out.println("Track " + t.getName() + " is on the right");
								timeLineGC.fillPolygon(new int[] {canvasWidth-4, y+9, canvasWidth-10, y+4, canvasWidth-10, y+14});
							}
						}
					}

					timeLineGC.drawImage(t.getPropertyImage(), 0 ,y+1);
					if(selectedTreeModels != null) {
						if(selectedTreeModels.contains(t)) {
							if(t.isLocked() || !t.isVisible() || t.isLockedBecauseParentIsLocked() || t.isInvisibleBecauseParentIsInvisible()) {
								timeLineGC.drawImage(TimeLine.getImage(TimeLine.IMAGE_NO_EDIT), 2 ,y+1);
							} else {
								timeLineGC.drawImage(TimeLine.getImage(TimeLine.IMAGE_EDIT), 2 ,y+1);
							}
						}
					}

					y += itemHeight;
				}
				timeLineGC.setForeground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				for(y = headerHeight; y < canvasHeight+3; y+=itemHeight) {
					timeLineGC.drawLine(0, y+itemHeight, canvasWidth, y+itemHeight);
				}

				//Cursor
				timeLineGC.setForeground(colorCursor);
				timeLineGC.setBackground(colorCursor);
				timeLineGC.drawLine(cursorOffset, 19, cursorOffset, canvasHeight);
				timeLineGC.fillPolygon(new int[] {cursorOffset-6, 19, cursorOffset+7, 19, cursorOffset, 26});
				timeLineGC.fillPolygon(new int[] {cursorOffset-6, canvasHeight, cursorOffset+7, canvasHeight, cursorOffset, canvasHeight-7});

				//Extra lijnen verticaal
				timeLineGC.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				timeLineGC.drawLine(0, headerHeight+1, 0, canvasHeight);
				timeLineGC.drawLine(propertyBoxWidth, headerHeight+1, propertyBoxWidth, canvasHeight);

				//Extra lijnen verticaal
				timeLineGC.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
				timeLineGC.drawLine(0, 0, 0, 18);
				timeLineGC.drawLine(76, 0, 76, 18);

				timeLineGC.drawImage(headerImage, 1, 1);

				timeLineGC.setForeground(colorBlack);
				timeLineGC.setBackground(colorWhite);
	
				event.gc.drawImage(timeLineImage, 0, 0);
				timeLineImage.dispose();
				timeLineGC.dispose();
			}
		});
	}


	public ScenarioEditor getScenarioEditor() {
		return scenarioEditor;
	}


	private void setAppropriateUnitLevel() {
		int unit = 7;

		do {
			unit--;
		} while(getUnitPixelWidth(cursorTime, unit) > 500);

		unitLevel = Math.max(unit,1); 
	}


	private long getWholeUnitTime(long time, int unit) {
		GregorianCalendar wholeUnitDate = new GregorianCalendar();
		wholeUnitDate.setTimeInMillis(time);

		switch(unit) {
		case TimeLine.UNIT_YEAR:
			wholeUnitDate.set(Calendar.MONTH, 0);
		case TimeLine.UNIT_MONTH:
			wholeUnitDate.set(Calendar.DAY_OF_MONTH, 1);
		case TimeLine.UNIT_DAY:
			wholeUnitDate.set(Calendar.HOUR_OF_DAY, 0);
		case TimeLine.UNIT_HOUR:
			wholeUnitDate.set(Calendar.MINUTE, 0);
		case TimeLine.UNIT_MINUTE:
			wholeUnitDate.set(Calendar.SECOND, 0);
		case TimeLine.UNIT_SECOND:
			wholeUnitDate.set(Calendar.MILLISECOND, 0);
		}

		return wholeUnitDate.getTimeInMillis();
	}

	private long getOtherWholeUnitTime(long time, int unit, int diff) {
		GregorianCalendar otherWholeUnitDate = new GregorianCalendar();
		otherWholeUnitDate.setTimeInMillis(getWholeUnitTime(time, unit));

		switch(unit) {
		case TimeLine.UNIT_YEAR:
			otherWholeUnitDate.add(Calendar.YEAR, diff);
			break;
		case TimeLine.UNIT_MONTH:
			otherWholeUnitDate.add(Calendar.MONTH, diff);
			break;
		case TimeLine.UNIT_DAY:
			otherWholeUnitDate.add(Calendar.DAY_OF_MONTH, diff);
			break;
		case TimeLine.UNIT_HOUR:
			otherWholeUnitDate.add(Calendar.HOUR, diff);
			break;
		case TimeLine.UNIT_MINUTE:
			otherWholeUnitDate.add(Calendar.MINUTE, diff);
			break;
		case TimeLine.UNIT_SECOND:
			otherWholeUnitDate.add(Calendar.SECOND, diff);
			break;
		}

		return otherWholeUnitDate.getTimeInMillis();
	}


	private void calculateGrid(int unit) {
		gridTimes = new ArrayList<Long>();
		gridOffsets = new ArrayList<Integer>();
		int offset = cursorOffset;

		int diff = 0;
		long time;

		while(offset > -1) {
			diff--;
			time = getOtherWholeUnitTime(cursorTime, unit, diff);
			gridTimes.add(time);
			offset = getOffSetOfTime(time);
			gridOffsets.add(offset);
		}

		diff = -1;

		while(offset < canvasWidth) {
			diff++;
			time = getOtherWholeUnitTime(cursorTime, unit, diff);
			gridTimes.add(time);
			offset = getOffSetOfTime(time);
			gridOffsets.add(offset);
		}

	}

	private String getLabel(long time, int unit) {

		GregorianCalendar unitDate = new GregorianCalendar();
		unitDate.setTimeInMillis(time);

		switch(unit) {
		case TimeLine.UNIT_YEAR:
			return ""+ unitDate.get(Calendar.YEAR);
		case TimeLine.UNIT_MONTH:

			if(zoomFactor < 2.3e7) {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.LONG, locale) + " " + unitDate.get(Calendar.YEAR);
			} else if(zoomFactor < 3.9e7) {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale) + " " + unitDate.get(Calendar.YEAR);
			} else {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale) + " \'" + String.valueOf(unitDate.get(Calendar.YEAR)).substring(2);
			}
		case TimeLine.UNIT_DAY:
			if(zoomFactor < 600000) {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.LONG, locale) + " " + unitDate.get(Calendar.DAY_OF_MONTH) + " "+ unitDate.get(Calendar.YEAR);
			} else if(zoomFactor < 900000) {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale) + " " + unitDate.get(Calendar.DAY_OF_MONTH) + " "+ unitDate.get(Calendar.YEAR);
			} else if(zoomFactor < 2000000) {
				return "" + unitDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale) + " " + unitDate.get(Calendar.DAY_OF_MONTH);
			} else {
				return "" + unitDate.get(Calendar.DAY_OF_MONTH);	
			}
		case TimeLine.UNIT_HOUR:
			if(zoomFactor < 100000) {
				return "" + unitDate.get(Calendar.HOUR_OF_DAY) + ":00";
			} else {
				return "" + unitDate.get(Calendar.HOUR_OF_DAY);
			}
		case TimeLine.UNIT_MINUTE:

			int minute = unitDate.get(Calendar.MINUTE);
			String minutes = ((minute < 10) ? "0" : "") + minute;
			String hours = "" + unitDate.get(Calendar.HOUR_OF_DAY);  
			String mh = hours + ":" + minutes;

			if(zoomFactor < 1800) {
				return mh;
			} else if(zoomFactor < 4000) {
				if(minute % 2 == 0) return mh;
			} else {
				if(minute % 5 == 0) return mh;
			}

			return "";

		case TimeLine.UNIT_SECOND:

			//System.out.println("zoomFactor: " + zoomFactor);


			if(zoomFactor < 12) {

				int minute2 = unitDate.get(Calendar.MINUTE);
				String minutes2 = ((minute2 < 10) ? "0" : "") + minute2;
				int seconds = unitDate.get(GregorianCalendar.SECOND);
				String seconds2 = ((seconds < 10) ? "0" : "") + seconds;
				return unitDate.get(GregorianCalendar.HOUR_OF_DAY) + ":" + minutes2 + ":" + seconds2;	
			} else if(zoomFactor < 30) {
				int minute2 = unitDate.get(GregorianCalendar.MINUTE);
				String minutes2 = ((minute2 < 10) ? "0" : "") + minute2;
				int seconds = unitDate.get(GregorianCalendar.SECOND);
				String seconds2 = ((seconds < 10) ? "0" : "") + seconds;
				return minutes2 + ":" + seconds2;	
			} else {
				int minute2 = unitDate.get(GregorianCalendar.MINUTE);
				String minutes2 = ((minute2 < 10) ? "0" : "") + minute2;
				int seconds = unitDate.get(GregorianCalendar.SECOND);
				String seconds2 = ((seconds < 10) ? "0" : "") + seconds;

				if(seconds % 5 == 0) return minutes2 + ":" + seconds2;	
			}

			return "";


		case TimeLine.UNIT_MILLISECOND:
			return "." + unitDate.get(GregorianCalendar.MILLISECOND);
		}
		return "";
	}

	private double getUnitPixelWidth(long time, int unit) {
		GregorianCalendar unitDate = new GregorianCalendar();
		unitDate.setTimeInMillis(time);

		switch(unit) {
		case TimeLine.UNIT_YEAR:
			return ((unitDate.getActualMaximum(GregorianCalendar.DAY_OF_YEAR)) * TimeLine.MS_DAY) / zoomFactor;
		case TimeLine.UNIT_MONTH:
			return ((unitDate.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)) * TimeLine.MS_DAY) / zoomFactor;
		case TimeLine.UNIT_DAY:
			return (TimeLine.MS_DAY / zoomFactor);
		case TimeLine.UNIT_HOUR:
			return (TimeLine.MS_HOUR / zoomFactor);
		case TimeLine.UNIT_MINUTE:
			return (TimeLine.MS_MINUTE / zoomFactor);
		case TimeLine.UNIT_SECOND:
			return (TimeLine.MS_SECOND / zoomFactor);
		case TimeLine.UNIT_MILLISECOND:
			return (TimeLine.MS_MILLISECOND / zoomFactor);
		}
		return -1;
	}

	private int getOffSetOfTime(long time) {
		return (int) ((cursorOffset - ((cursorTime - time) / zoomFactor))) +1; 
	}


	private void increaseZoomFactor() {
		zoomFactor *= 1.2;
		if(zoomFactor > 500000000) zoomFactor = 500000000;
		setAppropriateUnitLevel();
		thisTimeLine.doRedraw();
	}

	private void decreaseZoomFactor() {
		zoomFactor /= 1.2;
		if(zoomFactor < 1) zoomFactor = 1;
		setAppropriateUnitLevel();
		thisTimeLine.doRedraw();
	}


	public void increaseZoomFactorStep() {

		if(zoomFactor <= TimeLine.MS_SECOND) zoomFactor = TimeLine.MS_MILLISECOND;
		else if(zoomFactor <= TimeLine.MS_MINUTE) zoomFactor = TimeLine.MS_SECOND;
		else if(zoomFactor <= TimeLine.MS_HOUR) zoomFactor = TimeLine.MS_MINUTE;
		else if(zoomFactor <= TimeLine.MS_DAY) zoomFactor = TimeLine.MS_HOUR;
		else if(zoomFactor <= 30 * TimeLine.MS_DAY) zoomFactor = TimeLine.MS_DAY;
		else if(zoomFactor <= 365 * TimeLine.MS_DAY) zoomFactor = 30 * TimeLine.MS_DAY;


		if(zoomFactor > 500000000) zoomFactor = 500000000;
		if(zoomFactor < 1) zoomFactor = 1;
		setAppropriateUnitLevel();
		thisTimeLine.doRedraw();
	}

	public void decreaseZoomFactorStep() {

		if(zoomFactor >= 30 * TimeLine.MS_DAY) zoomFactor = 365 * TimeLine.MS_DAY;
		else if(zoomFactor >= TimeLine.MS_DAY) zoomFactor = 30 * TimeLine.MS_DAY;
		else if(zoomFactor >= TimeLine.MS_HOUR) zoomFactor = TimeLine.MS_DAY;
		else if(zoomFactor >= TimeLine.MS_MINUTE) zoomFactor = TimeLine.MS_HOUR;
		else if(zoomFactor >= TimeLine.MS_SECOND) zoomFactor = TimeLine.MS_MINUTE;
		else if(zoomFactor >= TimeLine.MS_MILLISECOND) zoomFactor = TimeLine.MS_SECOND;

		if(zoomFactor > 500000000) zoomFactor = 500000000;
		if(zoomFactor < 1) zoomFactor = 1;

		setAppropriateUnitLevel();
		thisTimeLine.doRedraw();
	}

	public void doRedraw() {
		display.syncExec(new Runnable() {

			public void run() {
				if(!thisTimeLine.isDisposed()) {
					thisTimeLine.redraw();
					SelectionDateTime.setTime(thisTimeLine.getCursorTime());
					thisTimeLine.getScenarioEditor().getTimeLineToolBar().getDateTime().setSelection(thisTimeLine.SelectionDateTime);
				}
			}
		});

	}

	public void doRedraw(ArrayList<Object> newTreeObjects) {
		treeObjects.clear();
		for(Object o : newTreeObjects) {
			treeObjects.add((TreeModel)((TreeItem)o).getData());
		}
		doRedraw();
	}

	public void increaseTime(int ms) {
		cursorTime += ms;
	}

	public void setTimeNow() {
		GregorianCalendar now = new GregorianCalendar();
		cursorTime = now.getTimeInMillis();
	}

	public void setLocked(boolean locked) {
		//System.out.println("setLocked: " + locked);
		isLocked = locked;
	}

	public static void loadImages() {
		images.add(Activator.getImageDescriptor("icons/dot.png").createImage());
		images.add(Activator.getImageDescriptor("icons/edit.png").createImage());
		images.add(Activator.getImageDescriptor("icons/no_edit.png").createImage());
		images.add(Activator.getImageDescriptor("icons/locked.png").createImage());
		images.add(Activator.getImageDescriptor("icons/locked_parent.png").createImage());
		images.add(Activator.getImageDescriptor("icons/invisible.png").createImage());
		images.add(Activator.getImageDescriptor("icons/invisible_parent.png").createImage());
		images.add(Activator.getImageDescriptor("icons/trackfolderempty.png").createImage());
		images.add(Activator.getImageDescriptor("icons/track.png").createImage());
	}

	public void dispose() {

	}

	public static Image getImage(int property) {
		return images.get(property);
	}

	public long getCursorTime() {
		return cursorTime;
	}

	public void setCursorTime(long cursorTime) {
		this.cursorTime = cursorTime;
	}

	public String getDateTimeLabel() {
		return sdf.format(cursorTime);
	}

	public void shiftTime(int wheelRotation) {
		cursorTime -= (wheelRotation * 10 * zoomFactor);
		thisTimeLine.doRedraw();
		thisTimeLine.getScenarioEditor().getEarthView().getWWD().redraw();
	}

	public Locale getLocale() {
		return locale;
	}
}
