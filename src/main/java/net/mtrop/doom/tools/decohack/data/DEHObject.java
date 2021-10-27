/*******************************************************************************
 * Copyright (c) 2020-2021 Matt Tropiano
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.tools.decohack.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.mtrop.doom.tools.decohack.data.enums.DEHFeatureLevel;

/**
 * Describes all DeHackEd objects and how to write them.
 * @author Matthew Tropiano
 * @param <SELF> this object's class.
 */
public abstract class DEHObject<SELF>
{
	/** Key / Value mapping for verbatim user values. */
	private Map<String, Object> userValues;

	protected DEHObject()
	{
		this.userValues = new TreeMap<>();
	}
	
	/**
	 * Clears/resets user values defined on this object.
	 * @return this object.
	 */
	@SuppressWarnings("unchecked")
	public SELF clearUserValues()
	{
		userValues.clear();
		return (SELF)this;
	}

	/**
	 * Copies user values from another object.
	 * @param source the source object.
	 * @return this object.
	 */
	@SuppressWarnings("unchecked")
	public SELF copyUserValuesFrom(DEHObject<SELF> source)
	{
		if (source == this)
			return (SELF)this;
		
		source.clearUserValues();
		for (String key : source.getUserValueKeys())
			setUserValue(key, source.userValues.get(key));
		return (SELF)this;
	}

	/**
	 * Sets an user value on this Thing.
	 * @param key the key.
	 * @param value the value.
	 * @return this thing.
	 */
	@SuppressWarnings("unchecked")
	public SELF setUserValue(String key, Object value)
	{
		userValues.put(key, value);
		return (SELF)this;		
	}

	/**
	 * @return all set user value keys.
	 */
	public String[] getUserValueKeys()
	{
		Set<String> set = userValues.keySet();
		return set.toArray(new String[set.size()]);
	}
	
	/**
	 * Copies this object's values.
	 * @param source the source object.
	 * @param level the highest feature level to copy over.
	 * @return this object.
	 */
	public abstract SELF copyFrom(SELF source);
	
	/**
	 * Writes this object to a DeHackEd file stream.
	 * @param writer the writer to write to.
	 * @param original the original object to compare to for writing changed fields.
	 * @param level the highest feature level to export for.
	 * @throws IOException if a write error occurs.
	 */
	public abstract void writeObject(Writer writer, SELF original, DEHFeatureLevel level) throws IOException;
	
}
