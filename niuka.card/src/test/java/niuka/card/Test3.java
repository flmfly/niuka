package niuka.card;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.opencv.core.MatOfPoint;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_stitching.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.*;

public class Test3 {

	public static void main(String[] args) {
		String inputFile = "/Users/Jeffrey/IMG_5155.JPG";
		// load input image
		Mat in = imread(inputFile);
		
		Mat incopy = new Mat(in.size(), in.type());
		in.copyTo(incopy);
		
		Mat gray = new Mat();

		cvtColor(in, gray, COLOR_RGB2GRAY);

		Mat filtered = new Mat();

		threshold(gray, gray,80, 255, CV_THRESH_BINARY);
		
		imwrite("/Users/Jeffrey/IMG_6761_gray.JPG", gray);
		
		blur(gray, filtered, new Size(3, 3));
		imwrite("/Users/Jeffrey/IMG_6761_blur.JPG", filtered);

		Mat edges = new Mat();

		int thresh = 50;
		Canny(filtered, edges, thresh, thresh * 2, 3, false);
		imwrite("/Users/Jeffrey/IMG_6761_edges.JPG", edges);

//		Mat dilated_edges = new Mat();

//		dilate(edges, dilated_edges, new Mat(), new Point(-1, -1), 2, 1, Scalar.RED);
//		imwrite("/Users/Jeffrey/IMG_6761_dilated.JPG", dilated_edges);

		MatVector contours = new MatVector();
		findContours(edges, contours, RETR_LIST, CHAIN_APPROX_SIMPLE);

		double largestArea = 0;
		Rect boundingRect = new Rect();
		int x, y;
		int index = 0;
		int index1 = 0;
		for (int i = 0; i < contours.size(); i++) {
			double area = contourArea(contours.get(i), false);
			
			rectangle(incopy, boundingRect(contours.get(i)), Scalar.BLUE);
			
			if (area > largestArea) {
				// Find the bounding rectangle for biggest contour
				System.out.println(area);
				rectangle(incopy, boundingRect(contours.get(i)), Scalar.YELLOW);
				largestArea = area;
//				boundingRect = boundingRect(contours.get(i));
				if (index > index1) {
					index1 = index;
				}
				index = i;

			}

		}
		
		rectangle(incopy, boundingRect(contours.get(index)), Scalar.YELLOW);
		rectangle(incopy, boundingRect(contours.get(index1)), Scalar.YELLOW);
		imwrite("/Users/Jeffrey/IMG_5155_result.JPG", incopy);
	}

}
