package btcoracle;

public class BitcoinException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 60409469425047317L;
	private String message;
	
	public BitcoinException(String msg) {
		message = msg;
	}
	
	public String getMessage() {
		return message;
	}

}
