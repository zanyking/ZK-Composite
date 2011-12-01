/**
 * 
 */
package demo.ui.ctrl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;

import demo.ui.composite.ImageLabel;
import demo.ui.composite.ImageLabel.SubmitEvent;

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

		imgLbl.addEventListener(ImageLabel.ON_SUBMIT, new EventListener<Event>(){
			public void onEvent(Event event) throws Exception {
				SubmitEvent ilEvt = (SubmitEvent) event;

				System.out.println("EVENT:" +
						ImageLabel.ON_SUBMIT+ " : "+ilEvt.getDescription());
				ilEvt.getTitle();
			}});
		
	}

	
	
}
