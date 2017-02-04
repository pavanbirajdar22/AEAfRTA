public class XorDataNKey {
	
	public static byte[] XorDataWithKey(String data,byte[] key) {

	    byte[] byteData = data.getBytes();
	    byte[] xorData = new byte[byteData.length];
	    
	    for(int i = 0; i < byteData.length; i++ ){
	    	xorData[i] = (byte)(0xff & ((int)byteData[i]) ^ ((int)key[i]));
	    }
	    return xorData;
	}
	
	public static String XorDataWithKey(byte[] byteData, byte[] key) {
	    
	    for(int i = 0; i < byteData.length;i++ ){
	    	byteData[i] = (byte)(0xff & ((int)byteData[i]) ^ ((int)key[i]));
	    }
	    return new String(byteData);
	}
	
	public static byte[] XorDataWithKeyAtIndex(byte[] xorData,byte[] key,int[] indexes) {
		for(int i=0;i<xorData.length;i++ ){
			xorData[i] = (byte)(0xff & ((int)xorData[i]) ^ ((int)key[indexes[i]]));
		}
		return xorData;
	}
}
