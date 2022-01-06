package com.minkiapps.form;

import com.minkiapps.form.model.FormProperties;
import com.minkiapps.form.model.enums.LocationStatus;
import com.minkiapps.form.model.enums.ScreenType;
import com.minkiapps.form.utils.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.ComponentProvider;
import ohos.bundle.IBundleManager;
import ohos.location.Locator;

import java.util.Arrays;

abstract public class FormAbility extends LifeCycleTrackerAbility implements FormController.FormContext {

    public static final int DEFAULT_DIMENSION_2X2 = 2;
    private static final int INVALID_FORM_ID = -1;
    private static final String TAG = FormAbility.class.getSimpleName();

    private static final String PERMISSION_LOCATION = "ohos.permission.LOCATION";
    private static final String PERMISSION_LOCATION_BACKGROUND = "ohos.permission.LOCATION_IN_BACKGROUND";

    private static final String[] locationPermissions = {
            PERMISSION_LOCATION,
            PERMISSION_LOCATION_BACKGROUND
    };

    private final FormControllerFactory formControllerFactory = getFormFactory();

    protected abstract FormControllerFactory getFormFactory();

    protected FormControllerManager getFormControllerManager() {
        return FormControllerManager.getInstance(this, formControllerFactory);
    }

    private String topWidgetSlice;

    private Locator locator = null;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(getMainRouteEntry());

        if (intentFromWidget(intent)) {
            topWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice != null) {
                setMainRoute(topWidgetSlice);
            }
        }
    }

    protected abstract String getMainRouteEntry();

    @Override
    protected ProviderFormInfo onCreateForm(Intent intent) {
        super.onCreateForm(intent);

        final long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        final String formName = intent.getStringParam(AbilitySlice.PARAM_FORM_NAME_KEY);
        final int dimension = intent.getIntParam(AbilitySlice.PARAM_FORM_DIMENSION_KEY, DEFAULT_DIMENSION_2X2);
        boolean isQTZ = false;
        final IntentParams intentParams = intent.getParam(AbilitySlice.PARAM_FORM_CUSTOMIZE_KEY);
        if ("TYPE_BALI".equals(intentParams.getParam("EXTENED_FA_CARD"))) {
            isQTZ = true;
        }

        LogUtils.d(TAG, "onCreateForm: formId=" + formId + ",formName=" + formName + " isQTZ: " + isQTZ);

        FormController formController = getFormControllerManager().getController(formId);
        formController = (formController == null) ? getFormControllerManager().createFormController(
                new FormProperties.Builder(formId)
                        .withName(formName)
                        .withDimension(dimension)
                        .withScreenType(isQTZ ? ScreenType.QTZ : ScreenType.NORMAL)
                        .build()) : formController;

        if (formController == null) {
            LogUtils.e(TAG, "Get null controller. formId: " + formId + ", formName: " + formName);
            return null;
        }

        return formController.bindFormData();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // Only response to it when starting from a service widget.
        if (intentFromWidget(intent)) {
            final String newWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice == null || !topWidgetSlice.equals(newWidgetSlice)) {
                topWidgetSlice = newWidgetSlice;
                restart();
            }
        } else {
            if (topWidgetSlice != null) {
                topWidgetSlice = null;
                restart();
            }
        }
    }

    @Override
    protected void onUpdateForm(long formId) {
        super.onUpdateForm(formId);
        final FormController formController = getFormControllerManager().getController(formId);
        formController.updateFormData();
    }

    @Override
    protected void onDeleteForm(long formId) {
        super.onDeleteForm(formId);
        getFormControllerManager().deleteFormController(formId);
    }

    @Override
    protected void onTriggerFormEvent(long formId, String message) {
        super.onTriggerFormEvent(formId, message);
        final FormController formController = getFormControllerManager().getController(formId);
        formController.onTriggerFormEvent(message);
    }

    private boolean intentFromWidget(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        return formId != INVALID_FORM_ID;
    }

    private String getRoutePageSlice(Intent intent) {
        final long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        if (formId == INVALID_FORM_ID) {
            return null;
        }

        final FormController formController = getFormControllerManager().getController(formId);
        if (formController == null) {
            return null;
        }
        Class<? extends AbilitySlice> clazz = formController.getRoutePageSlice(intent);
        if (clazz == null) {
            return null;
        }
        return clazz.getName();
    }

    @Override
    public void updateFormWidget(final long formId, final ComponentProvider componentProvider) {
        try {
            updateForm(formId, componentProvider);
        } catch (FormException e) {
            LogUtils.e(TAG, "Failed to update form: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateFormWidget(final long formId, final FormBindingData formBindingData) {
        try {
            updateForm(formId, formBindingData);
        } catch (FormException e) {
            LogUtils.e(TAG, "Failed to update form: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFormStillAlive(final long formId) {
        return getFormControllerManager().getAllFormIdFromSharePreference().contains(formId);
    }

    @Override
    public void updateAllForms() {
        getFormControllerManager().getAllFormIdFromSharePreference().forEach(this::onUpdateForm);
    }

    @Override
    public LocationStatus canUseLocation() {
        if (verifySelfPermission(PERMISSION_LOCATION) == IBundleManager.PERMISSION_DENIED) {
            return LocationStatus.PERMISSION_NOT_GRANTED;
        }

        if(locator == null) {
            locator = new Locator(this);
        }
        if (!locator.isLocationSwitchOn()) {
            return LocationStatus.DISABLED;
        }

        if (Arrays.stream(locationPermissions).allMatch(s -> verifySelfPermission(s) == IBundleManager.PERMISSION_GRANTED)) {
            return LocationStatus.USE_IN_BACKGROUND_READY;
        } else {
            return LocationStatus.WHILE_APP_IN_USE_READY;
        }
    }
}
