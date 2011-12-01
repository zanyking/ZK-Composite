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
import org.zkoss.zul.annotation.Composite;

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
		return Composites.DEF_CACHE.get(compositeClz, WebApps.getCurrent()).name;
	}
	/**
	 * 
	 * @param compositeClz
	 * @return
	 */
	public static String getMacroURI(Class<? extends Component> compositeClz){
		return Composites.DEF_CACHE.get(compositeClz, WebApps.getCurrent()).macroURI;
	}
	
	/**
	 * 
	 * @param compClass
	 * @return
	 */
	public static void register(Class<? extends Component> compClass, WebApp webapp){
		CompositeDef compositeDef = Composites.DEF_CACHE.get(compClass, webapp);
		
		if(compositeDef==null)
			throw new IllegalArgumentException(" cannot get a proper macroURI, " +
				"please check the spelling of zul file or class hierarchy of given composite: "+compClass);
		
		resolveCompDefReg(compositeDef, LanguageDefinition.lookup("xul/html"));
		
	}
	
	private static void resolveCompDefReg(CompositeDef compositeDef, LanguageDefinition langDef){
		
		Stack<Class<?>> stack = new Stack<Class<?>>();
		stack.push(compositeDef.klass);
		ComponentDefinition ref = null, curDef = null ;
		Class<?> suClz;
		while(!stack.isEmpty()){
			suClz = stack.peek().getSuperclass();
			
			try{
				ref = langDef.getComponentDefinition(suClz);
				curDef = ref.clone(langDef, compositeDef.name);
				//TODO: currently I have to ignore it cause I shouldn't assume the concrete class will be ComponentDefinitionImpl
				//compdef.setDeclarationURL(url); 
				curDef.setImplementationClass((Class<? extends Component>) stack.pop());
				langDef.addComponentDefinition(curDef);
				
			}catch(DefinitionNotFoundException e){
				stack.push(suClz);
			}
		}
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
	public static List<Class<? extends Component>> 
	scan(String _package_, final WebApp webapp){
		
		ClassFinder<Class<? extends Component>> finder = 
			new ClassFinder<Class<? extends Component>>(
				_package_, new CompositeClassAllocator(), true);
		
		final ArrayList<Class<? extends Component>> result = 
			new ArrayList<Class<? extends Component>>();
		
		finder.accept(new ResourceVisitor<Class<? extends Component>>() {
			public void visit(Class<? extends Component> compositeClass) {
				register(compositeClass, webapp);
				result.add(compositeClass);
				
			}
		});
		return result;
	}
}//end of class...



/**
 * 
 * @author Ian YT Tsai(zanyking)
 *
 */
/*package*/ class CompositeDef{
	
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
	public CompositeDef(String name, String macroURI, String content, Class<? extends Component> klass) {
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
/*package*/ class CompositeDefCache{
	private final Map<Class<?>, CompositeDef> cache = 
		Collections.synchronizedMap(new HashMap<Class<?>, CompositeDef>());
	
	/**
	 * 
	 * @param compClass the composite class that you want to define.
	 * @param webapp could be null if a webApp is not applicable.
	 * @return 
	 */
	public CompositeDef get(Class<?> compClass, WebApp webapp){
		if(compClass==null || 
				compClass.equals(AbstractComponent.class)||
				!isSubClass(compClass, AbstractComponent.class)){
			throw new IllegalArgumentException("is null or not a sub-class of ZK AbstractComponent: "+compClass);
		}
		return get0(compClass, webapp, compClass);
	}
	
	
	
	
	/* drive horse
	 * macroUri & zulContent must always be set together!
	 */
	private CompositeDef get0(Class<?> compClass, WebApp webapp, Class<?> oriClass){
		if(compClass==null || 
				compClass.equals(AbstractComponent.class)){//necessary  
				return null;
		}
		
		CompositeDef def = cache.get(compClass);
		if(def!=null){
			return def;
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
			String content = readTextContentIgnore(url);
			if(content!=null){
				macroUri = url.getPath();
				zulContent = content;
			}
		}
		
		if(zulContent == null){// inherit macroURI from Super class...
			CompositeDef superDef = get0(compClass.getSuperclass(), webapp, oriClass);
			if(superDef!=null){
				macroUri = superDef.macroURI;
				zulContent = superDef.zulContent;
			}
		}
		if(zulContent!=null){
			cache.put(compClass, 
				def = new CompositeDef(
					name, macroUri, zulContent, (Class<? extends Component>) oriClass));	
		}
		return def;
	}
	
	private static String errPrefix(Class<?> oriClass){
		return "error during resolving Composite Class \""+oriClass.getSimpleName()+"\": \n\t";
	}
	
	
	private static String getMacroURIContent(String uri, Class<?> compClass, WebApp webapp){
		//get text according to class path
		String text = readTextContentIgnore(compClass.getResource(uri));
		if(text==null && webapp!=null){//get text according to ZK web context
			text = readTextContentIgnore(webapp.getResource(uri));
		}
		return text;
	}
	
	private static boolean isSubClass(Class<?> subClz, Class<?> superClz){
		try {
			subClz.asSubclass(superClz);
		} catch (ClassCastException e) {
			return false;
		}
		return true;
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
	/**
	 * Generate a macroURI based on FQCN, 
	 * for example: "a.b.c.MyPanel.java" will return "a/b/c/MyPanel.zul"
	 * 
	 * @param clz he composite class
	 * @return a macroURI based on FQCN 
	 */
	private static URL generateMacroURL(Class<?> clz){
		String path = clz.getName().replace('.', '/')+".zul";
		return clz.getClassLoader().getResource(path);
	}
}//end of class...
