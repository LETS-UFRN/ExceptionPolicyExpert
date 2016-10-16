package br.ufrn.lets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

import br.ufrn.lets.exceptionexpert.ast.ParseAST;
import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.verifier.VerifyHandler;
import br.ufrn.lets.exceptionexpert.verifier.VerifySignaler;
import br.ufrn.lets.xml.ParseXML;

public class StartupClass implements IStartup {

	List<Rule> rules;
	
	List<ReturnMessage> verifySignalerMessages = new ArrayList<ReturnMessage>();
	
	List<ReturnMessage> verifyHandlerMessages = new ArrayList<ReturnMessage>();

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
		    @Override
		    public void run() {
		    	
		    	//Parse XML documentation rules
				rules = ParseXML.parse();
		    	
				//Configures the change listener
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IResourceChangeListener listener = new IResourceChangeListener() {
					public void resourceChanged(IResourceChangeEvent event) {
						
						//we are only interested in POST_BUILD events
						if (event.getType() != IResourceChangeEvent.POST_BUILD)
				            return;
						
						IResourceDelta rootDelta = event.getDelta();
						
						//List with all .java changed files
						final ArrayList<IResource> changedClasses = new ArrayList<IResource>();
						
						//Visit the children to get all .java changed files
						IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
							public boolean visit(IResourceDelta delta) {
								//only interested in changed resources (not added or removed)
								if (delta.getKind() != IResourceDelta.CHANGED)
									return true;
								//only interested in content changes
								if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
									return true;
								IResource resource = delta.getResource();
								//only interested in files with the "java" extension
								if (resource.getType() == IResource.FILE && 
										"java".equalsIgnoreCase(resource.getFileExtension())) {
									changedClasses.add(resource);
								}
								return true;
							}
						};
						
						
						try {
							rootDelta.accept(visitor);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				         
						if (!changedClasses.isEmpty()) {
							//If there are changed files
							for (IResource changedClass : changedClasses) {
								//Call the verifier for each changed class
								verifyHandlersAndSignalers(changedClass);
							}
						}
						
					}
					
				};
				
				//Plug the listener in the workspace
				workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
		    }
		});	
	}
	
	private void verifyHandlersAndSignalers(IResource changedClass) {

		ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(changedClass);
		
		//AST Tree from changed class
		CompilationUnit astRoot = ParseAST.parse(compilationUnit);
		
		ASTExceptionRepresentation astRep = ParseAST.getThrowsStatement(astRoot);

		System.out.println("Rule 1");
		VerifySignaler.astRoot = astRoot;
		verifySignalerMessages = VerifySignaler.verify(astRep, rules);

		System.out.println("Rule 2");
		VerifyHandler.astRoot = astRoot;
		verifyHandlerMessages = VerifyHandler.verify(astRep, rules);
		
		try {
			//TODO improve this - create a superclass
			for(ReturnMessage rm : verifySignalerMessages) {
				createMarker(changedClass, rm);
			}
			for(ReturnMessage rm : verifyHandlerMessages) {
				createMarker(changedClass, rm);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	private static void deleteMarkers(IResource res){
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		try {
			problems = res.findMarkers(null, true, depth);
			
			for (int i = 0; i < problems.length; i++) {
				problems[i].delete();
			}

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public static IMarker createMarker(IResource res, ReturnMessage rm)
			throws CoreException {
		
		//TODO verify if it can be called in a more appropriate place
		deleteMarkers(res);
		
		IMarker marker = null;
		marker = res.createMarker("br.ufrn.lets.view.MyMarkerId");
		marker.setAttribute(IMarker.TEXT, rm.getMessage());
		marker.setAttribute(IMarker.MESSAGE, rm.getMessage());
		marker.setAttribute(IMarker.LINE_NUMBER, rm.getLineNumber());
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		return marker;
	}
	
	
}
