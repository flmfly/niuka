package niuka.wechat.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@SpringBootApplication
@Controller
public class Login {

	@Value("${wechat.appid}")
	private String appId;

	@Value("${wechat.secret}")
	private String secret;

	@Value("${wechat.api.url}")
	private String url;

	@Value("${repository.user.url.search}")
	private String userUrlSearch;

	@Value("${repository.user.url.create}")
	private String userUrlCreate;

	@RequestMapping(value = "/wechat/login/{code}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String index(@PathVariable String code) throws Exception {
		Result result = this.getResult(code);
		String userId = "";
		if (null != result && result.successed()) {
			userId = this.getUserId(result);
		}
		return userId;
	}

	@RequestMapping(value = "/wechat/time", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String time(@PathVariable String code) throws Exception {
		return SDF.format(new Date());
	}

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String getUserId(Result result) throws Exception {
		// find user by openId
		String userId = null;
		User user = this.findUserByOpenId(result.getOpenid());
		if (null == user || null == user.getId()) {
			user = new User();
			user.setOpenId(result.getOpenid());
			user.setUnionid(result.getUnionid());

			user = this.createUser(user);
			userId = user.getId();
		} else {// TODO update user info
			userId = user.getId();
		}
		return userId;
	}

	private User findUserByOpenId(String openId) throws Exception {
		User user = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(userUrlSearch + "?openId=" + openId).openConnection();
			conn.setRequestProperty("SessionKey", "44054f97-9929-4017-97c9-0becd4196283");

			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				user = GSON.fromJson(new InputStreamReader(is), User.class);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
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
		return user;
	}

	// Content-Type:application/json
	private User createUser(User user) {
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(userUrlCreate).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("SessionKey", "44054f97-9929-4017-97c9-0becd4196283");
			conn.setDoOutput(true);
			user.setFirstLogin(SDF.format(new Date()));
			conn.getOutputStream().write(GSON.toJson(user).getBytes());
			conn.getOutputStream().flush();
			is = conn.getInputStream();
			return GSON.fromJson(new InputStreamReader(is), User.class);
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
		return user;
	}

	private Result getResult(String code) {
		Result result = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url + code).openConnection();
			conn.setRequestProperty("SessionKey", "44054f97-9929-4017-97c9-0becd4196283");
			is = conn.getInputStream();
			result = GSON.fromJson(new InputStreamReader(is), Result.class);
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
		return result;
	}

	private static final Gson GSON = new Gson();

	class Result {
		private String openid;
		private String session_key;
		private String unionid;
		private String errcode;
		private String errmsg;

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		public String getSession_key() {
			return session_key;
		}

		public void setSession_key(String session_key) {
			this.session_key = session_key;
		}

		public String getUnionid() {
			return unionid;
		}

		public void setUnionid(String unionid) {
			this.unionid = unionid;
		}

		public String getErrcode() {
			return errcode;
		}

		public void setErrcode(String errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public boolean successed() {
			return null == this.errcode;
		}
	}

	class User {

		private String id;

		private String openId;
		private String unionid;

		private String nickName;
		private Integer gender;
		private String language;
		private String city;
		private String province;
		private String country;
		private String avatarUrl;

		private String firstLogin;
		private String lastActive;

		public String getOpenId() {
			return openId;
		}

		public void setOpenId(String openId) {
			this.openId = openId;
		}

		public String getUnionid() {
			return unionid;
		}

		public void setUnionid(String unionid) {
			this.unionid = unionid;
		}

		public String getNickName() {
			return nickName;
		}

		public void setNickName(String nickName) {
			this.nickName = nickName;
		}

		public Integer getGender() {
			return gender;
		}

		public void setGender(Integer gender) {
			this.gender = gender;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getAvatarUrl() {
			return avatarUrl;
		}

		public void setAvatarUrl(String avatarUrl) {
			this.avatarUrl = avatarUrl;
		}

		public String getFirstLogin() {
			return firstLogin;
		}

		public void setFirstLogin(String firstLogin) {
			this.firstLogin = firstLogin;
		}

		public String getLastActive() {
			return lastActive;
		}

		public void setLastActive(String lastActive) {
			this.lastActive = lastActive;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(Login.class, args);
	}
}
