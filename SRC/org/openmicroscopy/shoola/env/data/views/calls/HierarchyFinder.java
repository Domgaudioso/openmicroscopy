/*
 * org.openmicroscopy.shoola.env.data.views.calls.HierarchyFinder
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.env.data.views.calls;


//Java imports
import java.util.Set;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.data.OmeroPojoService;
import org.openmicroscopy.shoola.env.data.views.BatchCall;
import org.openmicroscopy.shoola.env.data.views.BatchCallTree;
import pojos.CategoryGroupData;
import pojos.ExperimenterData;
import pojos.GroupData;
import pojos.ProjectData;

/** 
 * Command to find the data trees of a given <i>OME</i> hierarchy type 
 * containing some given images.
 * <p>The hierarchy that will be searched can be either Project/Dataset/Image
 * (P/D/I) or Category Group/Category/Image (CG/C/I).  All root nodes in the
 * specified hierarchy will be loaded that have at least one of the given 
 * images among their leaves.  A node <code>n</code> is retrieved <i>only</i>
 * if there's a path among the root node and one of the specified images that
 * contains <code>n</code>.</p>
 * <p>The object returned in the <code>DSCallOutcomeEvent</code> will be a
 * <code>Set</code> with all root nodes that were found.  Every root node is
 * linked to the found objects and so on until the leaf nodes, which are the
 * <i>passed in</i> <code>ImageData</code>s.</p>
 * <p>The type of the returned objects are <code>ProjectData, 
 * DatasetData, ImageData</code> in the case of a P/D/I hierarchy,
 * as <code>CategoryGroupData, CategoryData, ImageData</code> for a CG/C/I
 * hierarchy.</p>
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class HierarchyFinder
    extends BatchCallTree
{
    
    /** The root nodes of the found trees. */
    private Set         rootNodes;
    
    /** Searches the specified hierarchy. */
    private BatchCall   findCall;
    
    /**
     * Checks if the specified level is supported.
     * 
     * @param level The level to control.
     */
    private void checkRootLevel(Class level)
    {
        if (level.equals(ExperimenterData.class) ||
                level.equals(GroupData.class)) return;
        throw new IllegalArgumentException("Root level not supported");
    }
    
    /**
     * Creates a {@link BatchCall} to search the P/D/I, CG/C/I hierarchy.
     * 
     * @param hierarchyRootNodeType The type of the root node.
     * @param ids					A set of IDs of the top-most containers.
     * @param rootLevel             The level of the hierarchy either 
     *                              <code>GroupData</code> or 
     *                              <code>ExperimenterData</code>.
     * @param rootLevelID           The Id of the root.
     * @return The {@link BatchCall}.
     */
    private BatchCall makeCHBatchCall(final Class hierarchyRootNodeType, 
            						final Set ids, final Class rootLevel,
                                    final int rootLevelID)
    {
        return new BatchCall("Searching Container hierarchy") {
            public void doCall() throws Exception
            {
                OmeroPojoService os = context.getOmeroService();
                rootNodes = os.findContainerHierarchy(hierarchyRootNodeType,
                               ids, rootLevel, rootLevelID);
            }
        };
    }
    
    /**
     * Adds the {@link #findCall} to the computation tree.
     * 
     * @see BatchCallTree#buildTree()
     */
    protected void buildTree() { add(findCall); }
    
    /**
     * Returns the root node of the found trees.
     * 
     * @see BatchCallTree#getResult()
     */
    protected Object getResult() { return rootNodes; }
    
    /**
     * Creates a new instance to search the specified hierarchy for trees
     * containing the specified images.
     * If bad arguments are passed, we throw a runtime exception so to fail
     * early and in the caller's thread.
     * 
     * @param hierarchyRootNodeType The type of the root node in the hierarchy
     *                      to search.  Can be one out of: 
     *                      {@link ProjectData} or {@link CategoryGroupData}.
     * @param ids           Contains ids, one for each leaf image node.
     * @param rootLevel     The level of the hierarchy either 
     *                      <code>GroupData</code> or 
     *                      <code>ExperimenterData</code>.
     * @param rootLevelID   The Id of the root.
     */
    public HierarchyFinder(Class hierarchyRootNodeType, Set ids, 
                            Class rootLevel, int rootLevelID)
    {
        if (ids == null) throw new IllegalArgumentException("No images.");
        if (hierarchyRootNodeType == null) 
            throw new NullPointerException("No root node type.");
        try {
            ids.toArray(new Integer[] {});
        } catch (ArrayStoreException ase) {
            throw new IllegalArgumentException(
                    "images can only contain Integer objects.");
        } 
        checkRootLevel(rootLevel);
        if (rootLevelID < 0) 
            throw new IllegalArgumentException("Root level ID not valid.");
        if (hierarchyRootNodeType.equals(ProjectData.class) ||
            hierarchyRootNodeType.equals(CategoryGroupData.class))
            findCall = makeCHBatchCall(hierarchyRootNodeType, ids, rootLevel,
                                        rootLevelID);
        else
            throw new IllegalArgumentException("Unsupported type: "+
                                                hierarchyRootNodeType+".");
    }
    
}
