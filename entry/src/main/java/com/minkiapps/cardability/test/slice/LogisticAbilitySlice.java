package com.minkiapps.cardability.test.slice;

import com.minkiapps.cardability.test.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class LogisticAbilitySlice extends AbilitySlice {

    @Override
    protected void onStart(final Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_logistic);
    }
}
