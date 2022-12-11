package org.hockey.hockeyware.loader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

public class AES128
{

    private SecretKeySpec key;
    private IvParameterSpec ivspec;

    public AES128( String key )
    {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            ivspec = new IvParameterSpec( iv );

            SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
            KeySpec spec = new PBEKeySpec( key.toCharArray(), "bsshhhfhhbhhha!!!!".getBytes(), 65536, 128 );
            SecretKey tmp = factory.generateSecret( spec );
            this.key = new SecretKeySpec( tmp.getEncoded(), "AES" );
        } catch ( Exception e )
        {
            ErrorHandle.popup( "Error initializing encryption", true );
        }
    }

    public byte[] encrypt( String data )
    {
        try
        {
            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            cipher.init( Cipher.ENCRYPT_MODE, key, ivspec );
            return cipher.doFinal( data.getBytes( StandardCharsets.UTF_8 ) );
        } catch ( Exception e )
        {
            e.printStackTrace();
            ErrorHandle.popup( "Error encrypting text", true );
        }
        return null;
    }

    public String decrypt( byte[] data )
    {
        try
        {
            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            cipher.init( Cipher.DECRYPT_MODE, key, ivspec );
            return new String( cipher.doFinal( data ), StandardCharsets.UTF_8 );
        } catch ( Exception e )
        {
            ErrorHandle.popup( "Error decrypting text", true );
            throw new RuntimeException( e );
        }
    }
}
