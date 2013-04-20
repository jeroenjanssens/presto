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
import java.util.HashMap;
import java.util.Locale;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.jeroenjanssens.presto.sailing.ais.AISParameter;



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

public class JiglooTrackComposite extends org.eclipse.swt.widgets.Composite {

	private Group gWaypointPosition;
	private Label lName;

	private ToolBar toolBar2;
	private ToolItem toolItem1;
	private ToolItem toolItem2;
	private ToolItem toolItem3;
	private ToolItem toolItem4;
	private ToolBar toolBar1;
	private ToolItem addActionItem;
	public Combo vSelection;
	private Combo vPlace;
	private Label lPlace;
	private Combo vParent;
	private Label lParent;
	private Composite composite1;
	public Button vLocked;
	public Button vVisible;
	private Group gTrackSelection;
	private ToolBar actionToolBar;
	public Text vColor;
	private Label lColor;
	public Text vName;

	private Group gStaticAISDefaults;
	private Group gDynamicAISDefaults;

	private Group gETS;
	private Group gETA;

	public CDateTime vETS;
	public CDateTime vETA;


	private Display display;

	public HashMap<AISParameter, AISValueLabelAndControl> AISControls = new HashMap<AISParameter, AISValueLabelAndControl>();

	private static boolean isPresto = true;

	private TrackPropertiesComposite parent;

	public static void main(String[] args) {
		isPresto = false;
		showGUI();
	}

	public void enable(boolean enabled) {
		enableChildren(this.getChildren(), enabled);
	}

	private void enableChildren(Control[] children, boolean enabled) {
		for(int c = 0; c < children.length; c++) {
			Control child = children[c];

			if(!child.equals(this.gTrackSelection)) {
				child.setEnabled(enabled);
				if(child instanceof Composite) {
					enableChildren(((Composite) child).getChildren(), enabled);
				}
			}
		}
	}
	
	
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		JiglooTrackComposite inst = new JiglooTrackComposite(shell, SWT.NULL);
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

	public JiglooTrackComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		this.display = parent.getDisplay();
		this.parent = (TrackPropertiesComposite) parent;
		initGUI();
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			//this.setSize(200, 500);
			{
				gTrackSelection = new Group(this, SWT.NONE);
				GridLayout group3Layout = new GridLayout();
				gTrackSelection.setLayout(group3Layout);
				GridData group3LData = new GridData();
				group3LData.grabExcessHorizontalSpace = true;
				group3LData.horizontalAlignment = GridData.FILL;
				gTrackSelection.setLayoutData(group3LData);
				gTrackSelection.setText("Track Selection");
				{
					vSelection = new Combo(gTrackSelection, SWT.NONE);
					GridData combo1LData = new GridData();
					combo1LData.grabExcessHorizontalSpace = true;
					combo1LData.horizontalAlignment = GridData.FILL;
					vSelection.setLayoutData(combo1LData);
					
					
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
				gWaypointPosition.setText("Track Information");
				{
					lName = new Label(gWaypointPosition, SWT.NONE);
					lName.setText("Name:");
				}
				{
					vName = new Text(gWaypointPosition, SWT.BORDER);
					GridData text1LData = new GridData();
					text1LData.heightHint = 13;
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					vName.setLayoutData(text1LData);
					vName.setText("");
					vName.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveName();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vName.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}
				{
					lColor = new Label(gWaypointPosition, SWT.NONE);
					lColor.setText("Color:");
				}
				{
					vColor = new Text(gWaypointPosition, SWT.BORDER | SWT.CENTER);
					vColor.setBackground(null);
					GridData text2LData = new GridData();
					text2LData.grabExcessHorizontalSpace = true;
					text2LData.horizontalAlignment = GridData.FILL;
					vColor.setLayoutData(text2LData);
					vColor.setText("");
					vColor.setEditable(false);
					vColor.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseUp(MouseEvent evt) {
							ColorDialog cd = new ColorDialog(parent.getShell());
							cd.setText("Select Track Color");
							cd.setRGB(vColor.getBackground().getRGB());
							RGB newColor = cd.open();
							if (newColor != null) {
								vColor.setBackground(new Color(parent.getPropertiesView().getEarthView().getDisplay(), newColor));
								parent.saveColor();
							}
						}
					});
					vColor.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							
							
						}
					});
					vColor.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							//if(evt.keyCode == 13) parent.saveLongitude();
							//if(evt.keyCode == 27) parent.restore();
						}
					});
					vColor.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							//parent.restore();
						}
					});
					
				}
				{
					composite1 = new Composite(gWaypointPosition, SWT.NONE);
					GridLayout composite1Layout = new GridLayout();
					composite1Layout.numColumns = 2;
					composite1Layout.makeColumnsEqualWidth = true;
					composite1Layout.horizontalSpacing = 0;
					composite1Layout.marginHeight = 0;
					composite1Layout.marginWidth = 0;
					composite1Layout.verticalSpacing = 0;
					GridData composite1LData = new GridData();
					composite1LData.horizontalSpan = 2;
					composite1LData.grabExcessHorizontalSpace = true;
					composite1LData.horizontalAlignment = GridData.FILL;
					composite1.setLayoutData(composite1LData);
					composite1.setLayout(composite1Layout);
					{
						vVisible = new Button(composite1, SWT.CHECK | SWT.LEFT);
						GridData vVisibleLData = new GridData();
						vVisibleLData.grabExcessHorizontalSpace = true;
						vVisible.setLayoutData(vVisibleLData);
						vVisible.setText("Visible");
						vVisible.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent evt) {
								parent.saveVisible();
							}
						});
					}
					{
						vLocked = new Button(composite1, SWT.CHECK | SWT.LEFT);
						GridData vLockedLData = new GridData();
						vLockedLData.horizontalAlignment = GridData.END;
						vLocked.setLayoutData(vLockedLData);
						vLocked.setText("Locked");
						vLocked.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent evt) {
								parent.saveLocked();
							}
						});
					}
				}
				/*
				{
					lParent = new Label(gWaypointPosition, SWT.NONE);
					lParent.setText("Folder:");
				}
				{
					vParent = new Combo(gWaypointPosition, SWT.NONE);
					GridData vParentLData = new GridData();
					vParentLData.grabExcessHorizontalSpace = true;
					vParentLData.horizontalAlignment = GridData.FILL;
					vParent.setLayoutData(vParentLData);
					vParent.setText("Root");
				}
				{
					lPlace = new Label(gWaypointPosition, SWT.NONE);
					lPlace.setText("Place:");
				}
				{
					vPlace = new Combo(gWaypointPosition, SWT.NONE);
					GridData vPlaceLData = new GridData();
					vPlaceLData.horizontalAlignment = GridData.FILL;
					vPlaceLData.grabExcessHorizontalSpace = true;
					vPlace.setLayoutData(vPlaceLData);
					vPlace.setText("(First)");
				}
				*/

			}

			{
				gETS = new Group(this, SWT.NONE);
				GridLayout gETALayout = new GridLayout();
				gETALayout.makeColumnsEqualWidth = true;
				gETS.setLayout(gETALayout);
				GridData gETALData = new GridData();
				gETALData.grabExcessHorizontalSpace = true;
				gETALData.horizontalAlignment = GridData.FILL;
				gETS.setLayoutData(gETALData);
				gETS.setText("Date and Time of Start");
				{
					vETS = new CDateTime(gETS, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
					GridData text1LData = new GridData();
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					vETS.setLayoutData(text1LData);
					vETS.setSelection(new Date());
					vETS.setPattern("EEEE, MMMM d yyyy '@' HH:mm:ss");
					vETS.setLocale(new Locale("en", "US"));
					vETS.setSelection(new Date(1211889301373L));
					vETS.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent evt) {
							//System.out.println("" + vETS.getSelection().getTime());
							parent.saveETS();
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
					vETA.setSelection(new Date(1211889301373L));
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
				gStaticAISDefaults = new Group(this, SWT.NONE);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 2;
				gStaticAISDefaults.setLayout(group1Layout);
				GridData group1LData = new GridData();
				group1LData.verticalAlignment = GridData.BEGINNING;
				group1LData.grabExcessHorizontalSpace = true;
				group1LData.horizontalAlignment = GridData.FILL;
				gStaticAISDefaults.setLayoutData(group1LData);
				gStaticAISDefaults.setText("Static AIS Defaults");
			}
			{
				gDynamicAISDefaults = new Group(this, SWT.NONE);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 2;
				gDynamicAISDefaults.setLayout(group1Layout);
				GridData group1LData = new GridData();
				group1LData.verticalAlignment = GridData.BEGINNING;
				group1LData.grabExcessHorizontalSpace = true;
				group1LData.horizontalAlignment = GridData.FILL;
				gDynamicAISDefaults.setLayoutData(group1LData);
				gDynamicAISDefaults.setText("Dynamic AIS Defaults");

			}










			for(AISParameter p : AISParameter.values()) {
				if(!p.isDynamic()) {
					AISControls.put(p, new AISValueLabelAndControl(parent, gStaticAISDefaults, p));
				} else {
					AISControls.put(p, new AISValueLabelAndControl(parent, gDynamicAISDefaults, p));
				}
			}

			this.pack();
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
