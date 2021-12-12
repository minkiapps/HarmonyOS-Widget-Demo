package com.minkiapps.form.utils;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.Locale;

public class LogUtils {

    private static final int MSG_CHAR_LIMIT = 1000;

    private static final String TAG_LOG = "formmanager";

    private static final int DOMAIN_ID = 1001;

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(0, DOMAIN_ID, TAG_LOG);
    private static final String LOG_FORMAT = "%{public}s: %{public}s";

    private LogUtils() {
    }

    public static void d(String tag, String msg) {
        HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    public static void dLongMessage(String tag, String msg) {
        if (msg.length() > MSG_CHAR_LIMIT) {
            HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg.substring(0, MSG_CHAR_LIMIT));
            dLongMessage(tag, msg.substring(MSG_CHAR_LIMIT));
        } else {
            HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg);
        }

    }

    public static void i(String tag, String msg) {
        HiLog.info(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    public static void w(String tag, String msg) {
        HiLog.warn(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    public static void e(String tag, String msg) {
        HiLog.error(LABEL_LOG, LOG_FORMAT, tag, msg);
    }

    public static void e(String tag, String format, Object... args) {
        String buffMsg = String.format(Locale.ROOT, format, args);
        HiLog.error(LABEL_LOG, LOG_FORMAT, tag, buffMsg);
    }

    public static void i(String tag, String format, Object... args) {
        String buffMsg = String.format(Locale.ROOT, format, args);
        HiLog.info(LABEL_LOG, LOG_FORMAT, tag, buffMsg);
    }
}
