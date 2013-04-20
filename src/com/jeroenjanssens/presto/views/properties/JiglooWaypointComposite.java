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

package com.jeroenjanssens.presto.views.properties;
import java.util.Date;
import java.util.Locale;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */

/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class JiglooWaypointComposite extends org.eclipse.swt.widgets.Composite {

	private Group gWaypointPosition;
	private Label lLatitude;
	public Text vSpeed;
	private Label lSpeed;

	private ToolBar toolBar2;
	private ToolItem toolItem2;
	private ToolItem toolItem4;
	public Combo vSelection;
	public CDateTime vETA;
	private Group gETA;
	public Text vAngle;
	private Label lAngle;
	private Group gWaypointSelection;
	public Text vLongitude;
	private Label lLongitude;
	public Text vLatitude;

	private Group gCurve;

	private Display display;

	private static boolean isPresto = true;

	private WaypointPropertiesComposite parent;

	public static void main(String[] args) {
		isPresto = false;
		showGUI();
	}

	public void enable(boolean enabled, boolean enableSelection) {
		enableChildren(this.getChildren(), enabled);
		
		this.gWaypointSelection.setEnabled(enableSelection);
		enableChildren(this.gWaypointSelection.getChildren(), enableSelection);
	}

	private void enableChildren(Control[] children, boolean enabled) {
		for(int c = 0; c < children.length; c++) {
			Control child = children[c];

			if(!child.equals(this.gWaypointSelection)) {
				child.setEnabled(enabled);
				if(child instanceof Group) {
					enableChildren(((Group) child).getChildren(), enabled);
				}
			}
		}
	}

	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		JiglooWaypointComposite inst = new JiglooWaypointComposite(shell, SWT.NULL);
		Point size = inst.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if(size.x == 0 && size.y == 0) {
			inst.pack();
			shell.pack();
		} else {
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public JiglooWaypointComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		this.display = parent.getDisplay();
		if(!(parent instanceof Shell)) this.parent = (WaypointPropertiesComposite) parent;
		initGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			//this.setSize(200, 500);
			{
				gWaypointSelection = new Group(this, SWT.NONE);
				GridLayout group3Layout = new GridLayout();
				gWaypointSelection.setLayout(group3Layout);
				GridData group3LData = new GridData();
				group3LData.grabExcessHorizontalSpace = true;
				group3LData.horizontalAlignment = GridData.FILL;
				gWaypointSelection.setLayoutData(group3LData);
				gWaypointSelection.setText("Waypoint Selection");
				{
					vSelection = new Combo(gWaypointSelection, SWT.NONE);
					GridData combo1LData = new GridData();
					combo1LData.grabExcessHorizontalSpace = true;
					combo1LData.horizontalAlignment = GridData.FILL;
					vSelection.setLayoutData(combo1LData);
					vSelection.setText("Waypoint 0");
					vSelection.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							parent.selectFromCombo();
						}
					});
				}
			}


			{
				gWaypointPosition = new Group(this, SWT.NONE);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 2;
				gWaypointPosition.setLayout(group1Layout);
				GridData group1LData = new GridData();
				group1LData.verticalAlignment = GridData.BEGINNING;
				group1LData.grabExcessHorizontalSpace = true;
				group1LData.horizontalAlignment = GridData.FILL;
				gWaypointPosition.setLayoutData(group1LData);
				gWaypointPosition.setText("Waypoint Position");
				{
					lLatitude = new Label(gWaypointPosition, SWT.NONE);
					lLatitude.setText("Latitude:");
				}
				{
					vLatitude = new Text(gWaypointPosition, SWT.BORDER);
					GridData text1LData = new GridData();
					text1LData.heightHint = 13;
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					vLatitude.setLayoutData(text1LData);
					vLatitude.setText("");
					vLatitude.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveLatitude();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vLatitude.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}
				{
					lLongitude = new Label(gWaypointPosition, SWT.NONE);
					lLongitude.setText("Longitude:");
				}
				{
					vLongitude = new Text(gWaypointPosition, SWT.BORDER);
					GridData text2LData = new GridData();
					text2LData.grabExcessHorizontalSpace = true;
					text2LData.horizontalAlignment = GridData.FILL;
					vLongitude.setLayoutData(text2LData);
					vLongitude.setText("");
					vLongitude.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveLongitude();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vLongitude.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}

			}
			{
				gETA = new Group(this, SWT.NONE);
				GridLayout gETALayout = new GridLayout();
				gETALayout.makeColumnsEqualWidth = true;
				gETA.setLayout(gETALayout);
				GridData gETALData = new GridData();
				gETALData.grabExcessHorizontalSpace = true;
				gETALData.horizontalAlignment = GridData.FILL;
				gETA.setLayoutData(gETALData);
				gETA.setText("Date and Time of Arrival");
				{
					vETA = new CDateTime(gETA, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
					GridData text1LData = new GridData();
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					vETA.setLayoutData(text1LData);
					vETA.setSelection(new Date());
					vETA.setPattern("EEEE, MMMM d yyyy '@' HH:mm:ss");
					vETA.setLocale(new Locale("en", "US"));
					vETA.setSelection(null);
					vETA.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent evt) {
							//System.out.println("" + vETA.getSelection().getTime());
							parent.saveETA();
						}
					});

				}
			}

			{
				gCurve = new Group(this, SWT.NONE);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 2;
				gCurve.setLayout(group1Layout);
				GridData group1LData = new GridData();
				group1LData.verticalAlignment = GridData.BEGINNING;
				group1LData.grabExcessHorizontalSpace = true;
				group1LData.horizontalAlignment = GridData.FILL;
				gCurve.setLayoutData(group1LData);
				gCurve.setText("Waypoint Curve");
				{
					lSpeed = new Label(gCurve, SWT.NONE);
					lSpeed.setText("Speed:");
				}
				{
					GridData text3LData = new GridData();
					text3LData.horizontalAlignment = GridData.FILL;
					text3LData.grabExcessHorizontalSpace = true;
					vSpeed = new Text(gCurve, SWT.BORDER);
					vSpeed.setLayoutData(text3LData);
					vSpeed.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveSpeed();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vSpeed.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}
				{
					lAngle = new Label(gCurve, SWT.NONE);
					lAngle.setText("Angle:");
				}
				{
					GridData vAngleLData = new GridData();
					vAngleLData.horizontalAlignment = GridData.FILL;
					vAngleLData.grabExcessHorizontalSpace = true;
					vAngle = new Text(gCurve, SWT.BORDER);
					vAngle.setLayoutData(vAngleLData);
					vAngle.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveAngle();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vAngle.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}

			}







			this.pack();
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
