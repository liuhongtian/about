package jialo.dev.java.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缺省资源
 * 
 * @author liuhongtian
 *
 */
@RestController
@RequestMapping("/")
public class DefaultResource {

	private String htmlReadme;

	@Value("${spring.application.name:jialo}")
	String appName;

	public DefaultResource() {
		File file = new File("README.md");
		if (!file.exists()) {
			file = new File("config" + File.separator + "README.md");
		}

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file));) {
				String md = br.lines().reduce((a, b) -> a + System.lineSeparator() + b).orElse("");
				MutableDataSet options = new MutableDataSet();

				options.set(Parser.EXTENSIONS, Arrays.asList(GitLabExtension.create(), TocExtension.create(),
						TablesExtension.create(), StrikethroughExtension.create()));
				options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
				options.set(TablesExtension.FORMAT_TABLE_TRIM_CELL_WHITESPACE, true);

				Parser parser = Parser.builder(options).build();
				HtmlRenderer renderer = HtmlRenderer.builder(options).build();

				// You can re-use parser and renderer instances
				Node document = parser.parse(md);
				this.htmlReadme = renderer.render(document);

			} catch (IOException e) {
				// read README.md failed
				this.htmlReadme = "read README.md failed!";
			}
		} else {
			// no README.md
			this.htmlReadme = "README.md not exists!";
		}

	}

	/**
	 * 缺省接口，用于获取模块说明（README.md）
	 * 
	 * @return
	 */
	@GetMapping("/")
	@ResponseBody
	public ResponseEntity<String> readme() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity<>(this.htmlReadme, headers, HttpStatus.OK);
	}

	/**
	 * 状态探测。
	 * 
	 * @return
	 */
	@GetMapping("/probe/")
	@ResponseBody
	public ResponseEntity<String> probe() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "text/plain; charset=utf-8");
		return new ResponseEntity<>(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ": " + appName,
				headers, HttpStatus.OK);
	}

}
