package scotty.fetcher;

/**
 * 
 * 
*/
public interface Fetcher {

	/**
	 * 
	 * @param request
	 * @return
	 */
	public byte[] fetch(byte[] request);
}
