//CSCI 576 - Project , Media Query and Search , Team : Divya Seshadri and Shlok Naik
import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.Object;
import java.awt.geom.*;
import javax.imageio.ImageIO;
import static java.lang.Math.*;
import java.awt.Graphics2D;

public class ImageSearch {
	//finding the region in the target image where the source image is
	public static double[] findSourceImage(BufferedImage targetImage, BufferedImage sourceImage){
		int targetWidht = targetImage.getWidth(); int targetHeight = targetImage.getHeight();
		int sourceWidth = sourceImage.getWidth(); int sourceHeight = sourceImage.getHeight();
		assert(sourceWidth <= targetWidht && sourceHeight <= targetHeight);
		//will keep track of best position found
		int coordinateX = 0; int coordinateY = 0; 
		//output similarity measure from 0 to 1, with 0 being identical
		double lowestDiff = Double.POSITIVE_INFINITY; 
		//brute-force search through whole target image 
		for(int x = 0;x < targetWidht-sourceWidth;x++){
			for(int y = 0;y < targetHeight-sourceHeight;y++){
				double compareImages = compareImages(targetImage.getSubimage(x,y,sourceWidth,sourceHeight),sourceImage);
					if(compareImages < lowestDiff){
						coordinateX = x; coordinateY = y; lowestDiff = compareImages;
					}
				}
			}
		//return best location
		return new double[]{lowestDiff*1000,coordinateX,coordinateY};
	}
	//finding the difference between the images of same size 
	public static double compareImages(BufferedImage targetImage, BufferedImage sourceImage){
		assert(targetImage.getHeight() == sourceImage.getHeight() && targetImage.getWidth() == sourceImage.getWidth());
		double variation = 0.0;
		for(int x = 0;x < targetImage.getWidth();x++){
			for(int y = 0;y < targetImage.getHeight();y++){
				variation += compareARGB(targetImage.getRGB(x,y),sourceImage.getRGB(x,y))/Math.sqrt(3);
				}
			}
		return variation/(targetImage.getWidth()*targetImage.getHeight());
		}
	//calculates the difference between two ARGB colours (BufferedImage.TYPE_INT_ARGB)
	public static double compareARGB(int rgb1, int rgb2){
		double r1 = ((rgb1 >> 16) & 0xFF)/255.0; double r2 = ((rgb2 >> 16) & 0xFF)/255.0;
		double g1 = ((rgb1 >> 8) & 0xFF)/255.0;  double g2 = ((rgb2 >> 8) & 0xFF)/255.0;
		double b1 = (rgb1 & 0xFF)/255.0;         double b2 = (rgb2 & 0xFF)/255.0;
		double a1 = ((rgb1 >> 24) & 0xFF)/255.0; double a2 = ((rgb2 >> 24) & 0xFF)/255.0;
		//if there is transparency, the alpha values will make difference smaller
		return a1*a2*Math.sqrt((r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2));
		}
	//the main function
	public static void main(String[] args) { 
	   	String fileName = args[0];       
		int width = 352;
		int height = 288;	
		String fileName1 = args[1];   
		int swidth= (int) width;
		int sHeight= (int) height;
		BufferedImage imgTarget = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage outputImage = new BufferedImage( swidth, sHeight, BufferedImage.TYPE_INT_RGB );
		JFrame frame = new JFrame("CSCI 576 Project (Divya Seshadri & Shlok Naik)");
		JLabel label2 = new JLabel(new ImageIcon(outputImage));
		JLabel label1 = new JLabel(new ImageIcon(imgTarget));
		JPanel subPanel1 = new JPanel();
		try {
			//reading the inputs from the user
			File file = new File(args[1]);
			InputStream is = new FileInputStream(file);
			long len = file.length();
			byte[] bytes = new byte[(int)len];
			int offset = 0;
			int numRead = 0;
			File file1 = new File(args[0]);
			InputStream is1 = new FileInputStream(file1);
			long len1 = file1.length();
			byte[] bytes1 = new byte[(int)len1];
			int offset1 = 0;
			int numRead1 = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0 && offset1 < bytes1.length && (numRead1=is1.read(bytes1, offset1, bytes1.length-offset1)) >= 0) {
				offset += numRead;
				offset1 += numRead1;
				}
			int ind = 0;
			//separating the RGB values for the images
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					imgTarget.setRGB(x,y,pix);
					byte r1 = bytes1[ind];
					byte g1 = bytes1[ind+height*width];
					byte b1 = bytes1[ind+height*width*2]; 
					int pix1 = 0xff000000 | ((r1 & 0xff) << 16) | ((g1 & 0xff) << 8) | (b1 & 0xff);
					outputImage.setRGB(x,y,pix1);
					ind++;
					}
				}
		//displaying the GUI
		label2 = new JLabel ("Source Image",new ImageIcon(outputImage), SwingConstants.RIGHT);
		subPanel1.add (label2);
		label1 = new JLabel ("Target Image", new ImageIcon(imgTarget), SwingConstants.LEFT);
		subPanel1.add (label1);
		JScrollPane scrollPane = new JScrollPane(subPanel1);  
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  
		frame.getContentPane().add(scrollPane);  
		subPanel1.setBackground(Color.white);
		frame.pack();
		frame.setVisible(true);
		//array for storing the matching coordinates
		double[] result={0.0,0.0,0.0};
		int scaledHeight = sHeight; 
		int scaledWidth = swidth;
		double differenceValue = 1000.0;
		int scaleFactor=1;
		double x=0.0,y=0.0;
		for (int SCALE = 4; SCALE<=10;SCALE+=3) {
			scaledHeight = (int)Math.round(sHeight / SCALE);
			scaledWidth = (int)Math.round(swidth / SCALE);
			BufferedImage scaledImage = new BufferedImage(scaledWidth,scaledHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g1 = scaledImage.createGraphics();   
			g1.drawImage(outputImage, 0, 0, scaledWidth, scaledHeight, 0, 0, swidth, sHeight, null);  
			g1.dispose(); 
			result = findSourceImage(imgTarget,scaledImage);
			if(result[0] <= differenceValue) {
				scaleFactor=SCALE;
				x=result[1];
				y=result[2];
				differenceValue= result[0];
				}
			System.out.println("An iteration for finding the subimage is complete");
			System.out.println("Coordinates where similar match exists  :  " + result[1] +" , " + result[2]);
			System.out.println("....................................................");
			}
		System.out.println("Euclidean Difference: " +differenceValue / 1000.00 +"   Matching Coordinates:  " + x +" , " + y);
		Graphics2D g = imgTarget.createGraphics();   
		g.setColor(Color.red);
		g.drawRect((((int)x)- (swidth/scaleFactor)),(((int)y)-(sHeight/scaleFactor)) ,100,100);
		g.dispose(); 
        } 
		catch (FileNotFoundException e) {
		  e.printStackTrace();
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		//displaying the result
		JPanel subPanel2 = new JPanel();
		label1.setText("Resultant Image");
		subPanel2.setBackground(Color.white);
		frame.setSize(1050, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
