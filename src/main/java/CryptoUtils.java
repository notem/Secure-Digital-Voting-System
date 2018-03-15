import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class CryptoUtils
{
    /**
     *
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
     *
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
     *
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
}
