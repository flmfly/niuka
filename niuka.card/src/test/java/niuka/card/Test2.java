package niuka.card;

import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_core.mixChannels;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.Canny;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.adaptiveThreshold;
import static org.bytedeco.javacpp.opencv_imgproc.boundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.contourArea;
import static org.bytedeco.javacpp.opencv_imgproc.dilate;
import static org.bytedeco.javacpp.opencv_imgproc.findContours;
import static org.bytedeco.javacpp.opencv_imgproc.medianBlur;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;

public class Test2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Mat src =
		// imread("/Users/Jeffrey/Downloads/DetectRectangle-master/IMG_20160615_150849.png");
		Mat src = imread("/Users/Jeffrey/IMG_1594.JPG");

		Mat blurred = src.clone();
		medianBlur(src, blurred, 9);

		Mat gray0 = new Mat(blurred.size(), CV_8U), gray = new Mat();

		long totalArea = src.size().height() * src.size().width();

		long minAreaSize = (long) (totalArea * 0.2);
		long maxAreaSize = (long) (totalArea * 0.5);

		MatVector contours = new MatVector();

		MatVector blurredChannel = new MatVector();
		blurredChannel.put(blurred);
		MatVector gray0Channel = new MatVector();
		gray0Channel.put(gray0);

		Rect bestRect = null;

		double minRatioGap = Double.MAX_VALUE;
		for (int c = 0; c < 3; c++) {
			int ch[] = { c, 0 };
			mixChannels(blurredChannel, gray0Channel, new IntPointer(ch));

			int thresholdLevel = 1;
			for (int t = 0; t < thresholdLevel; t++) {
				if (t == 0) {
					Canny(gray0, gray, 10, 20, 3, true); // true ?
					dilate(gray, gray, new Mat()); // 1
													// ?
				} else {
					adaptiveThreshold(gray0, gray, thresholdLevel, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY,
							(src.size().width() + src.size().height()) / 200, t);
				}

				imwrite("/Users/Jeffrey/IMG_6761_gray_" + c + ".JPG", gray);

				findContours(gray, contours, new Mat(), RETR_LIST, CHAIN_APPROX_SIMPLE);

				// Each contour is stored as a vector of points (e.g.
				// std::vector<std::vector<cv::Point> >).
				for (int z = 0; z < contours.size(); z++) {
					Mat temp = contours.get(z);

					double area = contourArea(contours.get(z));
					if (area < minAreaSize || area > maxAreaSize) {
						continue;
					}
					// approxCurve = new Mat();
					// approxPolyDP(contours.get(z), approxCurve, arcLength(temp, true) * 0.02,
					// true);

					Rect rect = boundingRect(temp);
					double ratio = rect.height() * 1.00 / rect.width();
					// if (ratio < 0.50 || ratio > 0.74)
					// continue;

					double ratioGap = Math.abs(ratio - 0.618);
					if (ratioGap < minRatioGap) {
						minRatioGap = ratioGap;
						bestRect = rect;
					}

					// drawContours(src, contours, z, Scalar.BLUE);

					rectangle(src, rect.tl(), rect.br(), Scalar.BLUE);

					// aa.put(area, temp);
					// if (area >= maxArea) {
					// double maxCosine = 0;
					//
					// List<Point> curves = new ArrayList<Point>();
					//
					// IntRawIndexer in = temp.createIndexer();
					// long rows = in.rows(), cols = in.cols();
					// for (int i = 0; i < rows; i++) {
					// for (int j = 0; j < cols; j++) {
					// curves.add(new Point((int) in.get(i, j, 0), (int) in.get(i, j, 1)));
					// // for (int k = 0; k < channels; k++) {
					// // double v = in.getDouble(i, j, k);
					// //
					// // }
					// }
					//
					// }
					//
					// // for (int j = 2; j < 5; j++) {
					// //
					// // double cosine = Math.abs(angle(curves.get(j % 4), curves.get(j - 2),
					// // curves.get(j - 1)));
					// // maxCosine = Math.max(maxCosine, cosine);
					// // }
					//
					// // if (maxCosine < 0.3) {
					// maxArea = area;
					// maxId = z;
					// maxcontours = contours;
					// // }
					// }

				}
				imwrite("/Users/Jeffrey/IMG_6761_result.JPG", src);
			}
		}

		if (null != bestRect) {
			Mat aha = new Mat(src, bestRect);
			imwrite("/Users/Jeffrey/IMG_6761_aha.JPG", aha);
			resize(aha, aha, new Size(856, 540));

			// Mat grayMat = imread("/Users/Jeffrey/IMG_6761_aha.JPG",
			// CV_LOAD_IMAGE_GRAYSCALE);
			//
			// int thresh = 180;
			//
			// threshold(grayMat, grayMat, thresh, 255, THRESH_BINARY);
			// erode(grayMat, grayMat, getStructuringElement(CV_SHAPE_RECT, new Size(2,
			// 2)));
			// dilate(grayMat, grayMat, getStructuringElement(CV_SHAPE_RECT, new Size(2,
			// 2)));
			//
			// imwrite("/Users/Jeffrey/IMG_6761_grayMat.JPG", grayMat);

			// BytePointer outText;

			// TessBaseAPI api = new TessBaseAPI();
			// // Initialize tesseract-ocr with English, without specifying tessdata path
			//
			// if (api.Init("./tessdata", "eng") != 0) {
			// System.err.println("Could not initialize tesseract.");
			// System.exit(1);
			// }
			//// api.SetVariable("tessedit_char_whitelist", "0123456789");
			//
			// // Open input image with leptonica library
			// PIX image = pixRead("/Users/Jeffrey/IMG_6761_aha.JPG");
			// api.SetImage(image);
			// // Get OCR result
			// outText = api.GetUTF8Text();
			// System.out.println("OCR output:\n" + outText.getString());
			//
			// // Destroy used object and release memory
			// api.End();
			// outText.deallocate();
			// pixDestroy(image);
		}
	}

}
