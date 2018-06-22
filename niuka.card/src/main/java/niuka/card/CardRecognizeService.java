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
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardRecognizeService {

	@Autowired
	private TencentOCRServcie tencentOCRServcie;

	public RecognizeResult recognize(String filePath) throws IOException {

		RecognizeResult rr = new RecognizeResult();

		Mat src = imread(filePath);
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
				} else {
					adaptiveThreshold(gray0, gray, thresholdLevel, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY,
							(src.size().width() + src.size().height()) / 200, t);
				}

				findContours(gray, contours, new Mat(), RETR_LIST, CHAIN_APPROX_SIMPLE);

				for (int z = 0; z < contours.size(); z++) {
					Mat temp = contours.get(z);

					double area = contourArea(contours.get(z));
					if (area < minAreaSize || area > maxAreaSize) {
						continue;
					}

					Rect rect = boundingRect(temp);
					double ratio = rect.height() * 1.00 / rect.width();
					if (ratio < 0.55 || ratio > 0.70)
						continue;

					double ratioGap = Math.abs(ratio - 0.618);
					if (ratioGap < minRatioGap) {
						minRatioGap = ratioGap;
						bestRect = rect;
					}
				}

			}
		}

		if (null != bestRect) {
			Mat aha = new Mat(src, bestRect);

			File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".jpg");

			imwrite(tempFile.getAbsolutePath(), aha);

			RecognizeResult ocr = this.tencentOCRServcie
					.ocr(Base64.encodeBase64String(FileUtils.readFileToByteArray(tempFile)));
			if (null != ocr.getMemberNumber()) {
				rr.setMemberNumber(ocr.getMemberNumber());
			}

			resize(aha, aha, new Size(856, 540));

			imwrite(tempFile.getAbsolutePath(), aha);

			// 856*540

			rr.setImg(Base64.encodeBase64String(FileUtils.readFileToByteArray(tempFile)));

			tempFile.delete();
		}

		return rr;

	}
}
