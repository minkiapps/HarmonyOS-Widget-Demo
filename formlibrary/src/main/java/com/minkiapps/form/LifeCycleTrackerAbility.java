package com.minkiapps.form;

import com.minkiapps.form.utils.LogUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;

public abstract class LifeCycleTrackerAbility extends Ability {

    private static final String TAG = LifeCycleTrackerAbility.class.getSimpleName();

    enum LifeCycleState {
        NOT_INITIALISED,
        STARTED,
        FOREGROUND,
        ACTIVE,
        DESTROYED,
    }

    private LifeCycleState lifeCycleState = LifeCycleState.NOT_INITIALISED;

    @Override
    protected void onStart(final Intent intent) {
        super.onStart(intent);
        logLifeCycleStateChanged("Ability onStart");
        lifeCycleState = LifeCycleState.STARTED;
    }

    @Override
    protected void onForeground(final Intent intent) {
        super.onForeground(intent);
        logLifeCycleStateChanged("Ability onForeground");
        lifeCycleState = LifeCycleState.FOREGROUND;
    }

    @Override
    protected void onActive() {
        super.onActive();
        logLifeCycleStateChanged("Ability onActive");
        lifeCycleState = LifeCycleState.ACTIVE;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        logLifeCycleStateChanged("Ability onInactive");
        lifeCycleState = LifeCycleState.FOREGROUND;
    }

    @Override
    protected void onBackground() {
        super.onBackground();
        logLifeCycleStateChanged("Ability onBackground");
        lifeCycleState = LifeCycleState.STARTED;
    }

    @Override
    protected void onStop() {
        super.onStop();
        logLifeCycleStateChanged("Ability onStop");
        lifeCycleState = LifeCycleState.DESTROYED;
    }

    @Override
    protected ProviderFormInfo onCreateForm(final Intent intent) {
        logWithLifeCycleData("onCreateForm");
        return super.onCreateForm(intent);
    }

    @Override
    protected void onDeleteForm(final long formId) {
        logWithLifeCycleData(String.format("onDeleteForm formId: %d",formId));
        super.onDeleteForm(formId);
    }

    @Override
    protected void onUpdateForm(final long formId) {
        logWithLifeCycleData(String.format("onUpdateForm formId: %d",formId));
        super.onUpdateForm(formId);
    }

    @Override
    protected void onTriggerFormEvent(final long formId, final String message) {
        super.onTriggerFormEvent(formId, message);
        logWithLifeCycleData(String.format("OnTriggerFormEvent, formID: %d Message: %s", formId, message));
    }

    protected void logLifeCycleStateChanged(final String log) {
        LogUtils.d(TAG, String.format("%s, ability hashCode: %s", log, hashCode()));
    }

    protected void logWithLifeCycleData(final String log) {
        LogUtils.d(TAG, String.format("%s, lifeCycleState: %s, ability hashCode: %s", log, lifeCycleState, hashCode()));
    }
}
