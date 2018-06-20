package niuka.card;

import org.bytedeco.javacpp.*;
import org.opencv.core.MatOfPoint;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_stitching.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_imgproc.*;

public class Test {

	public static void main(String[] args) {
		String inputFile = "/Users/Jeffrey/IMG_6761.JPG";
		// load input image
		Mat in = imread(inputFile);
		Mat incopy = new Mat(in.size(), in.type());
		in.copyTo(incopy);
		// convert to grayscale
		Mat gray = new Mat(in.size(), CV_8UC1);
		if (in.channels() == 3) {
			cvtColor(in, gray, COLOR_RGB2GRAY);
		} else if (in.channels() == 1) {
			in.copyTo(gray);
		}

		Mat blurred = new Mat(gray.size(), gray.type());
		Mat binary4c = new Mat(gray.size(), gray.type());
		GaussianBlur(gray, blurred, new Size(3, 3), 0);
		// next we threshold the blurred image
		float th = 60;
		threshold(blurred, binary4c, th, 255, THRESH_BINARY);

		Mat mHierarchy = new Mat();
		MatVector contours = new MatVector();
		findContours(binary4c, contours, mHierarchy, RETR_LIST, CHAIN_APPROX_SIMPLE);

		double largestArea = 0;
		Rect boundingRect = new Rect();
		int x, y;
		int index = 0;
		int index1 = 0;
		for (int i = 0; i < contours.size(); i++) {

			Size size = contours.get(i).size();
			// if (size.height() != 4) {
			// continue;
			// }
			// drawContours(incopy, contours, i, Scalar.BLUE);
			// Find the area of contour
			double area = contourArea(contours.get(i), false);
			boundingRect = boundingRect(contours.get(i));
			rectangle(incopy, boundingRect, Scalar.YELLOW);
			if (area > largestArea) {
				// Find the bounding rectangle for biggest contour
				
				largestArea = area;
				System.out.println(area);
				if (index > index1) {
					index1 = index;
				}
				index = i;

			}
		}
//		drawContours(incopy, contours, index1, Scalar.YELLOW);
//		drawContours(incopy, contours, index, Scalar.YELLOW);
		
		

		// Apply crop and return result
		x = boundingRect.x();
		y = boundingRect.y();
		// Mat result = gray.apply(boundingRect);

		imwrite("/Users/Jeffrey/IMG_5155_grey.JPG", gray);
		imwrite("/Users/Jeffrey/IMG_5155_blurred.JPG", blurred);
		imwrite("/Users/Jeffrey/IMG_5155_binary4c.JPG", binary4c);
		imwrite("/Users/Jeffrey/IMG_5155_result.JPG", incopy);
	}

}
