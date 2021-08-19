package sw.mobileapp.photovault.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private String filePath = Environment.getExternalStorageDirectory().getPath()+ "/test/test.jpg";
    // AES加密后的文件
    private static final String outPath = Environment.getExternalStorageDirectory().getPath()+ "/test/encrypt.jpg";
    // 混入字节加密后文件
    private static final String bytePath = Environment.getExternalStorageDirectory().getPath()+ "/test/byte.jpg";
    //AES加密使用的秘钥，注意的是秘钥的长度必须是16位
    private static final String AES_KEY = "MyDifficultPassw";
    //混入的字节
    private static final String BYTE_KEY = "MyByte";


    /**
     * 混入字节加密
     */
    public  void addByte(){
        try {
            //获取图片的字节流
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            FileOutputStream fops = new FileOutputStream(bytePath);
            //混入的字节流
            byte[] bytesAdd = BYTE_KEY.getBytes();
            fops.write(bytesAdd);
            fops.write(bytes);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除混入的字节解密图片
     */
    public  void removeByte(){
        try {
            FileInputStream stream = null;
            stream = new FileInputStream(new File(bytePath));
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            int i=0;
            while ((n = stream.read(b)) != -1) {
                if(i==0){
                    //第一次写文件流的时候，移除我们之前混入的字节
                    out.write(b, BYTE_KEY.length(), n-BYTE_KEY.length());
                }else{
                    out.write(b, 0, n);
                }
                i++;
            }
            stream.close();
            out.close();
            //获取字节流显示图片
            byte[] bytes= out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用AES加密标准进行加密
     */
    public void aesEncrypt()  {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(outPath);
            //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            //Cipher类为加密和解密提供密码功能,获取实例
            Cipher cipher = Cipher.getInstance("AES");
            //初始化
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            //CipherOutputStream 为加密输出流
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用AES标准解密
     */
    public void aesDecrypt() {
        try {
            FileInputStream fis = null;
            fis = new FileInputStream(outPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            //CipherInputStream 为加密输入流
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            out.flush();
            out.close();
            cis.close();
            //获取字节流显示图片
            byte[] bytes= out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
