package com.minkiapps.widgetmanager;

import com.minkiapps.widgetmanager.model.WidgetInfo;
import com.minkiapps.widgetmanager.model.enums.LocationStatus;
import com.minkiapps.widgetmanager.model.enums.ScreenType;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.agp.components.ComponentProvider;
import ohos.app.Context;

public abstract class WidgetController {

    public interface WidgetContext extends Context{

        void updateWidget(final long formId, final ComponentProvider componentProvider);

        void updateWidget(final long formId, final FormBindingData formBindingData);

        boolean isWidgetStillAlive(final long formId);

        void updateAllWidgets();

        LocationStatus canUseLocation();
    }

    protected final WidgetContext widgetContext;
    protected final WidgetInfo widgetInfo;

    public WidgetController(final WidgetContext widgetContext,
                            final WidgetInfo widgetInfo) {
        this.widgetContext = widgetContext;
        this.widgetInfo = widgetInfo;
    }

    public abstract ProviderFormInfo bindWidgetData();

    public abstract void updateWidgetData();

    /**
     * Called when receive service widget message event
     * ONLY works for JS Widgets!
     */
    public abstract void onTriggerWidgetEvent(String message);
}
