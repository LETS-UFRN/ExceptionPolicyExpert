package br.ufrn.lets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import br.ufrn.lets.exceptionexpert.ast.ParseAST;
import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;
import br.ufrn.lets.exceptionexpert.verifier.ImproperHandlingVerifier;
import br.ufrn.lets.exceptionexpert.verifier.ImproperThrowingVerifier;
import br.ufrn.lets.exceptionexpert.verifier.PossibleHandlersInformation;
import br.ufrn.lets.xml.ParseXMLECLRules;
import exceptionexpert.Activator;

public class StartupClass implements IStartup {
    
	protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	List<ReturnMessage> messages = new ArrayList<ReturnMessage>();
	
	//http://stackoverflow.com/questions/28481943/proper-logging-for-eclipse-plug-in-development
	private ILog log = Activator.getDefault().getLog();
	
	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
		    @Override
		    public void run() {
		    	
		    	
		    	log.log(new Status(Status.INFO, "br.ufrn.lets.exceptionExpert", "Initializing ExceptionPolicyExpert Plug-in..."));
		    	
		    	IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    IEditorPart part = page.getActiveEditor();
			    if (!(part instanceof AbstractTextEditor))
			      return;

//			    ExceptionExpertView view = (ExceptionExpertView) page.findView(ExceptionExpertView.ID);
				
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
					    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "Something wrong happened when processing modified files. " + e.getLocalizedMessage()));
							e.printStackTrace();
						}
				         
						if (!changedClasses.isEmpty()) {
							
							List<IProject> projects = new ArrayList<IProject>();

							for (IResource changedClass : changedClasses) {
								IProject project = changedClass.getProject();
								
								if(!projects.contains(project))
									projects.add(project);
							}
							
							try{
								for (IProject project : projects) {
									//FIXME discover when the XML file was last modified, to verify if need to compile it
									IFile file = project.getFile("/src-gen/contract.xml");
									Document doc = ParseXMLECLRules.parseDocumentFromXMLFile(file.getLocation().toString());
									RulesRepository.setRules(ParseXMLECLRules.parse(doc));
								}
								
								//If there are changed files
								for (IResource changedClass : changedClasses) {
									//Call the verifier for each changed class
									verifyHandlersAndSignalers(changedClass);
								}
								
							} catch (CoreException e) {
						    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "The workspace does not have the file /src-gen/contract.xml, with ECL rules. Plug-in aborted. " + e.getLocalizedMessage()));
								e.printStackTrace();
								
							} catch (IOException e) {
						    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "The workspace does not have the file /src-gen/contract.xml, with ECL rules. Plug-in aborted. " + e.getLocalizedMessage()));
								e.printStackTrace();
								
							} catch (SAXException e) {
						    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "Invalid format of contract.xml file. Plug-in aborted. " + e.getLocalizedMessage()));
								e.printStackTrace();
								
							} catch (ParserConfigurationException e) {
						    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "Invalid format of contract.xml file. Plug-in aborted. " + e.getLocalizedMessage()));
								e.printStackTrace();
							}
							
						}
						
					}
					
				};
				
				//Plug the listener in the workspace
				workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
				
		    }
		});	
	}
	
	/**
	 * Method that calls the verifications
	 * @param changedClass
	 */
	private void verifyHandlersAndSignalers(IResource changedClass) throws CoreException {

		deleteMarkers(changedClass);
		
		ICompilationUnit compilationUnit = (ICompilationUnit) JavaCore.create(changedClass);
		
		//AST Tree from changed class
		CompilationUnit astRoot = ParseAST.parse(compilationUnit);
		
		ASTExceptionRepresentation astRep = ParseAST.parseClassASTToExcpetionRep(astRoot);

		messages = new ArrayList<ReturnMessage>();
		
		//Rule 1
		ImproperThrowingVerifier improperThrowingVerifier = new ImproperThrowingVerifier(astRep, log);
		messages.addAll(improperThrowingVerifier.verify());

		//Rule 3
		ImproperHandlingVerifier improperHandlingVerifier = new ImproperHandlingVerifier(astRep, log);
		messages.addAll(improperHandlingVerifier.verify());

		//Rule 4
		PossibleHandlersInformation possibleHandlersInformation = new PossibleHandlersInformation(astRep, log);
		messages.addAll(possibleHandlersInformation.verify());

		try {
			for(ReturnMessage rm : messages) {
				createMarker(changedClass, rm);
			}
		} catch (CoreException e) {
	    	log.log(new Status(Status.ERROR, "br.ufrn.lets.exceptionExpert", "Something wrong happend when creating/removing markers. " + e.getLocalizedMessage()));
			e.printStackTrace();
			throw e;
		}
			
	}
	
	/**
	 * Delete all the markers of ExceptionPolicyExpert type
	 * @param res Resource (class) to delete the marker
	 * @throws CoreException 
	 */
	private static void deleteMarkers(IResource res) throws CoreException {
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		problems = res.findMarkers("br.ufrn.lets.view.ExceptionPolicyExpertId", true, depth);

		for (int i = 0; i < problems.length; i++) {
			problems[i].delete();
		}
	}
	
	/**
	 * Create a marker for each returned message (information or violation)
	 * @param res Resource (class) to attach the marker
	 * @param rm Object with the marker message and marke line
	 * @throws CoreException
	 */
	public static void createMarker(IResource res, ReturnMessage rm)
			throws CoreException {
		
		IMarker marker = null;
		marker = res.createMarker("br.ufrn.lets.view.ExceptionPolicyExpertId");
		marker.setAttribute(IMarker.TEXT, rm.getMessage());
		marker.setAttribute(IMarker.MESSAGE, rm.getMessage());
		marker.setAttribute(IMarker.LINE_NUMBER, rm.getLineNumber());
		marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
	}

	public ILog getLog() {
		return log;
	}

	public void setLog(ILog log) {
		this.log = log;
	}
	
	
}
