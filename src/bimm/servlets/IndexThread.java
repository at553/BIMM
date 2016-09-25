package bimm.servlets;

import java.awt.CardLayout; 
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.impl.VisualWordsImageSearcher;
import net.semanticmetadata.lire.utils.AIMUtils;
import net.semanticmetadata.lire.utils.ImageUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import bimm.epad.cbir.CbirEpad;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageAnnotation;
import liredemo.SearchResultsTableModel;

public class IndexThread implements Runnable {

	private String imagePath;
	private String indexPath;
	private ImageSearchHits hits = null;
	private javax.swing.JLabel jLabelQueryImage = null;
	 private javax.swing.JTable resultsTable;
	 private SearchResultsTableModel tableModel;
	 private javax.swing.JProgressBar progressSearch;
	 private javax.swing.JScrollPane resultsPane;
	 private javax.swing.JPanel topPane;
	 
	 private javax.swing.JMenu rerankMenu;
	 private javax.swing.JMenu researchMenu;
	 private javax.swing.JMenuItem rerankFeature;
	 ArrayList<Document> positives;
	 ArrayList<Document> negatives;
	 
	 
	public ImageSearchHits getHits() {
		return hits;
	}

	private CbirEpad cbir = null;
	public IndexThread(String imagePath/*, CbirEpad cbir, JLabel jLabelQueryImage, JTable resultsTable, SearchResultsTableModel tableModel,
			JProgressBar progressSearch, JScrollPane resultsPane, JPanel topPane, JMenu rerankMenu, JMenu researchMenu, JMenuItem rerankFeature*/,
			ArrayList<Document> positives, ArrayList<Document> negatives, String indexPath )
	{
		this.imagePath = imagePath;
		this.positives = positives;
		this.negatives = negatives;
		this.indexPath = indexPath;
	}
	@Override
	public void run() {
		
		System.out.println("Running Index Thread");
		try {
			/*IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File("./index")));*/
			IndexReader reader = DirectoryReader.open(FSDirectory
					.open(new File(indexPath)));
			int numDocs = reader.numDocs();
			String fileName = new File(imagePath).getName();
			String[] fileNameSub = fileName.split("\\.");
			String uid = fileNameSub[0];
			ImageSearcher searcher = getSearcher(uid);
			// System.out.println("SEARCHER: " + searcher);
			//ImageSearchHits hits = null;
			BufferedImage bimg = null;
			File AIMFile = null;
		
			bimg = ImageIO.read(new FileInputStream(imagePath));

			hits = searcher.rf(bimg, positives, negatives,
						reader);

			ImageIcon i = new ImageIcon(ImageUtils.scaleImage(bimg, 200));
			i.setDescription(imagePath);

//			jLabelQueryImage.setIcon(i);
//            
//            tableModel.setHits(hits, progressSearch);
            reader.close();
//            Rectangle bounds = resultsTable.getCellRect(0, 0, true);
//            resultsPane.getViewport().setViewPosition(bounds.getLocation());
        } catch (Exception e)
        {
            // Nothing to do here ....
        	e.printStackTrace();
        } finally
        {
            //resultsTable.setRowHeight(220);
            //resultsTable.setRowHeight(300);
            // resultsTable.getColumnModel().getColumn(0).setMaxWidth(64);
            // resultsTable.getColumnModel().getColumn(0).setMinWidth(64);
             //resultsTable.getColumnModel().getColumn(1).setMaxWidth(150);
             //resultsTable.getColumnModel().getColumn(1).setMinWidth(150);
            
            //resultsTable.requestFocus();
            // resultsTable.getColumnModel().getColumn(0).setMaxWidth(64);
            // resultsTable.getColumnModel().getColumn(0).setMinWidth(64);
            //resultsTable.getColumnModel().getColumn(1).setMaxWidth(150);
            //resultsTable.getColumnModel().getColumn(1).setMinWidth(150);
           
//            resultsTable.setShowGrid(true);
//            resultsTable.setTableHeader(null);
//            final JPanel frame = topPane;
//            ((CardLayout) topPane.getLayout()).last(frame);
//            resultsTable.setEnabled(true);
//            // enable the menu for searching & re-ranking using alternative descriptors.
//            researchMenu.setEnabled(true);
//            rerankFeature.setEnabled(true);
            /*
             * idoux_p
             * Using specifics renderer and editor for each cell in the table,
             * allowing it to display both the image(icon) and the Relevance FeedBack
             * buttons(JRadioButton).
             */
          //  PanelCellEditorRenderer PanelCellEditorRenderer = new PanelCellEditorRenderer();                   
           // resultsTable.setDefaultEditor(CellData.class, PanelCellEditorRenderer);
            /*
             * END
             */
          //  ((CardLayout) topPane.getLayout()).last(frame);
           // resultsTable.setEnabled(true);
        }
		/**	reader.close();
			
		} catch (Exception e) {
			// Nothing to do here ....
			e.printStackTrace();
		} finally {
			// resultsTable.setRowHeight(220);
			
		}*/

	}
	
	private ImageSearcher getSearcher(String uid) throws Exception {
	    int numResults = 50;
	    
	    ImageSearcher searcher = null;
	    
	    /*if ( uid != null)
	    		searcher = ImageSearcherFactory.createBimmImageSearcher(uid, numResults, cbir);
	    	else*/
	    		searcher = ImageSearcherFactory.createFCTHImageSearcher(numResults);
	    		//throw new Exception("Cannot create a searcher without a valid uid");
   
	    return searcher;
	}


}

