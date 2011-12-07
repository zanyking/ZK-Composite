package test.definition;


import java.net.URL;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.zkoss.composite.Composite;
import org.zkoss.composite.CompositeCtrls;
import org.zkoss.composite.Composites;
import org.zkoss.zk.ui.Component;

import test.definition.testdata.correct.MyDiv;
import test.definition.testdata.correct.MySuperDiv;
import test.definition.testdata.wrong.NoProperMacroURI;
import test.definition.testdata.wrong.NotAZKCOmponent;
import test.definition.testdata.wrong.TestExplicitURI;

public class CompositeDef_TEST {

	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void scanPackage(){
		
		List<Class<? extends Component>> arr = 
			CompositeCtrls.scan("test.definition.testdata.correct", null);
		
		Assert.assertEquals(arr.size(), 1);
		Assert.assertEquals(arr.get(0), MySuperDiv.class);
	}
	

	@Test
	public void inheritanceTest(){
		String macroURI = CompositeCtrls.getMacroURIFromDefCache(MyDiv.class);
		URL url = MyDiv.class.getResource("MyDiv.zul");
//		System.out.println(macroURI);
//		System.out.println(url.getPath());
		Assert.assertEquals(macroURI, url.getPath());
	}
	
	@After
	public void tearDown() throws Exception {
	}

}