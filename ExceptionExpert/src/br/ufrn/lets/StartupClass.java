package br.ufrn.lets;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import br.ufrn.lets.view.ExceptionExpertView;


public class StartupClass implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
		    @Override
		    public void run() {
		    	IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    IEditorPart part = page.getActiveEditor();
			    if (!(part instanceof AbstractTextEditor))
			      return;
			    ITextEditor editor = (ITextEditor)part;
			    
			    IDocumentProvider dp = editor.getDocumentProvider();
			    IDocument doc = dp.getDocument(editor.getEditorInput());

		    	ExceptionExpertView view = (ExceptionExpertView) page.findView(ExceptionExpertView.ID);
				doc.addDocumentListener(view);
			    
		    }
		});	
	}

}
