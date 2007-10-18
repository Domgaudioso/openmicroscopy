/*
 * org.openmicroscopy.shoola.util.filter.file.CSVFilter 
 *
  *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.util.filter.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * 
 * Filters the <code>CSV</code> files. 
 * 
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class CSVFilter 
	extends FileFilter
	implements Filter
{

	/** Possible file extension. */
	public static final String  CSV = "csv";

	/**
	 * Returns the default extension of the file.
	 * 
	 * @return See above.
	 */
	public String getExtension() { return CSV; }
	
	/**
	 * 	Overriden to return the description of the filter.
	 * 	@see FileFilter#getDescription()
	 */
	public String getDescription() { return "Comma separated values"; }
    
	/**
	 * 	Overriden to accept file with the declared file extensions.
	 * @see FileFilter#accept(File)
	 */
	public boolean accept(File f)
	{
		if (f.isDirectory()) return true;
		String s = f.getName();
		String extension = null;
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length()-1)
			extension = s.substring(i+1).toLowerCase();
		if (extension != null)
			return (extension.equals(CSV));
		return false;
	}
	
}

