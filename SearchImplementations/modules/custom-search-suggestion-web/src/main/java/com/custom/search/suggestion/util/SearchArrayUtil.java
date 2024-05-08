package com.custom.search.suggestion.util;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Optional;

/**
 * The Class SearchArrayUtil.
 *
 * @author Dhivakar Sengottaiyan
 */
public class SearchArrayUtil {

	/**
	 * Maybe.
	 *
	 * @param texts the texts
	 * @return the optional
	 */
	public static Optional<String[]> maybe(String[] texts) {
		if (ArrayUtil.isEmpty(texts)) {
			return Optional.empty();
		}

		return Optional.of(texts);
	}

}