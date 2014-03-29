package btcoracle;
import org.json.JSONObject;

class Oracle {
	private String python;
	private String transaction;


	/**
	* Parses Bitcoin String, Verifies that it is signed correctly.
	* @return Hash of the python code
	**/
	private String btcVerify() {
		// Throws BitcoinException on bad signature
	}

	/**
	* Checks code against hash, then execute python code.
	* @param: Hash of the python code
	* @return: Outcome of python code
	**/
	private boolean pyVerify(String pyHash) {

	}

	/**
	* Signs the bitcoin transaction with our Oracle's private key.
	* Note: Uses bitcoind/bitcoinj to do this
	* @return: signed transaction
	**/
	private String signTransaction() {

	}


	public Oracle(String newPython, String newTransaction) {
		this.python = newPython;
		this.transaction = newTransaction;
	}

	public JSONObject response() {
		JSONObject ret = new JSONObject();



		String pyHash;
		try {
			pyHash = btcVerify();
		} catch (BitcoinException e) {
			// Append 400 status and error message, return
		}

		boolean resp;
		try {
			resp = pyVerify(pyHash);
		} catch (BitcoinException e) {
			// Append 400 status and error message, return
		}
		

		if (resp) {
			// Sign, set status to 200, message to signed transaction
		} else {
			// Set status to 200, failure message
		}

		return ret;
	}
}