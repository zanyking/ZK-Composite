/**
 * 
 */
package demo.ui.composite;

import org.zkoss.composite.Composite;
import org.zkoss.composite.Composites;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.Window;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
@Composite( name="mywin",  macroURI="~./partial/_MyWindowA.zul")
public class MyWindowA extends Window implements IdSpace {

	
	
	
	
	
	public MyWindowA(){
		Composites.doCompose(this, null);
	}
	
}
