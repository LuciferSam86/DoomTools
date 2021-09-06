/*******************************************************************************
 * Copyright (c) 2020-2021 Matt Tropiano
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.tools.decohack.data;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.mtrop.doom.tools.decohack.data.enums.DEHFeatureLevel;

/**
 * Describes all DeHackEd objects and how to write them.
 * @author Matthew Tropiano
 * @param <SELF> this object's class.
 */
public interface DEHObject<SELF>
{
	/**
	 * Copies this object's values.
	 * @param source the source object.
	 * @param level the highest feature level to copy over.
	 * @return this object.
	 */
	SELF copyFrom(SELF source);
	
	/**
	 * Writes this object to a DeHackEd file stream.
	 * @param writer the writer to write to.
	 * @param original the original object to compare to for writing changed fields.
	 * @param level the highest feature level to export for.
	 * @throws IOException if a write error occurs.
	 */
	void writeObject(Writer writer, SELF original, DEHFeatureLevel level) throws IOException;

	/**
	 * Dumps this object's field names to a contiguous name list.
	 * @param fieldNameList the field name list.
	 * @param level the highest feature level to export for.
	 */
	void dumpObjectFieldNames(List<String> fieldNameList, DEHFeatureLevel level);
	
	/**
	 * Dumps this object's field values to a contiguous value list. 
	 * Should correspond to names directly.
	 * @param fieldValueList the destination list.
	 * @param level the highest feature level to export for.
	 * @see #dumpObjectFieldNames(List)
	 */
	void dumpObjectFieldValues(List<Object> fieldValueList, DEHFeatureLevel level);
	
}
