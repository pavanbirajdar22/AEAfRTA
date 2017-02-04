public class XorDataNKey {
	public static byte[] XorDataWithKey(String data,byte[] key) {
	    byte[] byteData = data.getBytes();
	    byte[] xorData = new byte[128];
	    for(int i=0;i<byteData.length;i++ ){
	    	xorData[i] = (byte)(0xff & ((int)byteData[i]) ^ ((int)key[i]));
	    }
	    return xorData;
	}
	public static byte[] XorDataWithKeyAtIndex(byte[] xorData,byte[] key,int[] indexes) {
	    for(int i=0;i<xorData.length;i++ ){
	    	xorData[i] = (byte)(0xff & ((int)xorData[i]) ^ ((int)key[indexes[i]]));
	    }
	    return xorData;
	}
}
