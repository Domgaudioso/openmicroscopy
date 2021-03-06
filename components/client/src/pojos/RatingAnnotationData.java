/*
 * pojos.RatingAnnotationData
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
package pojos;



//Java imports

//Third-party libraries

//Application-internal dependencies
import ome.model.annotations.LongAnnotation;

/** 
 * Annotation used to rate an object. The five starts approach is 
 * selected.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class RatingAnnotationData 
	extends AnnotationData
{

	/** 
	 * The name space used to identify the archived annotation
	 * linked to a set of pixels.
	 */
	public static final String INSIGHT_RATING_NS = 
					"openmicroscopy.org/omero/insight/rating";
	
	/** Indicates the object is not rated. */
	public static final int LEVEL_ZERO = 0;
	
	/** Indicates the object is rated with one star. */
	public static final int LEVEL_ONE = 1;
	
	/** Indicates the object is rated with two stars. */
	public static final int LEVEL_TWO = 2;
	
	/** Indicates the object is rated with three stars. */
	public static final int LEVEL_THREE = 3;
	
	/** Indicates the object is rated with four stars. */
	public static final int LEVEL_FOUR = 4;
	
	/** Indicates the object is rated with five stars. */
	public static final int LEVEL_FIVE = 5;
	
	/**
	 * Checks if the passed value is one of the rating contants defined by 
	 * this class.
	 * 
	 * @param v The value to handle.
	 */
	private void checkValue(int v)
	{
		switch (v) {
			case LEVEL_ZERO:
			case LEVEL_ONE:
			case LEVEL_TWO:
			case LEVEL_THREE:
			case LEVEL_FOUR:
			case LEVEL_FIVE:
				break;
			default:
				throw new IllegalArgumentException("Rating value not supported.");
		}
	}
	
	/** Creates a new instance of value <code>LEVEL_ZERO</code>. */
	public RatingAnnotationData()
	{
		this(LEVEL_ZERO);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param value The rating value. One of the contants defined by 
	 * 				this class.
	 */
	public RatingAnnotationData(int value)
	{
		super(LongAnnotation.class);
		setRating(value);
		setNameSpace(INSIGHT_RATING_NS);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param annotation 	The {@link LongAnnotation} object corresponding to 
	 * 						this <code>DataObject</code>. 
     *            			Mustn't be <code>null</code>.
	 */
	public RatingAnnotationData(LongAnnotation annotation)
	{
		super(annotation);
		//check if it is correct.
		checkValue(getRating());
		setNameSpace(INSIGHT_RATING_NS);
	}
	
	/**
	 * Returns the rating value.
	 * 
	 * @return See above.
	 */
	public int getRating() 
	{ 
		return ((Long) getContent()).intValue();
	}
	
	/**
	 * Sets the rating value.
	 * 
	 * @param value The value to set. Must be One of the contants defined by 
	 * 				this class.
	 */
	public void setRating(int value)
	{
		checkValue(value);
		Long l = new Long(value);
		((LongAnnotation) asAnnotation()).setLongValue(l);
	}
	
	/**
	 * Returns the rating value.
	 * @see AnnotationData#getContent()
	 */
	public Object getContent()
	{
		return ((LongAnnotation) asAnnotation()).getLongValue();
	}

	/**
	 * Returns the rating value as a string.
	 * @see AnnotationData#getContentAsString()
	 */
	public String getContentAsString() { return ""+getRating(); }

	/**
	 * Sets the text annotation.
	 * @see AnnotationData#setContent(Object)
	 */
	public void setContent(Object content)
	{
		if (content == null)
			setRating(LEVEL_ZERO);
		else if (content instanceof Number) {
			Number n = (Number) content;
			setRating(n.intValue());
		} else
			throw new IllegalArgumentException("Value not supported.");
	}

}
