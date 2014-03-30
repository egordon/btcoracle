package orclient.util;

import java.security.MessageDigest;

public class Hasher {
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String sha256(String message) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(message.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		} catch (Exception e) {}
		byte[] digest = md.digest();
		return bytesToHex(digest).toLowerCase();
	}
	
	public static String addHashToTransaction(String transaction, String hash) {
		if(hash.length() != 64) {
			System.err.println("addHashToTransaction(): Invalid Hash!");
			return null;
		}
		int pubKeyIndex = transaction.indexOf("76a9");
		StringBuilder sb = new StringBuilder();
		sb.append(transaction.substring(0, pubKeyIndex - 2));
		sb.append("3b20"); // New Size of scriptPubKey and hash size
		sb.append(hash);
		sb.append(transaction.substring(pubKeyIndex));
		return sb.toString();
	}

}
