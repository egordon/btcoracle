package orclient.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class GlobalConfig {
	
	public static Map<String, String> globalConfig;
	
	static {
		globalConfig = new HashMap<String, String>();
		JSONArray ja = new JSONArray();
		globalConfig.put("txids", ja.toString());
	}
	
	private static void putTXID(String txid) {
		JSONArray ja = new JSONArray(globalConfig.get("txids"));
		ja.put(txid);
		globalConfig.put("txids", ja.toString());
	}
	
	public static JSONArray allTXID() {
		return new JSONArray(globalConfig.get("txids"));
	}
	
	public static void confirmTX(String txid, String fullSigned) {
		JSONObject js = getTransaction(txid);
		js.put("fullSigned", fullSigned);
		js.put("confirmed", "1");
		globalConfig.put(txid, js.toString());
	}
	
	public static void writeTransaction(String txid, String raw, String signed, String fullSigned, String confirmed, String python, String pyHash) {
		JSONObject js = new JSONObject();
		js.put("txid", txid);
		js.put("rawTransaction", raw);
		js.put("partialSigned", signed);
		js.put("fullSigned", fullSigned);
		js.put("confirmed", confirmed);
		js.put("python", python);
		js.put("pythonHash", pyHash);
		globalConfig.put(txid, js.toString());
		putTXID(txid);
	}
	
	public static JSONObject getTransaction(String txid) {
		return new JSONObject(globalConfig.get(txid));
	}
	
}
