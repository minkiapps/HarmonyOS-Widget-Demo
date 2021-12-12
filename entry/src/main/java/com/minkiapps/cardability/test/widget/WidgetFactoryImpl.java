package com.minkiapps.cardability.test.widget;

import com.minkiapps.cardability.test.widget.impl.JSJokeWidget;
import com.minkiapps.cardability.test.widget.impl.JokeWidget;
import com.minkiapps.cardability.test.widget.impl.LocationWidget;
import com.minkiapps.widgetmanager.WidgetController;
import com.minkiapps.widgetmanager.WidgetFactory;
import com.minkiapps.widgetmanager.model.WidgetInfo;

public class WidgetFactoryImpl implements WidgetFactory {

    //they should be the same name like in json.config forms declaration
    private static final String DEFAULT_WIDGET_NAME = "location_widget";
    private static final String JOKE_WIDGET_NAME = "joke_java_widget";
    private static final String JOKE_JS_WIDGET_NAME = "joke_js_widget";

    @Override
    public WidgetController createFormController(final WidgetController.WidgetContext widgetContext,
                                                 final WidgetInfo widgetInfo) {

        WidgetController widgetController = null;
        final String widgetName = widgetInfo.getName();
        switch (widgetName) {
            case DEFAULT_WIDGET_NAME:
                widgetController = new LocationWidget(widgetContext, widgetInfo);
                break;
            case JOKE_WIDGET_NAME:
                widgetController = new JokeWidget(widgetContext, widgetInfo);
                break;
            case JOKE_JS_WIDGET_NAME:
                widgetController = new JSJokeWidget(widgetContext, widgetInfo);
                break;
            default:
                break;
        }
        return widgetController;
    }
}
