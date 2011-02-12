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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardExportResourcesPage;
import org.eclipse.team.svn.core.resource.ILocalResource;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;


/**
 * The page of the Subversion Export Wizard.
 *
 * @author	Stephan Hochdoerfer <S.Hochdoerfer@bitExpert.de>
 */


@SuppressWarnings("restriction")
public class SubversionExportWizardPage extends WizardExportResourcesPage implements Listener
{
	private final String DIALOG_MSG = "Choose the directory the resources will be exported to";
	private String chosenDirectory;
	private Text textDestination, textMinRev, textMaxRev;
	private Button destBrowseBtn;
	private long maxRevisionNumber;
	protected IResource selectedProject;


	/**
	 * Creates a new {@link SubversionExportWizardPage}.
	 *
	 * @param IStructuredSelection
	 */
	public SubversionExportWizardPage(IStructuredSelection selection)
	{
		super("SVN Export by Revision", selection);
		this.setTitle("SVN Export by Revision");
		this.setDescription("Exports files based on their Revision numbers");

		@SuppressWarnings("rawtypes")
		Iterator selections = selection.iterator();
		while(selections.hasNext())
		{
			IResource oCurResource = (IResource) selections.next();
			if(IResource.PROJECT == oCurResource.getType())
			{
				this.selectedProject = oCurResource;
			}
		}

		this.maxRevisionNumber = Long.parseLong(getMaxRevNumber());
	}


	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event)
	{
	}


	/**
	 * @see org.eclipse.ui.dialogs.WizardDataTransferPage#queryOverwrite(java.lang.String)
	 */
	public String queryOverwrite(String pathString)
	{
		return null;
	}


	/**
	 * @see org.eclipse.ui.dialogs.WizardExportResourcesPage#createDestinationGroup(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createDestinationGroup(Composite parent)
	{
		Font font = parent.getFont();
		// destination specification group
		Composite destinationSelectionGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		destinationSelectionGroup.setLayout(layout);
		destinationSelectionGroup.setLayoutData(
			new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
			)
		);
		destinationSelectionGroup.setFont(font);
		Label destinationLabel = new Label(destinationSelectionGroup, SWT.NONE);
		destinationLabel.setText("To directory:");
		destinationLabel.setFont(font);

		// destination name entry field
		textDestination = new Text(
			destinationSelectionGroup, SWT.SINGLE | SWT.BORDER
		);
		textDestination.addListener(SWT.Modify, this);
		GridData data = new GridData(
			GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
		);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		textDestination.setLayoutData(data);
		textDestination.setFont(font);

		// destination browse button
		destBrowseBtn = new Button(destinationSelectionGroup, SWT.PUSH);
		destBrowseBtn.setText("Browse...");
		destBrowseBtn.addListener(SWT.Selection, this);
		destBrowseBtn.setFont(font);
		setButtonLayoutData(destBrowseBtn);
		destBrowseBtn.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dirDialog = new DirectoryDialog(getShell());
				dirDialog.setMessage(DIALOG_MSG);
				dirDialog.open();

				chosenDirectory = dirDialog.getFilterPath();
				if(chosenDirectory.charAt(chosenDirectory.length() - 1) == '\\')
				{
					textDestination.setText(chosenDirectory.replace('\\', '/'));
				}
				else
				{
					chosenDirectory = chosenDirectory + '\\';
					textDestination.setText(chosenDirectory.replace('\\', '/'));
				}
			}
		});

		new Label(parent, SWT.NONE); // vertical spacer

	}


	/**
	 * @see org.eclipse.ui.dialogs.WizardDataTransferPage#createOptionsGroup(org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptionsGroup(Composite parent)
	{
		Font font = parent.getFont();

		Group groupOptions = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		groupOptions.setLayout(layout);
		groupOptions.setLayoutData(
			new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
			)
		);
		groupOptions.setFont(font);
		groupOptions.setText("Options");

		Label labelMinRev = new Label(groupOptions, SWT.NONE);
		labelMinRev.setText("Min. Revision:");
		this.textMinRev = new Text(groupOptions, SWT.BORDER);
		this.textMinRev.setText("1");
		this.textMinRev.addListener(SWT.Modify, this);
		this.textMinRev.setLayoutData(
			new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
			)
		);

		Label labelMaxRev = new Label(groupOptions, SWT.NONE);
		labelMaxRev.setText("Max. Revision:");
		this.textMaxRev = new Text(groupOptions, SWT.BORDER);
		this.textMaxRev.setText(getMaxRevNumber());
		this.textMaxRev.addListener(SWT.Modify, this);
		this.textMaxRev.setLayoutData(
			new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
			)
		);
	}


	/**
	 * Returns the max revision number for the selected project.
	 */
	protected String getMaxRevNumber()
	{
		IResource resource   = this.selectedProject;
		ILocalResource local = SVNRemoteStorage.instance().asLocalResource(
			resource
		);
		this.maxRevisionNumber = local.getRevision();

		return "" + this.maxRevisionNumber;
	}


	/**
	 * Returns the revision number for the given resource.
	 *
	 * @param resource
	 * @return long
	 */
	private long getFileRevisionNumber(IResource resource)
	{
		ILocalResource local = SVNRemoteStorage.instance().asLocalResource(
			resource
		);

		return local.getRevision();
	}


	/**
	 * Creates given folder.
	 *
	 * @param path
	 */
	private void createFolder(String path)
	{
		java.io.File file = new java.io.File(path);
		file.mkdirs();
	}


	/**
	 * Exports the fiven file to the given location.
	 *
	 * @param object
	 * @param exportDestination
	 * @return boolean
	 */
	private boolean exportFile(Object object, String exportDestination)
	{
		if(object instanceof File)
		{
			File file         = (File) object;
			String filepath   = exportDestination +
				file.getFullPath().toString().substring(1);
			String folderPath = filepath.substring(
				0,
				filepath.lastIndexOf('/') + 1
			);

			createFolder(folderPath);

			try
			{
				InputStream in   = file.getContents();
				OutputStream out = new FileOutputStream(filepath);
				byte[] buf = new byte[in.available()];
				int len;
				while((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
			catch(IOException ex)
			{
				throw new RuntimeException();
			}
			catch (CoreException e)
			{
				MessageDialog.openError(
					getShell(),
					"Core Exception",
					e.getMessage()
				);
				return false;
			}
		}
		else
		{
			MessageDialog.openError(
				getShell(),
				"I/O Error",
				"Problems reading resources!"
			);
			return false;
		}

		return true;
	}


	/**
	 * Method that will collect all the files to export and will write them
	 * to the new export directory.
	 *
	 * @return boolean
	 */
	public boolean finish()
	{
		long lMaxRev = Long.parseLong(textMaxRev.getText());
		long lMinRev = Long.parseLong(textMinRev.getText());
		int noOfExportedFiles = 0;

		if(null == textDestination.getText() ||
				"" == textDestination.getText().trim())
		{
			return false;
		}

		if(null == textMaxRev.getText() || "" == textMaxRev.getText().trim())
		{
			return false;
		}

		if(null == textMinRev.getText() || "" == textMinRev.getText().trim())
		{
			return false;
		}

		String exportDestination   = textDestination.getText() + "/";
		@SuppressWarnings("rawtypes")
		List resourcesToBeExported = getSelectedResources();

		for(Object o : resourcesToBeExported)
		{
			if((getFileRevisionNumber((IResource) o) >= lMinRev) &&
				(getFileRevisionNumber((IResource) o) <= lMaxRev))
			{
				exportFile(o, exportDestination);
				noOfExportedFiles++;
			}
		}

		MessageDialog.openInformation(getShell(), "Success", noOfExportedFiles +
			" resources have been exported successfully");

		return true;
	}
}