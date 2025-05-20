package book.utils;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocalizationUtils {

	private final MessageSource messageSource;
	private final LocaleResolver localeResolver;

	public String getLocalizedMessage(String messageKey, Object... params) {
		HttpServletRequest request = WebUtils.getCurrentRequest();
		Locale locale = localeResolver.resolveLocale(request);
		return messageSource.getMessage(messageKey, params, locale);
	}
}
