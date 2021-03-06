package orclient.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class LocalBTC {
	
	public static URL bitcoinURL;
	
	public static final String rpcauth = "dGVzdGVnb3Jkb246dGVzdHBhc3N3b3Jk";
	
	static {
		try {
			bitcoinURL = new URL("http://localhost:8332");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			bitcoinURL = null;
		}
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
	
	public static String newBTCAddress() {
		JSONObject js = new JSONObject("{\"method\":\"getnewaddress\", \"params\": [\"orclient\"], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getString("result");
	}
	
	public static String signTransaction(String transaction) {
		JSONObject js = new JSONObject("{\"method\":\"signrawtransaction\", \"params\": [\""+transaction+"\"], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getJSONObject("result").getString("hex");
	}
	
	public static String getPubKey(String address) {
		JSONObject js = new JSONObject("{\"method\":\"validateaddress\", \"params\": [\""+address+"\"], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getJSONObject("result").getString("pubkey");
	}
	
	public static String generateMultisigAddress(String localPubKey, String oraclePubKey) {
		JSONObject js = new JSONObject("{\"method\":\"addmultisigaddress\", \"params\": [2, [\""+localPubKey+"\",\""+oraclePubKey+"\"]], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getString("result");
	}
	
	public static String createTransaction(String txid, String recipient, double amount) {
		JSONObject js = new JSONObject("{\"method\":\"createrawtransaction\", \"params\": [[{\"txid\":\""+txid+"\",\"vout\":0}], {\""+recipient+"\":"+amount+"}], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getString("result");
	}
	
	public static String sendFromAccount(String account, String address, double amount) {
		JSONObject js = new JSONObject("{\"method\":\"sendfrom\", \"params\": [\""+account+"\", \""+address+"\", "+amount+"], \"id\": 1}");
		js = sendBTCRequest(js);
		return js.getString("result");
	}
	
	public static void assignAccount(String address, String account) {
		JSONObject js = new JSONObject("{\"method\":\"setaccount\", \"params\": [\""+address+"\", \""+account+"\"], \"id\": 1}");
		sendBTCRequest(js);
	}
	
	public static Boolean checkBitcoind() {
		try {
			JSONObject js = new JSONObject("{\"method\":\"help\", \"params\": [], \"id\": 1}");
			js = sendBTCRequest(js);
			js.getString("result");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static Boolean sendTransaction(String transaction) {
		try {
			JSONObject js = new JSONObject("{\"method\":\"sendrawtransaction\", \"params\": [\""+transaction+"\"], \"id\": 1}");
			js = sendBTCRequest(js);
			js.getString("result");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static JSONObject sendToOracle(String signedTransaction, String python) {
		JSONObject ret = null;
		
		HttpURLConnection conn = null;
		URL url = null;
		try {
			url = new URL(GlobalConfig.globalConfig.get("oracleURL") + "?btcTransaction="+signedTransaction+"&pyCode="+python);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("GET");
			
			
			StringBuilder sb = new StringBuilder();
			
			int httpRes = conn.getResponseCode();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));  

			    String line = null;  

			    while ((line = br.readLine()) != null) {  
			    sb.append(line + "\n");  
			    }  

			    br.close(); 
			    
			    ret = new JSONObject(sb.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
		
		return ret;
	}
	
	public static void main(String args[]) {
		String txid = "a65981f01ee341690e8bf2528033427ae25936603b85e73e05028846eea59c21";
		String rec = "mwEh6oCjBFFWfe4KVbQTdGhG89aEMn9bDK";
		double amt = 1.0;
		
		String raw = LocalBTC.createTransaction(txid, rec, amt);
		raw = Hasher.addHashToTransaction(raw, Hasher.sha256("print 1"));
		String signed = LocalBTC.signTransaction(raw);
		String python = "print 1";
		String pyHash = Hasher.sha256(python);
		String confirmed = "0";
		String fullSigned = "";
		
		GlobalConfig.globalConfig.put("oracleURL", "http://btcoracle.ethankgordon.com:8000/");
		System.out.println(LocalBTC.sendToOracle(signed, "print+1"));
	}

}
