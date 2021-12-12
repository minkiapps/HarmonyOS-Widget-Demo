package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.slice.LogisticAbilitySlice;
import com.minkiapps.widgetmanager.WidgetController;
import com.minkiapps.widgetmanager.model.WidgetInfo;
import com.minkiapps.widgetmanager.model.enums.ScreenType;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.utils.zson.ZSONObject;

import java.util.Random;

public class LogisticWidget extends WidgetController {

    private static final String DATA_IS_QTZ = "isQtzType";
    private static final String DATA_DESCRIPTION = "description";

    private final String[] randomDescriptions = new String[] {
            "Vendor is processing the order.",
            "Arrived in destination country.",
            "Package will be delayed for few days.",
            "Package arrived and picked up.",
    };

    private final Random random = new Random();

    public LogisticWidget(final WidgetContext widgetContext, final WidgetInfo widgetInfo) {
        super(widgetContext, widgetInfo);
    }

    @Override
    public ProviderFormInfo bindWidgetData() {
        final ProviderFormInfo providerFormInfo = new ProviderFormInfo();
        providerFormInfo.setJsBindingData(new FormBindingData(createFormDataZSONObject()));
        return providerFormInfo;
    }

    @Override
    public void updateWidgetData() {
        widgetContext.updateWidget(widgetInfo.getWidgetId(), new FormBindingData(createFormDataZSONObject()));
    }

    private ZSONObject createFormDataZSONObject() {
        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put(DATA_IS_QTZ, widgetInfo.getScreenType() == ScreenType.QTZ);
        zsonObject.put(DATA_DESCRIPTION, randomDescriptions[random.nextInt(4)]);
        return zsonObject;
    }

    @Override
    public void onTriggerWidgetEvent(final String message) {

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return LogisticAbilitySlice.class;
    }
}
