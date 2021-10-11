package jialo.dev.java.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jialo.dev.java.store.JialoStore;
import jialo.dev.java.util.JialoUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * SSD资源。
 * 
 * @author liuhongtian
 *
 */
@RestController
@RequestMapping("/ssd")
public class SsdResource {

	protected Logger logger = LoggerFactory.getLogger(SsdResource.class);

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * 数据持久化
	 * 
	 * @return 当前数据
	 */
	@PutMapping("/flush")
	public ResponseEntity<String> flush() {
		JialoStore.flush();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json; charset=utf-8");
		return new ResponseEntity<>(gson.toJson(JialoStore.store()), headers, HttpStatus.OK);
	}

	/**
	 * 查询数据
	 * 
	 * @param request
	 * @return 当前指定路径的数据
	 */
	@GetMapping("/**")
	public ResponseEntity<String> query(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json; charset=utf-8");

		String uri = request.getRequestURI().substring(4);
		logger.info("request uri = " + uri);

		Object data = JialoUtils.query(uri);

		HttpStatus status = HttpStatus.NOT_FOUND;
		if (data != null) {
			status = HttpStatus.OK;
		}

		return new ResponseEntity<>(gson.toJson(data), headers, status);
	}

	/**
	 * 删除数据
	 * 
	 * @param request
	 * @return 新数据
	 */
	@DeleteMapping("/**")
	public ResponseEntity<String> remove(HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json; charset=utf-8");

		boolean save = Boolean.parseBoolean(request.getHeader("save"));

		String uri = request.getRequestURI().substring(4);
		logger.info("request uri = " + uri);

		JialoUtils.remove(uri, save);

		return new ResponseEntity<>(gson.toJson(JialoStore.store()), headers, HttpStatus.OK);
	}

	/**
	 * 合并数据，处理全部PUT请求。
	 * 
	 * @param data    待合并数据
	 * @param request
	 * @return 新数据
	 */
	@PutMapping("/**")
	public ResponseEntity<String> merge(@RequestBody String data, HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json; charset=utf-8");

		boolean save = Boolean.parseBoolean(request.getHeader("save"));

		String uri = request.getRequestURI().substring(4);
		logger.info("request uri = " + uri);

		var status = HttpStatus.INTERNAL_SERVER_ERROR;

		int st = JialoUtils.merge(uri, data, save);

		if (st == 0) {
			status = HttpStatus.OK;
		} else if (st == 1) {
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(gson.toJson(JialoStore.store()), headers, status);
	}

	public static void main(String... args) {
		String str = "liuhongtian";
		var json = gson.fromJson(str, String.class);
		System.out.println(json);
	}

}
