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

import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jeroenjanssens.presto.Activator;
import com.jeroenjanssens.presto.views.scenario.ScenarioEditor;



/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class TimeLineToolBar {

	int[] speeds = {50, 100, 200, 300, 400, 500, 1000, 6000, 12000, 24000, 60000, 360000};
	private Combo combo;
	private ToolItem lockItem;
	private Display display;
	private ScenarioEditor scenarioEditor;

	private TimeLine timeLine;
	private Animator animator;
	private CDateTime dateTime;

	private Image zoomInIcon;
	private Image zoomOutIcon;
	private Image playReverseIcon;
	private Image playIcon;
	private Image pauseIcon;
	private Image stopIcon;
	private Image prevFrameIcon;
	private Image nextFrameIcon;
	private Image homeIcon;
	private Image syncIcon;
	private Image lockIcon;
	
	
	public CDateTime getDateTime() {
		return dateTime;
	}

	public TimeLineToolBar(final ScenarioEditor scenarioEditor, Composite parent) {

		this.display = parent.getDisplay();
		this.scenarioEditor = scenarioEditor;

		this.timeLine = scenarioEditor.getTimeLine();
		this.animator = scenarioEditor.getAnimator();

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);

		zoomInIcon = Activator.getImageDescriptor("icons/zoomplus.gif").createImage(display);
		zoomOutIcon = Activator.getImageDescriptor("icons/zoomminus.gif").createImage(display);
		playReverseIcon = Activator.getImageDescriptor("icons/play_reverse.gif").createImage(display);
		playIcon = Activator.getImageDescriptor("icons/play.gif").createImage(display);
		stopIcon = Activator.getImageDescriptor("icons/stop.gif").createImage(display);
		prevFrameIcon = Activator.getImageDescriptor("icons/prev_frame.gif").createImage(display);
		nextFrameIcon = Activator.getImageDescriptor("icons/next_frame.gif").createImage(display);
		homeIcon = Activator.getImageDescriptor("icons/home.gif").createImage(display);
		lockIcon = Activator.getImageDescriptor("icons/locked_trans.png").createImage(display);

		ToolItem zoomOutItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem zoomInItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem sep1 = new ToolItem(toolBar, SWT.SEPARATOR);
		final ToolItem playReverseItem = new ToolItem (toolBar, SWT.CHECK);
		final ToolItem playItem = new ToolItem (toolBar, SWT.CHECK);
		final ToolItem stopItem = new ToolItem (toolBar, SWT.CHECK);
		ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem prevFrameItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem nextFrameItem = new ToolItem (toolBar, SWT.PUSH);
		ToolItem sep3 = new ToolItem(toolBar, SWT.SEPARATOR);
		//home
		//increase speed
		ToolItem sepc = new ToolItem(toolBar, SWT.SEPARATOR);
		combo = new Combo(toolBar, SWT.READ_ONLY | SWT.RIGHT);
		ToolItem homeItem = new ToolItem (toolBar, SWT.PUSH);
		lockItem = new ToolItem (toolBar, SWT.CHECK);



		parent.addDisposeListener(new DisposeListener() {
			
			public void widgetDisposed(DisposeEvent e) {
				//System.out.println("Disposing zoom icons and stuff...");
				
				zoomInIcon.dispose();
				zoomOutIcon.dispose();
				playReverseIcon.dispose();
				playIcon.dispose();
				pauseIcon.dispose();
				stopIcon.dispose();
				prevFrameIcon.dispose();
				nextFrameIcon.dispose();
				homeIcon.dispose();
				syncIcon.dispose();
				lockIcon.dispose();
				
			}
			
		});
		
		for(int s = 0; s < speeds.length; s++) {
			String sp = "" + (speeds[s]);
			sp += "%";
			combo.add(sp);
		}
		combo.select(1);
		combo.pack();
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				changeAnimatorSpeed();
			}

		});

		sepc.setWidth(combo.getSize().x);
		sepc.setControl(combo);

		ToolItem sep4 = new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem sepe = new ToolItem(toolBar, SWT.SEPARATOR);
		dateTime = new CDateTime(toolBar, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
		sepe.setControl(dateTime);
		sepe.setWidth(300);


		dateTime.setPattern("' ' EEEE, MMMM d yyyy '@' HH:mm:ss");
		dateTime.setLocale(new Locale("en", "US"));
		dateTime.setSelection(new Date(scenarioEditor.getTimeLine().getCursorTime()));
		dateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				scenarioEditor.getTimeLine().setCursorTime(dateTime.getSelection().getTime());
				scenarioEditor.getTimeLine().doRedraw();
				scenarioEditor.getEarthView().getWWD().redraw();
			}
		});

		zoomOutItem.setImage (zoomOutIcon);
		zoomInItem.setImage (zoomInIcon);
		playReverseItem.setImage (playReverseIcon);
		playItem.setImage (playIcon);
		stopItem.setImage (stopIcon);
		prevFrameItem.setImage (prevFrameIcon);
		nextFrameItem.setImage (nextFrameIcon);
		homeItem.setImage (homeIcon);
		lockItem.setImage (lockIcon);

		zoomInItem.setToolTipText("Zoom In");
		zoomOutItem.setToolTipText("Zoom Out");
		playReverseItem.setToolTipText("Play Reverse");
		playItem.setToolTipText("Play");
		stopItem.setToolTipText("Stop");

		prevFrameItem.setToolTipText("Previous Frame");
		nextFrameItem.setToolTipText("Next Frame");
		homeItem.setToolTipText("Set Time To Now");
		lockItem.setToolTipText("Lock Timeline");

		zoomInItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeLine.increaseZoomFactorStep();
			}
		});

		zoomOutItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeLine.decreaseZoomFactorStep();
			}
		});

		playReverseItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!animator.isPlayingReverse()) {
					animator.playReverse();
					playItem.setSelection(false);
					playReverseItem.setSelection(true);
					stopItem.setSelection(false);
				} else {
					animator.stop();
					playItem.setSelection(false);
					playReverseItem.setSelection(false);
					stopItem.setSelection(true);
				}
			}
		});

		playItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(!animator.isPlaying()) {
					animator.play();
					playItem.setSelection(true);
					playReverseItem.setSelection(false);
					stopItem.setSelection(false);
				} else {
					animator.stop();
					playItem.setSelection(false);
					playReverseItem.setSelection(false);
					stopItem.setSelection(true);
				}
			}
		});

		stopItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				animator.stop();
				playItem.setSelection(false);
				playReverseItem.setSelection(false);
				stopItem.setSelection(true);
			}
		});

		homeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				timeLine.setTimeNow();
				timeLine.doRedraw();
			}
		});

		lockItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleLocked();
			}
		});

		prevFrameItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				animator.prevFrame();
			}
		});

		nextFrameItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				animator.nextFrame();
			}
		});

		toolBar.pack ();
		stopItem.setSelection(true);
	}

	private void toggleLocked() {
		timeLine.setLocked(lockItem.getSelection());
	}

	private void changeAnimatorSpeed() {
		animator.setSpeed(speeds[combo.getSelectionIndex()]);
	}

}
