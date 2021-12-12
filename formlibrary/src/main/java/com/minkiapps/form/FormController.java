package com.minkiapps.form;

import com.minkiapps.form.model.FormProperties;
import com.minkiapps.form.model.enums.LocationStatus;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.app.Context;

public abstract class FormController {

    public interface FormContext extends Context{

        void updateFormWidget(final long formId, final ComponentProvider componentProvider);

        void updateFormWidget(final long formId, final FormBindingData formBindingData);

        boolean isFormStillAlive(final long formId);

        void updateAllForms();

        LocationStatus canUseLocation();
    }

    protected final FormContext formContext;
    protected final FormProperties formProperties;

    public FormController(final FormContext formContext,
                          final FormProperties formProperties) {
        this.formContext = formContext;
        this.formProperties = formProperties;
    }

    public abstract ProviderFormInfo bindFormData();

    public abstract void updateFormData();

    /**
     * Called when receive service widget message event
     * ONLY works for JS Widgets!
     */
    public abstract void onTriggerFormEvent(String message);

    public abstract Class<? extends AbilitySlice> getRoutePageSlice(Intent intent);
}
