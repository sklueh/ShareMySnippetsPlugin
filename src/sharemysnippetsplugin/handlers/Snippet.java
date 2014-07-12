package sharemysnippetsplugin.handlers;

import java.io.Serializable;

/**
 * Snippet
 * 
 * @author Sebastian Klüh (http://sklueh.de)
 */
public class Snippet implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String id = "";
	public String language = "";
	public String code = "";
	public String caption = "";
	  
	public String toString()
	{
	    return this.language + " " + this.caption;
	}
}