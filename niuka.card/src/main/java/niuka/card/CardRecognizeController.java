package niuka.card;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
@SpringBootApplication
public class CardRecognizeController {

	@Autowired
	private CardRecognizeService cardRecognizeService;

	@RequestMapping(value = "/uploadimg", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public String uploadImg(@RequestParam("file") MultipartFile multipartFile) {
		try {
			File imgFile = File.createTempFile(UUID.randomUUID().toString(), "jpg");
			multipartFile.transferTo(imgFile);
			return GSON.toJson(this.cardRecognizeService.recognize(imgFile.getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{}";
	}

	public static void main(String[] args) {
		SpringApplication.run(CardRecognizeController.class, args);
	}

	private static final Gson GSON = new Gson();

}
