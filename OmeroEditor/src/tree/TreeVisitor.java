
/*
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
 *	author Will Moore will@lifesci.dundee.ac.uk
 */

package tree;

import java.util.Iterator;


public class TreeVisitor {
	
	IDataFieldVisitor dataFieldVistor;
	
	public TreeVisitor(IDataFieldVisitor dataFieldVistor) {
		this.dataFieldVistor = dataFieldVistor;
	}
	
	public void visitTree(DataFieldNode rootNode) {
		
		Iterator <DataFieldNode>iterator = rootNode.iterator();

		while (iterator.hasNext()) {
			DataFieldNode node = iterator.next();
			DataField field = node.getDataField();
			
			dataFieldVistor.visit(field);
		}
	}

}
