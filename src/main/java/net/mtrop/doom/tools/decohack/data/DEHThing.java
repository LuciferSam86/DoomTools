/*******************************************************************************
 * Copyright (c) 2020-2021 Matt Tropiano
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.tools.decohack.data;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.mtrop.doom.tools.decohack.data.enums.DEHFeatureLevel;
import net.mtrop.doom.tools.decohack.data.enums.DEHThingFlag;
import net.mtrop.doom.util.RangeUtils;

/**
 * A single thing entry.
 * NOTE: All sound positions are 1-BASED. 0 = no sound, [index+1] is the sound.
 * @author Matthew Tropiano
 */
public class DEHThing implements DEHObject<DEHThing>, DEHThingTarget<DEHThing>
{
	private String name;
	
	private int editorNumber;
	
	private int health;
	private int speed; // written as fixed point 16.16 if MISSILE
	private int radius; // written as fixed point 16.16
	private int height; // written as fixed point 16.16
	private int damage;
	private int reactionTime;
	private int painChance;
	private int flags;
	private int mass;

	/** State indices (label name to index). */
	private Map<String, Integer> stateIndexMap;
	
	/** Alert sound position. */
	private int seeSoundPosition;
	/** Attack sound position. */
	private int attackSoundPosition;
	/** Pain sound position. */
	private int painSoundPosition;
	/** Death sound position. */
	private int deathSoundPosition;
	/** Active sound position. */
	private int activeSoundPosition;

	/** Dropped item. */
	private int droppedItem;

	/** MBF21 flags. */
	private int mbf21Flags;
	/** Infighting group. */
	private int infightingGroup;
	/** Projectile group. */
	private int projectileGroup;
	/** Splash group. */
	private int splashGroup;
	/** Nightmare/Fast speed of actor. */
	private int fastSpeed; // written as fixed point 16.16 if MISSILE
	/** Melee range. */
	private int meleeRange;  // written as fixed point 16.16
	/** Ripper sound position. */
	private int ripSoundPosition;

	/**
	 * Creates a new blank thing.
	 */
	public DEHThing()
	{
		this.stateIndexMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		
		setName("");
		clearProperties();
		clearFlags();
		clearSounds();
		clearLabels();
	}

	@Override
	public DEHThing copyFrom(DEHThing source) 
	{
		setName(source.name);
		setEditorNumber(source.editorNumber);
		
		setHealth(source.health);
		setSpeed(source.speed);
		setFastSpeed(source.fastSpeed);
		setRadius(source.radius);
		setHeight(source.height);
		setDamage(source.damage);
		setReactionTime(source.reactionTime);
		setPainChance(source.painChance);
		setFlags(source.flags);
		setMass(source.mass);
		
		setSeeSoundPosition(source.seeSoundPosition);
		setAttackSoundPosition(source.attackSoundPosition);
		setPainSoundPosition(source.painSoundPosition);
		setDeathSoundPosition(source.deathSoundPosition);
		setActiveSoundPosition(source.activeSoundPosition);
		
		setDroppedItem(source.droppedItem);

		setMBF21Flags(source.mbf21Flags);
		setInfightingGroup(source.infightingGroup);
		setProjectileGroup(source.projectileGroup);
		setSplashGroup(source.splashGroup);
		setFastSpeed(source.fastSpeed);
		setMeleeRange(source.meleeRange);
		setRipSoundPosition(source.ripSoundPosition);

		clearLabels();
		for (String label : source.getLabels())
			setLabel(label, source.getLabel(label));

		return this;
	}
	
	@Override
	public DEHThing clearProperties() 
	{
		setEditorNumber(EDITORNUMBER_NONE);
		setHealth(0);
		setSpeed(0);
		setRadius(0);
		setHeight(0);
		setDamage(0);
		setReactionTime(0);
		setPainChance(0);
		setMass(0);
		setDroppedItem(NO_ITEM);
		setInfightingGroup(DEFAULT_GROUP);
		setProjectileGroup(DEFAULT_GROUP);
		setSplashGroup(DEFAULT_GROUP);
		setFastSpeed(DEFAULT_FASTSPEED);
		setMeleeRange(DEFAULT_MELEE_RANGE);
		return this;
	}
	
	@Override
	public DEHThing clearSounds() 
	{
		setSeeSoundPosition(SOUND_NONE);
		setAttackSoundPosition(SOUND_NONE);
		setPainSoundPosition(SOUND_NONE);
		setDeathSoundPosition(SOUND_NONE);
		setActiveSoundPosition(SOUND_NONE);
		setRipSoundPosition(SOUND_NONE);
		return this;
	}

	@Override
	public DEHThing clearFlags() 
	{
		setFlags(0x00000000);
		setMBF21Flags(0x00000000);
		return this;
	}

	public String getName() 
	{
		return name;
	}
	
	public DEHThing setName(String name) 
	{
		this.name = name;
		return this;
	}
	
	public int getEditorNumber() 
	{
		return editorNumber;
	}

	public DEHThing setEditorNumber(int editorNumber)
	{
		if (editorNumber == 0)
			throw new IllegalArgumentException("Editor number can not be 0.");
		RangeUtils.checkRange("Editor number", -1, 0x10000, editorNumber);
		this.editorNumber = editorNumber;
		return this;
	}

	public int getHealth() 
	{
		return health;
	}

	public DEHThing setHealth(int health)
	{
		RangeUtils.checkRange("Health", 0, 999999, health);
		this.health = health;
		return this;
	}

	public int getSpeed()
	{
		return speed;
	}

	public DEHThing setSpeed(int speed) 
	{
		RangeUtils.checkRange("Speed", -32768, 32767, speed);
		this.speed = speed;
		return this;
	}

	public int getFastSpeed() 
	{
		return fastSpeed;
	}

	public DEHThing setFastSpeed(int fastSpeed) 
	{
		RangeUtils.checkRange("Fast speed", -1, 65535, fastSpeed);
		this.fastSpeed = fastSpeed;
		return this;
	}

	public int getRadius() 
	{
		return radius;
	}

	public DEHThing setRadius(int radius) 
	{
		RangeUtils.checkRange("Radius", 0, 65535, radius);
		this.radius = radius;
		return this;
	}

	public int getHeight() 
	{
		return height;
	}

	public DEHThing setHeight(int height) 
	{
		RangeUtils.checkRange("Height", 0, 65535, height);
		this.height = height;
		return this;
	}

	public int getDamage() 
	{
		return damage;
	}

	public DEHThing setDamage(int damage) 
	{
		RangeUtils.checkRange("Damage", -999999, 999999, damage);
		this.damage = damage;
		return this;
	}

	public int getReactionTime()
	{
		return reactionTime;
	}

	public DEHThing setReactionTime(int reactionTime)
	{
		RangeUtils.checkRange("Reaction time", 0, Integer.MAX_VALUE, reactionTime);
		this.reactionTime = reactionTime;
		return this;
	}

	public int getPainChance()
	{
		return painChance;
	}

	public DEHThing setPainChance(int painChance)
	{
		RangeUtils.checkRange("Pain chance", 0, Integer.MAX_VALUE, painChance);
		this.painChance = painChance;
		return this;
	}

	public int getFlags() 
	{
		return flags;
	}

	public DEHThing setFlags(int bits) 
	{
		this.flags = bits;
		return this;
	}

	@Override
	public DEHThing addFlag(int bits)
	{
		this.flags |= bits;
		return this;
	}

	@Override
	public DEHThing removeFlag(int bits)
	{
		this.flags &= ~bits;
		return this;
	}

	public int getMBF21Flags() 
	{
		return mbf21Flags;
	}

	public DEHThing setMBF21Flags(int bits) 
	{
		this.mbf21Flags = bits;
		return this;
	}

	@Override
	public DEHThing addMBF21Flag(int bits)
	{
		this.mbf21Flags |= bits;
		return this;
	}

	@Override
	public DEHThing removeMBF21Flag(int bits)
	{
		this.mbf21Flags &= ~bits;
		return this;
	}

	public int getMass() 
	{
		return mass;
	}

	public DEHThing setMass(int mass) 
	{
		RangeUtils.checkRange("Mass", 0, Integer.MAX_VALUE, mass);
		this.mass = mass;
		return this;
	}

	public int getMeleeRange() 
	{
		return meleeRange;
	}

	public DEHThing setMeleeRange(int meleeRange) 
	{
		RangeUtils.checkRange("Melee range", 0, 65535, meleeRange);
		this.meleeRange = meleeRange;
		return this;
	}

	public int getInfightingGroup() 
	{
		return infightingGroup;
	}
	
	public DEHThing setInfightingGroup(int infightingGroup) 
	{
		RangeUtils.checkRange("Infighting group", 0, Integer.MAX_VALUE, infightingGroup);
		this.infightingGroup = infightingGroup;
		return this;
	}
	
	public int getProjectileGroup() 
	{
		return projectileGroup;
	}
	
	public DEHThing setProjectileGroup(int projectileGroup) 
	{
		RangeUtils.checkRange("Projectile group", -1, Integer.MAX_VALUE, projectileGroup);
		this.projectileGroup = projectileGroup;
		return this;
	}
	
	public int getSplashGroup()
	{
		return splashGroup;
	}
	
	public DEHThing setSplashGroup(int splashGroup) 
	{
		RangeUtils.checkRange("Splash group", 0, Integer.MAX_VALUE, splashGroup);
		this.splashGroup = splashGroup;
		return this;
	}
	
	public int getDroppedItem() 
	{
		return droppedItem;
	}
	
	public DEHThing setDroppedItem(int droppedItem) 
	{
		RangeUtils.checkRange("Dropped item", 0, Integer.MAX_VALUE, droppedItem);
		this.droppedItem = droppedItem;
		return this;
	}
	
	public int getSpawnFrameIndex()
	{
		return getLabel(STATE_LABEL_SPAWN);
	}

	public DEHThing setSpawnFrameIndex(int spawnFrameIndex)
	{
		RangeUtils.checkRange("Spawn frame index", 0, Integer.MAX_VALUE, spawnFrameIndex);
		setLabel(STATE_LABEL_SPAWN, spawnFrameIndex);
		return this;
	}

	public int getWalkFrameIndex() 
	{
		return getLabel(STATE_LABEL_SEE);
	}

	public DEHThing setWalkFrameIndex(int walkFrameIndex) 
	{
		RangeUtils.checkRange("Walk frame index", 0, Integer.MAX_VALUE, walkFrameIndex);
		setLabel(STATE_LABEL_SEE, walkFrameIndex);
		return this;
	}

	public int getPainFrameIndex()
	{
		return getLabel(STATE_LABEL_PAIN);
	}

	public DEHThing setPainFrameIndex(int painFrameIndex) 
	{
		RangeUtils.checkRange("Pain frame index", 0, Integer.MAX_VALUE, painFrameIndex);
		setLabel(STATE_LABEL_PAIN, painFrameIndex);
		return this;
	}

	public int getMeleeFrameIndex() 
	{
		return getLabel(STATE_LABEL_MELEE);
	}

	public DEHThing setMeleeFrameIndex(int meleeFrameIndex)
	{
		RangeUtils.checkRange("Melee frame index", 0, Integer.MAX_VALUE, meleeFrameIndex);
		setLabel(STATE_LABEL_MELEE, meleeFrameIndex);
		return this;
	}

	public int getMissileFrameIndex() 
	{
		return getLabel(STATE_LABEL_MISSILE);
	}

	public DEHThing setMissileFrameIndex(int missileFrameIndex) 
	{
		RangeUtils.checkRange("Attack frame index", 0, Integer.MAX_VALUE, missileFrameIndex);
		setLabel(STATE_LABEL_MISSILE, missileFrameIndex);
		return this;
	}

	public int getDeathFrameIndex() 
	{
		return getLabel(STATE_LABEL_DEATH);
	}

	public DEHThing setDeathFrameIndex(int deathFrameIndex) 
	{
		RangeUtils.checkRange("Death frame index", 0, Integer.MAX_VALUE, deathFrameIndex);
		setLabel(STATE_LABEL_DEATH, deathFrameIndex);
		return this;
	}

	public int getExtremeDeathFrameIndex() 
	{
		return getLabel(STATE_LABEL_XDEATH);
	}

	public DEHThing setExtremeDeathFrameIndex(int extremeDeathFrameIndex)
	{
		RangeUtils.checkRange("Extreme death frame index", 0, Integer.MAX_VALUE, extremeDeathFrameIndex);
		setLabel(STATE_LABEL_XDEATH, extremeDeathFrameIndex);
		return this;
	}

	public int getRaiseFrameIndex()
	{
		return getLabel(STATE_LABEL_RAISE);
	}

	public DEHThing setRaiseFrameIndex(int raiseFrameIndex) 
	{
		RangeUtils.checkRange("Raise frame index", 0, Integer.MAX_VALUE, raiseFrameIndex);
		setLabel(STATE_LABEL_RAISE, raiseFrameIndex);
		return this;
	}

	@Override
	public String[] getLabels()
	{
		Set<String> labelSet = stateIndexMap.keySet();
		return labelSet.toArray(new String[labelSet.size()]);
	}

	@Override
	public boolean hasLabel(String label)
	{
		return stateIndexMap.containsKey(label);
	}

	@Override
	public int getLabel(String label)
	{
		return stateIndexMap.getOrDefault(label, 0);
	}

	@Override
	public DEHThing setLabel(String label, int index)
	{
		if (index < 0)
			throw new IllegalArgumentException("index cannot be < 0");
		
		if (index == 0)
			stateIndexMap.remove(label);
		else
			stateIndexMap.put(label, index);
		return this;
	}

	@Override
	public DEHThing clearLabels()
	{
		stateIndexMap.clear();
		return this;
	}

	public int getSeeSoundPosition() 
	{
		return seeSoundPosition;
	}

	public DEHThing setSeeSoundPosition(int seeSoundPosition)
	{
		RangeUtils.checkRange("Alert sound position", 0, Integer.MAX_VALUE, seeSoundPosition);
		this.seeSoundPosition = seeSoundPosition;
		return this;
	}

	public int getAttackSoundPosition()
	{
		return attackSoundPosition;
	}

	public DEHThing setAttackSoundPosition(int attackSoundPosition)
	{
		RangeUtils.checkRange("Attack sound position", 0, Integer.MAX_VALUE, attackSoundPosition);
		this.attackSoundPosition = attackSoundPosition;
		return this;
	}

	public int getPainSoundPosition()
	{
		return painSoundPosition;
	}

	public DEHThing setPainSoundPosition(int painSoundPosition) 
	{
		RangeUtils.checkRange("Pain sound position", 0, Integer.MAX_VALUE, painSoundPosition);
		this.painSoundPosition = painSoundPosition;
		return this;
	}

	public int getDeathSoundPosition()
	{
		return deathSoundPosition;
	}

	public DEHThing setDeathSoundPosition(int deathSoundPosition) 
	{
		RangeUtils.checkRange("Death sound position", 0, Integer.MAX_VALUE, deathSoundPosition);
		this.deathSoundPosition = deathSoundPosition;
		return this;
	}

	public int getActiveSoundPosition()
	{
		return activeSoundPosition;
	}

	public DEHThing setActiveSoundPosition(int activeSoundPosition)
	{
		RangeUtils.checkRange("Active sound position", 0, Integer.MAX_VALUE, activeSoundPosition);
		this.activeSoundPosition = activeSoundPosition;
		return this;
	}

	public int getRipSoundPosition()
	{
		return ripSoundPosition;
	}

	public DEHThing setRipSoundPosition(int ripSoundPosition)
	{
		RangeUtils.checkRange("Rip sound position", 0, Integer.MAX_VALUE, ripSoundPosition);
		this.ripSoundPosition = ripSoundPosition;
		return this;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof DEHThing)
			return equals((DEHThing)obj);
		return super.equals(obj);
	}
	
	public boolean equals(DEHThing obj) 
	{
		return editorNumber == obj.editorNumber
			&& health == obj.health
			&& speed == obj.speed
			&& fastSpeed == obj.fastSpeed
			&& radius == obj.radius
			&& height == obj.height
			&& damage == obj.damage
			&& reactionTime == obj.reactionTime
			&& painChance == obj.painChance
			&& flags == obj.flags
			&& mass == obj.mass
			&& getSpawnFrameIndex() == obj.getSpawnFrameIndex()
			&& getWalkFrameIndex() == obj.getWalkFrameIndex()
			&& getPainFrameIndex() == obj.getPainFrameIndex()
			&& getMeleeFrameIndex() == obj.getMeleeFrameIndex()
			&& getMissileFrameIndex() == obj.getMissileFrameIndex()
			&& getDeathFrameIndex() == obj.getDeathFrameIndex()
			&& getExtremeDeathFrameIndex() == obj.getExtremeDeathFrameIndex()
			&& getRaiseFrameIndex() == obj.getRaiseFrameIndex()
			&& seeSoundPosition == obj.seeSoundPosition
			&& activeSoundPosition == obj.activeSoundPosition
			&& attackSoundPosition == obj.attackSoundPosition
			&& painSoundPosition == obj.painSoundPosition
			&& deathSoundPosition == obj.deathSoundPosition
			&& mbf21Flags == obj.mbf21Flags
			&& meleeRange == obj.meleeRange
			&& infightingGroup == obj.infightingGroup
			&& projectileGroup == obj.projectileGroup
			&& splashGroup == obj.splashGroup
			&& ripSoundPosition == obj.ripSoundPosition
		;
	}	
	
	@Override
	public void writeObject(Writer writer, DEHThing thing, DEHFeatureLevel level) throws IOException
	{
		boolean isProjectile = (getFlags() & DEHThingFlag.MISSILE.getValue()) != 0;
		boolean thingIsProjectile = (thing.getFlags() & DEHThingFlag.MISSILE.getValue()) != 0;

		int speedVal = isProjectile ? getSpeed() << 16 : getSpeed();
		int thingSpeedVal = thingIsProjectile ? thing.getSpeed() << 16 : thing.getSpeed();

		if (getEditorNumber() != thing.getEditorNumber())
			writer.append("ID # = ").append(String.valueOf(getEditorNumber())).append("\r\n");
		
		if (getHealth() != thing.getHealth())
			writer.append("Hit points = ").append(String.valueOf(getHealth())).append("\r\n");
		if (speedVal != thingSpeedVal)
			writer.append("Speed = ").append(String.valueOf(speedVal)).append("\r\n");
		if (getRadius() != thing.getRadius())
			writer.append("Width = ").append(String.valueOf(getRadius() << 16)).append("\r\n");
		if (getHeight() != thing.getHeight())
			writer.append("Height = ").append(String.valueOf(getHeight() << 16)).append("\r\n");
		if (getDamage() != thing.getDamage())
			writer.append("Missile damage = ").append(String.valueOf(getDamage())).append("\r\n");
		if (getReactionTime() != thing.getReactionTime())
			writer.append("Reaction time = ").append(String.valueOf(getReactionTime())).append("\r\n");
		if (getPainChance() != thing.getPainChance())
			writer.append("Pain chance = ").append(String.valueOf(getPainChance())).append("\r\n");
		if (getFlags() != thing.getFlags())
			writer.append("Bits = ").append(String.valueOf(getFlags())).append("\r\n");
		if (getMass() != thing.getMass())
			writer.append("Mass = ").append(String.valueOf(getMass())).append("\r\n");

		if (getSpawnFrameIndex() != thing.getSpawnFrameIndex())
			writer.append("Initial frame = ").append(String.valueOf(getSpawnFrameIndex())).append("\r\n");
		if (getWalkFrameIndex() != thing.getWalkFrameIndex())
			writer.append("First moving frame = ").append(String.valueOf(getWalkFrameIndex())).append("\r\n");
		if (getPainFrameIndex() != thing.getPainFrameIndex())
			writer.append("Injury frame = ").append(String.valueOf(getPainFrameIndex())).append("\r\n");
		if (getMeleeFrameIndex() != thing.getMeleeFrameIndex())
			writer.append("Close attack frame = ").append(String.valueOf(getMeleeFrameIndex())).append("\r\n");
		if (getMissileFrameIndex() != thing.getMissileFrameIndex())
			writer.append("Far attack frame = ").append(String.valueOf(getMissileFrameIndex())).append("\r\n");
		if (getDeathFrameIndex() != thing.getDeathFrameIndex())
			writer.append("Death frame = ").append(String.valueOf(getDeathFrameIndex())).append("\r\n");
		if (getExtremeDeathFrameIndex() != thing.getExtremeDeathFrameIndex())
			writer.append("Exploding frame = ").append(String.valueOf(getExtremeDeathFrameIndex())).append("\r\n");
		if (getRaiseFrameIndex() != thing.getRaiseFrameIndex())
			writer.append("Respawn frame = ").append(String.valueOf(getRaiseFrameIndex())).append("\r\n");

		if (getSeeSoundPosition() != thing.getSeeSoundPosition())
			writer.append("Alert sound = ").append(String.valueOf(getSeeSoundPosition())).append("\r\n");
		if (getActiveSoundPosition() != thing.getActiveSoundPosition())
			writer.append("Action sound = ").append(String.valueOf(getActiveSoundPosition())).append("\r\n");
		if (getAttackSoundPosition() != thing.getAttackSoundPosition())
			writer.append("Attack sound = ").append(String.valueOf(getAttackSoundPosition())).append("\r\n");
		if (getPainSoundPosition() != thing.getPainSoundPosition())
			writer.append("Pain sound = ").append(String.valueOf(getPainSoundPosition())).append("\r\n");
		if (getDeathSoundPosition() != thing.getDeathSoundPosition())
			writer.append("Death sound = ").append(String.valueOf(getDeathSoundPosition())).append("\r\n");

		// Extended features
		if (level.supports(DEHFeatureLevel.EXTENDED))
		{
			if (getDroppedItem() != thing.getDroppedItem())
				writer.append("Dropped item = ").append(String.valueOf(getDroppedItem())).append("\r\n");
		}

		// MBF21 features
		if (level.supports(DEHFeatureLevel.MBF21))
		{
			int fastSpeedVal = isProjectile && getFastSpeed() != DEFAULT_FASTSPEED ? getFastSpeed() << 16 : getFastSpeed();
			int thingFastSpeedVal = thingIsProjectile && thing.getFastSpeed() != DEFAULT_FASTSPEED ? thing.getFastSpeed() << 16 : thing.getFastSpeed();

			if (getMBF21Flags() != thing.getMBF21Flags())
				writer.append("MBF21 Bits = ").append(String.valueOf(getMBF21Flags())).append("\r\n");
			if (getInfightingGroup() != thing.getInfightingGroup())
				writer.append("Infighting group = ").append(String.valueOf(getInfightingGroup())).append("\r\n");
			if (getProjectileGroup() != thing.getProjectileGroup())
				writer.append("Projectile group = ").append(String.valueOf(getProjectileGroup())).append("\r\n");
			if (getSplashGroup() != thing.getSplashGroup())
				writer.append("Splash group = ").append(String.valueOf(getSplashGroup())).append("\r\n");
			if (fastSpeedVal != thingFastSpeedVal)
				writer.append("Fast speed = ").append(String.valueOf(fastSpeedVal)).append("\r\n");
			if (getMeleeRange() != thing.getMeleeRange())
				writer.append("Melee range = ").append(String.valueOf(getMeleeRange() << 16)).append("\r\n");
			if (getRipSoundPosition() != thing.getRipSoundPosition())
				writer.append("Rip sound = ").append(String.valueOf(getRipSoundPosition())).append("\r\n");
		}

		writer.flush();
	}

	@Override
	public void dumpObjectFieldNames(List<String> fieldNameList, DEHFeatureLevel level)
	{
		// TODO Finish this.
	}

	@Override
	public void dumpObjectFieldValues(List<Object> fieldValueList, DEHFeatureLevel level) 
	{
		// TODO Finish this.
	}

}
