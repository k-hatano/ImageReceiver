import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.imageio.*;

import javax.swing.*;
import javax.swing.filechooser.*;

public class ImageReceiverImport {
	ImageReceiver parent;
	File lastFile = null;

	public ImageReceiverImport(ImageReceiver ImageReceiver) {
		parent = ImageReceiver;
	}

	public void showImportFileDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG file (.jpg)", "jpg", "jpeg"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Multi-Picture Object (.mpo)", "mpo"));
		if (lastFile != null) {
			chooser.setSelectedFile(lastFile);
		}
		int res = chooser.showOpenDialog(this.parent);
		if (res == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			lastFile = file;
			importImage(file.getAbsolutePath());
		}
	}

	public boolean importImage(String path) {
		File file = new File(path);
		this.parent.setTitle(file.getName());

		try {
			FileInputStream stream = new FileInputStream(file);
			System.out.println("");
			System.out.println("importImage available=" + stream.available());
			System.out.println("");

            BufferedImage image = ImageIO.read(new File(path));
			parent.appendStringToInfo("Size: " + (image.getWidth() + "x" + image.getHeight()));
			this.parent.imageReceiverCanvas.iFileImage = image;
			this.parent.imageReceiverCanvas.repaint();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

		return true;
	}

	public boolean importFromBinary(byte[] bytes) {
		System.out.println("");
		System.out.println("importFromBinary length=" + bytes.length);
		System.out.println("");
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

            BufferedImage image = ImageIO.read(stream);
			parent.appendStringToInfo("Size: " + (image.getWidth() + "x" + image.getHeight()));
			this.parent.imageReceiverCanvas.iFileImage = image;
			this.parent.imageReceiverCanvas.repaint();
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

		return true;
	}
}
