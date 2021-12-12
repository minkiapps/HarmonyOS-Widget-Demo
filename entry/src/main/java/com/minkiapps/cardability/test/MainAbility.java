package com.minkiapps.cardability.test;

import com.minkiapps.cardability.test.slice.LocationAbilitySlice;
import com.minkiapps.cardability.test.form.FormControllerFactoryImpl;
import com.minkiapps.form.FormAbility;
import com.minkiapps.form.FormControllerFactory;

import java.util.Arrays;

import static com.minkiapps.cardability.test.slice.LocationAbilitySlice.REQUEST_PERMISSION_CODE;

public class MainAbility extends FormAbility {

    @Override
    protected String getMainRouteEntry() {
        return LocationAbilitySlice.class.getName();
    }

    @Override
    protected FormControllerFactory getFormFactory() {
        return new FormControllerFactoryImpl();
    }

    @Override
    public void onRequestPermissionsFromUserResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE && Arrays.stream(grantResults).allMatch(i -> i == 0)) {
            updateAllForms();
        }
    }
}
