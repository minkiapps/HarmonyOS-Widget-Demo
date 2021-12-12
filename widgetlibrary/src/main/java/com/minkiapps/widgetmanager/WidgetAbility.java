package com.minkiapps.widgetmanager;

import com.minkiapps.widgetmanager.model.WidgetInfo;
import com.minkiapps.widgetmanager.model.enums.LocationStatus;
import com.minkiapps.widgetmanager.model.enums.ScreenType;
import com.minkiapps.widgetmanager.utils.LogUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.ComponentProvider;
import ohos.location.Locator;

import java.util.Arrays;

abstract public class WidgetAbility extends LifeCycleTrackerAbility implements WidgetController.WidgetContext {

    public static final int DEFAULT_DIMENSION_2X2 = 2;
    private static final int INVALID_FORM_ID = -1;
    private static final String TAG = WidgetAbility.class.getSimpleName();

    private static final String[] locationPermission = {
            "ohos.permission.LOCATION"
    };

    private final WidgetFactory widgetFactory = getWidgetFactory();

    protected abstract WidgetFactory getWidgetFactory();

    protected WidgetControllerManager getWidgetControllerManager() {
        return WidgetControllerManager.getInstance(this, widgetFactory);
    }

    private String topWidgetSlice;

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

        WidgetController widgetController = getWidgetControllerManager().getController(formId);
        widgetController = (widgetController == null) ? getWidgetControllerManager().createFormController(
                new WidgetInfo.Builder(formId)
                        .withName(formName)
                        .withDimension(dimension)
                        .withScreenType(isQTZ ? ScreenType.QTZ : ScreenType.NORMAL)
                        .build()) : widgetController;

        if (widgetController == null) {
            LogUtils.e(TAG, "Get null controller. formId: " + formId + ", formName: " + formName);
            return null;
        }

        return widgetController.bindWidgetData();
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
        final WidgetController widgetController = getWidgetControllerManager().getController(formId);
        widgetController.updateWidgetData();
    }

    @Override
    protected void onDeleteForm(long formId) {
        super.onDeleteForm(formId);
        getWidgetControllerManager().deleteWidgetController(formId);
    }

    @Override
    protected void onTriggerFormEvent(long formId, String message) {
        super.onTriggerFormEvent(formId, message);
        final WidgetController widgetController = getWidgetControllerManager().getController(formId);
        widgetController.onTriggerWidgetEvent(message);
    }

    private boolean intentFromWidget(Intent intent) {
        long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        return formId != INVALID_FORM_ID;
    }

    private String getRoutePageSlice(Intent intent) {
        final long widgetId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        if (widgetId == INVALID_FORM_ID) {
            return null;
        }

        final WidgetController widgetController = getWidgetControllerManager().getController(widgetId);
        if (widgetController == null) {
            return null;
        }
        Class<? extends AbilitySlice> clazz = widgetController.getRoutePageSlice(intent);
        if (clazz == null) {
            return null;
        }
        return clazz.getName();
    }

    @Override
    public void updateWidget(final long formId, final ComponentProvider componentProvider) {
        try {
            updateForm(formId, componentProvider);
        } catch (FormException e) {
            LogUtils.e(TAG, "Failed to update form: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWidget(final long formId, final FormBindingData formBindingData) {
        try {
            updateForm(formId, formBindingData);
        } catch (FormException e) {
            LogUtils.e(TAG, "Failed to update form: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isWidgetStillAlive(final long formId) {
        return getWidgetControllerManager().getAllWidgetIdFromSharePreference().contains(formId);
    }

    @Override
    public void updateAllWidgets() {
        getWidgetControllerManager().getAllWidgetIdFromSharePreference().forEach(this::onUpdateForm);
    }

    @Override
    public LocationStatus canUseLocation() {
        if (!Arrays.stream(locationPermission).allMatch(s -> verifySelfPermission(s) == 0)) {
            return LocationStatus.PERMISSION_NOT_GRANTED;
        }

        final Locator locator = new Locator(this);
        if (!locator.isLocationSwitchOn()) {
            return LocationStatus.DISABLED;
        }

        return LocationStatus.READY;
    }
}
