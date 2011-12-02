/**
 * 
 */
package org.zkoss.composite;

import java.io.InputStream;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.zkoss.spring.core.io.Resource;
import org.zkoss.spring.util.ClassUtils;
import org.zkoss.util.cpr.ResourceAllocator;
import org.zkoss.util.cpr.ResourceVisitor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.annotation.Composite;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
public class AnnotatedCompositeClassAllocator implements ResourceAllocator<Class<? extends Component>> {

	
	private static final String COMPOSITE_FQCN;
	static{
		String tempName = Composite.class.getCanonicalName();
		COMPOSITE_FQCN = ClassUtils.convertClassNameToResourcePath(tempName);
	}
	
	
	/**
	 * A class that matches conditions bellow will trigger {@link ResourceVisitor#visit(Object)}<br>
	 * 
	 * <ol>
	 * <li> has Annotation {@link Component}
	 * <li> Modifier is PUBLIC
	 * </ol>
	 * @throws IOException if any IO issue occurred.
	 * @throws RuntimeException other exception if class name is not correct or not a Component Class.
	 * 
	 */
	public void allocate(Resource resource, ResourceVisitor<Class<? extends Component>> visitor) {
		try {
			InputStream in = resource.getInputStream();
			try {
				ClassReader reader = new ClassReader(in);
				ClassNode clzNode = new ClassNode();
				reader.accept(clzNode, ClassReader.SKIP_DEBUG);
				
				ArrayList<AnnotationNode> annos = new ArrayList<AnnotationNode>();
				if(clzNode.visibleAnnotations!=null)
					annos.addAll(clzNode.visibleAnnotations);
				if(clzNode.invisibleAnnotations!=null)
					annos.addAll(clzNode.invisibleAnnotations);
				if(annos.isEmpty())return;
				boolean hasCompositeAnnotation = false;
				for(AnnotationNode anno : annos){
					if(anno.desc.contains(COMPOSITE_FQCN)){
						hasCompositeAnnotation = true;
						break;
					}
				}
				if(!hasCompositeAnnotation)return;// has no Composite Annotation
				if((clzNode.access & Opcodes.ACC_PUBLIC)==0)return;// this is not a public class, cannot instantiate.
				
				String fqcn = ClassUtils.convertResourcePathToClassName(clzNode.name);
				Class<?> clz = (Class<?>) Class.forName(fqcn);
				try{
					visitor.visit(clz.asSubclass(Component.class));	
				}catch(ClassCastException e){
					throw new ClassCastException(clz+" has \"Composite\" annotation but not a ZK Component!");
				}
				
			}finally{
				if(in!=null)in.close();
			}
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
		
	}

}
