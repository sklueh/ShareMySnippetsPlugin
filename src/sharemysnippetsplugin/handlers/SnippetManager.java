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
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;

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
	      IEditorPart editor_part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	      if ( editor_part instanceof ITextEditor ) 
	      {
	          final ITextEditor editor = (ITextEditor)editor_part;
	          IDocumentProvider provider = editor.getDocumentProvider();
	          IDocument document = provider.getDocument( editor.getEditorInput() );
	          ISelection selection = editor.getSelectionProvider().getSelection();
	          if ( selection instanceof TextSelection ) 
	          {
	              final TextSelection text_selection = (TextSelection)selection;
	              
	              //Insert snippet
	              document.replace( text_selection.getOffset(), text_selection.getLength(), ((Snippet)dialog.getFirstResult()).code);
	          }
	      }
	    }
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    }
	    
		return null;
	}
}