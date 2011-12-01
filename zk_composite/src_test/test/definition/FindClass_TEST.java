/**
 * 
 */
package test.definition;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.composite.CompositeClassAllocator;
import org.zkoss.util.cpr.ClassFinder;
import org.zkoss.util.cpr.ResourceVisitor;
import org.zkoss.zk.ui.Component;

import demo.ui.composite.ImageLabel;
import demo.ui.composite.MyWindowA;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
public class FindClass_TEST {
	
	
	
	@Test
	public void testClasspathResourceFinder(){
		
		ClassFinder<Class<? extends Component>> finder = 
			new ClassFinder<Class<? extends Component>>("demo.ui.composite", 
					new CompositeClassAllocator(), true);
		
		final ArrayList<Class<? extends Component>> arr = 
			new ArrayList<Class<? extends Component>>();
		
		finder.accept(new ResourceVisitor<Class<? extends Component>>() {
			public void visit(Class<? extends Component> compositeClass) {
				System.out.println("Candidate Composite Class: "+
						compositeClass.getCanonicalName());
				arr.add(compositeClass);
				
			}
		});
		System.out.println("done..." );
		
		
		Assert.assertEquals(2, arr.size());
		Assert.assertEquals(ImageLabel.class, arr.get(0));
		Assert.assertEquals(MyWindowA.class, arr.get(1));
	}

}
