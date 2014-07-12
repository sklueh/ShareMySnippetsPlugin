package sharemysnippetsplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.text.IDocument;

/**
 * SnippetManager
 * 
 * @author Sebastian Klüh (http://sklueh.de)
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SnippetManager extends AbstractHandler 
{
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
	    try
	    {
	      //Snippet selection dialog
	      FilteredItemsSelectionDialog dialog = new SelectSnippetDialog(new Shell(), true, event);
	      dialog.setInitialPattern("");
	      dialog.open();
	      
	      //Getting current editor instance
	      IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	      IEditorPart part = editor;
	      ITextEditor editor_tmp = (ITextEditor)part;
	      IDocumentProvider dp = editor_tmp.getDocumentProvider();
	      IDocument doc = dp.getDocument(editor_tmp.getEditorInput());
	      int offset = doc.getLineOffset(doc.getNumberOfLines() - 4);
	      
	      //Insert snippet
	      doc.replace(offset, 0, ((Snippet)dialog.getFirstResult()).code + "\n");
	    }
	    catch (Exception ex) {}
	    
		return null;
	}
}