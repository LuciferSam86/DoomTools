package net.mtrop.doom.tools.gui.apps.swing.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileFilter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import net.mtrop.doom.tools.gui.managers.DoomToolsEditorProvider;
import net.mtrop.doom.tools.gui.managers.DoomToolsGUIUtils;
import net.mtrop.doom.tools.gui.managers.DoomToolsIconManager;
import net.mtrop.doom.tools.gui.managers.DoomToolsLanguageManager;
import net.mtrop.doom.tools.gui.managers.DoomToolsSettingsManager;
import net.mtrop.doom.tools.struct.swing.SwingUtils;
import net.mtrop.doom.tools.struct.util.IOUtils;

import static javax.swing.BorderFactory.*;
import static net.mtrop.doom.tools.struct.swing.ContainerFactory.*;
import static net.mtrop.doom.tools.struct.swing.ComponentFactory.*;
import static net.mtrop.doom.tools.struct.swing.SwingUtils.*;

/**
 * The editor panel for editing many files at once.
 * @author Matthew Tropiano
 */
public class MultiFileEditorPanel extends JPanel
{
	private static final long serialVersionUID = -3208735521175265227L;
	
	private static final FileFilter[] NO_FILTERS = new FileFilter[0];
	
	public interface ActionNames
	{
		String ACTION_SAVE = "save";
		String ACTION_SAVE_AS = "save-as";
		String ACTION_SAVE_ALL = "save-all";
		String ACTION_CLOSE = "close";
		String ACTION_CLOSE_ALL = "close-all";
		String ACTION_CLOSE_ALL_BUT_CURRENT = "close-all-but-current";
		String ACTION_CUT = "cut";
		String ACTION_COPY = "copy";
		String ACTION_PASTE = "paste";
		String ACTION_DELETE = "delete";
		String ACTION_SELECT_ALL = "select-all";
		String ACTION_UNDO = "undo";
		String ACTION_REDO = "redo";
	}
	
	// ======================================================================
	
	private DoomToolsEditorProvider editorProvider;
	private DoomToolsSettingsManager settings;
	private DoomToolsIconManager icons;
	private DoomToolsLanguageManager language;
	private DoomToolsGUIUtils utils;
	
	// ======================================================================

	/** The main editor tabs. */
	private JTabbedPane mainEditorTabs;
	/** File path. */
	private JLabel filePathLabel;
	/** Caret position data. */
	private JLabel caretPositionLabel;
	/** Spacing mode. */
	private JLabel spacingModeLabel;
	/** Encoding mode. */
	private JLabel encodingModeLabel;
	/** Syntax style mode. */
	private JLabel syntaxStyleLabel;
	
	// ======================================================================

	/** The action map that is a mapping of {@link RTextArea} actions to redirected ones for this panel. */
	private Map<String, Action> unifiedActionMap;
	
	/** Save Action */
	private Action saveAction;
	/** Save As Action */
	private Action saveAsAction;
	/** Save All Action */
	private Action saveAllAction;
	/** Close Action */
	private Action closeAction;
	/** Close All Action */
	private Action closeAllAction;
	/** Close All But Current Action */
	private Action closeAllButCurrentAction;
	/** Cut Action */
	private Action cutAction;
	/** Copy Action */
	private Action copyAction;
	/** Paste Action */
	private Action pasteAction;
	/** Delete Action */
	private Action deleteAction;
	/** Select All Action */
	private Action selectAllAction;
	/** Undo Action */
	private Action undoAction;
	/** Redo Action */
	private Action redoAction;
	
	// ======================================================================
	
	/** All editors. */
	private Map<Component, EditorHandle> allEditors;
	/** The currently selected editor. */
	private EditorHandle currentEditor;
	/** The panel listener. */
	private Listener listener;

	/**
	 * Creates a new multi-file editor panel.
	 */
	public MultiFileEditorPanel()
	{
		this(null);
	}
	
	/**
	 * Creates a new multi-file editor panel.
	 * @param listener the listener.
	 */
	public MultiFileEditorPanel(Listener listener)
	{
		this.editorProvider = DoomToolsEditorProvider.get();
		this.icons = DoomToolsIconManager.get();
		this.language = DoomToolsLanguageManager.get();
		this.settings = DoomToolsSettingsManager.get();
		this.utils = DoomToolsGUIUtils.get();
		
		this.allEditors = new HashMap<>();
		this.currentEditor = null;
		this.listener = listener;
		
		this.mainEditorTabs = apply(tabs(TabPlacement.TOP, TabLayoutPolicy.SCROLL), (tabs) -> {
			tabs.addChangeListener((event) -> {
				int index = tabs.getSelectedIndex();
				if (index >= 0)
				{
					setCurrentEditor(allEditors.get(tabs.getTabComponentAt(tabs.getSelectedIndex())));
					tabs.getComponentAt(index).requestFocus();
				}
				else
				{
					setCurrentEditor(null);
				}
			});
		});
		this.filePathLabel = label();
		this.caretPositionLabel = label();
		this.spacingModeLabel = label();
		this.encodingModeLabel = label();
		this.syntaxStyleLabel = label();
		
		this.saveAction = utils.createActionFromLanguageKey("texteditor.action.save", (event) -> saveCurrentEditor());
		this.saveAsAction = utils.createActionFromLanguageKey("texteditor.action.saveas", (event) -> saveCurrentEditorAs());
		this.saveAllAction = utils.createActionFromLanguageKey("texteditor.action.saveall", (event) -> saveAllEditors());
		this.closeAction = utils.createActionFromLanguageKey("texteditor.action.close", (event) -> closeCurrentEditor());
		this.closeAllAction = utils.createActionFromLanguageKey("texteditor.action.closeall", (event) -> closeAllEditors());
		this.closeAllButCurrentAction = utils.createActionFromLanguageKey("texteditor.action.closeallbutcurrent", (event) -> closeAllButCurrentEditor());
		
		this.cutAction = utils.createActionFromLanguageKey("texteditor.action.cut", (event) -> currentEditor.editorPanel.textArea.cut());
		this.copyAction = utils.createActionFromLanguageKey("texteditor.action.copy", (event) -> currentEditor.editorPanel.textArea.copy());
		this.pasteAction = utils.createActionFromLanguageKey("texteditor.action.paste", (event) -> currentEditor.editorPanel.textArea.paste());
		this.deleteAction = utils.createActionFromLanguageKey("texteditor.action.delete", (event) -> currentEditor.editorPanel.textArea.replaceSelection(""));
		this.selectAllAction = utils.createActionFromLanguageKey("texteditor.action.selectall", (event) -> currentEditor.editorPanel.textArea.selectAll());
		this.undoAction = utils.createActionFromLanguageKey("texteditor.action.undo", (event) -> currentEditor.editorPanel.textArea.undoLastAction());
		this.redoAction = utils.createActionFromLanguageKey("texteditor.action.redo", (event) -> currentEditor.editorPanel.textArea.redoLastAction());
		
		this.unifiedActionMap = apply(new HashMap<>(), (map) -> {
			map.put(ActionNames.ACTION_SAVE, saveAction);
			map.put(ActionNames.ACTION_SAVE_AS, saveAsAction);
			map.put(ActionNames.ACTION_SAVE_ALL, saveAllAction);
			map.put(ActionNames.ACTION_CLOSE, closeAction);
			map.put(ActionNames.ACTION_CLOSE_ALL, closeAllAction);
			map.put(ActionNames.ACTION_CLOSE_ALL_BUT_CURRENT, closeAllButCurrentAction);
			map.put(ActionNames.ACTION_CUT, cutAction);
			map.put(ActionNames.ACTION_COPY, copyAction);
			map.put(ActionNames.ACTION_PASTE, pasteAction);
			map.put(ActionNames.ACTION_DELETE, deleteAction);
			map.put(ActionNames.ACTION_SELECT_ALL, selectAllAction);
			map.put(ActionNames.ACTION_UNDO, undoAction);
			map.put(ActionNames.ACTION_REDO, redoAction);
		});
		
		containerOf(this, new BorderLayout(0, 2),
			node(BorderLayout.CENTER, this.mainEditorTabs),
			node(BorderLayout.SOUTH, containerOf(new GridLayout(1, 2),
				node(containerOf(createBevelBorder(BevelBorder.LOWERED), node(filePathLabel))),
				node(containerOf(new GridLayout(1, 4),
					node(containerOf(createBevelBorder(BevelBorder.LOWERED), node(syntaxStyleLabel))),
					node(containerOf(createBevelBorder(BevelBorder.LOWERED), node(encodingModeLabel))),
					node(containerOf(createBevelBorder(BevelBorder.LOWERED), node(spacingModeLabel))),
					node(containerOf(createBevelBorder(BevelBorder.LOWERED), node(caretPositionLabel)))
				))
			))
		);
	}
	
	/**
	 * Creates a new editor tab with a name.
	 * @param tabName the name of the tab.
	 * @param content the initial content of the new editor.
	 */
	public void newEditor(String tabName, String content)
	{
		createNewTab(tabName, null, null, content);
	}
	
	/**
	 * Opens a file into a new tab.
	 * Does nothing if the file is a directory.
	 * @param file the file to load.
	 * @param encoding the file encoding.
	 * @throws FileNotFoundException if the file could not be found. 
	 * @throws IOException if the file could not be read.
	 * @throws SecurityException if the OS is forbidding the read.
	 */
	public void openFileEditor(File file, Charset encoding) throws FileNotFoundException, IOException
	{
		if (file.isDirectory())
			return;
		
		StringWriter writer = new StringWriter();
		try (Reader reader = new InputStreamReader(new FileInputStream(file), encoding))
		{
			IOUtils.relay(reader, writer, 8192);
		}
		
		// Handle special case - if the only editor open is a new buffer with no file and no changes, remove it.
		if (getOpenEditorCount() == 1 && !currentEditor.needsToSave() && currentEditor.contentSourceFile == null)
			closeCurrentEditor();
		
		createNewTab(file.getName(), file, encoding, writer.toString());
	}
	
	/**
	 * Saves the current editor to its current file, or a new file if no current file.
	 * If no current editor, this does nothing.
	 * @return true if a successful save occurred, false otherwise.
	 * @see #saveCurrentEditorAs()
	 */
	public boolean saveCurrentEditor()
	{
		if (currentEditor == null)
			return false;

		return saveEditor(currentEditor);
	}

	/**
	 * Saves the current editor to a new file, prompting for the new file.
	 * If no current editor, this does nothing.
	 * @return true if a successful save occurred, false otherwise.
	 */
	public boolean saveCurrentEditorAs() 
	{
		if (currentEditor == null)
			return false;
		
		File editorFile = utils.chooseFile(this, getLastPathKey(), 
			language.getText("texteditor.action.save.title", currentEditor.editorTab.getTabTitle()), 
			language.getText("texteditor.action.save.approve"), 
			getSaveFileTypes()
		);
		
		if (editorFile == null)
			return false;
		
		return saveEditorToFile(currentEditor, editorFile);
	}

	/**
	 * Attempts to save all open editors.
	 */
	public void saveAllEditors()
	{
		for (Entry<Component, EditorHandle> editor : allEditors.entrySet())
		{
			EditorHandle handle = editor.getValue();
			if (!handle.needsToSave())
				continue;
			
			File editorFile;
			if ((editorFile = handle.contentSourceFile) == null)
			{
				editorFile = utils.chooseFile(this, getLastPathKey(), 
					language.getText("texteditor.action.save.title", handle.editorTab.getTabTitle()), 
					language.getText("texteditor.action.save.approve"), 
					getSaveFileTypes()
				);

				if (editorFile == null)
					return;
				
				saveEditorToFile(handle, editorFile);
			}
		}
	}

	/**
	 * Closes the current selected editor.
	 */
	public void closeCurrentEditor() 
	{
		if (currentEditor == null)
			return;
		
		attemptToCloseEditor(currentEditor);
	}

	/**
	 * Attempts to close all editors.
	 * @return true if it is safe to close all editors, and all editors were closed, false if one editor closing was cancelled.
	 */
	public boolean closeAllEditors() 
	{
		// Find all editors to close.
		Set<Component> tabsToClose = new HashSet<>();
		
		for (Entry<Component, EditorHandle> editor : allEditors.entrySet())
		{
			EditorHandle handle = editor.getValue();
			
			if (!editorCanClose(handle))
				return false;
			
			tabsToClose.add(editor.getKey());
		}
		
		for (Component tab : tabsToClose)
			removeEditorByTab(tab);
		return true;
	}

	/**
	 * Closes all but the current editor.
	 * @return true if it is safe to close all closeable editors, and all closeable editors were closed, false if one editor closing was cancelled.
	 */
	public boolean closeAllButCurrentEditor() 
	{
		if (currentEditor == null)
			return false;
		
		// Find all editors to close.
		Set<Component> tabsToClose = new HashSet<>();
		
		for (Entry<Component, EditorHandle> editor : allEditors.entrySet())
		{
			EditorHandle handle = editor.getValue();
			if (currentEditor == handle)
				continue;
			
			if (!editorCanClose(handle))
				return false;
			
			tabsToClose.add(editor.getKey());
		}
		
		for (Component tab : tabsToClose)
			removeEditorByTab(tab);
		return true;
	}

	/**
	 * Calls a consumer function for the current editor, if any.
	 * @param consumer the consumer to call and pass the current editor handle to.
	 */
	public void forCurrentEditor(Consumer<EditorHandle> consumer)
	{
		if (currentEditor != null)
			consumer.accept(currentEditor);
	}

	/**
	 * Calls a consumer function for each open editor.
	 * @param consumer the consumer to call and pass the editor handle to.
	 */
	public void forEachOpenEditor(Consumer<EditorHandle> consumer)
	{
		allEditors.values().forEach(consumer);
	}

	/**
	 * Gets the action for manipulating the currently selected editor.
	 * @param actionName am action name {@link ActionNames}.
	 * @return the corresponding action, or null if no action.
	 */
	public Action getActionFor(String actionName)
	{
		return unifiedActionMap.get(actionName);
	}
	
	/**
	 * Gets the amount of open editors.
	 * @return the amount of editors still open.
	 */
	public int getOpenEditorCount()
	{
		return allEditors.size();
	}
	
	/**
	 * Gets the amount of unsaved editors.
	 * @return the amount of editors still unsaved.
	 */
	public int getUnsavedEditorCount()
	{
		int count = 0;
		for (Entry<Component, EditorHandle> editor : allEditors.entrySet())
		{
			if (editor.getValue().needsToSave())
				count++;
		}
		
		return count;
	}
	
	/**
	 * Changes the encoding of the text in the current editor.
	 * @param charset the new charset for the editor.
	 */
	public void changeCurrentEditorEncoding(Charset charset)
	{
		if (currentEditor != null)
		{
			currentEditor.changeEncoding(charset);
			updateLabels();
		}
	}
	
	/**
	 * Creates a new editor, returning the editor tab.
	 * @param title the tab title.
	 * @param content the content.
	 * @param attachedFile the content source file (if any, can be null).
	 * @param fileCharset the file's source charset.
	 * @return the editor handle created.
	 */
	protected final EditorHandle createNewTab(String title, File attachedFile, Charset fileCharset, String content)
	{
		RSyntaxTextArea textArea = new RSyntaxTextArea();
		
		String styleName;
		if (attachedFile != null)
			styleName = editorProvider.getStyleByFile(attachedFile);
		else
			styleName = SyntaxConstants.SYNTAX_STYLE_NONE;
		
		textArea.setText(content);
		textArea.setCaretPosition(0);
		
		EditorHandle handle = attachedFile != null 
			? new EditorHandle(attachedFile, fileCharset, styleName, textArea) 
			: new EditorHandle(title, styleName, textArea)
		;

		// ==================================================================
		
		// TODO: Apply themes/editor default settings.
		
		settings.getEditorViewSettings("default").applyTo(textArea);
		
		// ==================================================================
		
		allEditors.put(handle.editorTab, handle);

		mainEditorTabs.addTab(null, handle.editorPanel);
		
		// The tab just added will be at the end.
		int tabIndex = mainEditorTabs.getTabCount() - 1;
		mainEditorTabs.setTabComponentAt(tabIndex, handle.editorTab);
		
		mainEditorTabs.setSelectedIndex(tabIndex);
		if (mainEditorTabs.getTabCount() == 1) // workaround for weird implementation.
			setCurrentEditor(handle);
		textArea.requestFocus();
		
		if (listener != null)
			listener.onOpen(handle);
		return handle;
	}

	/**
	 * Called to get the key for the "last path" accessed by the editor for saving or opening a file.
	 * @return the key.
	 */
	protected String getLastPathKey()
	{
		return "editor.file";
	}

	/**
	 * Called to get the save file types.
	 * @return the file filters for the
	 */
	protected FileFilter[] getSaveFileTypes()
	{
		return NO_FILTERS;
	}
	
	private void setCurrentEditor(EditorHandle handle)
	{
		currentEditor = handle;
		updateActionStates();
		updateLabels();
		if (listener != null)
			listener.onCurrentEditorChange(handle);
	}
	
	private void removeEditorByTab(Component tabComponent)
	{
		int index;
		if ((index = mainEditorTabs.indexOfTabComponent(tabComponent)) >= 0)
		{
			mainEditorTabs.remove(index);
			EditorHandle handle = allEditors.remove(tabComponent);
			if (handle != null && listener != null)
				listener.onClose(handle);
		}
		
		updateActionStates();
	}

	private void updateActionEditorStates()
	{
		if (currentEditor == null)
		{
			saveAction.setEnabled(false);
			undoAction.setEnabled(false);
			redoAction.setEnabled(false);
		}
		else
		{
			saveAction.setEnabled(currentEditor.needsToSave());
			undoAction.setEnabled(currentEditor.editorPanel.textArea.canUndo());
			redoAction.setEnabled(currentEditor.editorPanel.textArea.canRedo());
		}
	}

	private void updateTextActionStates()
	{
		boolean editorPresent = currentEditor != null;
		if (editorPresent)
		{
			RSyntaxTextArea textArea = currentEditor.editorPanel.textArea;
			boolean hasSelection = (textArea.getSelectionEnd() - textArea.getSelectionStart()) > 0; 
			cutAction.setEnabled(hasSelection);
			copyAction.setEnabled(hasSelection);
			deleteAction.setEnabled(hasSelection);
		}
		else
		{
			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
			deleteAction.setEnabled(false);
		}
	}
	
	private void updateActionStates()
	{
		updateActionEditorStates();
		updateTextActionStates();
		boolean editorPresent = currentEditor != null;
		saveAsAction.setEnabled(editorPresent);
		saveAllAction.setEnabled(getOpenEditorCount() > 0);
		pasteAction.setEnabled(editorPresent);
		selectAllAction.setEnabled(editorPresent);
	}
	
	private void updateLabels()
	{
		if (currentEditor != null)
		{
			RSyntaxTextArea textArea = currentEditor.editorPanel.textArea;
			int line = textArea.getCaretLineNumber() + 1;
			int offset = textArea.getCaretOffsetFromLineStart();
			int characterOffset = textArea.getCaretPosition();
			int tabSize = textArea.getTabSize();
			boolean usesSpaces = textArea.getTabsEmulated();
			
			int selection = textArea.getSelectionEnd() - textArea.getSelectionStart(); 
			
			File file = currentEditor.getContentSourceFile();
			filePathLabel.setText(file != null ? file.getAbsolutePath() : "");
			syntaxStyleLabel.setText(currentEditor.currentStyle);
			caretPositionLabel.setText(line + " : " + offset + " : " + characterOffset + (selection > 0 ? " [" + selection + "]" : ""));
			encodingModeLabel.setText(currentEditor.contentCharset.displayName());
			spacingModeLabel.setText((usesSpaces ? "Spaces: " : "Tabs: ") + tabSize);
		}
		else
		{
			syntaxStyleLabel.setText("");
			filePathLabel.setText("");
			caretPositionLabel.setText("");
			encodingModeLabel.setText("");
			spacingModeLabel.setText("");
		}
	}
	
	private void updateActionsIfCurrent(EditorHandle handle)
	{
		if (handle == currentEditor)
			updateActionEditorStates();
	}

	/**
	 * Attempts to close an editor.
	 */
	private void attemptToCloseEditor(EditorHandle handle)
	{
		if (editorCanClose(handle))
			removeEditorByTab(handle.editorTab);
	}
	
	/**
	 * Decides whether an editor can close or not.
	 * @return true if handled, false to halt closing.
	 */
	private boolean editorCanClose(EditorHandle handle)
	{
		if (!handle.needsToSave())
			return true;
		
		Boolean saveChoice = modal(this, utils.getWindowIcons(), 
			language.getText("texteditor.action.save.modal.title"),
			containerOf(label(language.getText("texteditor.action.save.modal.message", handle.editorTab.getTabTitle()))), 
			utils.createChoiceFromLanguageKey("texteditor.action.save.modal.option.save", true),
			utils.createChoiceFromLanguageKey("texteditor.action.save.modal.option.nosave", false),
			utils.createChoiceFromLanguageKey("doomtools.cancel", null)
		).openThenDispose();
		
		if (saveChoice == null)
			return false;
		
		if (saveChoice)
			return saveEditor(handle);
		return true;
	}

	private boolean saveEditor(EditorHandle handle)
	{
		File editorFile;
		if ((editorFile = handle.contentSourceFile) == null)
		{
			editorFile = utils.chooseFile(this, getLastPathKey(), 
				language.getText("texteditor.action.save.title", handle.editorTab.getTabTitle()), 
				language.getText("texteditor.action.save.approve"), 
				getSaveFileTypes()
			);
			
			if (editorFile == null)
				return false;
		}
		
		return saveEditorToFile(handle, editorFile);
	}

	private boolean saveEditorToFile(EditorHandle handle, File targetFile)
	{
		Charset targetCharset = handle.contentCharset;
		String content = handle.editorPanel.textArea.getText();
	
		StringReader reader = new StringReader(content);
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile), targetCharset)) {
			IOUtils.relay(reader, writer, 8192);
		} catch (IOException e) {
			error(this, language.getText("texteditor.action.save.error", targetFile.getAbsolutePath()));
			return false;
		}
		
		handle.onSaveChange(targetFile);
		updateActionStates();
		if (listener != null)
			listener.onSave(handle);
		return true;
	}

	/**
	 * The listener.
	 */
	public interface Listener
	{
		/**
		 * Called on a current editor changing focus.
		 * @param handle the new current handle.
		 */
		void onCurrentEditorChange(EditorHandle handle);
		
		/**
		 * Called when an editor is opened or created.
		 * Assume that the editor was successfully opened.
		 * Can be called multiple times, if many at once were opened.
		 * @param handle the handle opened.
		 */
		void onOpen(EditorHandle handle);
		
		/**
		 * Called when an editor is saved.
		 * Assume that the editor was successfully saved.
		 * Can be called multiple times, if many at once were saved.
		 * @param handle the handle saved.
		 */
		void onSave(EditorHandle handle);
		
		/**
		 * Called when an editor is closed.
		 * Assume that all precautions and checks were taken to ensure that it can be closed.
		 * Can be called multiple times, if many at once were closed.
		 * @param handle the handle closed.
		 */
		void onClose(EditorHandle handle);
	}
	
	/**
	 * Editor handle.
	 */
	public class EditorHandle
	{
		private Icon savedIcon;
		private Icon unsavedIcon;
		
		/** Connected file. Can be null. */
		private File contentSourceFile;
		/** Buffer charset, for saving. */
		private Charset contentCharset;
		/** Timestamp of last change to buffer. */
		private long contentLastModified;
		/** Timestamp of last change to file. */
		private long contentSourceFileLastModified;
		/** Current RSyntaxTextArea style. */
		private String currentStyle;
		
		/** Tab component panel. */
		private EditorTab editorTab;
		/** Editor panel. */
		private EditorPanel editorPanel;
		
		private EditorHandle(String title, String styleName, RSyntaxTextArea textArea)
		{
			this.savedIcon = icons.getImage("script.png");
			this.unsavedIcon = icons.getImage("script-unsaved.png");

			this.contentSourceFile = null;
			this.contentCharset = Charset.defaultCharset();
			this.contentLastModified = -1L;
			this.contentSourceFileLastModified = -1L;
			this.currentStyle = styleName;
			
			this.editorTab = new EditorTab(savedIcon, title, (c, e) -> attemptToCloseEditor(this));
			this.editorPanel = new EditorPanel(textArea);
			textArea.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void removeUpdate(DocumentEvent e) 
				{
					onChange();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) 
				{
					onChange();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) 
				{
					onChange();
				}
			});
			
			textArea.setSyntaxEditingStyle(styleName);
		}
		
		private EditorHandle(File contentSourceFile, Charset sourceCharset, String styleName, RSyntaxTextArea textArea)
		{
			this(contentSourceFile.getName(), styleName, textArea);
			this.contentSourceFile = contentSourceFile;
			this.contentCharset = sourceCharset;
			this.contentLastModified = contentSourceFile.lastModified();
			this.contentSourceFileLastModified = contentSourceFile.lastModified();
			this.editorTab.setTabIcon(savedIcon);
		}

		/**
		 * Changes the encoding of the editor.
		 * @param newCharset the new charset.
		 */
		public void changeEncoding(Charset newCharset)
		{
			contentCharset = newCharset;
			updateLabels();
		}
		
		/**
		 * Changes the syntax style.
		 * @param styleName the new style.
		 */
		public void changeStyleName(String styleName)
		{
			currentStyle = styleName;
			editorPanel.textArea.setSyntaxEditingStyle(styleName);
			updateLabels();
		}
		
		/**
		 * @return the editor tab name.
		 */
		public String getEditorTabName()
		{
			return editorTab.getTabTitle();
		}

		/**
		 * @return the source file. Can be null.
		 */
		public File getContentSourceFile() 
		{
			return contentSourceFile;
		}

		/**
		 * @return the editor content.
		 */
		public String getContent()
		{
			return editorPanel.textArea.getText();
		}

		/**
		 * @return true if this editor has unsaved data.
		 */
		public boolean needsToSave()
		{
			return contentLastModified > contentSourceFileLastModified;
		}
		
		// Should call to update title on tab and timestamps.
		private void onSaveChange(File path)
		{
			editorTab.setTabIcon(savedIcon);
			editorTab.setTabTitle(path.getName());
			contentSourceFile = path;
			contentSourceFileLastModified = path.lastModified();
		}
		
		private void onChange()
		{
			editorTab.setTabIcon(unsavedIcon);
			contentLastModified = System.currentTimeMillis();
			updateActionsIfCurrent(this);
		}

	}

	/**
	 * A single editor tab.
	 */
	protected class EditorTab extends JPanel
	{
		private static final long serialVersionUID = 6056215456163910928L;
		
		private JLabel titleLabel;
		private JButton closeButton;
		
		private EditorTab(Icon icon, String title, ComponentActionHandler<JButton> closeHandler)
		{
			this.titleLabel = label(JLabel.LEADING, icon, title);
			this.closeButton = apply(button(icons.getImage("close-icon.png"), closeHandler), (b) -> {
				b.setBorder(null);
				b.setOpaque(false);
			});
			setOpaque(false);
			containerOf(this, (Border)null, new FlowLayout(FlowLayout.LEADING, 8, 0),
				node(titleLabel),
				node(closeButton)
			);
		}
		
		private void setTabIcon(Icon icon)
		{
			titleLabel.setIcon(icon);
		}
		
		private void setTabTitle(String title)
		{
			titleLabel.setText(title);
		}
		
		private String getTabTitle()
		{
			return titleLabel.getText();
		}
		
	}

	/**
	 * A single editor panel.
	 */
	protected class EditorPanel extends JPanel
	{
		private static final long serialVersionUID = -1623390677113162251L;
		
		private RTextScrollPane scrollPane;
		private RSyntaxTextArea textArea;
		
		private EditorPanel(RSyntaxTextArea textArea)
		{
			this.textArea = textArea;
			this.scrollPane = new RTextScrollPane(textArea);
			
			textArea.addCaretListener((e) -> {
				updateTextActionStates();
				updateLabels();
			});
			
			textArea.addHyperlinkListener((hevent) -> {
				if (hevent.getEventType() != HyperlinkEvent.EventType.ACTIVATED)
					return;

				try {
					SwingUtils.browse(hevent.getURL().toURI());
				} catch (IOException | URISyntaxException e1) {
					// Do nothing.
				}
			});
			
			containerOf(this, node(BorderLayout.CENTER, scrollPane));
		}
		
		public RSyntaxTextArea getTextArea() 
		{
			return textArea;
		}
		
	}
	
	/**
	 * An encapsulation of a series of editor settings for the current editor.  
	 */
	public static class EditorViewSettings
	{
		private int tabSize;
		private boolean tabsEmulated;
		private boolean lineWrap;
		private boolean wrapStyleWord;
		
		public EditorViewSettings()
		{
			setTabSize(4);
			setTabsEmulated(false);
			setLineWrap(false);
			setWrapStyleWord(false);
		}
		
		public int getTabSize() 
		{
			return tabSize;
		}

		public boolean isTabsEmulated() 
		{
			return tabsEmulated;
		}

		public boolean isLineWrap()
		{
			return lineWrap;
		}

		public boolean isWrapStyleWord()
		{
			return wrapStyleWord;
		}

		public void setTabSize(int tabSize)
		{
			this.tabSize = tabSize;
		}

		public void setTabsEmulated(boolean tabsEmulated) 
		{
			this.tabsEmulated = tabsEmulated;
		}

		public void setLineWrap(boolean lineWrap)
		{
			this.lineWrap = lineWrap;
		}

		public void setWrapStyleWord(boolean wrapStyleWord) 
		{
			this.wrapStyleWord = wrapStyleWord;
		}

		public void applyTo(RSyntaxTextArea target)
		{
			target.setTabSize(tabSize);
			target.setTabsEmulated(tabsEmulated);
			target.setLineWrap(lineWrap);
			target.setWrapStyleWord(wrapStyleWord);
		}
	}
	
	/**
	 * An encapsulation of a series of editor settings around code building.  
	 */
	public static class EditorSettings
	{
		public EditorSettings()
		{
			/*
			setMarginLineEnabled(boolean)
			setMarginLinePosition(int)
			setRoundedSelectionEdges(boolean)
			setHighlightCurrentLine(boolean)
			setAnimateBracketMatching(boolean)
			setAutoIndentEnabled(boolean)
			setBracketMatchingEnabled(boolean)
			setClearWhitespaceLinesEnabled(boolean)
			setCloseCurlyBraces(boolean)
			setCloseMarkupTags(boolean)
			setCodeFoldingEnabled(boolean)
			setEOLMarkersVisible(boolean)
			setHighlighter(Highlighter)
			setHighlightSecondaryLanguages(boolean)
			setShowMatchedBracketPopup(boolean)
			setUseFocusableTips(boolean)
			setWhitespaceVisible(boolean)
			setPaintTabLines(boolean)
			setMarkOccurrences(boolean)
			setMarkAllOnOccurrenceSearches(boolean)
			setMarkOccurrencesDelay(int)
			setPaintMatchedBracketPair(boolean)
			setPaintMarkOccurrencesBorder(boolean)
			setUseSelectedTextColor(boolean);

			setParserDelay(int)

			setHyperlinksEnabled(boolean)
			setLinkScanningMask(int)
			setLinkGenerator(LinkGenerator)

			 */
		}
	}
	
}
