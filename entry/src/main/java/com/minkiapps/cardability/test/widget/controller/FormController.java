package com.minkiapps.cardability.test.widget.controller;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.app.Context;

/**
 * The api set for form controller.
 */
public abstract class FormController {

    public interface FormContext extends Context{

        void updateWidget(final long formId, final ComponentProvider componentProvider);

        boolean isWidgetStillAlive(final long formId);

        LocationStatus canUseLocation();

        enum LocationStatus {
            READY,
            DISABLED,
            PERMISSION_NOT_GRANTED
        }
    }

    protected final FormContext formContext;

    /**
     * The name of current form service widget
     */
    protected final String formName;

    /**
     * The dimension of current form service widget
     */
    protected final int dimension;

    public FormController(final FormContext formContext,
                          final String formName,
                          final Integer dimension) {
        this.formContext = formContext;
        this.formName = formName;
        this.dimension = dimension;
    }

    /**
     * Bind data for a form
     *
     * @return ProviderFormInfo
     */
    public abstract ProviderFormInfo bindFormData();

    /**
     * Update form data
     *
     * @param formId the id of service widget to be updated
     */
    public abstract void updateFormData(long formId);

    /**
     * Called when receive service widget message event
     * ONLY works for JS Widgets!
     *
     * @param formId  form id
     * @param message the message context sent by service widget message event
     */
    public abstract void onTriggerFormEvent(long formId, String message);

    /**
     * Get the destination ability slice to route
     *
     * @param intent intent of current page slice
     * @return the destination ability slice name to route
     */
    public abstract Class<? extends AbilitySlice> getRoutePageSlice(Intent intent);
}
