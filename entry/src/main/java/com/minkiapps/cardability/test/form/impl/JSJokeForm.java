package com.minkiapps.cardability.test.form.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.cardability.test.slice.JokeAbilitySlice;
import com.minkiapps.form.FormController;
import com.minkiapps.form.model.FormProperties;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.app.dispatcher.task.Revocable;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;
import retrofit2.Response;

import java.io.IOException;
import java.security.Permission;

public class JSJokeForm extends FormController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JSJokeForm.class.getName());

    private static final String ACTION_RELOAD_JOKE = "RELOAD_JOKE";

    private Revocable revocable;

    public JSJokeForm(final FormContext formContext, final FormProperties formProperties) {
        super(formContext, formProperties);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        loadJoke();
        return new ProviderFormInfo();
    }

    @Override
    public void updateFormData() {
        loadJoke();
    }

    private void loadJoke() {
        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","slategrey");
        zsonObject.put("joke_text","Loading joke...");
        formContext.updateFormWidget(formProperties.getFormId(), new FormBindingData(zsonObject));

        if(revocable != null) {
            revocable.revoke();
        }
        revocable = formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final Response<Joke> jokeResponse = MyApplication.getApiService().fetchJokes().execute();
                if(jokeResponse.isSuccessful()) {
                    final Joke joke = jokeResponse.body();
                    updateForm(formProperties.getFormId(), joke);
                }
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch jokes: " + e.getMessage(),e);
            }
        });
    }

    private void updateForm(final long WidgetId, final Joke joke) {
        if(!formContext.isFormStillAlive(WidgetId))
            return;

        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","grey");
        zsonObject.put("joke_text", joke.getValue());
        formContext.updateFormWidget(WidgetId,new FormBindingData(zsonObject));
    }

    @Override
    public void onTriggerFormEvent(final String message) {
        if(message.equals(ACTION_RELOAD_JOKE)) {
            loadJoke();
        }
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return JokeAbilitySlice.class;
    }

    @Override
    public void onDelete() {
        if(revocable != null) {
            revocable.revoke();
        }
    }
}
