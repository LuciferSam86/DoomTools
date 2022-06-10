/*******************************************************************************
 * Copyright (c) 2020-2021 Matt Tropiano
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package net.mtrop.doom.tools.decohack.data.enums;

import java.util.Map;

import net.mtrop.doom.tools.decohack.data.DEHActionPointer;
import net.mtrop.doom.tools.struct.util.EnumUtils;

import static net.mtrop.doom.tools.decohack.data.DEHActionPointer.*;

/**
 * Enumeration of action pointers for frames.
 * NOTE: KEEP THIS ORDER SORTED IN THIS WAY! It is used as breaking categories for the pointer dumper!
 * @author Matthew Tropiano
 * @author Xaser Acheron
 */
public enum DEHActionPointerDoom19 implements DEHActionPointer
{
	// TODO: Finish docs!!
	
	NULL          (0,   "NULL"),
	
	// ========== Doom Weapon Action Pointers ==========
	
	LIGHT0        (1,   true,  "Light0", usage(
		"Sets the player's extra lighting level to 0.",
		"Used to reset lighting after weapon fire. See A_Light1 and A_Light2."
	)),
	
	WEAPONREADY   (2,   true,  "WeaponReady", usage(
		"Enables weapon ready checks for the calling player.",
		"Listens for the calling player's weapon fire input flag for firing a weapon. If so, weapon enters the FIRE state.",
		"Also sets the calling actor to S_PLAY (state 149) if the actor is in the S_PLAY_ATK1 or S_PLAY_ATK2 states (154 or 155).",
		"Also if the current weapon is WP_CHAINSAW (slot 7) and it is called on state S_SAW (67), it will play sound \"SAWIDL\".",
		"Also puts this weapon into the LOWER state if the player is NOT dead and has a pending weapon to switch to.",
		"Also sets the weapon graphic offsets for bobbing animation by player speed."
	)),
	
	LOWER         (3,   true,  "Lower", usage(
		"Lowers the weapon by a specific offset amount.",
		"The weapon is lowered by a certain offset amount (LOWERSPEED) until it is off the screen. If it is, the pending weapon is switched to and enters its RAISE state.",
		"Can be called multiple times during one gametic to lower a weapon more quickly.",
		"Since this is affected by graphic offset, you can use the offset parameters to influence this behavior."
	)),
	
	RAISE         (4,   true,  "Raise", usage(
		"Raises the weapon by a specific offset amount.",
		"The weapon is raised by a certain offset amount (RAISESPEED) until it reaches a certain offset (WEAPONTOP). Once it does, it enters the READY state.",
		"Can be called multiple times during one gametic to raise a weapon more quickly.",
		"Since this is affected by graphic offset, you can use the offset parameters to influence this behavior."
	)),
	
	PUNCH         (6,   true,  "Punch", usage(
		"Performs a punch hitscan attack from the calling player.",
		"If it is in range of a shootable actor, it plays the \"PUNCH\" sound, deals 1d10 x2 damage to that actor, further multiplied by 10 if the calling player has the Berserk powerup.",
		"If the attack deals damage, it will turn the player towards what it punched.",
		"The range of the attack is 64 map units."
	)),
	
	REFIRE        (9,   true,  "ReFire", usage(
		"Performs weapon refire logic.",
		"Checks if the player's weapon fire input flag is set, and if possible, the weapon is fired again.",
		"On refire, the player's \"refire\" count is incremented, the ammo amount is checked/deducted, and the weapon enters the FIRE state if all conditions are good.",
		"However, if the player is dead, is switching away from a weapon, or the weapon fire input flag is not set, the \"refire\" count on the player is set to 0, and ammo checks occur.",
		"NOTE: The \"refire\" count influences things like certain hitscan attack accuracies and which firing state to use on the chaingun (see A_FireCGun, A_FirePistol)."
	)),
	
	FIREPISTOL    (14,  true,  "FirePistol", usage(
		"Fires a pistol shot from the calling player.",
		"Plays the sound \"PISTOL\", fires one hitscan attack, and subtracts 1 from the ammo type of this weapon.",
		"The hitscan attack does 5 x 1d3 damage.",
		"This also sets the calling player's state to S_PLAY_ATK2 (155), and displays the FLASH state for the weapon on the HUD.",
		"If \"refire\" is 0, the hitscan is dead center, with no random angle."
	)),
	
	LIGHT1        (17,  true,  "Light1", usage(
		"Sets the player's extra lighting level to 1.",
		"See A_Light0 to reset."
	)),
	
	FIRESHOTGUN   (22,  true,  "FireShotgun", usage(
		"Fires a shotgun shot from the calling player.",
		"Plays the sound \"SHOTGN\", fires 7 hitscan attacks, and subtracts 1 from the ammo type of this weapon.",
		"Each hitscan attack does 5 x 1d3 damage.",
		"This also sets the calling player's state to S_PLAY_ATK2 (155), and displays the FLASH state for the weapon on the HUD."
	)),
	
	LIGHT2        (31,  true,  "Light2", usage(
		"Sets the player's extra lighting level to 2.",
		"See A_Light0 to reset."
	)),
	
	FIRESHOTGUN2  (36,  true,  "FireShotgun2", usage(
		"Fires a super-shotgun shot from the calling player.",
		"Plays the sound \"DSHTGN\", fires 20 hitscan attacks, and subtracts 2 from the ammo type of this weapon.",
		"Each hitscan attack does 5 x 1d3 damage.",
		"This also sets the calling player's state to S_PLAY_ATK2 (155), and displays the FLASH state for the weapon on the HUD."
	)),
	
	CHECKRELOAD   (38,  true,  "CheckReload", usage(
		"Checks if the calling player has sufficient ammo to fire for this weapon.",
		"If the player does not have sufficient ammo, this starts a switch to the next preferred weapon.",
		"If the weapon is in slot 8 (Super Shotgun), it checks for 2 for the current weapon.",
		"If the weapon is in slot 6 (BFG), it checks for [BFGCells] for the current weapon.",
		"All other slots, it checks for 1 ammo."
	)),
	
	OPENSHOTGUN2  (39,  true,  "OpenShotgun2", usage(
		"Plays the sound \"DBOPN\" from the calling player."
	)),
	
	LOADSHOTGUN2  (41,  true,  "LoadShotgun2", usage(
		"Plays the sound \"DBLOAD\" from the calling player."
	)),
	
	CLOSESHOTGUN2 (43,  true,  "CloseShotgun2", usage(
		"Plays the sound \"DBCLS\" from the calling player and calls A_ReFire."
	)),
	
	FIRECGUN      (52,  true,  "FireCGun", usage(
		"Fires the chaingun from the calling player.",
		"Plays the sound \"PISTOL\", fires one hitscan attack, and subtracts 1 from the ammo type of this weapon.",
		"This also sets the calling player's state to S_PLAY_ATK2 (state 155).",
		"If \"refire\" is 0, the hitscan is dead center.",
		"NOTE: The FLASH state displayed for this firing is based on the offset from state S_CHAIN1 (52). If 52, FLASH. If 53, FLASH + 1."
	)),
	
	GUNFLASH      (60,  true,  "GunFlash", usage(
		"Performs a gun flash for the calling player.",
		"Sets the calling player's state to S_PLAY_ATK2 (state 155), and displays the FLASH state for the weapon on the HUD."
	)),
	
	FIREMISSILE   (61,  true,  "FireMissile", usage(
		"Fires a rocket from the calling player.",
		"Subtracts 1 from the ammo type of this weapon and fires thing slot MT_ROCKET (34)."
	)),
	
	SAW           (71,  true,  "Saw", usage(
		"Performs a single chainsaw scan attack from the calling player.",
		"If it is in range of a shootable actor, it plays the \"SAWHIT\" sound from the calling player, deals 1d10 x2 damage to that actor.",
		"If the attack deals damage, it will turn the player towards what it punched.",
		"The range of the attack is 64 map units.",
		"If the attack misses, it plays the \"SAWFUL\" sound from the calling player."
	)),
	
	FIREPLASMA    (77,  true,  "FirePlasma", usage(
		"Fires a plasma rifle projectile from the calling player.",
		"Subtracts 1 from the ammo type of this weapon and fires thing slot MT_PLASMA (35).",
		"Displays the FLASH or FLASH+1 state for the weapon on the HUD at random."
	)),
	
	BFGSOUND      (84,  true,  "BFGsound", usage(
		"Plays the sound \"BFG\" from the calling player."
	)),
	
	FIREBFG       (86,  true,  "FireBFG", usage(
		"Fires a BFG shot from the calling player.",
		"Subtracts [BFGCells] from the ammo type of this weapon and fires thing slot MT_BFG (36)."
	)),

	// ========== Doom Thing Action Pointers ==========
	
	BFGSPRAY      (119, false, "BFGSpray", usage(
		"Fires the BFG tracer spray.",
		"In the direction of this projectile's trajectory, a set of 40 hitscans are fired from the actor that shot the projectile that do 15d7 damage each.",
		"If the tracers hit any shootable actor, it spawns thing MT_EXTRABFG (43) at their position."
	)),
	
	EXPLODE       (127, false, "Explode", usage(
		"Makes a splash damage check from the calling actor.",
		"The splash check damages all shootable actors (including the caller) in a 128 map unit area for a maximum of 128 damage the closer they are to the center of the calling actor.",
		"Contrary to expectation, no sound is played and no actors are spawned."
	)),
	
	PAIN          (157, false, "Pain", usage(
		"Plays the calling actor's PAIN sound, if defined."
	)),
	
	PLAYERSCREAM  (159, false, "PlayerScream", usage(
		"Plays a player death sound from the calling actor.",
		"The played sound is \"PLDETH\", unless the calling actor's health is lower than -50, then it plays \"PDIEHI\"."
	)),
	
	FALL          (160, false, "Fall", usage(
		"Unsets the calling actor's SOLID flag."
	)),
	
	XSCREAM       (166, false, "XScream", usage(
		"Plays the \"SLOP\" sound from the calling actor."
	)),
	
	LOOK          (174, false, "Look", usage(
		"Looks for a valid target for the calling actor.",
		"When a target is found, the calling actor's SIGHT sound is played, if defined, its target is set to the seen actor, and it enters its SEE state.",
		"If the calling actor is in thing slot MT_SPIDER (20) or MT_CYBORG (22), the SIGHT sound is played at full volume (in MBF21 patches, this can be altered via the FULLVOLSOUNDS flag).",
		"This MUST be called before A_Chase is called - A_Chase requires a target to pursue, or this actor jumps back to its SPAWN state.",
		"This pointer, along with A_Chase, comprises the \"AI\" of Doom monsters."
	)),
	
	CHASE         (176, false, "Chase", usage(
		"Pursues the calling actor's current target.",
		"If the target is in melee range, the calling actor jumps to its MELEE state, if defined.",
		"If the target is in missile range, the calling actor jumps to its MISSILE state, if defined.",
		"The actor will randomly play its ACTIVE sound, if defined.",
		"If the calling actor loses its target or its target is not flagged as SHOOTABLE, it jumps to its SPAWN state.",
		"This pointer, along with A_Look, comprises the \"AI\" of Doom monsters."
	)),
	
	FACETARGET    (184, false, "FaceTarget", usage(
		"Faces the calling actor towards its current target.",
		"Note that this does not affect the aiming of attacks, just actor facing angle.",
		"See also: A_Chase, A_Look."
	)),
	
	POSATTACK     (185, false, "PosAttack", usage(
		"Calls A_FaceTarget and performs a single hitscan attack.",
		"This also plays the sound \"PISTOL\" from the calling actor.",
		"If the calling actor does not have a target, this does nothing."
	)),
	
	SCREAM        (190, false, "Scream", usage(
		"Plays the calling actor's DEATH sound, if defined.",
		"This has some special behavior: if the DEATH sound is \"PODTH1\", \"PODTH2\", or \"PODTH3\", it will play one of those 3.",
		"If the DEATH sound is \"BGDTH1\" or \"BGDTH2\" it will play one of those 2.",
		"If the calling actor is in thing slot MT_SPIDER (20) or MT_CYBORG (22), the sound is played at full volume.",
		"In MBF21 or higher patches, this \"full volume\" behavior can be overridden with the FULLVOLSOUNDS thing flag."
	)),
	
	VILECHASE     (243, false, "VileChase", usage(
		"Does exactly what A_Chase does, with a few differences.",
		"If a dead actor (CORPSE flag is set) with a defined RAISE state is in range, the calling actor will enter state S_VILE_HEAL1 (266) and play sound \"SLOP\".",
		"The revived actor has its health reset, its flags restored, and its target cleared, and it enters its RAISE state.",
		"Note that this calling actor enters a hardcoded state (266) on \"heal\". This behavior cannot be changed via this pointer."
	)),
	
	VILESTART     (255, false, "VileStart", usage(
		"Plays the sound \"VILATK\" from the calling actor."
	)),
	
	VILETARGET    (257, false, "VileTarget", usage(
		"Starts the archvile attack.",
		"Calls A_FaceTarget and spawns thing MT_FIRE (slot 5) on the calling actor's target.",
		"Also calls A_Fire on the spawned fire to put it in front of the player, if the caller has line-of-sight to the target.",
		"The fire is set to the calling actor's tracer pointer, and the fire's target is the calling actor's target."
	)),
	
	VILEATTACK    (264, false, "VileAttack", usage(
		"Performs the actual archvile attack.",
		"If the calling actor has a line-of-sight to its target, play sound \"BAREXP\" and damage the target 20 points of damage.",
		"The attack further makes a radius attack happen for 70 damage from the caller's tracer actor (the fire spawned via A_VileTarget).",
		"If the calling actor does not have a target, this does nothing."
	)),
	
	STARTFIRE     (281, false, "StartFire", usage(
		"Calls A_Fire and plays sound \"FLAMST\" from the calling actor."
	)),
	
	FIRE          (282, false, "Fire", usage(
		"The archvile fire special.",
		"Puts the calling actor in front of its \"tracer\" actor if line-of-sight is maintained with its \"target\".",
		"If this actor has no tracer reference, this does nothing."
	)),
	
	FIRECRACKLE   (285, false, "FireCrackle", usage(
		"Calls A_Fire and plays sound \"FLAME\" from the calling actor."
	)),
	
	TRACER        (316, false, "Tracer", usage(
		"Turns the trajectory of a missile with a tracer reference towards its target.",
		"The angle adjustment only occurs on a gametic that is not a multiple of 4.",
		"If the adjustment occurs, this also spawns a bullet puff (MT_PUFF, slot 38) AND rocket smoke (MT_SMOKE, slot 8) at the calling actor's position. A lot of source ports correct this behavior to remove the puff.",
		"If the calling actor has no tracer target, this does no adjustment, but may still spawn puffs/smoke.",
		"Older versions of the Doom Engine will also align the \"gametics\" to a global ticker, potentially ruining demo playback through the title screen."
	)),
	
	SKELWHOOSH    (336, false, "SkelWhoosh", usage(
		"Calls A_FaceTarget and plays sound \"SKESWG\" from the calling actor.",
		"If the calling actor has no target, this does nothing."
	)),
	
	SKELFIST      (338, false, "SkelFist", usage(
		"Calls A_FaceTarget and attempts a melee attack from the calling actor.",
		"If the attack connects, this does 1d10 x 6 damage ",
		"If the calling actor has no target, this does nothing."
	)),

	SKELMISSILE   (341, false, "SkelMissile"),
	FATRAISE      (376, false, "FatRaise"),
	FATATTACK1    (377, false, "FatAttack1"),
	FATATTACK2    (380, false, "FatAttack2"),
	FATATTACK3    (383, false, "FatAttack3"),
	BOSSDEATH     (397, false, "BossDeath"),
	CPOSATTACK    (417, false, "CPosAttack"),
	CPOSREFIRE    (419, false, "CPosRefire"),
	TROOPATTACK   (454, false, "TroopAttack"),
	SARGATTACK    (487, false, "SargAttack"),
	HEADATTACK    (506, false, "HeadAttack"),
	BRUISATTACK   (539, false, "BruisAttack"),
	SKULLATTACK   (590, false, "SkullAttack"),
	METAL         (603, false, "Metal"),
	SPOSATTACK    (616, false, "SPosAttack"),
	SPIDREFIRE    (618, false, "SpidRefire"),
	BABYMETAL     (635, false, "BabyMetal"),
	BSPIATTACK    (648, false, "BspiAttack"),
	HOOF          (676, false, "Hoof"),
	CYBERATTACK   (685, false, "CyberAttack"),
	PAINATTACK    (711, false, "PainAttack"),
	PAINDIE       (718, false, "PainDie"),
	KEENDIE       (774, false, "KeenDie"),
	BRAINPAIN     (779, false, "BrainPain"),
	BRAINSCREAM   (780, false, "BrainScream"),
	BRAINDIE      (783, false, "BrainDie"),
	BRAINAWAKE    (785, false, "BrainAwake"),
	BRAINSPIT     (786, false, "BrainSpit"),
	SPAWNSOUND    (787, false, "SpawnSound"),
	SPAWNFLY      (788, false, "SpawnFly"),
	BRAINEXPLODE  (801, false, "BrainExplode");
	
	/** Originating frame (for DEH 3.0 format 19). */
	private Usage usage;
	/** Originating frame (for DEH 3.0 format 19). */
	private int frame;
	/** Is weapon pointer. */
	private boolean weapon;
	/** Mnemonic name for BEX/DECORATE. */
	private String mnemonic;

	private static final DEHActionPointerParamType[] NO_PARAMS = new DEHActionPointerParamType[0];
	
	private DEHActionPointerDoom19(int frame, String mnemonic)
	{
		this(frame, false, mnemonic, BLANK_USAGE);
	}

	private DEHActionPointerDoom19(int frame, String mnemonic, Usage usage)
	{
		this(frame, false, mnemonic, usage);
	}

	private DEHActionPointerDoom19(int frame, boolean weapon, String mnemonic)
	{
		this(frame, weapon, mnemonic, BLANK_USAGE);
	}

	private DEHActionPointerDoom19(int frame, boolean weapon, String mnemonic, Usage usage)
	{
		this.frame = frame;
		this.weapon = weapon;
		this.mnemonic = mnemonic;
		this.usage = usage;
	}

	private static final Map<String, DEHActionPointerDoom19> MNEMONIC_MAP = EnumUtils.createCaseInsensitiveNameMap(DEHActionPointerDoom19.class);
	
	public static DEHActionPointer getActionPointerByMnemonic(String mnemonic)
	{
		return MNEMONIC_MAP.get(mnemonic);
	}
	
	@Override
	public int getFrame() 
	{
		return frame;
	}
	
	@Override
	public boolean isWeapon()
	{
		return weapon;
	}
	
	@Override
	public String getMnemonic() 
	{
		return mnemonic;
	}
	
	@Override
	public DEHActionPointerType getType() 
	{
		return DEHActionPointerType.DOOM19;
	}
	
	@Override
	public DEHActionPointerParamType[] getParams()
	{
		return NO_PARAMS;
	}

	@Override
	public DEHActionPointerParamType getParam(int index)
	{
		return null;
	}

	@Override
	public Usage getUsage() 
	{
		return usage;
	}
	
}
