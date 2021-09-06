/*******************************************************************************
 * Copyright (c) 2020-2021 Matt Tropiano
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.tools.decohack.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import net.mtrop.doom.tools.decohack.data.enums.DEHFeatureLevel;
import net.mtrop.doom.util.RangeUtils;

/**
 * A single state.
 * @author Matthew Tropiano
 */
public class DEHState implements DEHObject<DEHState>
{
	private int spriteIndex;
	private int frameIndex; // 28 max
	private boolean bright;
	private int nextStateIndex;
	private int duration;
	private int misc1;
	private int misc2;
	private int[] args;
	private int mbf21Flags;
	
	/**
	 * Creates a new state.
	 */
	public DEHState()
	{
		set(
			0,
			0, 
			false,
			0, 
			-1,
			0,
			0,
			new int[0],
			0x00
		);
	}
	
	public static DEHState create(int spriteIndex, int frameIndex, boolean bright, int nextStateIndex, int duration)
	{
		return create(spriteIndex, frameIndex, bright, nextStateIndex, duration, 0, 0, new int[0], 0);
	}

	public static DEHState create(int spriteIndex, int frameIndex, boolean bright, int nextStateIndex, int duration, int mbfFlags)
	{
		return create(spriteIndex, frameIndex, bright, nextStateIndex, duration, 0, 0, new int[0], mbfFlags);
	}

	public static DEHState create(int spriteIndex, int frameIndex, boolean bright, int nextStateIndex, int duration, int misc1, int misc2, int[] args, int mbfFlags)
	{
		return (new DEHState()).set(
			spriteIndex,
			frameIndex, 
			bright,
			nextStateIndex, 
			duration,
			misc1,
			misc2,
			args,
			mbfFlags
		);
	}

	@Override
	public DEHState copyFrom(DEHState source) 
	{
		setSpriteIndex(source.spriteIndex);
		setFrameIndex(source.frameIndex);
		setBright(source.bright);
		setNextStateIndex(source.nextStateIndex);
		setDuration(source.duration);
		setMisc1(source.misc1);
		setMisc2(source.misc2);
		setArgs(source.args);
		setMBF21Flags(source.mbf21Flags);
		return this;
	}
	
	public DEHState set(int spriteIndex, int frameIndex, boolean bright, int nextStateIndex, int duration)
	{
		return set(spriteIndex, frameIndex, bright, nextStateIndex, duration, 0, 0, new int[0], 0);
	}
	
	public DEHState set(int spriteIndex, int frameIndex, boolean bright, int nextStateIndex, int duration, int misc1, int misc2, int[] args, int mbfFlags)
	{
		setSpriteIndex(spriteIndex);
		setFrameIndex(frameIndex);
		setBright(bright);
		setNextStateIndex(nextStateIndex);
		setDuration(duration);
		setMisc1(misc1);
		setMisc2(misc2);
		setArgs(args);
		setMBF21Flags(mbfFlags);
		return this;
	}
	
	public int getSpriteIndex()
	{
		return spriteIndex;
	}
	
	public DEHState setSpriteIndex(int spriteIndex)
	{
		RangeUtils.checkRange("Sprite index", 0, Integer.MAX_VALUE, spriteIndex);
		this.spriteIndex = spriteIndex;
		return this;
	}
	
	public int getFrameIndex()
	{
		return frameIndex;
	}
	
	public DEHState setFrameIndex(int frameIndex)
	{
		RangeUtils.checkRange("Sprite frame index", 0, 28, frameIndex);
		this.frameIndex = frameIndex;
		return this;
	}
	
	public boolean isBright()
	{
		return bright;
	}
	
	public DEHState setBright(boolean bright) 
	{
		this.bright = bright;
		return this;
	}
	
	public int getNextStateIndex()
	{
		return nextStateIndex;
	}
	
	public DEHState setNextStateIndex(int nextStateIndex)
	{
		RangeUtils.checkRange("Next state index", 0, Integer.MAX_VALUE, nextStateIndex);
		this.nextStateIndex = nextStateIndex;
		return this;
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public DEHState setDuration(int duration) 
	{
		RangeUtils.checkRange("Duration", -1, 9999, duration);
		this.duration = duration;
		return this;
	}
	
	public int getMisc1() 
	{
		return misc1;
	}
	
	public DEHState setMisc1(int misc1) 
	{
		this.misc1 = misc1;
		return this;
	}
	
	public int getMisc2()
	{
		return misc2;
	}
	
	public DEHState setMisc2(int misc2)
	{
		this.misc2 = misc2;
		return this;
	}
	
	public int[] getArgs()
	{
		return args;
	}
	
	public DEHState setArgs(int[] args)
	{
		this.args = args;
		return this;
	}

	public DEHState setArgs(List<Integer> arglist)
	{
		// gotta do this manually, 'cause unboxing, yuck :P
		this.args = new int[arglist.size()];
		int i = 0;
		for (Integer arg : arglist)
		{
			this.args[i] = arg;
			i++;
		}
		return this;
	}
	
	public int getMBF21Flags() 
	{
		return mbf21Flags;
	}
	
	public DEHState setMBF21Flags(int flags) 
	{
		this.mbf21Flags = flags;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof DEHState)
			return equals((DEHState)obj);
		return super.equals(obj);
	}
	
	public boolean equals(DEHState obj) 
	{
		return spriteIndex == obj.spriteIndex
			&& frameIndex == obj.frameIndex
			&& bright == obj.bright
			&& nextStateIndex == obj.nextStateIndex
			&& duration == obj.duration
			&& misc1 == obj.misc1
			&& misc2 == obj.misc2
			&& Arrays.equals(args, obj.args)
			&& mbf21Flags == obj.mbf21Flags
		;
	}	
		
	@Override
	public void writeObject(Writer writer, DEHState state, DEHFeatureLevel level) throws IOException
	{
		if (getSpriteIndex() != state.getSpriteIndex())
			writer.append("Sprite number = ").append(String.valueOf(spriteIndex)).append("\r\n");
		if (getFrameIndex() != state.getFrameIndex() || isBright() != state.isBright())
			writer.append("Sprite subnumber = ").append(String.valueOf(frameIndex | (bright ? 0x08000 : 0x00000))).append("\r\n");
		if (getNextStateIndex() != state.getNextStateIndex())
			writer.append("Next frame = ").append(String.valueOf(nextStateIndex)).append("\r\n");
		if (getDuration() != state.getDuration())
			writer.append("Duration = ").append(String.valueOf(duration)).append("\r\n");
		if (getMisc1() != state.getMisc1())
			writer.append("Unknown 1 = ").append(String.valueOf(misc1)).append("\r\n");
		if (getMisc2() != state.getMisc2())
			writer.append("Unknown 2 = ").append(String.valueOf(misc2)).append("\r\n");
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			int[] args = getArgs();
			for (int i = 0; i < args.length; i++)
				if (i >= state.getArgs().length || args[i] != state.getArgs()[i])
					writer.append("Args").append(String.valueOf(i+1)).append(" = ").append(String.valueOf(args[i])).append("\r\n");
			if (getMBF21Flags() != state.getMBF21Flags())
				writer.append("MBF21 Bits = ").append(String.valueOf(mbf21Flags)).append("\r\n");
		}
		writer.flush();
	}

	@Override
	public void dumpObjectFieldNames(List<String> fieldNameList, DEHFeatureLevel level) 
	{
		fieldNameList.add("Sprite Index");
		fieldNameList.add("Frame Index");
		fieldNameList.add("Next State");
		fieldNameList.add("Duration");
		fieldNameList.add("Bright");
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			fieldNameList.add("MBF21 Flags");
		}
		fieldNameList.add("Misc1");
		fieldNameList.add("Misc2");
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			fieldNameList.add("Arg0");
			fieldNameList.add("Arg1");
			fieldNameList.add("Arg2");
			fieldNameList.add("Arg3");
			fieldNameList.add("Arg4");
			fieldNameList.add("Arg5");
			fieldNameList.add("Arg6");
		}
	}
	
	@Override
	public void dumpObjectFieldValues(List<Object> fieldValueList, DEHFeatureLevel level) 
	{
		fieldValueList.add(getSpriteIndex());
		fieldValueList.add(getFrameIndex());
		fieldValueList.add(getNextStateIndex());
		fieldValueList.add(getDuration());
		fieldValueList.add(isBright());
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			fieldValueList.add(getMBF21Flags());
		}
		fieldValueList.add(getMisc1());
		fieldValueList.add(getMisc2());
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			for (int i = 0; i < getArgs().length; i++)
				fieldValueList.add(getArgs()[i]);
		}
	}
	
}
