package book.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlFormatUtil {
	public static String formatUrl(String url) {
		if (url == null || url.trim().isEmpty()) {
			return "";
		}

		// Chuẩn hóa URL để tránh lỗi thừa dấu `/`
		String formattedUrl = url.replaceAll("//+", "/").trim();

		// Kiểm tra xem có query string không
		try {
			URI uri = new URI(formattedUrl);
			return uri.getPath(); // Chỉ lấy phần path, bỏ query string
		} catch (URISyntaxException e) {
			return formattedUrl;
		}
	}
}
