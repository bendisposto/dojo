package tddtrainer.handbook;

import java.awt.Desktop;
import java.io.File;

/**
 * shows the Handbook inside the application
 */
public class Handbook {
	/**
	 * shows the Handbook in PDF Viewer application 
	 * @throws Exception if the pdf can not be opened
	 */
	public void showPDF() throws Exception {
		// PDF zeigen            		
		if (Desktop.isDesktopSupported()) {
	        File myFile = new File("Nutzerhandbuch.pdf");
	        Desktop.getDesktop().open(myFile);
		} else {
			throw new Exception("Desktop.isDesktopSupported() = false");
		}
	}
}
