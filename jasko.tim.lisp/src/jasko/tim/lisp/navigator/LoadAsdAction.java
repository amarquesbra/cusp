package jasko.tim.lisp.navigator;


import jasko.tim.lisp.*;
import jasko.tim.lisp.swank.*;
import jasko.tim.lisp.editors.actions.FileCompiler;
import jasko.tim.lisp.builder.LispBuilder;

import org.eclipse.core.resources.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;
import org.eclipse.core.resources.IResource;


public class LoadAsdAction implements IActionDelegate {
	IStructuredSelection selection;

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}

	public void run(IAction action) {
		if (selection != null && selection.isEmpty() == false 
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IFile) {
				IFile file = (IFile) obj;
				try{
					file.getParent().deleteMarkers(LispBuilder.MARKER_TYPE, false, IResource.DEPTH_INFINITE);					
					file.getParent().deleteMarkers(FileCompiler.COMPILE_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);					
				} catch (Exception e) {
					e.printStackTrace();
				}
				/*
				String filePath = file.getLocation().toOSString().replace("\\", "\\\\");
				String asdName = file.getName().replace(".asd", "");
				String command = "(progn (load \"" + filePath + "\") (asdf:oos 'asdf:load-op \"" + asdName + "\"))";
				*/
				SwankInterface swank = LispPlugin.getDefault().getSwank();
				//swank.sendEval(command, null);
				swank.sendLoadASDF(file.getLocation().toString(), new FileCompiler.CompileListener(null));
			}
		}
	}

	public IWorkbench getWorkbench() {
		return LispPlugin.getDefault().getWorkbench();
	}
}

