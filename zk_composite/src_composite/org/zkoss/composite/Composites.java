/**
 * 
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
 * this class is designed for a sub framework with a bunch of Utilities API to achieve better design and easy development in ZK Framework.
 * 
 * 
 * @author Ian YT Tsai(zanyking)
 *
 */
public class Composites {
	private Composites(){}//Utility class
	static final CompositeDefCache DEF_CACHE = new CompositeDefCache();
	/**
	 * current value is: "org.zkoss.composite.packageScan"
	 */
	public static final String LIB_PROPERTY_SCAN_PACKAGE = "org.zkoss.composite.packageScan";
	
	
	/**
	 * 
	 * @param <T>
	 * @param clz
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static <T extends Component> T 
	getInstance(Class<T> compClass,  Map<String, ? extends Object> args){
		
		T instance;
		try {
			instance = compClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException("an exception occured while construction", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
		
		CompositeDef def = DEF_CACHE.get(compClass, WebApps.getCurrent());
		if(def==null){
			throw new IllegalArgumentException(
				"cannot find proper macroURI declaration plase check your composite zul spelling");
		}
		doCompose(def, instance, args);
		return instance; 
	}
	/**
	 * 
	 * @param composite
	 * @param compDef
	 * @param args
	 */
	private static void doCompose(CompositeDef compDef, Component composite,  Map args){
		doComposeDirectly(compDef.zulContent, null, composite, args);
	}
	
	/**
	 * This method will make the given component become a ZK MVC controller 
	 * which children rendering is based on a zul file.<br>
	 * the template must be located in class path /[FQCN].zul.<br>
	 * for example, a Composite classes FQCN is: a.b.c.MyPanel.java<br> 
	 * then the template zul must be: /a/b/c/MyPanel.zul
	 * 
	 * @param composite
	 * @param args
	 * @throws IOException
	 */
	public static void doCompose(Component composite,  Map args){
		CompositeDef def = 
			DEF_CACHE.get(composite.getClass(), WebApps.getCurrent());
		if(def==null){
			throw new IllegalArgumentException(
				"cannot find proper macroURI declaration plase check your composite zul spelling");
		}
		doCompose( def, composite, args);
	}
	/**
	 * 
	 * @param composite
	 * @param url
	 * @param args
	 * @throws IOException
	 */
	public static void doCompose(URL url, Component composite, Map args){
		String text = CompositeDefCache.readTextContentIgnore(url);
		if(text ==null){
			throw new IllegalArgumentException(" cannot get zul text content from url: "+url);
		}
		doComposeDirectly(text, null, composite, args);
	}
	/**
	 * @param path a web context uri or ZK's "~./" resource path
	 * @param composite
	 * @param args
	 * @throws IOException
	 */
	public static void doCompose(String path, Component composite,   Map args) {
		Executions.createComponents(path, composite, args);
		autowire(composite);
		Selectors.wireEventListeners(composite, composite); 
	}
	/**
	 * 
	 * @param zulContent
	 * @param extention
	 * @param composite
	 * @param args
	 */
	public static void doComposeDirectly(String zulContent, String extention, Component composite,  Map args){
		Executions.createComponentsDirectly(zulContent, extention, composite, args);
		autowire(composite);
		Selectors.wireEventListeners(composite, composite); 
	}
	/**
	 * 
	 * @param comp
	 */
	private static void autowire(Component comp){
		IdSpace spaceOwner = comp.getSpaceOwner();
		if(spaceOwner instanceof Page)
			Selectors.wireVariables((Page) spaceOwner, comp);
		else
			Selectors.wireVariables((Component) spaceOwner, comp);
	}
	
}//end of class...



