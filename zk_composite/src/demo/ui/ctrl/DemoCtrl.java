/**
 * 
 */
package demo.ui.ctrl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;

import demo.ui.composite.ImageLabel;
import demo.ui.composite.ImageLabel.AfterEditEvent;

/**
 * @author ian
 *
 */

public class DemoCtrl extends GenericForwardComposer{

	
	private ImageLabel imgLbl;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		
		imgLbl.setTitle("new Title");
		imgLbl.setDescription("this is a desc!!!");
		imgLbl.setImagePath("/res/img/ShoppingCart-16x16.png");

		imgLbl.addEventListener(ImageLabel.ON_AFTER_EDIT, new EventListener<Event>(){
			public void onEvent(Event event) throws Exception {
				AfterEditEvent ilEvt = (AfterEditEvent) event;

				System.out.println("EVENT:" +
						ImageLabel.ON_AFTER_EDIT+ " : "+ilEvt.getDescription());
				ilEvt.getTitle();
			}});
		
	}

	
	
}
