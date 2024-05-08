package com.custom.search.suggestion.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The Class SearchStringUtil.
 *
 * @author Dhivakar Sengottaiyan
 */
public class SearchStringUtil {

	/**
	 * Maybe.
	 *
	 * @param s the s
	 * @return the optional
	 */
	public static Optional<String> maybe(String s) {
		s = StringUtil.trim(s);

		if (Validator.isBlank(s)) {
			return Optional.empty();
		}

		return Optional.of(s);
	}

	/**
	 * Split and unquote.
	 *
	 * @param optional the optional
	 * @return the string[]
	 */
	public static String[] splitAndUnquote(Optional<String> optional) {
		return Optional.ofNullable(
			optional
		).orElse(
			Optional.empty()
		).map(
			SearchStringUtil::splitAndUnquote
		).orElse(
			new String[0]
		);
	}

	/**
	 * Split and unquote.
	 *
	 * @param s the s
	 * @return the string[]
	 */
	public static String[] splitAndUnquote(String s) {
		return Stream.of(
			StringUtil.split(s.trim(), CharPool.COMMA)
		).map(
			String::trim
		).map(
			StringUtil::unquote
		).toArray(
			String[]::new
		);
	}
	
}