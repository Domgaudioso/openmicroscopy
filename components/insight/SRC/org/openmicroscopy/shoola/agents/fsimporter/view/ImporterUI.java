/*
 * org.openmicroscopy.shoola.agents.fsimporter.view.ImporterUI 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.fsimporter.view;


//Java imports
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.fsimporter.IconManager;
import org.openmicroscopy.shoola.agents.fsimporter.ImporterAgent;
import org.openmicroscopy.shoola.agents.fsimporter.util.FileImportComponent;
import org.openmicroscopy.shoola.env.data.model.ImportableObject;
import org.openmicroscopy.shoola.env.ui.TaskBar;
import org.openmicroscopy.shoola.env.ui.TopWindow;
import org.openmicroscopy.shoola.util.ui.ClosableTabbedPane;
import org.openmicroscopy.shoola.util.ui.TitlePanel;
import org.openmicroscopy.shoola.util.ui.UIUtilities;

/** 
 * The {@link Importer}'s View. Displays the on-going import and the finished
 * ones.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
class ImporterUI 
	extends TopWindow
{

	/** The window's title. */
	private static final String TITLE = "Imports";
	
	/** Reference to the model. */
	private ImporterModel	model;
	
	/** Reference to the control. */
	private ImporterControl	controller;
	
	/** The total of imports. */
	private int total;
	
	/** The component displaying the various imports. */
	private ClosableTabbedPane tabs;
	
	/** Flag used to indicate that the component has been displayed or not. */
	private boolean initialized;
	
	/** The identifier of the UI element. */
	private int uiElementID;
	
	/** Keeps track of the imports. */
	private Map<Integer, ImporterUIElement> uiElements;
	
	/**
	 * Builds and lays out the controls.
	 * 
	 * @return See above.
	 */
	private JPanel buildControls()
	{
		JPanel p = new JPanel();
		p.add(new JButton(controller.getAction(ImporterControl.CLOSE_BUTTON)));
		p.add(Box.createHorizontalStrut(5));
		p.add(new JButton(controller.getAction(ImporterControl.SEND_BUTTON)));
		return UIUtilities.buildComponentPanelRight(p);
	}
	
	/** Builds and lays out the UI. */
	private void buildGUI()
	{
		Container container = getContentPane();
		container.setLayout(new BorderLayout(0, 0));
		IconManager icons = IconManager.getInstance();
		TitlePanel tp = new TitlePanel(TITLE, "", "Displays " +
				"the ongoing and done imports.", 
				icons.getIcon(IconManager.IMPORT_48));
		container.add(tp, BorderLayout.NORTH);
		container.add(tabs, BorderLayout.CENTER);
		container.add(buildControls(), BorderLayout.SOUTH);
	}
	
	/** Displays the window. */
	private void display()
	{
		if (initialized) return;
		initialized = true;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(8*(screenSize.width/10), 8*(screenSize.height/10));
	}
	
	/** Initializes the components. */
	private void initComponents()
	{
		uiElementID = 0;
		uiElements = new LinkedHashMap<Integer, ImporterUIElement>();
		tabs = new ClosableTabbedPane(JTabbedPane.TOP, 
				JTabbedPane.WRAP_TAB_LAYOUT);
		tabs.setAlignmentX(LEFT_ALIGNMENT);
		tabs.addPropertyChangeListener(controller);
	}
	
	/**
     * Creates the menu bar.
     * 
     * @return The menu bar.
     */
    private JMenuBar createMenuBar()
    {
    	TaskBar tb = ImporterAgent.getRegistry().getTaskBar();
    	JMenuBar bar = tb.getTaskBarMenuBar();
    	
    	return bar;
    }
    
	/** Creates a new instance. */
	ImporterUI()
	{
		super(TITLE);
	}

	/**
     * Links this View to its Controller.
     * 
     * @param model 		The Model.
     * @param controller	The Controller.
     */
	void initialize(ImporterModel model, ImporterControl controller)
	{
		this.model = model;
		this.controller = controller;
		total = 0;
		initComponents();
		setJMenuBar(createMenuBar());
		buildGUI();
	}

	/**
	 * Returns the collection of files that could not be imported.
	 * 
	 * @return See above.
	 */
	List<FileImportComponent> getMarkedFiles()
	{
		Component[] comps = tabs.getComponents();
		if (comps == null || comps.length == 0) return null;
		List<FileImportComponent> list = new ArrayList<FileImportComponent>();
		List<FileImportComponent> l;
		ImporterUIElement element;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i] instanceof ImporterUIElement) {
				element = (ImporterUIElement) comps[i];
				l = element.getMarkedFiles();
				if (l != null && l.size() > 0)
					list.addAll(l);
			}
		}
		return list;
	}
	
	/**
	 * Adds the component to the display.
	 * 
	 * @param object The component to add.
	 */
	ImporterUIElement addImporterElement(ImportableObject object)
	{
		if (object == null) return null;
		int n = tabs.getComponentCount();
		String title = "Import #"+(total+1);
		ImporterUIElement element = new ImporterUIElement(controller,
				uiElementID, n, title, object);
		IconManager icons = IconManager.getInstance();
		tabs.insertTab(title, icons.getIcon(IconManager.IMPORT), 
				element, "", total);
		total++;
		uiElements.put(uiElementID, element);
		uiElementID++;
		if (!isVisible()) {
			setVisible(true);
			display();
		}
		return element;
	}
	
	/**
	 * Returns the UI element corresponding to the passed index.
	 * 
	 * @param index The identifier of the component
	 * @return See above.
	 */
	ImporterUIElement getUIElement(int index)
	{
		return uiElements.get(index);
	}

	/**
	 * Returns the first element with data to import
	 * @return See above.
	 */
	ImporterUIElement getElementToStartImportFor()
	{
		if (uiElements.size() == 0) return null;
		Iterator<ImporterUIElement> i = uiElements.values().iterator();
		ImporterUIElement element;
		while (i.hasNext()) {
			element = i.next();
			if (!element.isDone())
				return element;
		}
		return null;
	}
	
	/**
	 * Removes the specified element. Returns the element or <code>null</code>.
	 * 
	 * @param index The index of the import view.
	 * @return See above.
	 */
	ImporterUIElement removeImportElement(int index)
	{
		Iterator<ImporterUIElement> i = uiElements.values().iterator();
		ImporterUIElement element;
		ImporterUIElement found = null;
		while (i.hasNext()) {
			element = i.next();
			if (element.getIndex() == index) {
				found = element;
				break;
			}
		}
		if (found != null) uiElements.remove(found);
		return found;
	}
	
	/**
	 * Returns <code>true</code> if errors to send, <code>false</code>
	 * otherwise.
	 * 
	 * @return See above.
	 */
	boolean hasFailuresToSend()
	{
		Iterator<ImporterUIElement> i = uiElements.values().iterator();
		while (i.hasNext()) {
			if (i.next().hasFailuresToSend())
				return true;
		}
		return false;
	}
	
}