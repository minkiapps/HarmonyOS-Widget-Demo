package com.minkiapps.widgetmanager.model;

import com.minkiapps.widgetmanager.model.enums.ScreenType;

public class WidgetInfo {

    private final long widgetId;
    private final String name;
    private final int dimension;
    private final ScreenType screenType;

    public WidgetInfo(final long widgetId,
                      final String name,
                      final int dimension,
                      final ScreenType screenType) {
        this.widgetId = widgetId;
        this.name = name;
        this.dimension = dimension;
        this.screenType = screenType;
    }

    public long getWidgetId() {
        return widgetId;
    }

    public String getName() {
        return name;
    }

    public int getDimension() {
        return dimension;
    }

    public ScreenType getScreenType() {
        return screenType;
    }

    public static class Builder {

        private final long formId;
        private int dimension = 2;
        private ScreenType screenType = ScreenType.NORMAL;
        private String name = "widget";

        public Builder(final long formId) {
            this.formId = formId;
        }

        public Builder withDimension(final int dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withScreenType(final ScreenType screenType) {
            this.screenType = screenType;
            return this;
        }

        public final WidgetInfo build() {
            return new WidgetInfo(
                    formId,
                    name,
                    dimension,
                    screenType
            );
        }
    }
}
