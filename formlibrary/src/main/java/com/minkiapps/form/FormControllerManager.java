package com.minkiapps.form;

import com.minkiapps.form.model.FormProperties;
import com.minkiapps.form.model.enums.ScreenType;
import com.minkiapps.form.utils.LogUtils;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormControllerManager {

    private static final String TAG = FormControllerManager.class.getSimpleName();

    private static final String SHARED_SP_NAME = "form_info_sp.xml";

    private static final String PREF_FORM_NAME = "formName";
    private static final String PREF_DIMENSION = "dimension";
    private static final String PREF_SCREEN_TYPE = "screen_type";

    private final HashMap<Long, FormController> controllerHashMap = new HashMap<>();

    private final FormController.FormContext formContext;
    private final FormControllerFactory formControllerFactory;
    private final Preferences preferences;

    private static FormControllerManager instance;

    private FormControllerManager(final FormController.FormContext formContext,
                                  final FormControllerFactory formControllerFactory) {
        this.formContext = formContext;
        this.formControllerFactory = formControllerFactory;
        DatabaseHelper databaseHelper = new DatabaseHelper(formContext.getApplicationContext());
        preferences = databaseHelper.getPreferences(SHARED_SP_NAME);
    }

    public synchronized static FormControllerManager getInstance(final FormController.FormContext formContext,
                                                                 final FormControllerFactory formControllerFactory) {
        if(instance == null) {
            instance = new FormControllerManager(formContext, formControllerFactory);
        }
        return instance;
    }

    public synchronized FormController createFormController(final FormProperties formProperties) {
        final long formId = formProperties.getFormId();
        final String formName = formProperties.getName();
        final int dimension = formProperties.getDimension();
        final ScreenType screenType = formProperties.getScreenType();

        LogUtils.d(TAG, "saveFormId() formId: " + formId + ", formName: " + formName + ", preferences: " + preferences);
        if (preferences != null) {
            ZSONObject formObj = new ZSONObject();
            formObj.put(PREF_FORM_NAME, formName);
            formObj.put(PREF_DIMENSION, dimension);
            formObj.put(PREF_SCREEN_TYPE, screenType.name());
            preferences.putString(Long.toString(formId), ZSONObject.toZSONString(formObj));
            preferences.flushSync();
        }

        // Create controller instance.
        final FormController controller = newInstance(formId, formName, dimension, screenType);

        // Cache the controller.
        if (controller != null) {
            if (!controllerHashMap.containsKey(formId)) {
                controllerHashMap.put(formId, controller);
            }
        }

        return controller;
    }

    public synchronized FormController getController(final long formId) {
        if (controllerHashMap.containsKey(formId)) {
            return controllerHashMap.get(formId);
        }
        final Map<String, ?> forms = preferences.getAll();
        final String formIdString = Long.toString(formId);
        if (forms.containsKey(formIdString)) {
            final ZSONObject formObj = ZSONObject.stringToZSON((String) forms.get(formIdString));
            final String formName = formObj.getString(PREF_FORM_NAME);
            final int dimension = formObj.getIntValue(PREF_DIMENSION);
            final ScreenType screenType = ScreenType.parseValue(formObj.getString(PREF_SCREEN_TYPE));
            final FormController controller = newInstance(formId, formName, dimension, screenType);
            controllerHashMap.put(formId, controller);
        }
        return controllerHashMap.get(formId);
    }

    private FormController newInstance(final long formId,
                                       final String formName,
                                       final int dimension,
                                       final ScreenType screenType) {
        return formControllerFactory.createFormController(
                formContext,
                new FormProperties.Builder(formId)
                        .withName(formName)
                        .withDimension(dimension)
                        .withScreenType(screenType)
                        .build()
        );
    }


    public List<Long> getAllFormIdFromSharePreference() {
        final List<Long> result = new ArrayList<>();
        final Map<String, ?> forms = preferences.getAll();
        for (String formId : forms.keySet()) {
            result.add(Long.parseLong(formId));
        }
        return result;
    }

    public synchronized void deleteFormController(long formId) {
        preferences.delete(Long.toString(formId));
        preferences.flushSync();
        controllerHashMap.remove(formId);
    }
}
