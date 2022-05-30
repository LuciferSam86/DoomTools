package net.mtrop.doom.tools.gui.managers.parsing;

import com.blackrook.rookscript.lang.ScriptFunctionType;

import net.mtrop.doom.tools.DoomMakeMain;
import net.mtrop.doom.tools.WadScriptMain.Resolver;

/** 
 * DoomMake Completion Provider.
 * @author Matthew Tropiano
 */
public class DoomMakeCompletionProvider extends WadScriptCompletionProvider
{
	public DoomMakeCompletionProvider()
	{
		super();
		for (Resolver r : DoomMakeMain.getAllDoomMakeResolvers())
			for (ScriptFunctionType type : r.resolver.getFunctions())
				addCompletion(new FunctionCompletion(this, r.namespace, type));
	}
}

