import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class CryptoUtils
{
    static
    {   // add Bouncy Castle Crypto provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * @param modStr base64 encoded string representing the public key modulus
     * @return a new Java PublicKey instance
     */
    public static PublicKey createPublicKey(String modStr)
    {
        try
        {
            byte nbytes[] = Base64.getDecoder().decode(modStr); // n
            byte ebytes[] = {0x01, 0x00, 0x01};                 // 65537
            BigInteger modulus = new BigInteger(Hex.encodeHexString(nbytes), 16);        // byte array converted to hex string
            BigInteger publicExponent = new BigInteger(Hex.encodeHexString(ebytes), 16); // because it doesn't work otherwise
            return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param modStr base64 encoding of the RSA key's public modulus
     * @param privExp base64 encoding of the RSA key's private exponent
     * @return new PrivateKey construct
     */
    public static PrivateKey createPrivateKey(String modStr, String privExp)
    {
        try
        {
            byte nbytes[] = Base64.getDecoder().decode(modStr);  // n
            byte dbytes[] = Base64.getDecoder().decode(privExp); // d
            BigInteger modulus = new BigInteger(Hex.encodeHexString(nbytes),16);            // byte array converted to hex string
            BigInteger privateExponent = new BigInteger(Hex.encodeHexString(dbytes),16);    // because it doesn't work otherwise
            return KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(modulus, privateExponent));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * generate a new RSA(4096) keypair
     * @return Public and Private key pair
     */
    public static KeyPair generateKeys()
    {
        try
        {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(4096);
            return gen.generateKeyPair();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key a Java crypto key (should be RSAPublicKey or RSAPrivateKey in this app)
     * @return base64 encoding of the keyData (pkcs8 if private, spki if public)
     */
    public static String exportKey(Key key)
    {
        String format = key.getFormat();
        if (format != null)
            return Base64.getEncoder().encodeToString(key.getEncoded());
        return null;
    }

    /**
     * @param data base64 encoded string representing the plain text
     * @param publicKey the RSA(4096) public key to use to encrypt the data
     * @return base64 encoding of the ciphertext
     */
    @SuppressWarnings("Duplicates")
    public static String encryptData(String data, PublicKey publicKey)
    {
        try
        {
            byte bytes[] = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            cipher.init(ENCRYPT_MODE, publicKey);
            cipher.update(bytes);
            return Base64.getEncoder().encodeToString(cipher.doFinal());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data base64 encoded string representing the cipher text
     * @param privateKey the RSA(4096) private key that can decrypt the data
     * @return base64 encoding of the plaintext
     */
    @SuppressWarnings("Duplicates")
    public static String decryptData(String data, PrivateKey privateKey)
    {
        try
        {
            byte bytes[] = Base64.getDecoder().decode(data);
            Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BC");
            cipher.init(DECRYPT_MODE, privateKey);
            cipher.update(bytes);
            return Base64.getEncoder().encodeToString(cipher.doFinal());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param data the signed data encoded as a base64 string
     * @param signature base64 encoded signature
     * @param publicKey the public key that can be used to decrypt the signature
     * @return true if signature and data verified successfully
     */
    public static boolean verifySignature(String data, String signature, PublicKey publicKey)
    {
        try
        {
            byte dbytes[] = Base64.getDecoder().decode(data);
            byte sbytes[] = Base64.getDecoder().decode(signature);
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(publicKey);
            sign.update(dbytes);
            return sign.verify(sbytes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     */
    public static String signData(String data, PrivateKey privKey)
    {
        try
        {
            byte dbytes[] = Base64.getDecoder().decode(data);
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privKey);
            sig.update(dbytes);
            return Base64.getEncoder().encodeToString(sig.sign());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Utility method to compute SHA-256 hashes 
     * @param data Base-64 encoded string 
     * @return	Base-64 encoded SHA-256 hash of the input
     */
    public static String hashData(String data)
    {
    	try
    	{
    		byte dbytes[] = Base64.getDecoder().decode(data);
    		//...
    		return Base64.getEncoder().encodeToString(dbytes);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
}
