/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.simplecaptcha;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.SecureRandom;
import nl.captcha.text.producer.TextProducer;

/**
 * @author Brian Wing Shun Chan
 */
public class DictionaryWordTextProducer implements TextProducer {

    int reqLength = 2;


    public DictionaryWordTextProducer(int passedLength) {
        this.reqLength = passedLength;
    }

    @Override
    public String getText() {
        // return WordsUtil.getRandomWord();
        return generateCaptchaString(reqLength);
    }

    private String generateCaptchaString(int reqLength) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        String captcha = StringPool.BLANK;
        while (!captcha.chars().anyMatch(Character::isDigit)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.reqLength; i++) {
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
            }
            captcha = sb.toString();
        }
        return captcha;
    }

}