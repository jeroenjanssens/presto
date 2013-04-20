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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jeroenjanssens.presto.sailing.ais.AISNavigationStatus;
import com.jeroenjanssens.presto.sailing.ais.AISParameter;




/**
 * @author Jeroen Janssens
 * @created June 6, 2009
 */

public class AISValueLabelAndControl {

	private AISParameter aisParameter;
	private Label label;
	private Control control;

	@SuppressWarnings("unused")
	private TrackPropertiesComposite parent;
	
	public AISValueLabelAndControl(final TrackPropertiesComposite parent, Composite composite, final AISParameter aisParameter) {
		this.parent = parent;
		this.aisParameter = aisParameter;

		label = new Label(composite, SWT.NONE);
		label.setText(aisParameter.getTextName() + ":");

		if(aisParameter.getType().equals(String.class)) {
			control = new Text(composite, SWT.BORDER);
			
			control.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					//System.out.println("Key Pressed " + evt.keyCode);
					if(evt.keyCode == 13) parent.saveAISValue(aisParameter, ((Text) control).getText());
					if(evt.keyCode == 27) parent.restore();
				}
			});
			control.addFocusListener(new FocusAdapter() {@Override
			public void focusLost(FocusEvent evt) {parent.restore();}});
			
		} else if(aisParameter.getType().equals(Boolean.class)) {
			control = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
			//((Combo) control).add("");			
			((Combo) control).add("true");
			((Combo) control).add("false");
			
			((Combo) control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					parent.saveAISValue(aisParameter, Boolean.parseBoolean(((Combo) control).getText()));
				}
			});
			
			//control.addFocusListener(new FocusAdapter() {public void focusLost(FocusEvent evt) {parent.restore();}});
			
		} else if(aisParameter.getType().equals(Double.class)) {
			control = new Text(composite, SWT.BORDER);
			
			control.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					//System.out.println("Key Pressed " + evt.keyCode);
					if(evt.keyCode == 13) parent.saveAISValue(aisParameter, ((Text) control).getText().replace(",", "."));
					if(evt.keyCode == 27) parent.restore();
				}
			});
			control.addFocusListener(new FocusAdapter() {@Override
			public void focusLost(FocusEvent evt) {parent.restore();}});
			
		} else if(aisParameter.getType().equals(Integer.class)) {
			control = new Text(composite, SWT.BORDER);
			control.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					//System.out.println("Key Pressed " + evt.keyCode);
					if(evt.keyCode == 13) parent.saveAISValue(aisParameter, ((Text) control).getText().replace(",", "."));
					if(evt.keyCode == 27) parent.restore();
				}
			});
			control.addFocusListener(new FocusAdapter() {@Override
			public void focusLost(FocusEvent evt) {parent.restore();}});
		} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
			control = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
			((Combo) control).add("");			
			for(AISNavigationStatus s : AISNavigationStatus.values()) {
				((Combo) control).add(s.getText());
			}
			((Combo) control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					parent.saveAISValue(aisParameter,  Integer.valueOf(((Combo) control).getSelectionIndex()));
				}
			});
			
		} else if(aisParameter.getType().equals(CDateTime.class)) {
			control = new CDateTime(composite, CDT.TAB_FIELDS | CDT.CLOCK_24_HOUR | CDT.DATE_LONG | CDT.TIME_MEDIUM | CDT.BORDER | CDT.SPINNER);
			((CDateTime) control).setPattern("MMMM d yyyy '@' HH:mm:ss");
			((CDateTime) control).setLocale(new Locale("en", "US"));
			
			((CDateTime) control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent evt) {
					parent.saveAISValue(aisParameter, Long.valueOf(((CDateTime) control).getSelection().getTime()));
				}
			});
		} else {
			control = new Text(composite, SWT.BORDER);
			control.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent evt) {
					//System.out.println("Key Pressed " + evt.keyCode);
					if(evt.keyCode == 13) parent.saveAISValue(aisParameter, ((Text) control).getText());
					if(evt.keyCode == 27) parent.restore();
				}
			});
			control.addFocusListener(new FocusAdapter() {@Override
			public void focusLost(FocusEvent evt) {parent.restore();}});
		}

		GridData LData = new GridData();
		LData.grabExcessHorizontalSpace = true;
		LData.horizontalAlignment = GridData.FILL;
		control.setLayoutData(LData);
	}

	public Label getLabel() {
		return label;
	}

	public Control getControl() {
		return control;
	}
	
	public void setValue(Object value) {
		if(aisParameter.getType().equals(String.class)) {
			if(value != null) ((Text) control).setText((String)value); else ((Text) control).setText("");
		} else if(aisParameter.getType().equals(Boolean.class)) {
			if(value != null) ((Combo) control).select(((Boolean)value)?0:1); else ((Combo) control).select(1);
		} else if(aisParameter.getType().equals(Double.class)) {
			if(value != null) ((Text) control).setText(((Double)value).toString()); else ((Text) control).setText("");
		} else if(aisParameter.getType().equals(Integer.class)) {
			//System.out.println("setValue Integer!");
			if(value != null) ((Text) control).setText(((Integer)value).toString()); else ((Text) control).setText("");
			//System.out.println("setValue Integer 2!");
		} else if(aisParameter.getType().equals(AISNavigationStatus.class)) {
			if(value != null) ((Combo) control).select(((Integer)value).intValue()); else ((Combo) control).select(0);
		} else if(aisParameter.getType().equals(CDateTime.class)) {
			if(value != null) ((CDateTime) control).setSelection(new Date(((Long)value).longValue())); else ((CDateTime) control).setSelection(null);
		} else {
			
		}
	}
}
