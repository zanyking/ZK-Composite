/**
 * 
 */
package test.definition.testdata.wrong;

import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zul.annotation.Composite;

import test.definition.testdata.correct.MySuperDiv;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
@Composite(macroURI="~./partial/TestExplicitURI.zul" )
public class TestExplicitURI extends MySuperDiv implements IdSpace {

}
