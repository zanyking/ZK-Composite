/**
 * 
 */
package org.zkoss.composite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.zkoss.lang.Library;
import org.zkoss.util.cpr.ClassFinder;
import org.zkoss.util.cpr.ResourceVisitor;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
public final class CompositeCtrls {
	private CompositeCtrls(){}//Utility class
	
	
	/**
	 * 
	 * @param compositeClz
	 * @return
	 */
	public static String getName(Class<? extends Component> compositeClz){
		return Composites.URI_DEF_CACHE.get(compositeClz, WebApps.getCurrent()).name;
	}
	/**
	 * 
	 * @param compositeClz
	 * @return
	 */
	public static String getMacroURI(Class<? extends Component> compositeClz){
		return Composites.URI_DEF_CACHE.get(compositeClz, WebApps.getCurrent()).macroURI;
	}
	
	/**
	 * 
	 * @param compClass
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static void register(Class<? extends Component> compClass, WebApp webapp) throws SecurityException, NoSuchMethodException{
		
		//must have public default constructor, otherwise zk parser cannot instantiate an instance. 
		//PS: none static inner class wont pass this check
		compClass.getConstructor();//see if we can get one.
		
		LanguageDefinition langDef =  LanguageDefinition.lookup("xul/html");
		ComponentDefinition def = getPredefinedSuperType(compClass, langDef);
		if(def !=null){
			MacroURIDef mUriDef = Composites.URI_DEF_CACHE.get(compClass, webapp);
			ComponentDefinition curDef = def.clone(langDef, mUriDef.name);
			curDef.setImplementationClass(compClass);
			langDef.addComponentDefinition(curDef);
		}
		
	}
	
	private static ComponentDefinition getPredefinedSuperType(
		Class<? extends Component> compClass, LanguageDefinition langDef){
		
		ComponentDefinition def = null;
		Class<?> suClz = compClass.getSuperclass();
		while(def==null){
			if(suClz==null || suClz.equals(AbstractComponent.class))
				return null;
			
			try{
				def = langDef.getComponentDefinition(suClz);
			}catch(DefinitionNotFoundException e){
				suClz = suClz.getSuperclass();
			}
		}
		return def;
	}
	
	/**
	 * 
	 * @param webapp
	 * @return
	 */
	public static List<Class<? extends Component>> scan(WebApp webapp){
		String _package_ = Library.getProperty(Composites.LIB_PROPERTY_SCAN_PACKAGE);
		return scan(_package_, webapp);
	}
	
	/**
	 * 
	 * @param _package_
	 * @param webapp
	 * @return
	 */
	public static List<Class<? extends Component>> scan(String _package_, final WebApp webapp){
		
		ClassFinder<Class<? extends Component>> finder = 
			new ClassFinder<Class<? extends Component>>(
				_package_, new AnnotatedCompositeClassAllocator(), true);
		
		final ArrayList<Class<? extends Component>> result = 
			new ArrayList<Class<? extends Component>>();
		
		finder.accept(new ResourceVisitor<Class<? extends Component>>() {
			public void visit(Class<? extends Component> compositeClass) {
				try {
					
					register(compositeClass, webapp);
					result.add(compositeClass);
					
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				} catch (NoSuchMethodException e) {
					// simply ignore this class, cause it has no public default constructor. 
				}
			}
		});
		return result;
	}
	
	
	/**
	 * 
	 * @param url
	 * @return null if IOException
	 */
	static String readTextContentIgnore(URL url){
		if(url==null)return null;
		String content;
		try {
			content = readTextContent(url.openStream(), 20*1024);
		} catch (IOException e) {
			return null;
		}
		return content;
	}
	private static String readTextContent(InputStream in, int chunkSize) throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			byte[] chunk = new byte[chunkSize];
			int readLen = -1;
			while( (readLen = in.read(chunk)) != -1){
				bout.write(chunk, 0, readLen);
			}
			return new String(bout.toByteArray(), "UTF-8");
			
		}finally{
			if(in !=null)in.close();
		}
	}

}//end of class...



/**
 * 
 * @author Ian YT Tsai(zanyking)
 *
 */
/*package*/ class MacroURIDef{
	
	public final String macroURI;
	public final String name;
	public final String zulContent;
	public final Class<? extends Component> klass;
	/**
	 * 
	 * @param name
	 * @param macroURI
	 * @param content
	 * @param superClass
	 */
	public MacroURIDef(String name, String macroURI, String content, Class<? extends Component> klass) {
		this.name = name;
		this.macroURI = macroURI;
		this.zulContent = content;
		this.klass = klass;
	}
	
}//end of class...

/**
 * 
 * @author Ian YT Tsai(zanyking)
 *
 */
/*package*/ class MacroURICache{
	private final Map<Class<? extends Component>, MacroURIDef> cache = 
		Collections.synchronizedMap(new HashMap<Class<? extends Component>, MacroURIDef>());
	
	/**
	 * 
	 * @param compClass the composite class that you want to define.
	 * @param webapp could be null if a webApp is not applicable.
	 * @return 
	 */
	public MacroURIDef get(Class<? extends Component> compClass, WebApp webapp){
		if(compClass==null || 
			compClass.equals(AbstractComponent.class)||
			!AbstractComponent.class.isAssignableFrom(compClass)){
			
			throw new IllegalArgumentException("is null or not a sub-class of ZK AbstractComponent: "+compClass);
		}
		return get0(compClass, webapp, compClass);
	}
	
	
	
	
	/* drive horse
	 * macroUri & zulContent must always be set together!
	 */
	private MacroURIDef get0(Class<?> compClass, WebApp webapp, Class<?> oriClass){
		if(compClass==null || 
				compClass.equals(AbstractComponent.class)){//necessary  
				return null;
		}
		
		MacroURIDef mUriDef = cache.get(compClass);
		if(mUriDef!=null){
			return mUriDef;
		}
		
		String name = compClass.getSimpleName().toLowerCase();
		
		String macroUri = null;
		String zulContent = null;
		
		Composite anno = compClass.getAnnotation(Composite.class);
		if(anno!=null ){// define Composite explicitly according to source's Java Annotation declaration.
			if(!anno.name().isEmpty()){ 
				name = anno.name(); 
				if(name==null || name.isEmpty())
					throw new IllegalArgumentException(errPrefix(oriClass)+
						"Composite's component name shouldn't be declared as null or empty explicitly :"+ compClass);
			}
			if(!anno.macroURI().isEmpty()){// use explicit Composite declaration.
				macroUri = anno.macroURI();
				zulContent = getMacroURIContent(macroUri, compClass, webapp);
				if(zulContent==null)// loading failed
					throw new IllegalArgumentException(errPrefix(oriClass)+
						"the macroURI content retrieving of \"" +compClass+ "\" has failed, macroURI: "+macroUri);
			}
		}
		
		if(macroUri==null){// get macroURI according to class naming convention
			URL url = generateMacroURL(compClass);
			String content = CompositeCtrls.readTextContentIgnore(url);
			if(content!=null){
				macroUri = url.getPath();
				zulContent = content;
			}
		}
		
		if(zulContent == null){// inherit macroURI from Super class...
			MacroURIDef superDef = get0(compClass.getSuperclass(), webapp, oriClass);
			if(superDef!=null){
				macroUri = superDef.macroURI;
				zulContent = superDef.zulContent;	
			}
		}

		Class<? extends Component> compClz = compClass.asSubclass(Component.class);
		cache.put(compClz, mUriDef = new MacroURIDef(
			name, macroUri, zulContent, compClz));
		return mUriDef;
	}
	
	private static String errPrefix(Class<?> oriClass){
		return "error during resolving Composite Class \""+oriClass.getSimpleName()+"\": \n\t";
	}
	
	
	private static String getMacroURIContent(String uri, Class<?> compClass, WebApp webapp){
		//get text according to class path
		String text = CompositeCtrls.readTextContentIgnore(compClass.getResource(uri));
		if(text==null && webapp!=null){//get text according to ZK web context
			text = CompositeCtrls.readTextContentIgnore(webapp.getResource(uri));
		}
		return text;
	}
	/**
	 * Generate a macroURI based on FQCN, 
	 * for example: "a.b.c.MyPanel.java" will return "a/b/c/MyPanel.zul"
	 * 
	 * @param clz he composite class
	 * @return a macroURI based on FQCN 
	 */
	private static URL generateMacroURL(Class<?> clz){
		String pkgPath = clz.getPackage().getName().replace('.', '/');
		String path = pkgPath+"/"+clz.getSimpleName()+".zul";
		return clz.getClassLoader().getResource(path);
	}

}//end of class...
