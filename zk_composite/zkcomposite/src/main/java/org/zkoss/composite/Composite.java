/**
 * 
 */
package org.zkoss.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * used to annotate a Composite component in ZK Framework 6.0
 * 
 * @since ZK 6.0
 * @author Ian YT Tsai(zanyking)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Composite{

	/**
	 * 
	 * By default, ZK Composite will use composite's <b>[FQCN] + ".zul"</b> as the macroURI path if not provided.<br>
	 * For example, a Composite class's FQCN is: <i>a.b.c.MyPanel.java</i><br> 
	 * then the template zuml required will be: <i>/a/b/c/MyPanel.zul</i>
	 * and the resource context which will be used to resolve the URI will be current application's class path.<br>
	 * this design will make the content ZUML been put side by it's Composite class, and makes the project's source management plus package management easier.
	 * 
	 * @return
	 */
	String macroURI() default "";

	/**
	 * will use composite's class.simpleName.toLowerCase() as composite's default component name in ZUML.<br> 
	 * NOTE: please make sure the given name is valid in XML
	 * 
	 * @return the tag name which will be used in ZUL
	 */
	String name() default "";
	
}
