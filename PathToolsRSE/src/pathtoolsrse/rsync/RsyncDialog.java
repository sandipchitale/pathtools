package pathtoolsrse.rsync;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class RsyncDialog extends TitleAreaDialog {

	private RsyncEndpoint fromRsyncEndpoint;
	private RsyncEndpoint toRsyncEndpoint;
	
	public RsyncDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Generate rsync configutaion");
		
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        GridLayout gridLayout = (GridLayout) parentComposite.getLayout();
        gridLayout.numColumns = 2; // 3;
        gridLayout.makeColumnsEqualWidth = false;

        GridData layoutData = (GridData) parentComposite.getChildren()[0].getLayoutData();
        layoutData.horizontalSpan = 2; //3;

		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.horizontalSpacing = 5;
		gridLayout.verticalSpacing = 5;

        
		return parentComposite;
	}

	

	@Override
	protected void okPressed() {

		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {


		((GridLayout) parent.getLayout()).numColumns++;

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

	}

	@Override
	protected void buttonPressed(int buttonId) {
		
		super.buttonPressed(buttonId);
	}

}
