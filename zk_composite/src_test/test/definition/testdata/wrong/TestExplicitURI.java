/**
 * 
 */
package test.definition.testdata.wrong;

import org.zkoss.composite.Composite;
import org.zkoss.zk.ui.IdSpace;

import test.definition.testdata.correct.MySuperDiv;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
@Composite(macroURI="~./partial/TestExplicitURI.zul" )
public class TestExplicitURI extends MySuperDiv implements IdSpace {

}
