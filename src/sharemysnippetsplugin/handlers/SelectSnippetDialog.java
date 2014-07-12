package sharemysnippetsplugin.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import sharemysnippetsplugin.Activator;

/**
 * SelectSnippetDialog
 * 
 * @author Sebastian Klüh (http://sklueh.de)
 */
public class SelectSnippetDialog extends FilteredItemsSelectionDialog
{
	public static ArrayList<Snippet> resources = new ArrayList<Snippet>();
	
	@SuppressWarnings("unchecked")
	public SelectSnippetDialog(Shell shell, boolean multi, ExecutionEvent event)
	{
	    super(shell, multi);
	    
	    File file = new File(System.getProperty("user.home") + "\\share-my-snippets-config.dat");
	    boolean touched = RefreshSnippetCache.touch(file);
	    long fileAgeMilliseconds = new Date().getTime() - file.lastModified();
	    
	    if (fileAgeMilliseconds >86400000L || touched == true) //older than 1 day or touched
	    {
	      try
	      {
	    	resources.clear();
		    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		    MessageDialog.openInformation(window.getShell(), "Share-My-Snippets", "Your snippet cache is not up to date. Please wait, fetching snippets...");
		    resources = RefreshSnippetCache.refreshCache();
	    	  
	        FileOutputStream fout = new FileOutputStream(file);
	        ObjectOutputStream oos = new ObjectOutputStream(fout);
	        oos.writeObject(resources);
	        oos.close();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }
	    }
	    else
	    {
	      try
	      {
	        FileInputStream fin = new FileInputStream(file);
	        ObjectInputStream ois = new ObjectInputStream(fin);
	        resources = (ArrayList<Snippet>)ois.readObject();
	        ois.close();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }
	    }
	    
	    setTitle("Browse snippets :)");
	    setSelectionHistory(new ResourceSelectionHistory());
	}

	@Override
	protected IDialogSettings getDialogSettings() 
	{
	    IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection("Browse snippets on share-my-snippets.de");
	    
	    if (settings == null) 
	    {
	      settings = Activator.getDefault().getDialogSettings().addNewSection("Browse snippets on share-my-snippets.de");
	    }
	    return settings;
	}

	@Override
	protected IStatus validateItem(Object item) 
	{
		return Status.OK_STATUS;
	}

	@Override
	protected ItemsFilter createFilter() 
	{
	      return new ItemsFilter() 
	      {
	         public boolean matchItem(Object item) 
	         {
	            return matches(item.toString());
	         }
	         
	         public boolean isConsistentItem(Object item) 
	         {
	            return true;
	         }
	      };
	}

	@Override
	protected Comparator<?> getItemsComparator() 
	{
		return new Comparator<Object>() 
		{
	         public int compare(Object arg0, Object arg1) 
	         {
	            return arg0.toString().compareTo(arg1.toString());
	         }
	    };
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException 
	{
	      progressMonitor.beginTask("Searching", resources.size());
	      
	      for (Iterator<Snippet> iter = resources.iterator(); iter.hasNext();) 
	      {
	         contentProvider.add(iter.next(), itemsFilter);
	         progressMonitor.worked(1);
	      }
	      progressMonitor.done();
	}

	@Override
	public String getElementName(Object item) 
	{
		return item.toString();
	}
	
	@Override
	protected Control createExtendedContentArea(Composite parent) 
	{
		return null;
	}
	
	private void setSelectionHistory(ResourceSelectionHistory resourceSelectionHistory) {}
}