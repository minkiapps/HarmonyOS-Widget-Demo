package com.minkiapps.cardability.test;

import com.minkiapps.cardability.test.slice.MainAbilitySlice;
import com.minkiapps.cardability.test.widget.WidgetFactoryImpl;
import com.minkiapps.widgetmanager.WidgetAbility;
import com.minkiapps.widgetmanager.WidgetFactory;
import ohos.aafwk.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainAbility extends WidgetAbility {

    private static final int REQUEST_PERMISSION_CODE = 10001;

    private static final String[] locationPermission = {
            "ohos.permission.LOCATION",
            "ohos.permission.LOCATION_IN_BACKGROUND"
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        final List<String> permissionList = new ArrayList<>();
        for (String s : locationPermission) {
            if (verifySelfPermission(s) != 0 && canRequestPermission(s)) {
                permissionList.add(s);
            }
        }

        if (permissionList.size() > 0) {
            requestPermissionsFromUser(permissionList.toArray(new String[0]), REQUEST_PERMISSION_CODE);
        }
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
