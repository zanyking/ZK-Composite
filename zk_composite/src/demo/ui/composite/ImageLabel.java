package demo.ui.composite;

import org.zkoss.composite.Composites;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.annotation.Composite;



@Composite(name="imglabel")
public class ImageLabel extends Div implements IdSpace {
	
	private Groupbox item;
	private Image labelImage;
	private Label titleLabel, descLabel;
	public ImageLabel(){
		Composites.doCompose(this, null);
	}
	
	private InplaceEditor fInplaceEditor;
	public void onClick$titleLabel(){
		if(fInplaceEditor==null){
			fInplaceEditor = new InplaceEditor();
			fInplaceEditor.setParent(item);
		}else{
			fInplaceEditor.onClick$cancelBtn();
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
		
		private Textbox editTitle, editDesc;
		private Button cancelBtn, submitBtn;
		
		public InplaceEditor(){
			Executions.createComponents(
					"/composite/InplaceEditor.zul", this, null);
			Components.wireVariables(this, this);
			Components.addForwards(this, this);
			editDesc.setValue(getDescription());
			editTitle.setValue(getTitle());
		}
		public void onClick$submitBtn(){
			setTitle(editTitle.getValue());
			setDescription(editDesc.getValue());
			Events.postEvent(new SubmitEvent());
			onClick$cancelBtn();
		}

		public void onClick$cancelBtn(){
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
