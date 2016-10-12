package br.ufrn.lets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import br.ufrn.lets.exceptionexpert.ast.ParseAST;
import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.verifier.VerifyHandler;
import br.ufrn.lets.exceptionexpert.verifier.VerifySignaler;
import br.ufrn.lets.xml.ParseXML;

public class StartupClass implements IStartup {

	IResource resource;
	
	List<ReturnMessage> verifySignalerMessages = new ArrayList<ReturnMessage>();
	
	List<ReturnMessage> verifyHandlerMessages = new ArrayList<ReturnMessage>();

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
			    
		    	//Gets the file in edition
				resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
				
				//Configures the change listener
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IResourceChangeListener listener = new IResourceChangeListener() {
					public void resourceChanged(IResourceChangeEvent event) {
						
						//we are only interested in POST_BUILD events
						if (event.getType() != IResourceChangeEvent.POST_BUILD)
				            return;
						
						IResourceDelta rootDelta = event.getDelta();
						
						//Find the actual class in edition
						IResourceDelta docDelta = rootDelta.findMember(resource.getFullPath());
						
						//Return if there is no modification in the actual class in edition
						if (docDelta == null)
							return;
						
						//Call controller
						verifyHandlersAndSignalers(docDelta);
						
					}
					
				};
				workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
		    }
		});	
	}
	
	private void verifyHandlersAndSignalers(IResourceDelta docDelta) {

		IResource resource2 = docDelta.getResource();
		ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(resource2);
		
		//AST Tree from current editor
		CompilationUnit astRoot = ParseAST.parse(compilationUnit);
		
		//Parse XML documentation rules
		List<Rule> rules = ParseXML.parse();

		ASTExceptionRepresentation astRep = ParseAST.getThrowsStatement(astRoot);

		System.out.println("Rule 1");
		VerifySignaler.astRoot = astRoot;
		verifySignalerMessages = VerifySignaler.verify(astRep, rules);

		System.out.println("Rule 2");
		VerifyHandler.astRoot = astRoot;
		verifyHandlerMessages = VerifyHandler.verify(astRep, rules);
		
		try {
			for(ReturnMessage rm : verifySignalerMessages) {
				createMarker(resource, rm);
			}
			for(ReturnMessage rm : verifyHandlerMessages) {
				createMarker(resource, rm);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static IMarker createMarker(IResource res, ReturnMessage rm)
			throws CoreException {
		IMarker marker = null;
		marker = res.createMarker("br.ufrn.lets.view.MyMarkerId");
		marker.setAttribute(IMarker.TEXT, rm.getMessage());
		marker.setAttribute(IMarker.MESSAGE, rm.getMessage());
		marker.setAttribute(IMarker.LINE_NUMBER, rm.getLineNumber());
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		return marker;
	}
	
	
}
