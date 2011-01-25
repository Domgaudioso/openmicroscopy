/*
 * org.openmicroscopy.shoola.agents.treeviewer.ProjectsLoader 
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
package org.openmicroscopy.shoola.agents.treeviewer;



//Java imports
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.treeviewer.view.TreeViewer;
import org.openmicroscopy.shoola.agents.util.browser.TreeImageDisplay;
import org.openmicroscopy.shoola.env.data.views.CallHandle;
import pojos.ProjectData;

/** 
 * Loads the datasets/images contained in the project hosted by the passed 
 * node. This class calls the <code>loadHierarchy</code> method in the
 * <code>HierarchyBrowsingView</code>. 
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
public class ProjectsLoader 
	extends DataTreeViewerLoader
{

	/** Handle to the asynchronous call so that we can cancel it. */
    private CallHandle  		handle;
    
    /** Reference to the node hosting the project to browse. */
    private TreeImageDisplay 	node;
    
    /** The user's identifier. */
    private long 				userID;
    
    /** The group's identifier. */
    private long 				groupID;
    
    /**
     * Creates a new instance.
     * 
     * @param viewer The viewer this loader is for. 
     *               Mustn't be <code>null</code>.
     * @param node   The node hosting the project to browse.
     *               Mustn't be <code>null</code>.
     * @param userID The user's identifier.
     * @param groupID The group's identifier.            
     */
    public ProjectsLoader(TreeViewer viewer, TreeImageDisplay node, long userID,
    		long groupID)
	{
		super(viewer);
		if (node == null)
			throw new IllegalArgumentException("No node of reference.");
		this.node = node;
		this.userID = userID;
		this.groupID = groupID;
	}
	
	 /**
     * Retrieves the data.
     * @see DataTreeViewerLoader#load()
     */
    public void load()
    {
    	long userID = TreeViewerAgent.getUserDetails().getId();
    	long id = node.getUserObjectId();
    	List<Long> ids = new ArrayList<Long>();
    	ids.add(id);
    	handle = hiBrwView.loadHierarchy(ProjectData.class, ids, userID, groupID,
    			this);
    }

    /**
     * Cancels the data loading.
     * @see DataTreeViewerLoader#cancel()
     */
    public void cancel() { handle.cancel(); }

    /**
     * Feeds the result back to the viewer.
     * @see DataTreeViewerLoader#handleResult(Object)
     */
    public void handleResult(Object result)
    {
        if (viewer.getState() == TreeViewer.DISCARDED) return;  //Async cancel.
        viewer.browseHierarchyRoots(node, (Set) result);
    }
    
}