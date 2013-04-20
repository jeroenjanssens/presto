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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


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

public class JiglooScenarioComposite extends org.eclipse.swt.widgets.Composite {
	private Group group1;
	private Label label1;
	public CDateTime vCreated;
	private Label label4;
	public Text vDescription;
	private Group group2;
	public Scale vDifficulty;
	private Label label3;
	public Text vAuthor;
	private Label label2;
	public Text vName;

	private ScenarioPropertiesComposite parent;

	/**
	 * Auto-generated main method to display this 
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * Auto-generated method to display this 
	 * org.eclipse.swt.widgets.Composite inside a new Shell.
	 */
	public static void showGUI() {
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		JiglooScenarioComposite inst = new JiglooScenarioComposite(shell, SWT.NULL);
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

	public JiglooScenarioComposite(org.eclipse.swt.widgets.Composite parent, int style) {
		super(parent, style);
		this.parent = (ScenarioPropertiesComposite) parent;
		initGUI();
	}

	public void enable(boolean enabled) {
		enableChildren(this.getChildren(), enabled);
	}

	private void enableChildren(Control[] children, boolean enabled) {
		for(int c = 0; c < children.length; c++) {
			Control child = children[c];

			child.setEnabled(enabled);
			if(child instanceof Group) {
				enableChildren(((Group) child).getChildren(), enabled);
			}
		}
	}

	private void initGUI() {
		try {
			GridLayout thisLayout = new GridLayout();
			thisLayout.makeColumnsEqualWidth = true;
			this.setLayout(thisLayout);
			{
				group1 = new Group(this, SWT.NONE);
				GridLayout group1Layout = new GridLayout();
				group1Layout.numColumns = 2;
				group1.setLayout(group1Layout);
				GridData group1LData = new GridData();
				group1LData.grabExcessHorizontalSpace = true;
				group1LData.horizontalAlignment = GridData.FILL;
				group1.setLayoutData(group1LData);
				group1.setText("Scenario Information");
				{
					label1 = new Label(group1, SWT.NONE);
					label1.setText("Name:");
				}
				{
					vName = new Text(group1, SWT.BORDER);
					GridData text1LData = new GridData();
					text1LData.grabExcessHorizontalSpace = true;
					text1LData.horizontalAlignment = GridData.FILL;
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
					label2 = new Label(group1, SWT.NONE);
					label2.setText("Author:");
				}
				{
					vAuthor = new Text(group1, SWT.BORDER);
					GridData text2LData = new GridData();
					text2LData.grabExcessHorizontalSpace = true;
					text2LData.horizontalAlignment = GridData.FILL;
					vAuthor.setLayoutData(text2LData);
					vAuthor.setText("");
					vAuthor.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent evt) {
							//System.out.println("Key Pressed " + evt.keyCode);
							if(evt.keyCode == 13) parent.saveAuthor();
							if(evt.keyCode == 27) parent.restore();
						}
					});
					vAuthor.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.restore();
						}
					});
				}
				{
					label3 = new Label(group1, SWT.NONE);
					label3.setText("Difficulty:");
				}
				{
					vDifficulty = new Scale(group1, SWT.NONE);
					vDifficulty.setMinimum(1);
					vDifficulty.setMinimum(1);
					vDifficulty.setIncrement(1);
					GridData scale1LData = new GridData();
					scale1LData.horizontalAlignment = GridData.FILL;
					scale1LData.grabExcessHorizontalSpace = true;
					vDifficulty.setLayoutData(scale1LData);
					vDifficulty.setPageIncrement(1);
					vDifficulty.setMaximum(10);
					vDifficulty.setSelection(1);
					vDifficulty.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent evt) {
							parent.saveDifficulty();
						}
						@Override
						public void widgetSelected(SelectionEvent evt) {
							parent.saveDifficulty();
						}
					});

				}
				{
					label4 = new Label(group1, SWT.NONE);
					label4.setText("Created:");
				}
				{
					vCreated = new CDateTime(group1, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG| CDT.TIME_MEDIUM| CDT.BORDER| CDT.SPINNER);
					GridData text1LData = new GridData();
					text1LData.horizontalAlignment = GridData.FILL;
					text1LData.grabExcessHorizontalSpace = true;
					vCreated.setLayoutData(text1LData);
					vCreated.setSelection(new Date());
					vCreated.setPattern("MMMM d yyyy '@' HH:mm:ss");
					vCreated.setLocale(new Locale("en", "US"));
					vCreated.setSelection(new Date(1211889301373L));
					vCreated.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent evt) {
							parent.saveCreated();
						}
					});

				}
			}
			{
				group2 = new Group(this, SWT.NONE);
				GridLayout group2Layout = new GridLayout();
				group2Layout.makeColumnsEqualWidth = true;
				group2.setLayout(group2Layout);
				GridData group2LData = new GridData();
				group2LData.horizontalAlignment = GridData.FILL;
				group2LData.grabExcessHorizontalSpace = true;
				group2LData.verticalAlignment = GridData.FILL;
				group2LData.grabExcessVerticalSpace = true;
				group2LData.minimumHeight = 150;
				group2.setLayoutData(group2LData);
				group2.setText("Scenario Description");
				{
					vDescription = new Text(group2, SWT.MULTI | SWT.WRAP | SWT.BORDER| SWT.V_SCROLL);
					GridData text3LData = new GridData();
					text3LData.horizontalAlignment = GridData.FILL;
					text3LData.grabExcessHorizontalSpace = true;
					text3LData.grabExcessVerticalSpace = true;
					text3LData.verticalAlignment = GridData.FILL;
					vDescription.setLayoutData(text3LData);
					vDescription.setText("");
					vDescription.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(FocusEvent evt) {
							parent.saveDescription();
						}
					});
				}
			}
			this.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
