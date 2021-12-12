package com.minkiapps.widgetmanager.model.enums;

public enum ScreenType {
    NORMAL, QTZ;

    public static ScreenType parseValue(final String value) {
        if(value == null) {
            return ScreenType.NORMAL;
        }
        switch (value.toUpperCase()) {
            case "QTZ":
                return ScreenType.QTZ;
            case "NORMAL": default:
                return ScreenType.NORMAL;
        }
    }
}
