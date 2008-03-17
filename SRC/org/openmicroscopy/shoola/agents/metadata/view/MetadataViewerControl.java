/*
 * org.openmicroscopy.shoola.agents.metadata.view.MetadataViewerControl 
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
package org.openmicroscopy.shoola.agents.metadata.view;



//Java imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.metadata.actions.AddAction;
import org.openmicroscopy.shoola.agents.metadata.actions.BrowseAction;
import org.openmicroscopy.shoola.agents.metadata.actions.MetadataViewerAction;
import org.openmicroscopy.shoola.agents.metadata.actions.RefreshAction;
import org.openmicroscopy.shoola.agents.metadata.actions.RemoveAction;
import org.openmicroscopy.shoola.agents.metadata.actions.RemoveAllAction;

/** 
 * The MetadataViewer's Controller.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
class MetadataViewerControl
{

	/** Identifies the <code>Refresh</code> action. */
	static final Integer		REFRESH = new Integer(0);
	
	/** Identifies the <code>Browse</code> action. */
	static final Integer		BROWSE = new Integer(1);
	
	/** Identifies the <code>Remove</code> action. */
	static final Integer		REMOVE = new Integer(2);
	
	/** Identifies the <code>Remove all</code> action. */
	static final Integer		REMOVE_ALL = new Integer(3);
	
	/** Identifies the <code>Add</code> action. */
	static final Integer		ADD = new Integer(4);
	
	/** 
	 * Reference to the {@link MetadataViewer} component, which, in this
	 * context, is regarded as the Model.
	 */
	private MetadataViewer						model;

	/** Reference to the View. */
	private MetadataViewerUI					view;
	
	/** Maps actions ids onto actual <code>Action</code> object. */
	private Map<Integer, MetadataViewerAction>	actionsMap;
	
	/** Helper method to create all the UI actions. */
	private void createActions()
	{
		actionsMap.put(REFRESH, new RefreshAction(model));
		actionsMap.put(BROWSE, new BrowseAction(model));
		actionsMap.put(ADD, new AddAction(model));
		actionsMap.put(REMOVE, new RemoveAction(model));
		actionsMap.put(REMOVE_ALL, new RemoveAllAction(model));
	}
	
	/**
	 * Creates a new instance.
	 * The
	 * {@link #initialize(MetadataViewer, MetadataViewerUI) initialize} 
	 * method should be called straigh 
	 * after to link this Controller to the other MVC components.
	 */
	MetadataViewerControl() {}
	
	/**
	 * Links this Controller to its Model and its View.
	 * 
	 * @param model  Reference to the {@link ImViewer} component, which, in 
	 *               this context, is regarded as the Model.
	 *               Mustn't be <code>null</code>.
	 * @param view   Reference to the View.  Mustn't be <code>null</code>.
	 */
	void initialize(MetadataViewer model, MetadataViewerUI view)
	{
		if (model == null) throw new NullPointerException("No model.");
		if (view == null) throw new NullPointerException("No view.");
		this.model = model;
		this.view = view;
		actionsMap = new HashMap<Integer, MetadataViewerAction>();
		createActions();
	}
	
	/**
	 * Returns the action corresponding to the specified id.
	 * 
	 * @param id One of the flags defined by this class.
	 * @return The specified action.
	 */
	MetadataViewerAction getAction(Integer id) { return actionsMap.get(id); }

}
