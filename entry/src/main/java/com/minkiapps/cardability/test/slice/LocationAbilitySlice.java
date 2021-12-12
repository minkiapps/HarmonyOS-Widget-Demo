package com.minkiapps.cardability.test.slice;

import com.minkiapps.cardability.test.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class LocationAbilitySlice extends AbilitySlice {

    public static final int REQUEST_PERMISSION_CODE = 10001;

    private static final String[] locationPermission = {
            "ohos.permission.LOCATION",
            "ohos.permission.LOCATION_IN_BACKGROUND"
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_location);

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
    public void onActive() {
        super.onActive();
    }

    @Override
    public void requestPermissionsFromUser(final String[] permissions, final int requestCode) {
        super.requestPermissionsFromUser(permissions, requestCode);
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
