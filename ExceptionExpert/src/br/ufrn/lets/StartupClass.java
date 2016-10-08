package br.ufrn.lets;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
				
		    	//Markers - initial and temporary code
				IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);

				try {
					createMarker(resource);
					findMarkers(resource);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				//End of markers

		    }
		});	
	}
	
	public static IMarker createMarker(IResource res)
			throws CoreException {
		IMarker marker = null;
		marker = res.createMarker("br.ufrn.lets.view.MyMarkerId");
		marker.setAttribute(IMarker.TEXT, "this is one of my markers");
		marker.setAttribute(IMarker.MESSAGE, "My Marker Message");
		marker.setAttribute(IMarker.LINE_NUMBER, 4);
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		return marker;
	}
	
	public static void findMarkers(IResource res){
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		try {
			problems = res.findMarkers(null, true, depth);
			
			for (int i = 0; i < problems.length; i++) {
				System.out.println(problems[i].getAttribute(IMarker.LINE_NUMBER));
				System.out.println(problems[i].getAttribute(IMarker.MESSAGE));
				System.out.println(problems[i].exists());
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	

}
