/*
 * Copyright (c) 2007-2011 bitExpert AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package de.bitexpert.eclipse.svn.svnexport.ui.wizards;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * Main wizard class to control the wizard`s workflow.
 *
 * @author	Stephan Hochdoerfer <S.Hochdoerfer@bitExpert.de>
 */


public class SubversionExportWizard extends Wizard implements IExportWizard
{
	private SubversionExportWizardPage page;
	protected IStructuredSelection     selection;
	protected IWorkbench               workbench;


	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		this.workbench = workbench;
		this.selection = selection;
		this.setWindowTitle("SVN Export By Revision");
	}


	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages()
	{
		page = new SubversionExportWizardPage(selection);
		addPage(page);
	}


	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 * @return boolean
	 */
	@Override
	public boolean performFinish()
	{
		return page.finish();
	}
}