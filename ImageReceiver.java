import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class ImageReceiver extends JFrame implements ActionListener {
	JPanel pHeaderPanel, pCanvasPanel;
	JMenuBar mbMenuBar;
	JMenu mFile,mServer;
	JMenuItem miOpen,miQuit,miOpenSendPage;
	ImageReceiverImport imageReceiverImport;
	ImageReceiverCanvas imageReceiverCanvas;
	ImageReceiverServer imageReceiverServer;
	
	ImageReceiver() {
		super();

		setSize(800,812);
		setLayout(new BorderLayout());
		setTitle("ImageReceiver");

		mbMenuBar = new JMenuBar();

		mFile = new JMenu("File");
		miOpen = new JMenuItem("Open...");
		miOpen.addActionListener(this);
		miOpen.setAccelerator(KeyStroke.getKeyStroke('O',KeyEvent.CTRL_MASK));
		mFile.add(miOpen);
		mFile.addSeparator();
		miQuit = new JMenuItem("Quit");
		miQuit.addActionListener(this);
		miQuit.setAccelerator(KeyStroke.getKeyStroke('Q',KeyEvent.CTRL_MASK));
		mFile.add(miQuit);
		mbMenuBar.add(mFile);

		mServer = new JMenu("Server");
		miOpenSendPage = new JMenuItem("Open Send Page");
		miOpenSendPage.addActionListener(this);
		mServer.add(miOpenSendPage);
		mbMenuBar.add(mServer);

		setJMenuBar(mbMenuBar);

		pCanvasPanel = new JPanel();
		pCanvasPanel.setLayout(new BorderLayout());

		imageReceiverCanvas = new ImageReceiverCanvas(this);
		pCanvasPanel.add("Center", imageReceiverCanvas);

		imageReceiverImport = new ImageReceiverImport(this);

		imageReceiverServer = new ImageReceiverServer(this);

		add("Center", pCanvasPanel);

		pHeaderPanel = new JPanel();
		pHeaderPanel.setLayout(new BorderLayout());

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				closeWindow();
			}
		});

		new DropTarget(this,new Dropper(this));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == miQuit) {
			closeWindow();
		} else if (arg0.getSource() == miOpen) {
			imageReceiverImport.showImportFileDialog();
		} else if (arg0.getSource() == miOpenSendPage) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(new URI("http://localhost:3000"));
			} catch (URISyntaxException ignore) {
				
			} catch (IOException ignore) {
				
			}
		}
	}

	public static void main(String[] argv){
		ImageReceiver imageReceiver = new ImageReceiver();
		imageReceiver.show();
	}

	public void closeWindow(){
		System.exit(0);
	}

	class Dropper extends DropTargetAdapter{
		ImageReceiver parent;

		Dropper(ImageReceiver imageReceiver){
			super();
			parent = imageReceiver;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void drop(DropTargetDropEvent arg0) {
			try {
				Transferable t = arg0.getTransferable();
				if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					arg0.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

					File file = ((java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor)).get(0);
					parent.imageReceiverImport.importImage(file.getAbsolutePath());
				}
			}
			catch (Exception ex){
				ex.printStackTrace(System.err);
			}
		}
	}
}
