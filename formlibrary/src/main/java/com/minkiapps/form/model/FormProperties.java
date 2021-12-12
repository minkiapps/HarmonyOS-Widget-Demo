package com.minkiapps.form.model;

import com.minkiapps.form.model.enums.ScreenType;

public class FormProperties {

    private final long formId;
    private final String name;
    private final int dimension;
    private final ScreenType screenType;

    public FormProperties(final long formId,
                          final String name,
                          final int dimension,
                          final ScreenType screenType) {
        this.formId = formId;
        this.name = name;
        this.dimension = dimension;
        this.screenType = screenType;
    }

    public long getFormId() {
        return formId;
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

        public final FormProperties build() {
            return new FormProperties(
                    formId,
                    name,
                    dimension,
                    screenType
            );
        }
    }
}
