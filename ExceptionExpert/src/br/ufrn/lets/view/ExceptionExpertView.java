package br.ufrn.lets.view;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import br.ufrn.lets.exceptionexpert.ast.ParseAST;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.SignalerClass;
import br.ufrn.lets.exceptionexpert.verifier.VerifyException;
import br.ufrn.lets.exceptionexpert.verifier.VerifySignaler;
import br.ufrn.lets.xml.ParseXML;

public class ExceptionExpertView extends ViewPart implements IDocumentListener {

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
		System.out.println("Focus");
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		System.out.println("View atualizada \\O/");

	}


	@Override
	public void documentChanged(DocumentEvent event) {
		System.out.println("View atualizada \\O/");

		//AST Tree from current editor
		CompilationUnit astRoot = ParseAST.parse();
		
		
		//Parse XML documentation rules
		List<Rule> rules = ParseXML.parse();
		
		
		 SignalerClass signaler = ParseAST.getThrowsStatement(astRoot);
		 
		 if (signaler != null) {
			 System.out.println("Rule 1");
			 VerifySignaler.verify(signaler, rules);
		 }
		
		//Verifiy if there is some rule related to the current class
//			VerifyException verifyException = new VerifyException();
//			if (verifyException.verifyRuleCurrentClass(ast)) {
//				
//				
//				
//			}
		
		//Pega o conteudo todo do documento em edição
		textView.setText(event.getDocument().get());
	}


	public Text getTextView() {
		return textView;
	}


	public void setTextView(Text textView) {
		this.textView = textView;
	}

}
