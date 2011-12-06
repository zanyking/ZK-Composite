/**
 * 
 */
package org.zkoss.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Ian YT Tsai(zanyking)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Composite{

	/**
	 * will use composite's FQCN + ".zul" as the default macroURI.
	 * @return
	 */
	String macroURI() default "";

	/**
	 * 
	 * @return the tag name which will be used in ZUL
	 */
	String name() default "";
	
}
