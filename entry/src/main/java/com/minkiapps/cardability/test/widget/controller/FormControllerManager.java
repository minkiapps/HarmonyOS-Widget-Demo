package com.minkiapps.cardability.test.widget.controller;

import com.minkiapps.cardability.test.widget.impl.JSJokeWidget;
import com.minkiapps.cardability.test.widget.impl.JokeWidget;
import com.minkiapps.cardability.test.widget.impl.LocationWidget;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Form controller manager.
 */
public class FormControllerManager {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, FormControllerManager.class.getName());

    private static final String SHARED_SP_NAME = "form_info_sp.xml";
    private static final String FORM_NAME = "formName";
    private static final String DIMENSION = "dimension";
    private static FormControllerManager managerInstance = null;
    private final HashMap<Long, FormController> controllerHashMap = new HashMap<>();

    //they should be the same name like in json.config forms declaration
    private static final String DEFAULT_WIDGET_NAME = "widget";
    private static final String JOKE_WIDGET_NAME = "joke_widget";
    private static final String JOKE_JS_WIDGET_NAME = "jsjokecard";

    private final FormController.FormContext formContext;
    private final Preferences preferences;

    /**
     * Constructor with context.
     *
     * @param formContext
     */
    private FormControllerManager(final FormController.FormContext formContext) {
        this.formContext = formContext;
        DatabaseHelper databaseHelper = new DatabaseHelper(this.formContext.getApplicationContext());
        preferences = databaseHelper.getPreferences(SHARED_SP_NAME);
    }

    /**
     * Singleton mode.
     *
     * @return FormControllerManager instance.
     */
    public static FormControllerManager getInstance(final FormController.FormContext formContext) {
        if (managerInstance == null) {
            synchronized (FormControllerManager.class) {
                if (managerInstance == null) {
                    managerInstance = new FormControllerManager(formContext);
                }
            }
        }
        return managerInstance;
    }

    /**
     * Save the form id and form name.
     *
     * @param formId    form id.
     * @param formName  form name.
     * @param dimension form dimension
     * @return FormController form controller
     */
    public synchronized FormController createFormController(long formId, String formName, int dimension) {
        if (formId < 0 || formName.isEmpty()) {
            return null;
        }
        HiLog.debug(TAG,
                "saveFormId() formId: " + formId + ", formName: " + formName + ", preferences: " + preferences);
        if (preferences != null) {
            ZSONObject formObj = new ZSONObject();
            formObj.put(FORM_NAME, formName);
            formObj.put(DIMENSION, dimension);
            preferences.putString(Long.toString(formId), ZSONObject.toZSONString(formObj));
            preferences.flushSync();
        }

        // Create controller instance.
        final FormController controller = newInstance(formName, dimension);

        // Cache the controller.
        if (controller != null) {
            if (!controllerHashMap.containsKey(formId)) {
                controllerHashMap.put(formId, controller);
            }
        }

        return controller;
    }

    /**
     * Get the form controller instance.
     *
     * @param formId form id.
     * @return the instance of form controller.
     */
    public synchronized FormController getController(long formId) {
        if (controllerHashMap.containsKey(formId)) {
            return controllerHashMap.get(formId);
        }
        Map<String, ?> forms = preferences.getAll();
        String formIdString = Long.toString(formId);
        if (forms.containsKey(formIdString)) {
            ZSONObject formObj = ZSONObject.stringToZSON((String) forms.get(formIdString));
            String formName = formObj.getString(FORM_NAME);
            int dimension = formObj.getIntValue(DIMENSION);
            FormController controller = newInstance(formName, dimension);
            controllerHashMap.put(formId, controller);
        }
        return controllerHashMap.get(formId);
    }

    private FormController newInstance(String formName, int dimension) {
        FormController formController = null;
        switch (formName) {
            case DEFAULT_WIDGET_NAME:
                formController = new LocationWidget(formContext, formName, dimension);
                break;
            case JOKE_WIDGET_NAME:
                formController = new JokeWidget(formContext, formName, dimension);
                break;
            case JOKE_JS_WIDGET_NAME:
                formController = new JSJokeWidget(formContext, formName, dimension);
                break;
            default:
                break;
        }
        return formController;
    }

    /**
     * Get all form id from the share preference
     *
     * @return form id list
     */
    public List<Long> getAllFormIdFromSharePreference() {
        List<Long> result = new ArrayList<>();
        Map<String, ?> forms = preferences.getAll();
        for (String formId : forms.keySet()) {
            result.add(Long.parseLong(formId));
        }
        return result;
    }

    /**
     * Delete a form controller
     *
     * @param formId form id
     */
    public synchronized void deleteFormController(long formId) {
        preferences.delete(Long.toString(formId));
        preferences.flushSync();
        controllerHashMap.remove(formId);
    }
}
