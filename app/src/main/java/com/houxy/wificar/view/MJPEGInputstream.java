package com.houxy.wificar.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Houxy on 2016/11/19.
 */

public class MJPEGInputStream extends DataInputStream{

    //文件头标识
    private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
    //文件尾标识
    private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;

    public static MJPEGInputStream read(String sUrl) {
        HttpURLConnection urlConnection;
        URL url = null;
        try {
            url = new URL(sUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            return new MJPEGInputStream(urlConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MJPEGInputStream(InputStream in) { super(new BufferedInputStream(in, FRAME_MAX_LENGTH)); }

    private int getEndOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for(int i=0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if(c == sequence[seqIndex]) {
                seqIndex++;
                if(seqIndex == sequence.length) return i + 1;
            } else seqIndex = 0;
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSequence(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] headerBytes) throws IOException, NumberFormatException {
        String CONTENT_LENGTH = "Content-Length";

        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    public Bitmap readMjpegFrame() throws IOException {
        int mContentLength;
        //TODO 懵逼...
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        try {
            mContentLength = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            mContentLength = getEndOfSequence(this, EOF_MARKER);
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(headerLen);
        readFully(frameData);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
    }
}
