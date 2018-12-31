package com.reader.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class StringTool {

	/**
	 *  strings to Hexadecimal array,seperate string by space  
	 * @param strHexValue   Hexadecimal string
	 * @return	byte array
	 */
	public static byte[] stringToByteArray(String strHexValue) {
		String[] strAryHex = strHexValue.split(" ");
        byte[] btAryHex = new byte[strAryHex.length];

        try {
			int nIndex = 0;
			for (String strTemp : strAryHex) {
			    btAryHex[nIndex] = (byte) Integer.parseInt(strTemp, 16);
			    nIndex++;
			}
        } catch (NumberFormatException e) {

        }

        return btAryHex;
    }
	/**
	 * The encode formate of data scanned by scnner;
	 * @param date the source data
	 * @return the encode formate;
	 * @throws IOException decode exception;
	 */
	public static String charsetName(byte[] date) throws IOException {
		Charset charset = null;
		InputStream is = new ByteArrayInputStream(date);
		CodepageDetectorProxy detectorProxy = CodepageDetectorProxy.getInstance();
		detectorProxy.add(new ParsingDetector(false));
		detectorProxy.add(JChardetFacade.getInstance());
		detectorProxy.add(ASCIIDetector.getInstance());
		detectorProxy.add(UnicodeDetector.getInstance());
		charset = detectorProxy.detectCodepage(is,date.length);
		if (charset != null) {
			return charset.name();
		} else {
			return "utf-8";
		}
	}
	/**
	 * get the subbytes of the parent bytes;
	 * @param bytes parent bytes;
	 * @param start start positon;
	 * @param end end position;
	 * @return the child bytes;
	 */
	public static byte[] subBytes(byte[] bytes, int start, int end){
		byte[] subBytes = new byte[end - start];
		System.arraycopy(bytes,start,subBytes,0,end - start);
		return subBytes;
	}
	/**
	 * string array to Hexadecimal array
	 * @param strAryHex	 string array needs to be transfered
	 * @param nLen		 length   
	 * @return	  byte array
	 */
    public static byte[] stringArrayToByteArray(String[] strAryHex, int nLen) {
    	if (strAryHex == null) return null;

    	if (strAryHex.length < nLen) {
    		nLen = strAryHex.length;
    	}

    	byte[] btAryHex = new byte[nLen];

    	try {
    		for (int i = 0; i < nLen; i++) {
    			btAryHex[i] = (byte) Integer.parseInt(strAryHex[i], 16);
    		}
    	} catch (NumberFormatException e) {
	
        }

    	return btAryHex;
    }

    /**
	 * Hexadecimal character array to strings
	 * @param btAryHex	 string array needs to be transfered
	 * @param nIndex	  start position
	 * @param nLen		  transfered length
	 * @return	          transfered strings
	 */
    public static String byteArrayToString(byte[] btAryHex, int nIndex, int nLen) {
    	if (nIndex + nLen > btAryHex.length) {
    		nLen = btAryHex.length - nIndex;
    	}

    	String strResult = String.format("%02X", btAryHex[nIndex]);
    	for (int nloop = nIndex + 1; nloop < nIndex + nLen; nloop++ ) {
    		String strTemp = String.format(" %02X", btAryHex[nloop]);

    		strResult += strTemp;
    	}

    	return strResult;
    }

    /**
	 * intercept string to the specified length and shift into character array
	 * @param strValue	   input strings
	 * @return	           byte array
	 */
    public static String[] stringToStringArray(String strValue, int nLen) {
        String[] strAryResult = null;

        if (strValue != null && !strValue.equals("")) {
            ArrayList<String> strListResult = new ArrayList<String>();
            String strTemp = "";
            int nTemp = 0;

            for (int nloop = 0; nloop < strValue.length(); nloop++) {
                if (strValue.charAt(nloop) == ' ') {
                    continue;
                } else {
                    nTemp++;
                    
                    if (!Pattern.compile("^(([A-F])*([a-f])*(\\d)*)$")
                    		.matcher(strValue.substring(nloop, nloop + 1))
                    		.matches()) {
                        return strAryResult;
                    }

                    strTemp += strValue.substring(nloop, nloop + 1);

                    if ((nTemp == nLen) || (nloop == strValue.length() - 1 
                    		&& (strTemp != null && !strTemp.equals("")))) {
                        strListResult.add(strTemp);
                        nTemp = 0;
                        strTemp = "";
                    }
                }
            }
            if (strListResult.size() > 0) {
            	strAryResult = new String[strListResult.size()];
                for (int i = 0; i < strAryResult.length; i++) {
                	strAryResult[i] = strListResult.get(i);
                }
            }
        }
        return strAryResult;
    }
    
}
