package net.mtrop.doom.tools.gui.doommake.swing.panels;

import javax.swing.JFrame;

import net.mtrop.doom.tools.gui.doommake.DoomMakeProjectHelper;
import net.mtrop.doom.tools.gui.doommake.DoomMakeProjectHelper.ProcessCallException;
import net.mtrop.doom.tools.struct.swing.SwingUtils;

import static net.mtrop.doom.tools.struct.swing.ContainerFactory.*;

import java.io.File;
import java.io.FileNotFoundException;

public final class DoomMakeProjectTargetListPanelTest 
{
	public static void main(String[] args) throws FileNotFoundException, ProcessCallException 
	{
		SwingUtils.setSystemLAF();
		SwingUtils.apply(frame("Test", new DoomMakeProjectTargetListPanel(
			DoomMakeProjectHelper.get().getProjectTargets(new File(args[0])),
			(target) -> { System.out.println("SELECT: " + target); }, 
			(target) -> { System.out.println("DCLICK: " + target); }
		)), 
		(frame) -> {
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		});
	}
}