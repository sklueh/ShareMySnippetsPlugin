package sharemysnippetsplugin.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

/**
 * SnippetManager
 * 
 * @author Sebastian Klüh (http://sklueh.de)
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshSnippetCache extends AbstractHandler 
{	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
      try
      {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Share-My-Snippets", "Please wait, fetching snippets...");
      }
      catch (ExecutionException e2)
      {
        e2.printStackTrace();
      }
		
      refreshCache();
      return null;
	}
	
	public static ArrayList<Snippet> refreshCache()
	{
		ArrayList<Snippet> resources = new ArrayList<Snippet>();
				
		File file = new File(System.getProperty("user.home") + "\\share-my-snippets-config.dat");
		touch(file);
		
		file.delete();
		touch(file);
		  
		String json_array = "";
		  
		try
		{
			json_array = getText("http://share-my-snippets.de/snippets_json.php");
			    
			JSONArray obj = (JSONArray)JSONValue.parse(json_array);
			for (Object element : obj) 
			{
			    	if (RefreshSnippetCache.isValid(element))
					{
						String snippet_id = ((JSONArray)element).get(0).toString();
						String snippet_caption = ((JSONArray)element).get(1).toString();
						String snippet_code = ((JSONArray)element).get(2).toString();
						String snippet_language = ((JSONArray)element).get(3).toString();
						
						Snippet tmpSnippet = new Snippet();
						tmpSnippet.id = snippet_id;
						tmpSnippet.language = snippet_language;
						tmpSnippet.code = snippet_code;
						tmpSnippet.caption = snippet_caption;
						resources.add(tmpSnippet);
					}
			  }
		}
		catch (Exception e1)
		{
		  e1.printStackTrace();
		}
		  
		try
		{
		  FileOutputStream fout = new FileOutputStream(file);
		  ObjectOutputStream oos = new ObjectOutputStream(fout);
		  oos.writeObject(resources);
		  oos.close();
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}
  
		return resources;
	}
	
	public static String getText(String url) throws Exception
	{
	    URL website = new URL(url);
	    URLConnection connection = website.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));	    
	    StringBuilder response = new StringBuilder();
	    String inputLine;
	    
	    while ((inputLine = in.readLine()) != null)
	    {
	      response.append(inputLine);
	    }
	    in.close();
	    
	    return response.toString();
	}
	
	public static boolean touch(File file)
	{
	    try
	    {
	      if (!file.exists()) 
	      {
	        new FileOutputStream(file).close();
	        return true;
	      }
	    }
	    catch (IOException localIOException) {}
	    return false;
	}
	
	public static boolean isValid(Object element)
	{
		return (((JSONArray)element).get(0) != null) && 
	            (((JSONArray)element).get(1) != null) && 
	            (((JSONArray)element).get(2) != null) && 
	            (((JSONArray)element).get(3) != null);
	}
}