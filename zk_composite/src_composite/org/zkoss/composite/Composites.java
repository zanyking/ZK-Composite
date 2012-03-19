/**
 * this class is designed for a sub framework with a bunch of Utilities API to achieve better design and easy development in ZK Framework.
 */
package org.zkoss.composite;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.select.Selectors;

/**
 * 
 * A Utility class to help application developer to implement a Composite in ZK Framework 6.0.<br> 
 * For implementing a composite in ZK, you can follow the steps bellow:<br>
 * <ol>
 * <li> Make sure your component class extends a ZK Component, such as {@link org.zkoss.zul.Div} or {@link org.zkoss.zul.Window} 
 * <li> Make sure your component class implements {@link org.zkoss.zk.ui.IdSpace} to avoid id conflict problem.
 * <li> Call one of the <i>doCompose(...)</i> method in this class  
 * </ol>
 * <p>
 * The <i>doCompose(...)</i> process will parse {@link Composite} annotation (which supposed to annotate on your class) and gather the macroURI information for 
 * {@link Executions}'s <i>createComponents(...)</i> method series to generate Composite's content components, 
 * then it will use ZK6 {@link Selectors} wiring API to wire variables and event listening methods.
 * </p>
 * @since ZK 6.0
 * @author Ian YT Tsai(zanyking)
 *
 */
public class Composites {
	private Composites(){}//Utility class
	static final DefCache DEF_CACHE = new DefCache();
	/**
	 * current value is: "org.zkoss.composite.packageScan"
	 */
	public static final String LIB_PROPERTY_SCAN_PACKAGE = "org.zkoss.composite.packageScan";

	/**
	 * This method will make the given component instance become a ZK Composite(an ZK MVC Controller). <br>
	 * If the instance's class(Composite class) has {@link Composite} annotation, this method will based on the given meta to generate content children.<br>
	 * If there's no proper zuml content provided, this method will assume the application developer will assemble the content Component tree themselves.  
	 * 
	 * @since ZK 6.0
	 *  @param composite the composite instance that need to be processed
	 *  @param args ZK parser Context variables, the value inside can be accessed by EL[arg.varName] in zuml.(can be null)  
	 */
	public static void doCompose(Component composite,  Map args){
		CompositeDef def = 
			DEF_CACHE.get(composite.getClass(), WebApps.getCurrent());
		doCompose( def, composite, composite, args);
	}
	
	/**
	 * @param composite
	 * @param macroUriDef
	 * @param args
	 */
	private static void doCompose(CompositeDef macroUriDef, Component composite, Object controller,  Map args){
		if(macroUriDef!=null && 
			macroUriDef.zulContent!=null && 
			!macroUriDef.zulContent.isEmpty()){
			Executions.createComponentsDirectly(macroUriDef.zulContent, null, composite, args);	
		}
		wireController(composite, controller);
		
	}
	
	/**
	 * This method will make the given component instance become a Composite(an ZK MVC Controller)
	 * which content children rendering is based on a zuml file.<br>
	 * 
	 * @since ZK 6.0
	 * @param url the url where your content zul comes from
	 * @param composite the composite instance
	 * @param arg ZK parser Context variables, the value inside can be accessed by EL[arg.varName] in zuml.  (can be null)
	 * @throws IOException 
	 */
	public static void doCompose(URL url, Component composite, Map arg) throws IOException{
		String text = CompositeCtrls.readTextContent(url);
		doComposeDirectly(text, null, composite, arg);
	}
	
	/**
	 * This method will make the given component instance become a ZK Composite(an ZK MVC Controller) which content children rendering is based on a zuml file.<br>
	 * use a ZK Context path (Web context + ZK resource Context) to allocate zuml content to process given composite.<>  
	 * 
	 * @since ZK 6.0
	 * @param path a web context uri or ZK's resource path ex: "~./ABC.zul" 
	 * @param composite the composite component instance
	 * @param args ZK parser Context variables, the value inside can be accessed by EL[arg.varName] in zuml.   (can be null)
	 */
	public static void doCompose(String path, Component composite, Map args) {
		Executions.createComponents(path, composite, args);
		wireController(composite, composite);
	}
	
	/**
	 * This method will make the given component instance become a ZK Composite(an ZK MVC Controller)
	 * which content children rendering is based on a zuml file.<br>
	 * 
	 * This doCompose implementation will call {@link Executions#createComponentsDirectly(String, String, Component, Map)} to generate content for given composite instance<br>
	 * 
	 * @since ZK 6.0
	 * @param textContent the zul content for composite, must follow xml format 
	 * @param extention the file extension to  tell ZK parser how to parse this page.
	 * @param composite the composite instance that need to be processed
	 * @param args ZK parser Context variables, the value inside can be accessed by EL[arg.varName] in zuml.  (can be null)
	 */
	public static void doComposeDirectly(String textContent, String extension, Component composite,  Map args){
		Executions.createComponentsDirectly(textContent, extension, composite, args);	
		wireController(composite, composite);
	}
	/**
	 * 
	 * @param comp
	 * @param controller
	 */
	private static void wireController(Component comp, Object controller){
		Selectors.wireVariables(comp, controller, null);
		Selectors.wireComponents(comp, controller, true);
		Selectors.wireEventListeners(comp, controller);		
	}
	
	
}//end of class...



