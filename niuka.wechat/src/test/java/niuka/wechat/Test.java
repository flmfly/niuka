package niuka.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.sun.javafx.collections.MappingChange.Map;

public class Test {

	public static void main(String[] args) throws Exception, ClientProtocolException, IOException {

		InputStream is = null;
		HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection) new URL("https://www.5dvip.com:8443/user/search/findByOpenId?openId=1213")
					.openConnection();
			conn.setRequestProperty("SessionKey", "44054f97-9929-4017-97c9-0becd4196283");
			is = conn.getInputStream();
			HashMap map = new Gson().fromJson(new InputStreamReader(is), HashMap.class);
			System.out.println(1);
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
	}

}
