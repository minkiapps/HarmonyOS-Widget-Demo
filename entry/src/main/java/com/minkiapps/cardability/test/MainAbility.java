package com.minkiapps.cardability.test;

import com.minkiapps.cardability.test.slice.LocationAbilitySlice;
import com.minkiapps.cardability.test.widget.WidgetFactoryImpl;
import com.minkiapps.widgetmanager.WidgetAbility;
import com.minkiapps.widgetmanager.WidgetFactory;

import java.util.Arrays;

import static com.minkiapps.cardability.test.slice.LocationAbilitySlice.REQUEST_PERMISSION_CODE;

public class MainAbility extends WidgetAbility {

    @Override
    protected String getMainRouteEntry() {
        return LocationAbilitySlice.class.getName();
    }

    @Override
    protected WidgetFactory getWidgetFactory() {
        return new WidgetFactoryImpl();
    }

    @Override
    public void onRequestPermissionsFromUserResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE && Arrays.stream(grantResults).allMatch(i -> i == 0)) {
            updateAllWidgets();
        }
    }
}
