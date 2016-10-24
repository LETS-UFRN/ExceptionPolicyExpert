package br.ufrn.lets.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class ExceptionExpertView extends ViewPart {

	public static final String ID = "br.ufrn.lets.view.ExceptionExpertViewId";

	private Text textView;
    
    @Override
    public void createPartControl(Composite parent) {       
    	System.out.println("A view iniciou!");
    	
    	GridLayout layout = new GridLayout(5, false);
        parent.setLayout(layout);
        
        textView = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        textView.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
        | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL));
    }

    
	@Override
	public void setFocus() {
		
	}

	public Text getTextView() {
		return textView;
	}

	public void setTextView(Text textView) {
		this.textView = textView;
	}

}