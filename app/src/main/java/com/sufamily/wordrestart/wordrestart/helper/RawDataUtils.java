package com.sufamily.wordrestart.wordrestart.helper;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by N1245 on 2015-03-04.
 */
public class RawDataUtils {

    /**
     * It loads word list from file.
     * @return
     */
    public List<String> readRawResource(Context ctx, int rid, String delimeter) {
        List<String> readData = new ArrayList<>();
        String rawText = readRawTextFile(ctx, rid);
        StringTokenizer stringTokenizer = new StringTokenizer(rawText, delimeter);
        while (stringTokenizer.hasMoreTokens()){
            String token = stringTokenizer.nextToken();
            readData.add(token);
        }
        return readData;
    }

    /**
     * Read raw resource file.
     * @param ctx
     * @param resId
     * @return
     */
    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }

}
