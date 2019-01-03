package panacea.ADVaction;

import panacea.common.DTObject;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import panacea.Validator.WebManager;
import panaceaweb.utility.Common;

import panacea.Validator.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;



public class mtwofaconfval extends WebManager {

	public mtwofaconfval(HttpSession _session) {
		super(_session);
		commoninstance = new Common(getsession());

	}
	public static final int DEFAULT_TIME_STEP_SECONDS = 30;
	/** set to the number of digits to control 0 prefix, set to 0 for no prefix */
	private static int NUM_DIGITS_OUTPUT = 6;

	private static final String blockOfZeros;

	static {
		char[] chars = new char[NUM_DIGITS_OUTPUT];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = '0';
		}
		blockOfZeros = new String(chars);
	}

	Common commoninstance;

	DTObject revalDTO = new DTObject();

	public DTObject userIdkeypress(DTObject InputoBj) {
		try {
			String _userId = "";
			String _loggeduserId = "";
			if (InputoBj.containsKey("USER_ID") == true) {
				if (InputoBj.getValue("USER_ID") == null)
					_userId = "";
				else
					_userId = InputoBj.getValue("USER_ID").trim().toString();
			}
			if (InputoBj.containsKey("LOGGED_USER_ID") == true) {
				if (InputoBj.getValue("LOGGED_USER_ID") == null)
					_loggeduserId = "";
				else
					_loggeduserId = InputoBj.getValue("LOGGED_USER_ID").trim().toString();
			}
			Init_ResultObj(InputoBj);
			if (!_userId.equalsIgnoreCase(_loggeduserId)) {
				Resultobj.setValue(ErrorKey, "Session User and Entered User Id Doesn't match");
			}

		} catch (Exception e) {
			Resultobj.setValue(ErrorKey, "Error in userIdkeypress");
		}
		return Resultobj.copyofDTO();
	}

	public DTObject userPasswordkeypress(DTObject InputoBj) {
		try {
			String _userpword = "";
			String _loggeduserpword = "";
			if (InputoBj.containsKey("USER_PASSWORD") == true) {
				if (InputoBj.getValue("USER_PASSWORD") == null)
					_userpword = "";
				else
					_userpword = InputoBj.getValue("USER_PASSWORD").trim().toString();
			}
			if (InputoBj.containsKey("LOGGED_USER_PASSWORD") == true) {
				if (InputoBj.getValue("LOGGED_USER_PASSWORD") == null)
					_loggeduserpword = "";
				else
					_loggeduserpword = InputoBj.getValue("LOGGED_USER_PASSWORD").trim().toString();
			}
			Init_ResultObj(InputoBj);
			if (!_userpword.equalsIgnoreCase(_loggeduserpword)) {
				Resultobj.setValue(ErrorKey, "Password Doesn't Match");
			}

		} catch (Exception e) {
			Resultobj.setValue(ErrorKey, "Error in userPasswordkeypress");
		}
		return Resultobj.copyofDTO();
	}

	public DTObject generateSecretKey(DTObject InputoBj) {
		try {
			try {
				String _userid = InputoBj.getValue("USER_ID").trim();
				String _userpassword = InputoBj.getValue("USER_PASSWORD").trim();
				if (String.valueOf(_userid).trim().equalsIgnoreCase("")) {
					Resultobj.setValue(ErrorKey, BLANK_CHECK);
				}
				if (String.valueOf(_userpassword).trim().equalsIgnoreCase("")) {
					Resultobj.setValue(ErrorKey, BLANK_CHECK);
				}
				Init_ResultObj(InputoBj);
				String secretKey = generateBase32Secret();
				Resultobj.setValue("SECRET_KEY", secretKey);
			} catch (Exception e) {
				Resultobj.setValue(ErrorKey, "Error in generateSecretKey");
			}
			return Resultobj.copyofDTO();
		} finally {
			try {
				closeConnection();
			} catch (Exception e) {
			}
		}
	}

	public static String generateBase32Secret() {
		return generateBase32Secret(16);
	}

	/**
	 * Similar to {@link #generateBase32Secret()} but specifies a character length.
	 */
	public static String generateBase32Secret(int length) {
		StringBuilder sb = new StringBuilder(length);
		Random random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			int val = random.nextInt(32);
			if (val < 26) {
				sb.append((char) ('A' + val));
			} else {
				sb.append((char) ('2' + (val - 26)));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Return the current number to be checked. This can be compared against user
	 * input.
	 * 
	 * <p>
	 * WARNING: This requires a system clock that is in sync with the world.
	 * </p>
	 * 
	 * @param base32Secret
	 *            Secret string encoded using base-32 that was used to generate the
	 *            QR code or shared with the user.
	 * @return A number as a string with possible leading zeros which should match
	 *         the user's authenticator application output.
	 */
	public static String generateCurrentNumberString(String base32Secret) throws GeneralSecurityException {
		return generateNumberString(base32Secret, System.currentTimeMillis(), DEFAULT_TIME_STEP_SECONDS);
	}
	
	/**
	 * Similar to {@link #generateCurrentNumberString(String)} except exposes other
	 * parameters. Mostly for testing.
	 * 
	 * @param base32Secret
	 *            Secret string encoded using base-32 that was used to generate the
	 *            QR code or shared with the user.
	 * @param timeMillis
	 *            Time in milliseconds.
	 * @param timeStepSeconds
	 *            Time step in seconds. The default value is 30 seconds here. See
	 *            {@link #DEFAULT_TIME_STEP_SECONDS}.
	 * @return A number as a string with possible leading zeros which should match
	 *         the user's authenticator application output.
	 */
	public static String generateNumberString(String base32Secret, long timeMillis, int timeStepSeconds)
			throws GeneralSecurityException {
		long number = generateNumber(base32Secret, timeMillis, timeStepSeconds);
		return zeroPrepend(number, NUM_DIGITS_OUTPUT);
	}
	
	
	/**
	 * Return the string prepended with 0s. Tested as 10x faster than
	 * String.format("%06d", ...); Exposed for testing.
	 */
	static String zeroPrepend(long num, int digits) {
		String numStr = Long.toString(num);
		if (numStr.length() >= digits) {
			return numStr;
		} else {
			StringBuilder sb = new StringBuilder(digits);
			int zeroCount = digits - numStr.length();
			sb.append(blockOfZeros, 0, zeroCount);
			sb.append(numStr);
			return sb.toString();
		}
	}

	/**
	 * Decode base-32 method. I didn't want to add a dependency to Apache Codec just
	 * for this decode method. Exposed for testing.
	 */
	static byte[] decodeBase32(String str) {
		// each base-32 character encodes 5 bits
		int numBytes = ((str.length() * 5) + 7) / 8;
		byte[] result = new byte[numBytes];
		int resultIndex = 0;
		int which = 0;
		int working = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			int val;
			if (ch >= 'a' && ch <= 'z') {
				val = ch - 'a';
			} else if (ch >= 'A' && ch <= 'Z') {
				val = ch - 'A';
			} else if (ch >= '2' && ch <= '7') {
				val = 26 + (ch - '2');
			} else if (ch == '=') {
				// special case
				which = 0;
				break;
			} else {
				throw new IllegalArgumentException("Invalid base-32 character: " + ch);
			}
			/*
			 * There are probably better ways to do this but this seemed the most
			 * straightforward.
			 */
			switch (which) {
			case 0:
				// all 5 bits is top 5 bits
				working = (val & 0x1F) << 3;
				which = 1;
				break;
			case 1:
				// top 3 bits is lower 3 bits
				working |= (val & 0x1C) >> 2;
				result[resultIndex++] = (byte) working;
				// lower 2 bits is upper 2 bits
				working = (val & 0x03) << 6;
				which = 2;
				break;
			case 2:
				// all 5 bits is mid 5 bits
				working |= (val & 0x1F) << 1;
				which = 3;
				break;
			case 3:
				// top 1 bit is lowest 1 bit
				working |= (val & 0x10) >> 4;
				result[resultIndex++] = (byte) working;
				// lower 4 bits is top 4 bits
				working = (val & 0x0F) << 4;
				which = 4;
				break;
			case 4:
				// top 4 bits is lowest 4 bits
				working |= (val & 0x1E) >> 1;
				result[resultIndex++] = (byte) working;
				// lower 1 bit is top 1 bit
				working = (val & 0x01) << 7;
				which = 5;
				break;
			case 5:
				// all 5 bits is mid 5 bits
				working |= (val & 0x1F) << 2;
				which = 6;
				break;
			case 6:
				// top 2 bits is lowest 2 bits
				working |= (val & 0x18) >> 3;
				result[resultIndex++] = (byte) working;
				// lower 3 bits of byte 6 is top 3 bits
				working = (val & 0x07) << 5;
				which = 7;
				break;
			case 7:
				// all 5 bits is lower 5 bits
				working |= (val & 0x1F);
				result[resultIndex++] = (byte) working;
				which = 0;
				break;
			}
		}
		if (which != 0) {
			result[resultIndex++] = (byte) working;
		}
		if (resultIndex != result.length) {
			result = Arrays.copyOf(result, resultIndex);
		}
		return result;
	}
	
	/**
	 * Similar to {@link #generateNumberString(String, long, int)} but this returns
	 * a long instead of a string.
	 * 
	 * @return A number which should match the user's authenticator application
	 *         output.
	 */
	public static long generateNumber(String base32Secret, long timeMillis, int timeStepSeconds)
			throws GeneralSecurityException {

		byte[] key = decodeBase32(base32Secret);

		byte[] data = new byte[8];
		long value = timeMillis / 1000 / timeStepSeconds;
		for (int i = 7; value > 0; i--) {
			data[i] = (byte) (value & 0xFF);
			value >>= 8;
		}

		// encrypt the data with the key and return the SHA1 of it in hex
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		// if this is expensive, could put in a thread-local
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		// take the 4 least significant bits from the encrypted string as an offset
		int offset = hash[hash.length - 1] & 0xF;

		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		for (int i = offset; i < offset + 4; ++i) {
			truncatedHash <<= 8;
			// get the 4 bytes at the offset
			truncatedHash |= (hash[i] & 0xFF);
		}
		// cut off the top bit
		truncatedHash &= 0x7FFFFFFF;

		// the token is then the last 6 digits in the number
		truncatedHash %= 1000000;

		return truncatedHash;
	}

	
	public static String getTOTPCode(String secretKey) {
        //String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }
	
	public DTObject ValidateOTP(DTObject InputoBj) {
		String secretKey = "";
		String OneTimeCode = "";
		String _userOneTimePassword = "";
		try {
			if (InputoBj.containsKey("SECRET_KEY") == true) {
				if (InputoBj.getValue("SECRET_KEY") == null)
					secretKey = "";
				else
					secretKey = InputoBj.getValue("SECRET_KEY").trim().toString();
			}
			if (InputoBj.containsKey("NEW_PASSWORD") == true) {
				if (InputoBj.getValue("NEW_PASSWORD") == null)
					_userOneTimePassword = "";
				else
					_userOneTimePassword = InputoBj.getValue("NEW_PASSWORD").trim().toString();
			}
			Init_ResultObj(InputoBj);
			
			System.out.println("current otp: "+generateNumberString(secretKey, System.currentTimeMillis(), DEFAULT_TIME_STEP_SECONDS));
			System.out.println("30 seconds before otp: "+generateNumberString(secretKey, System.currentTimeMillis() - 30000, DEFAULT_TIME_STEP_SECONDS));
			System.out.println("30 seconds after otp: "+generateNumberString(secretKey, System.currentTimeMillis() + 30000, DEFAULT_TIME_STEP_SECONDS));
			
			OneTimeCode = generateNumberString(secretKey, System.currentTimeMillis(), DEFAULT_TIME_STEP_SECONDS);
			if (!_userOneTimePassword.equals(OneTimeCode)) {
				if (!_userOneTimePassword.equals(generateNumberString(secretKey, System.currentTimeMillis() - 30000, DEFAULT_TIME_STEP_SECONDS))
					|| !_userOneTimePassword.equals(generateNumberString(secretKey, System.currentTimeMillis() + 30000,DEFAULT_TIME_STEP_SECONDS))) {
					Resultobj.setValue(ErrorKey, "OTP Not Matched");
				}
			}
			
			/*OneTimeCode = generateCurrentNumberString(secretKey);
			if (!_userOneTimePassword.equals(OneTimeCode)) {
				Resultobj.setValue(ErrorKey, "OTP Not Matched");
			}*/
			/*OneTimeCode = getTOTPCode(secretKey);
			if (!_userOneTimePassword.equals(OneTimeCode)) {
				Resultobj.setValue(ErrorKey, "OTP Not Matched");
			}*/
			
			/*String lastCode = null;
			while (true) {
	            String code = generateCurrentNumberString(secretKey);
	            if (!code.equals(lastCode)) {
	                System.out.println(code);
	            }
	            lastCode = code;
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException e) {};
	        }*/

		} catch (Exception e) {
			Resultobj.setValue(ErrorKey, "Error Occured in Validating OTP");
		}
		return Resultobj.copyofDTO();
	}


}
