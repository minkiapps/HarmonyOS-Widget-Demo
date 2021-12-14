package com.minkiapps.cardability.test.form.impl;

import com.minkiapps.cardability.test.slice.LogisticAbilitySlice;
import com.minkiapps.form.FormController;
import com.minkiapps.form.model.FormProperties;
import com.minkiapps.form.model.enums.ScreenType;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.utils.zson.ZSONObject;

import java.util.Random;

public class LogisticForm extends FormController {

    private static final String DATA_IS_QTZ = "isQtzType";
    private static final String DATA_DESCRIPTION = "description";

    private final String[] randomDescriptions = new String[] {
            "Vendor is processing the order.",
            "Arrived in destination country.",
            "Package will be delayed for few days.",
            "Package arrived and picked up.",
    };

    private final Random random = new Random();

    public LogisticForm(final FormContext formContext, final FormProperties formProperties) {
        super(formContext, formProperties);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        final ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        providerFormInfo.setJsBindingData(new FormBindingData(createFormDataZSONObject()));
        return providerFormInfo;
    }

    @Override
    public void updateFormData() {
        formContext.updateFormWidget(formProperties.getFormId(), new FormBindingData(createFormDataZSONObject()));
    }

    private ZSONObject createFormDataZSONObject() {
        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put(DATA_IS_QTZ, formProperties.getScreenType() == ScreenType.QTZ);
        zsonObject.put(DATA_DESCRIPTION, randomDescriptions[random.nextInt(4)]);
        return zsonObject;
    }

    @Override
    public void onTriggerFormEvent(final String message) {

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return LogisticAbilitySlice.class;
    }

    @Override
    public void onDelete() {

    }
}
