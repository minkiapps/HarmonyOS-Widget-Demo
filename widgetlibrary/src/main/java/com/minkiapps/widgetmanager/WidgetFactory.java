package com.minkiapps.widgetmanager;

import com.minkiapps.widgetmanager.model.WidgetInfo;

public interface WidgetFactory {

    WidgetController createFormController(final WidgetController.WidgetContext widgetContext,
                                          final WidgetInfo widgetInfo);
}
