package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.cardability.test.widget.controller.FormController;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JSJokeWidget extends FormController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JSJokeWidget.class.getName());

    private static final String ACTION_RELOAD_JOKE = "RELOAD_JOKE";

    public JSJokeWidget(final FormContext formContext, final String formName, final Integer dimension) {
        super(formContext, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.debug(TAG, "bind form data when create form");
        return new ProviderFormInfo();
    }

    @Override
    public void updateFormData(final long formId) {
        loadJoke(formId);
    }

    private void loadJoke(final long formId) {
        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","slategrey");
        zsonObject.put("joke_text","Loading joke...");
        formContext.updateWidget(formId,new FormBindingData(zsonObject));
        formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final Response<Joke> jokeResponse = MyApplication.getApiService().fetchJokes().execute();
                if(jokeResponse.isSuccessful()) {
                    final Joke joke = jokeResponse.body();
                    updateForm(formId, joke);
                }
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch jokes: " + e.getMessage(),e);
            }
        });
    }

    private void updateForm(final long formId, final Joke joke) {
        if(!formContext.isWidgetStillAlive(formId))
            return;

        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","grey");
        zsonObject.put("joke_text", joke.getValue());
        formContext.updateWidget(formId,new FormBindingData(zsonObject));
    }

    @Override
    public void onTriggerFormEvent(final long formId, final String message) {
        if(message.equals(ACTION_RELOAD_JOKE)) {
            loadJoke(formId);
        }
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return null;
    }
}
