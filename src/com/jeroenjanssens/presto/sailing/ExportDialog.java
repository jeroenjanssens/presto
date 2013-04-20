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

package com.jeroenjanssens.presto.sailing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jeroenjanssens.presto.model.Scenario;
import com.jeroenjanssens.presto.model.Track;



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

public class ExportDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Label lFileName;
	private Text vFileName;
	private Button bBrowse;
	private Label lStartTime;
	private Button vOnlyVisibleTracks;
	private Combo vProtocol;
	private Label lProtocol;
	private CDateTime vStartTime;
	private Label lEndTime;
	private CDateTime vEndTime;
	private Label label2;
	private Composite composite2;
	private Button bExport;
	private Button button2;
	private	ExportDialog thisDialog;
	private Scenario scenario = null;

	/**
	 * Auto-generated main method to display this 
	 * org.eclipse.swt.widgets.Dialog inside a new Shell.
	 */
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			ExportDialog inst = new ExportDialog(shell, null);
			inst.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ExportDialog(Shell parent, Scenario scenario) {
		super(parent, SWT.NULL);
		this.scenario = scenario;
	}

	public void open() {
		thisDialog = this;
		long earliestTime = 0;
		long latestTime = 0;
		if(scenario != null) {
			earliestTime = Long.MAX_VALUE;
			latestTime = 0;
			for(Track t : scenario.getRootFolder().getAllTracks(false, false)) {
				long time1 = t.getTime();
				long time2 = t.getLastWaypoint().getTime();
				if(time1 < earliestTime) {
					earliestTime = time1;
				}
				if(time2 > latestTime) {
					latestTime = time2;
				}
			}
		}
		try {
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			String scenarioName = "";
			if(scenario != null) {
				scenarioName = " " + scenario.getName();
			}
			dialogShell.setText("Export Scenario" + scenarioName);
			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.numColumns = 3;
			dialogShellLayout.marginHeight = 15;
			dialogShellLayout.marginWidth = 15;
			dialogShellLayout.horizontalSpacing = 10;
			dialogShellLayout.verticalSpacing = 10;
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(428, 248);
			{
				lFileName = new Label(dialogShell, SWT.NONE);
				GridData lFileNameLData = new GridData();
				lFileNameLData.grabExcessHorizontalSpace = true;
				lFileName.setText("File Name:");
			}
			{
				GridData vFileNameLData = new GridData();
				vFileNameLData.horizontalAlignment = GridData.FILL;
				vFileNameLData.grabExcessHorizontalSpace = true;
				vFileName = new Text(dialogShell, SWT.BORDER);
				vFileName.setLayoutData(vFileNameLData);
				vFileName.addModifyListener(new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						updateState();
					}

				});
			}
			{
				bBrowse = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
				bBrowse.setText(" Browse... ");
				bBrowse.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) {
						FileDialog fd = new FileDialog(dialogShell, SWT.SAVE);
						fd.setText("Save");
						fd.setFilterPath("C:/");
						String[] filterExt = { "*.*" };
						fd.setFilterExtensions(filterExt);
						fd.setFileName(vFileName.getText());
						String selected = fd.open();
						if(selected != null) {
							vFileName.setText(selected);
						}
						//System.out.println(selected);
					}
				});
			}
			{
				lProtocol = new Label(dialogShell, SWT.NONE);
				lProtocol.setText("Protocol:");
			}
			{
				vProtocol = new Combo(dialogShell, SWT.NONE);
				GridData vProtocolLData = new GridData();
				vProtocolLData.grabExcessHorizontalSpace = true;
				vProtocolLData.horizontalAlignment = GridData.FILL;
				vProtocolLData.horizontalSpan = 2;
				vProtocol.setLayoutData(vProtocolLData);
				vProtocol.add("Comma Separated Values");
				vProtocol.add("NMEA");
				vProtocol.setText("Comma Separated Values");
			}
			{
				vOnlyVisibleTracks = new Button(dialogShell, SWT.CHECK | SWT.LEFT);
				GridData vOnlyVisibleTracksLData = new GridData();
				vOnlyVisibleTracksLData.horizontalSpan = 3;
				vOnlyVisibleTracksLData.horizontalAlignment = GridData.FILL;
				vOnlyVisibleTracksLData.grabExcessHorizontalSpace = true;
				vOnlyVisibleTracks.setLayoutData(vOnlyVisibleTracksLData);
				vOnlyVisibleTracks.setText("Export only visible tracks");
			}
			{
				lStartTime = new Label(dialogShell, SWT.NONE);
				lStartTime.setText("Start Time:");
			}
			{



				vStartTime = new CDateTime(dialogShell, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
				GridData text1LData = new GridData();
				text1LData.horizontalAlignment = GridData.FILL;
				text1LData.grabExcessHorizontalSpace = true;
				text1LData.horizontalSpan = 2;
				vStartTime.setLayoutData(text1LData);
				if(scenario != null && earliestTime != 0) {
					vStartTime.setSelection(new Date(earliestTime-1000));
				} else {
					vStartTime.setSelection(null);
				}
				vStartTime.setPattern("EEEE, MMMM d yyyy '@' HH:mm:ss");
				vStartTime.setLocale(new Locale("en", "US"));
			}
			{
				lEndTime = new Label(dialogShell, SWT.NONE);
				lEndTime.setText("End Time:");
			}
			{
				vEndTime = new CDateTime(dialogShell, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
				GridData text1LData = new GridData();
				text1LData.horizontalAlignment = GridData.FILL;
				text1LData.grabExcessHorizontalSpace = true;
				text1LData.horizontalSpan = 2;
				vEndTime.setLayoutData(text1LData);

				vEndTime.setPattern("EEEE, MMMM d yyyy '@' HH:mm:ss");
				vEndTime.setLocale(new Locale("en", "US"));
				if(scenario != null && latestTime != 0) {
					vEndTime.setSelection(new Date(latestTime+1000));
				} else {
					vEndTime.setSelection(null);
				}
			}
			{
				GridData label2LData = new GridData();
				label2LData.horizontalAlignment = GridData.FILL;
				label2LData.grabExcessHorizontalSpace = true;
				label2LData.horizontalSpan = 3;
				label2 = new Label(dialogShell, SWT.SEPARATOR | SWT.HORIZONTAL);
				label2.setLayoutData(label2LData);
			}
			{
				composite2 = new Composite(dialogShell, SWT.NONE);
				GridLayout composite2Layout = new GridLayout();
				composite2Layout.makeColumnsEqualWidth = true;
				composite2Layout.numColumns = 2;
				GridData composite2LData = new GridData();
				composite2LData.horizontalAlignment = GridData.END;
				composite2LData.horizontalSpan = 3;
				composite2.setLayoutData(composite2LData);
				composite2.setLayout(composite2Layout);
				{
					bExport = new Button(composite2, SWT.PUSH | SWT.CENTER);
					GridData button1LData = new GridData();
					button1LData.widthHint = 75;
					button1LData.heightHint = 23;
					bExport.setLayoutData(button1LData);
					bExport.setText("Export");
					bExport.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							//thisDialog.setReturnValue(text1.getText());
							//thisDialog.dialogShell.dispose();

							File f = new File(vFileName.getText());
							if(f.exists()) {
								if(!MessageDialog.openQuestion(dialogShell, "Question", "The file '" + f.getAbsolutePath()  + "' already exists. Are you sure you want to overwrite the existing file?")) {
									return;	
								}	
							}

							long startTime = 0;
							Date sd = vStartTime.getSelection();
							if(sd != null) startTime = sd.getTime();

							long endTime = 0;
							Date ed = vEndTime.getSelection();
							if(ed != null) endTime = ed.getTime();

							try {
								IProtocol proto;
								if (vProtocol.getText().equals("NMEA")) {
									proto = new AISProtocol();
								} else {
									proto = new CSVProtocol();
								}
								new ProgressMonitorDialog(dialogShell).run(true, true, new Logger(scenario, vFileName.getText(), startTime, endTime, vOnlyVisibleTracks.getSelection(), proto));
								MessageDialog.openInformation(dialogShell, "Export successful", "The scenario has been successfully exported.");
								thisDialog.dialogShell.dispose();
							} catch (InvocationTargetException e) {
								MessageDialog.openError(dialogShell, "Error", e.getMessage());
							} catch (InterruptedException e) {
								MessageDialog.openInformation(dialogShell, "Cancelled", e.getMessage());
							}
						}
					});
				}
				thisDialog.dialogShell.setDefaultButton(bExport);
				{
					button2 = new Button(composite2, SWT.PUSH | SWT.CENTER);
					GridData button2LData = new GridData();
					button2LData.widthHint = 75;
					button2LData.heightHint = 23;
					button2.setLayoutData(button2LData);
					button2.setText("Cancel");
					button2.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							thisDialog.dialogShell.dispose();
						}
					});
				}
			}
			updateState();
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateState() {
		boolean enabled = true;
		boolean delete = true;
		File f = new File(vFileName.getText());


		if(!f.isAbsolute()) {
			bExport.setEnabled(false);
			return;
		}

		if(f.isDirectory()) {
			bExport.setEnabled(false);
			return;
		}

		delete = !f.exists();

		try {
			f.createNewFile();
		} catch (IOException e) {
			enabled = false;
		} finally {
			if(delete) {
				if(f.exists()) {
					f.delete();
				}
			}
		}

		bExport.setEnabled(enabled);

	}

}
