package demo.ui.composite;

import org.zkoss.composite.Composites;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Listen;
import org.zkoss.zk.ui.select.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.annotation.Composite;



@Composite(name="imglabel")
public class ImageLabel extends Div implements IdSpace {
	
	@Wire
	private Groupbox item;
	@Wire
	private Image labelImage;
	@Wire
	private Label titleLabel;
	@Wire
	private Label descLabel;
	
	public ImageLabel(){
		Composites.doCompose(this, null);
	}
	
	private InplaceEditor fInplaceEditor;
	
	@Listen("onClick= #titleLabel")
	public void doTitleClick(){
		if(fInplaceEditor==null){
			fInplaceEditor = new InplaceEditor();
			fInplaceEditor.setParent(item);
		}else{
			fInplaceEditor.doCancel();
		}
	}	
	
	public void setTitle(String title) {
		titleLabel.setValue(title);
	}
	public void setDescription(String description) {
		descLabel.setValue(description);
	}
	public void setImagePath(String src) {
		labelImage.setSrc(src);
	}
	
	public String getImagePath() {
		return labelImage.getSrc();
	}
	public String getTitle() {
		return titleLabel.getValue();
	}
	public String getDescription() {
		return descLabel.getValue();
	}
	
	
	
	public class InplaceEditor extends Div implements IdSpace {
		@Wire
		private Textbox editTitle;
		@Wire
		private Textbox editDesc;
		@Wire
		private Button cancelBtn;
		@Wire
		private Button submitBtn;
		
		public InplaceEditor(){
			Composites.doCompose(this, null);
			editDesc.setValue(getDescription());
			editTitle.setValue(getTitle());
		}
		
		@Listen("onClick = #submitBtn")
		public void doSubmit(){
			setTitle(editTitle.getValue());
			setDescription(editDesc.getValue());
			Events.postEvent(new SubmitEvent());
			doCancel();
		}
		
		@Listen("onClick = #cancelBtn")
		public void doCancel(){
			this.detach();
			fInplaceEditor = null;
		}
	}
	public static final String ON_SUBMIT = "onSubmit";
	public class SubmitEvent extends Event{
		public SubmitEvent() {
			super(ON_SUBMIT, ImageLabel.this);
		}
		
		public String getImagePath() {
			return labelImage.getSrc();
		}
		public String getTitle() {
			return titleLabel.getValue();
		}
		public String getDescription() {
			return descLabel.getValue();
		}
	}
}
