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

package com.jeroenjanssens.presto.views.scenario.tree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.jeroenjanssens.presto.model.Folder;
import com.jeroenjanssens.presto.model.Track;
import com.jeroenjanssens.presto.model.TreeModel;


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

public class RenameDialog extends org.eclipse.swt.widgets.Dialog {

	private Shell dialogShell;
	private Composite composite1;
	private Label label1;
	private Button button2;
	private Button button1;
	private Composite composite2;
	private Label label2;
	private Text text1;
	private	RenameDialog thisDialog;
	private String returnValue;
	/**
	* Auto-generated main method to display this 
	* org.eclipse.swt.widgets.Dialog inside a new Shell.
	*/
	public static void main(String[] args) {
		try {
			Display display = Display.getDefault();
			Shell shell = new Shell(display);
			RenameDialog inst = new RenameDialog(shell, SWT.NULL);
			String name = inst.open(null);
			System.out.println("New name : " + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RenameDialog(Shell parent, int style) {
		super(parent, style);
		thisDialog = this;
	}

	public String open(TreeModel tm) {
		returnValue = null;
		try {
			final Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			if(tm instanceof Folder) {
				dialogShell.setText("Rename Folder");
				this.setText("Rename Folder");
			} else if (tm instanceof Track) {
				dialogShell.setText("Rename Track");
				this.setText("Rename Track");
			} else {
				dialogShell.setText("Rename");
				this.setText("Rename");
			}
			GridLayout dialogShellLayout = new GridLayout();
			dialogShellLayout.makeColumnsEqualWidth = true;
			dialogShell.setLayout(dialogShellLayout);
			{
				composite1 = new Composite(dialogShell, SWT.NONE);
				GridLayout composite1Layout = new GridLayout();
				composite1Layout.numColumns = 2;
				GridData composite1LData = new GridData();
				composite1LData.horizontalAlignment = GridData.FILL;
				composite1LData.grabExcessHorizontalSpace = true;
				composite1.setLayoutData(composite1LData);
				composite1.setLayout(composite1Layout);
				{
					label1 = new Label(composite1, SWT.NONE);
					label1.setText("Name:");
				}
				{
					text1 = new Text(composite1, SWT.BORDER);
					GridData text1LData = new GridData();
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					text1.setLayoutData(text1LData);
					if(tm instanceof TreeModel) {
						text1.setText(tm.getName());
					}
					text1.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent evt) {
							String s = text1.getText();
							if(s.length() < 1) {
								button1.setEnabled(false);
							} else {
								button1.setEnabled(true);
							}
						}
					});
				}
			}
			{
				GridData label2LData = new GridData();
				label2LData.horizontalAlignment = GridData.FILL;
				label2LData.grabExcessHorizontalSpace = true;
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
				composite2.setLayoutData(composite2LData);
				composite2.setLayout(composite2Layout);
				{
					button1 = new Button(composite2, SWT.PUSH | SWT.CENTER);
					GridData button1LData = new GridData();
					button1LData.widthHint = 75;
					button1LData.heightHint = 23;
					button1.setLayoutData(button1LData);
					button1.setText("OK");
					button1.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							thisDialog.setReturnValue(text1.getText());
							thisDialog.dialogShell.dispose();
						}
					});
				}
				thisDialog.dialogShell.setDefaultButton(button1);
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
							thisDialog.setReturnValue(null);
							thisDialog.dialogShell.dispose();
						}
					});
				}
			}
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setSize(317, 111);
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
		return this.returnValue;
	}

	protected void setReturnValue(String name) {
		this.returnValue = name;
	}
	
}
