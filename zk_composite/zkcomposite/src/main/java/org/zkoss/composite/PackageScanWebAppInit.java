/**
 * 
 */
package org.zkoss.composite;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.lang.Library;
import org.zkoss.util.cpr.Consts;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
public class PackageScanWebAppInit implements WebAppInit {
	
	
	private static final String PACKAGEs_REGEX = Consts.PACKAGE_REGEX+"([\\s]*[,][\\s]*"+Consts.PACKAGE_REGEX+")*";
	private static final Pattern PACKAGES_PTN = Pattern.compile(PACKAGEs_REGEX);
	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.WebAppInit#init(org.zkoss.zk.ui.WebApp)
	 */
	@Override
	public void init(WebApp wapp) throws Exception {
		String pkgStrs = Library.getProperty(Composites.LIB_PROPERTY_SCAN_PACKAGE);
		if(pkgStrs==null || (pkgStrs = pkgStrs.trim()).isEmpty()){
			return;
		}
		Matcher ma = PACKAGES_PTN.matcher(pkgStrs);
		if(!ma.matches())
			throw new IllegalArgumentException("the given package setting's format is not valid, " +
					"please make sure it can pass this regex check: " +PACKAGEs_REGEX+
					"\t pkgs: "+ pkgStrs);
		
		for(String pkgStr : pkgStrs.split("[,]")){
			CompositeCtrls.scan(pkgStr.trim(), wapp);
		}
		
	}

}
