package com.minkiapps.widgetmanager;

import com.minkiapps.widgetmanager.model.WidgetInfo;
import com.minkiapps.widgetmanager.model.enums.ScreenType;
import com.minkiapps.widgetmanager.utils.LogUtils;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetControllerManager {

    private static final String TAG = WidgetControllerManager.class.getSimpleName();

    private static final String SHARED_SP_NAME = "form_info_sp.xml";

    private static final String PREF_FORM_NAME = "formName";
    private static final String PREF_DIMENSION = "dimension";
    private static final String PREF_SCREEN_TYPE = "screen_type";

    private final HashMap<Long, WidgetController> controllerHashMap = new HashMap<>();

    private final WidgetController.WidgetContext widgetContext;
    private final WidgetFactory widgetFactory;
    private final Preferences preferences;

    private static WidgetControllerManager instance;

    private WidgetControllerManager(final WidgetController.WidgetContext widgetContext,
                                    final WidgetFactory widgetFactory) {
        this.widgetContext = widgetContext;
        this.widgetFactory = widgetFactory;
        DatabaseHelper databaseHelper = new DatabaseHelper(widgetContext.getApplicationContext());
        preferences = databaseHelper.getPreferences(SHARED_SP_NAME);
    }

    public synchronized static WidgetControllerManager getInstance(final WidgetController.WidgetContext widgetContext,
                                                                   final WidgetFactory widgetFactory) {
        if(instance == null) {
            instance = new WidgetControllerManager(widgetContext, widgetFactory);
        }
        return instance;
    }

    public synchronized WidgetController createFormController(final WidgetInfo widgetInfo) {
        final long widgetId = widgetInfo.getWidgetId();
        final String widgetName = widgetInfo.getName();
        final int dimension = widgetInfo.getDimension();
        final ScreenType screenType = widgetInfo.getScreenType();

        LogUtils.d(TAG, "saveFormId() widgetId: " + widgetId + ", widgetName: " + widgetName + ", preferences: " + preferences);
        if (preferences != null) {
            ZSONObject formObj = new ZSONObject();
            formObj.put(PREF_FORM_NAME, widgetName);
            formObj.put(PREF_DIMENSION, dimension);
            formObj.put(PREF_SCREEN_TYPE, screenType.name());
            preferences.putString(Long.toString(widgetId), ZSONObject.toZSONString(formObj));
            preferences.flushSync();
        }

        // Create controller instance.
        final WidgetController controller = newInstance(widgetId, widgetName, dimension, screenType);

        // Cache the controller.
        if (controller != null) {
            if (!controllerHashMap.containsKey(widgetId)) {
                controllerHashMap.put(widgetId, controller);
            }
        }

        return controller;
    }

    public synchronized WidgetController getController(final long widgetId) {
        if (controllerHashMap.containsKey(widgetId)) {
            return controllerHashMap.get(widgetId);
        }
        final Map<String, ?> forms = preferences.getAll();
        final String formIdString = Long.toString(widgetId);
        if (forms.containsKey(formIdString)) {
            final ZSONObject formObj = ZSONObject.stringToZSON((String) forms.get(formIdString));
            final String formName = formObj.getString(PREF_FORM_NAME);
            final int dimension = formObj.getIntValue(PREF_DIMENSION);
            final ScreenType screenType = ScreenType.parseValue(formObj.getString(PREF_SCREEN_TYPE));
            final WidgetController controller = newInstance(widgetId, formName, dimension, screenType);
            controllerHashMap.put(widgetId, controller);
        }
        return controllerHashMap.get(widgetId);
    }

    private WidgetController newInstance(final long widgetId,
                                         final String widgetName,
                                         final int dimension,
                                         final ScreenType screenType) {
        return widgetFactory.createFormController(
                widgetContext,
                new WidgetInfo.Builder(widgetId)
                        .withName(widgetName)
                        .withDimension(dimension)
                        .withScreenType(screenType)
                        .build()
        );
    }


    public List<Long> getAllWidgetIdFromSharePreference() {
        final List<Long> result = new ArrayList<>();
        final Map<String, ?> forms = preferences.getAll();
        for (String formId : forms.keySet()) {
            result.add(Long.parseLong(formId));
        }
        return result;
    }

    public synchronized void deleteWidgetController(long formId) {
        preferences.delete(Long.toString(formId));
        preferences.flushSync();
        controllerHashMap.remove(formId);
    }
}
