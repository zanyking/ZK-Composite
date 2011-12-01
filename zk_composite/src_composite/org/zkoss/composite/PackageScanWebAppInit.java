/**
 * 
 */
package org.zkoss.composite;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * @author Ian YT Tsai(zanyking)
 *
 */
public class PackageScanWebAppInit implements WebAppInit {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.util.WebAppInit#init(org.zkoss.zk.ui.WebApp)
	 */
	@Override
	public void init(WebApp wapp) throws Exception {
		CompositeCtrls.scan(wapp);
	}

}
