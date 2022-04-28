package net.mtrop.doom.tools.gui.apps.swing.panels;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import net.mtrop.doom.tools.common.Common;
import net.mtrop.doom.tools.gui.managers.DoomToolsGUIUtils;
import net.mtrop.doom.tools.gui.managers.DoomToolsLanguageManager;
import net.mtrop.doom.tools.struct.swing.FormFactory.JFormField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import static javax.swing.BorderFactory.*;
import static net.mtrop.doom.tools.struct.swing.ContainerFactory.*;
import static net.mtrop.doom.tools.struct.swing.ComponentFactory.*;
import static net.mtrop.doom.tools.struct.swing.FormFactory.*;

/**
 * A Find/Replace panel.
 * @author Matthew Tropiano
 */
public class FindReplacePanel extends JPanel
{
	private static final long serialVersionUID = -4681647971661962906L;

	// =======================================================================

	private DoomToolsLanguageManager language;
	private DoomToolsGUIUtils utils;

	// =======================================================================
	
	private JFormField<String> findField;
	private JFormField<String> replaceField;

	private boolean backwards;
	private boolean selectionOnly;
	
	private JFormField<Boolean> matchCase;
	private JFormField<Boolean> wholeWord;
	private JFormField<Boolean> regularExpression;
	private JFormField<Boolean> wrapSearch;
	
	// =======================================================================
	
	private RTextArea currentTarget;
	private SearchContext context;
	private JLabel message;

	public FindReplacePanel()
	{
		this.language = DoomToolsLanguageManager.get();
		this.utils = DoomToolsGUIUtils.get();

		this.findField = stringField((unused) -> updateSearchContext());
		this.replaceField = stringField((unused) -> updateSearchContext());
		
		this.backwards = false;
		this.selectionOnly = false;
		
		this.wholeWord = checkBoxField(checkBox((c, e) -> updateSearchContext()));
		this.matchCase = checkBoxField(checkBox((c, e) -> updateSearchContext()));
		this.regularExpression = checkBoxField(checkBox((c, e) -> updateSearchContext()));
		this.wrapSearch = checkBoxField(checkBox((c, e) -> updateSearchContext()));
		
		this.currentTarget = null;
		this.context = null;
		this.message = label();
		
		JRadioButton forwardButton = radio(true, (c, e) -> {
			if (c.isSelected())
			{
				backwards = false;
				updateSearchContext();
			}
		});
		JRadioButton backButton = radio(false, (c, e) -> {
			if (c.isSelected())
			{
				backwards = true;
				updateSearchContext();
			}
		});
		JRadioButton allButton = radio(true, (c, e) -> {
			if (c.isSelected())
			{
				selectionOnly = false;
				updateSearchContext();
			}
		});
		JRadioButton selectedButton = radio(false, (c, e) -> {
			if (c.isSelected())
			{
				selectionOnly = true;
				updateSearchContext();
			}
		});
		group(forwardButton, backButton);
		group(allButton, selectedButton);
		
		forwardButton.setSelected(true);
		allButton.setSelected(true);
		
		containerOf(this, new BorderLayout(0, 4),
			node(BorderLayout.NORTH, containerOf(new GridLayout(3, 1, 0, 4),
				node(form(96)
					.addField(language.getText("texteditor.modal.find.field.find"), findField)
					.addField(language.getText("texteditor.modal.find.field.replace"), replaceField)
				),
				node(containerOf(new GridLayout(1, 2, 4, 0),
					node(containerOf(createTitledBorder(createEtchedBorder(), language.getText("texteditor.modal.find.field.direction"), TitledBorder.LEADING, TitledBorder.TOP), 
						node(form(112)
							.addField(language.getText("texteditor.modal.find.field.direction.forward"), radioField(forwardButton))
							.addField(language.getText("texteditor.modal.find.field.direction.backward"), radioField(backButton))
						)
					)),
					node(containerOf(createTitledBorder(createEtchedBorder(), language.getText("texteditor.modal.find.field.scope"), TitledBorder.LEADING, TitledBorder.TOP), 
						node(form(112)
							.addField(language.getText("texteditor.modal.find.field.scope.all"), radioField(allButton))
							.addField(language.getText("texteditor.modal.find.field.scope.selected"), radioField(selectedButton))
						)
					))
				)),
				node(containerOf(createTitledBorder(createEtchedBorder(), language.getText("texteditor.modal.find.field.options"), TitledBorder.LEADING, TitledBorder.TOP), new GridLayout(1, 2, 4, 4),
					node(form(112)
						.addField(language.getText("texteditor.modal.find.field.options.case"), matchCase)
						.addField(language.getText("texteditor.modal.find.field.options.wholeword"), wholeWord)
					),
					node(form(112)
						.addField(language.getText("texteditor.modal.find.field.options.wrap"), wrapSearch)
						.addField(language.getText("texteditor.modal.find.field.options.regex"), regularExpression)
					)
				))
			)),
			node(BorderLayout.CENTER, dimension(256, 16), message), // empty
			node(BorderLayout.SOUTH, containerOf(
				node(BorderLayout.CENTER, containerOf()), // empty
				node(BorderLayout.LINE_END, containerOf(new GridLayout(2, 2, 4, 4),
					node(utils.createButtonFromLanguageKey("texteditor.modal.find.button.find", (c, e) -> onFind())),
					node(utils.createButtonFromLanguageKey("texteditor.modal.find.button.replace", (c, e) -> onReplace())),
					node(utils.createButtonFromLanguageKey("texteditor.modal.find.button.replaceall", (c, e) -> onReplaceAll()))
				))
			))
		);
	}
	
	/**
	 * Sets the "find" field to a chunk of text and blanks out the "replace" field.
	 * @param text the text to set.
	 */
	public void setFind(String text)
	{
		findField.setValue(text);
		replaceField.setValue("");
	}
	
	/**
	 * Sets the target text area.
	 * @param target the target text area.
	 */
	public void setTarget(RTextArea target)
	{
		this.currentTarget = target;
		this.context = null;
	}
	
	private void updateSearchContext()
	{
		if (context == null)
			return;
		
		context.setMarkAll(false); // TODO: Add option for marking.
		
		context.setSearchFor(findField.getValue());
		context.setReplaceWith(replaceField.getValue());
		context.setSearchSelectionOnly(selectionOnly);
		context.setMatchCase(matchCase.getValue());
		context.setWholeWord(wholeWord.getValue());
		context.setRegularExpression(regularExpression.getValue());
		context.setSearchForward(!backwards);
		context.setSearchWrap(wrapSearch.getValue());
	}
	
	private void initContext()
	{
		if (context == null)
		{
			context = new SearchContext();
			updateSearchContext();
		}
	}
	
	private void showMessage(String text)
	{
		if (!Common.isEmpty(text))
			Toolkit.getDefaultToolkit().beep();
		message.setText(text);
	}
	
	private void handleResults(SearchResult result)
	{
		if (result.isWrapped())
			showMessage(language.getText("texteditor.modal.find.message.wrap")); 
		else if (!result.wasFound())
			showMessage(language.getText("texteditor.modal.find.message.noresult"));
		else
			showMessage("");
	}
	
	// Find next occurrence.
	private void onFind()
	{
		initContext();
		handleResults(SearchEngine.find(currentTarget, context));
	}
	
	// Replace current selection.
	private void onReplace()
	{
		initContext();
		handleResults(SearchEngine.replace(currentTarget, context));
	}
	
	// Replace current selection and find next and keep replacing.
	private void onReplaceAll()
	{
		initContext();
		handleResults(SearchEngine.replaceAll(currentTarget, context));
	}
	
}
