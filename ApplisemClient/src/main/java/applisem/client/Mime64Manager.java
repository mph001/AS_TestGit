
package applisem.client;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Unused Class to manage Base 64 encoding and decoding
 * @author LBO
 */
public class Mime64Manager{
  
  static Mime64Manager service = null;  
  
  BASE64Decoder decoder = new BASE64Decoder();
  BASE64Encoder encoder = new BASE64Encoder();

  Mime64Manager(){ 
  }

  public static Mime64Manager getMime64Manager(){
  if(service==null) service = new Mime64Manager();
  return service;
  }

  public String Decode(String encoded) throws IOException{
  String decoded= null; 
  byte[] decoded_bytes;
  decoded_bytes = decoder.decodeBuffer(encoded);
  decoded = new String(decoded_bytes);
  return decoded ;
  }
  
  public String Encode(String decoded){
  byte [] decoded_bytes = decoded.getBytes();
  return encoder.encode(decoded_bytes);
  }
  
}
