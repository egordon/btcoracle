package btcoracle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

class Oracle {
	private String python;
	private String transaction;
	
	private static URL bitcoinURL;
	private static final String rpcauth = "dGVzdGVnb3Jkb246dGVzdHBhc3N3b3Jk";
	private static final String oracle = "n2aziZuwMDNXpzn3DbwtT1schaK1tjaYem";
	
	static {
		try {
			bitcoinURL = new URL("http://localhost:8332");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			bitcoinURL = null;
		}
	}
	
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
	
	public static JSONObject sendBTCRequest(JSONObject input) {
		JSONObject ret = null;
		
		HttpURLConnection conn = null;
		
		try {
			conn = (HttpURLConnection) bitcoinURL.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic " + rpcauth);
			conn.setRequestMethod("POST");
			
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(input.toString());
			wr.flush();
			
			StringBuilder sb = new StringBuilder();
			
			int httpRes = conn.getResponseCode();
			
			if (httpRes == HttpURLConnection.HTTP_OK) {
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  

			    String line = null;  

			    while ((line = br.readLine()) != null) {  
			    sb.append(line + "\n");  
			    }  

			    br.close(); 
			    
			    ret = new JSONObject(sb.toString());
			} else {
				ret = new JSONObject("{ \"error\" : \"" + conn.getResponseMessage() + "\" }");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
		
		return ret;
	}


	/**
	* Parses Bitcoin String, Verifies that it is signed correctly.
	* @return Hash of the python code
	**/
	private String btcVerify() {
		// Throws BitcoinException on bad signature
		JSONObject js = sendBTCRequest(new JSONObject("{\"method\":\"decoderawmessage\",\"params\":[\""+transaction+"\"], \"id\":1"));
		js = js.getJSONObject("result");
		int opDropIndex = js.toString().indexOf("OP_DROP");
		if (opDropIndex < 0) throw new BitcoinException("Improper Bitcoin Request");
		String pyHex = js.toString().substring(opDropIndex - 65, opDropIndex - 1);
		return pyHex;
	}

	/**
	* Checks code against hash, then execute python code.
	* @param: Hash of the python code
	* @return: Outcome of python code
	**/
	private boolean pyVerify(String pyHash) {
		// Check Hash
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA-256");
	
			md.update(python.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] digest = md.digest();
			if (!bytesToHex(digest).equals(pyHash)) return false;
			
			// Run Python Code
			Process p;
			String cmd = "printf \"" + python + "\" | python";
			p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			if (br.read() == '1') {
				return true;
			} else return false;
		} catch (Exception e) {
			throw new BitcoinException("Error: Exception parsing python code.");
		}
	}


	public Oracle(String newPython, String newTransaction) {
		this.python = newPython;
		this.transaction = newTransaction;
	}

	public JSONObject response() {
		JSONObject ret = new JSONObject();

		String pyHash = "";
		try {
			pyHash = btcVerify();
		} catch (BitcoinException e) {
			// Append 400 status and error message, return
			ret.append("status", 400);
			ret.append("data", e.getMessage());
			return ret;
		}

		boolean resp = false;
		try {
			resp = pyVerify(pyHash);
		} catch (BitcoinException e) {
			// Append 400 status and error message, return
		}
		

		if (resp) {
			// Sign, set status to 200, message to signed transactione 
			
			// Dump Oracle Private Key
			JSONObject js = sendBTCRequest(new JSONObject("{ \"method\": \"dumpprivkey\", \"params\": [\""+oracle+"\"], \"id\": 1}"));
			String privKey = js.getString("result");
			
			StringBuilder sb = new StringBuilder();
			sb.append("{\"method\":\"signrawtransaction\",\"params\":[\"");
			sb.append(this.transaction);
			sb.append("\", [], [\"");
			sb.append(privKey);
			sb.append("\"], \"id\":\"1\"}");
			
			js = sendBTCRequest(new JSONObject(sb.toString()));
			js = js.getJSONObject("result");
			
			if (js.getBoolean("complete")) {
				ret.append("status", 500);
				ret.append("data", js.getString("hex"));
			} else {
				ret.append("status", 500);
				ret.append("data", "Error signing transaction.");
			}
		} else {
			ret.append("status", 200);
			ret.append("data", "Oracle script failed");
		}

		return ret;
	}
	
	public static void main(String args[]) {
		
	}
}