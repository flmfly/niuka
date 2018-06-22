package niuka.card;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class TencentOCRServcie {

	@Value("${tencent.ai.appid}")
	private Integer appId;

	@Value("${tencent.ai.appkey}")
	private String appKey;

	@Value("${tencent.ai.ocr.general.url}")
	private String url;

	public RecognizeResult ocr(String image) {
		RecognizeResult rtn = new RecognizeResult();
		InputStream is = null;
		HttpsURLConnection conn = null;
		try {
			String query = this.getReqSign(image);
			query = query + "&sign=" + MD5Utils.digest(query).toUpperCase();
			conn = (HttpsURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.getOutputStream().write(query.getBytes());
			conn.getOutputStream().flush();
			is = conn.getInputStream();
			OCRResult result = GSON.fromJson(new InputStreamReader(is), OCRResult.class);

			int maxCount = Integer.MIN_VALUE;
			String number = null;
			for (OCRItem item : result.getData().getItem_list()) {
				String itemStr = item.getItemstring();
				int c = 0;
				for (int i = 0; i < itemStr.length(); i++) {
					if (Character.isDigit(itemStr.charAt(i))) {
						c++;
					}
				}
				if (c >= maxCount) {
					maxCount = c;
					number = itemStr;
				}
			}
			rtn.setMemberNumber(number);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (null != conn) {
				conn.disconnect();
			}
		}
		return rtn;
	}

	private String getReqSign(String image) throws UnsupportedEncodingException {
		StringJoiner sj = new StringJoiner("&");
		sj.add("app_id=" + appId);
		sj.add("image=" + URLEncoder.encode(image, "utf-8"));
		sj.add("nonce_str=" + RandomStringUtils.random(16, true, true));
		sj.add("time_stamp=" + (long) System.currentTimeMillis() / 1000);
		sj.add("app_key=" + this.appKey);
		return sj.toString();
	}

	private static final Gson GSON = new Gson();

	class OCRResult {
		private String ret;
		private String msg;
		private OCRData data;

		public String getRet() {
			return ret;
		}

		public void setRet(String ret) {
			this.ret = ret;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public OCRData getData() {
			return data;
		}

		public void setData(OCRData data) {
			this.data = data;
		}
	}

	class OCRData {
		private List<OCRItem> item_list;

		public List<OCRItem> getItem_list() {
			return item_list;
		}

		public void setItem_list(List<OCRItem> item_list) {
			this.item_list = item_list;
		}

	}

	class OCRItem {
		private String item;
		private String itemstring;
		private List<OCRItemcoord> itemcoord;
		private List<OCRWord> words;

		public String getItem() {
			return item;
		}

		public void setItem(String item) {
			this.item = item;
		}

		public String getItemstring() {
			return itemstring;
		}

		public void setItemstring(String itemstring) {
			this.itemstring = itemstring;
		}

		public List<OCRItemcoord> getItemcoord() {
			return itemcoord;
		}

		public void setItemcoord(List<OCRItemcoord> itemcoord) {
			this.itemcoord = itemcoord;
		}

		public List<OCRWord> getWords() {
			return words;
		}

		public void setWords(List<OCRWord> words) {
			this.words = words;
		}

	}

	class OCRItemcoord {
		private Integer x;
		private Integer y;
		private Integer width;
		private Integer height;

		public Integer getX() {
			return x;
		}

		public void setX(Integer x) {
			this.x = x;
		}

		public Integer getY() {
			return y;
		}

		public void setY(Integer y) {
			this.y = y;
		}

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}

	}

	class OCRWord {
		private String character;
		private Double confidence;

		public String getCharacter() {
			return character;
		}

		public void setCharacter(String character) {
			this.character = character;
		}

		public Double getConfidence() {
			return confidence;
		}

		public void setConfidence(Double confidence) {
			this.confidence = confidence;
		}

	}
}
