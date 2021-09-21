package com.minkiapps.cardability.test;

import com.minkiapps.cardability.test.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.content.Intent;
import com.minkiapps.cardability.test.widget.controller.FormController;
import com.minkiapps.cardability.test.widget.controller.FormControllerManager;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.agp.components.ComponentProvider;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Locator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainAbility extends Ability implements FormController.FormContext {

    public static final int DEFAULT_DIMENSION_2X2 = 2;
    private static final int INVALID_FORM_ID = -1;
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, MainAbility.class.getName());
    private static final int REQUEST_PERMISSION_CODE = 10001;

    private String topWidgetSlice;

    private static final String[] locationPermission = {
            "ohos.permission.LOCATION",
            "ohos.permission.LOCATION_IN_BACKGROUND"
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        if (intentFromWidget(intent)) {
            topWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice != null) {
                setMainRoute(topWidgetSlice);
            }
        }

        final List<String> permissionList = new ArrayList<>();
        for (String s : locationPermission) {
            if (verifySelfPermission(s) != 0 && canRequestPermission(s)) {
                permissionList.add(s);
            }
        }

        if (permissionList.size() > 0) {
            requestPermissionsFromUser(permissionList.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }

        stopAbility(intent);
    }

    @Override
    public void onRequestPermissionsFromUserResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE && Arrays.stream(grantResults).allMatch(i -> i == 0)) {
            FormControllerManager.getInstance(MainAbility.this)
                    .getAllFormIdFromSharePreference()
                    .forEach(formId -> getMainTaskDispatcher().asyncDispatch(() -> onUpdateForm(formId))
                    );
        }
    }

    @Override
    protected ProviderFormInfo onCreateForm(Intent intent) {
        HiLog.debug(TAG, "onCreateForm");
        final long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        final String formName = intent.getStringParam(AbilitySlice.PARAM_FORM_NAME_KEY);
        final int dimension = intent.getIntParam(AbilitySlice.PARAM_FORM_DIMENSION_KEY, DEFAULT_DIMENSION_2X2);
        HiLog.debug(TAG, "onCreateForm: formId=" + formId + ",formName=" + formName);
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        FormController formController = formControllerManager.getController(formId);
        formController = (formController == null) ? formControllerManager.createFormController(formId,
                formName, dimension) : formController;

        if (formController == null) {
            HiLog.error(TAG, "Get null controller. formId: " + formId + ", formName: " + formName);
            return null;
        }

        formController.updateFormData(formId);
        return formController.bindFormData();
    }

    @Override
    protected void onUpdateForm(long formId) {
        HiLog.debug(TAG, "onUpdateForm");
        super.onUpdateForm(formId);
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        final FormController formController = formControllerManager.getController(formId);
        formController.updateFormData(formId);
    }

    @Override
    protected void onDeleteForm(long formId) {
        HiLog.debug(TAG, "onDeleteForm: formId=" + formId);
        super.onDeleteForm(formId);
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        formControllerManager.deleteFormController(formId);
    }

    @Override
    protected void onTriggerFormEvent(long formId, String message) {
        HiLog.debug(TAG, "onTriggerFormEvent: " + message);
        super.onTriggerFormEvent(formId, message);
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        final FormController formController = formControllerManager.getController(formId);
        formController.onTriggerFormEvent(formId, message);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intentFromWidget(intent)) { // Only response to it when starting from a service widget.
            String newWidgetSlice = getRoutePageSlice(intent);
            if (topWidgetSlice == null || !topWidgetSlice.equals(newWidgetSlice)) {
                topWidgetSlice = newWidgetSlice;
                restart();
            }
        }
    }

    private boolean intentFromWidget(Intent intent) {
        final long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        return formId != INVALID_FORM_ID;
    }

    private String getRoutePageSlice(Intent intent) {
        final long formId = intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, INVALID_FORM_ID);
        if (formId == INVALID_FORM_ID) {
            return null;
        }
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        final FormController formController = formControllerManager.getController(formId);
        if (formController == null) {
            return null;
        }
        final Class<? extends AbilitySlice> clazz = formController.getRoutePageSlice(intent);
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
            HiLog.error(TAG, "Failed to update form: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isWidgetStillAlive(final long formId) {
        final FormControllerManager formControllerManager = FormControllerManager.getInstance(this);
        return formControllerManager.getAllFormIdFromSharePreference().contains(formId);
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
