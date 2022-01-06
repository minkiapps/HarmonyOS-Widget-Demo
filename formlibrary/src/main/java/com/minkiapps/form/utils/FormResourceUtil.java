package com.minkiapps.form.utils;

import ohos.app.Context;

public class FormResourceUtil {

    public static final String TAG = FormResourceUtil.class.getSimpleName();

    //for JS widget you cannot use context.getString to acquire string resource, use this method instead
    public static String getStringResourceFromWidgetContext(final Context context,
                                                            final int resourceId,
                                                            final Object... args) {
        try {
            return context.getResourceManager()
                    .getElement(resourceId)
                    .getString(args);
        } catch (Exception e) {
            LogUtils.e(TAG, String.format("Failed to get string resource: %s", e.getMessage()));
            return null;
        }
    }
}
