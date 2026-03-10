package com.platform.authcommon.utils;

import java.util.zip.Adler32;

public class FastHashUtils {


    private final ThreadLocal<Adler32> adler32ThreadLocal =
            ThreadLocal.withInitial(Adler32::new);


    public long computeHash(Object... fields) {
        Adler32 adler32 = adler32ThreadLocal.get();
        adler32.reset();

        for (Object field : fields) {
            if (field instanceof byte[]) {
                byte[] data = (byte[]) field;
                adler32.update(data, 0, Math.min(data.length, 1024));
            } else {
                adler32.update(field.toString().getBytes());
            }
        }
        return adler32.getValue();
    }

    public static void main(String[] args) {

        FastHashUtils utils = new FastHashUtils();

        long hash = utils.computeHash("34", "45", "55");
        System.out.println(hash);


    }


}
