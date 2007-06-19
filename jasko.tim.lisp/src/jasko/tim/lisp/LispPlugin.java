package jasko.tim.lisp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jasko.tim.lisp.swank.*;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LispPlugin extends AbstractUIPlugin {
	
	private SwankInterface swank; 
	
	//The shared instance.
	private static LispPlugin plugin;
    
    private static String CUSP_VERSION = "0.0.0";
    private static String RELEASE_DATE = "0000.00.00";
    
    private static String CONSOLE_NAME = "jasko.tim.lisp.console";
	
	/**
	 * The constructor.
	 */
	public LispPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		cm =  new ColorManager(this);
		
		try {
			swank = new SwankInterface();
			Properties props = new Properties();
			
			InputStream in = LispPlugin.class.getResourceAsStream("/cusp.properties");
			props.load(in);
			in.close();
			
			CUSP_VERSION = props.getProperty("cusp.version");
			RELEASE_DATE = props.getProperty("cusp.release_date");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SwankInterface getSwank() {
		return swank;
	}
    
    public static String getVersion () {
        return CUSP_VERSION;
    }
    
    public static String getReleaseDate () {
        return RELEASE_DATE;
    }

	private MessageConsole getConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++){
			if (CONSOLE_NAME.equals(existing[i].getName())){
				return (MessageConsole) existing[i];				
			}
		}
		//no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
	
	/**
	 * Put string to console.
	 * @param str
	 */
	public void out(String str){
			MessageConsole myConsole = getConsole();
			MessageConsoleStream out = myConsole.newMessageStream();
			out.println(str);					
	}
	    
	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		cm.dispose();
		getSwank().disconnect();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static LispPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("jasko.tim.lisp", path);
	}
	
	
	private ColorManager cm;
	public ColorManager getColorManager() {
		return cm;
	}
	
	public String getPluginPath() {
		try {
			String path = FileLocator.resolve(Platform.getBundle("jasko.tim.lisp").getEntry("/") ).getFile();
			if (System.getProperty("os.name").toLowerCase().contains("windows")){
				if(path.matches("/\\w:/.*")){
					path = path.substring(1);
				}
			}
			return path;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
