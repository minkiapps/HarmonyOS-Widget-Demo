package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.cardability.test.widget.controller.FormController;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JokeWidgetImpl extends FormController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JokeWidgetImpl.class.getName());

    private static final int DEFAULT_DIMENSION_2X4 = 3;
    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    private final String pattern = "HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X4, ResourceTable.Layout_form_joke_widget_2_4);
    }

    public JokeWidgetImpl(final FormContext formContext, final String formName, final Integer dimension) {
        super(formContext, formName, dimension);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.debug(TAG, "bind form data when create form");
        return new ProviderFormInfo(RESOURCE_ID_MAP.get(dimension), formContext);
    }

    @Override
    public void updateFormData(final long formId) {
        formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            final Response<List<Joke>> listResponse;
            try {
                listResponse = MyApplication.getApiService().fetchJokes().execute();
                if(listResponse.isSuccessful()) {
                    final List<Joke> jokes = listResponse.body();
                    if(jokes != null && !jokes.isEmpty()) {
                        updateForm(formId, jokes.get(0));
                    } else {
                        HiLog.debug(TAG, "No joke is available :(");
                    }
                }
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch jokes: " + e.getMessage(),e);
            }
        });
    }

    private void updateForm(final long formId, final Joke joke) {
        if(!formContext.isWidgetStillAlive(formId))
            return;

        formContext.getMainTaskDispatcher().asyncDispatch(() -> {
            final ComponentProvider componentProvider = new ComponentProvider(ResourceTable.Layout_form_joke_widget_2_4, formContext);
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_setup, joke.getSetup());
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_punchline, joke.getPunchline());
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_created_time, "Joke is from " + simpleDateFormat.format(new Date()));
            formContext.updateWidget(formId, componentProvider);
        });
    }

    @Override
    public void onTriggerFormEvent(final long formId, final String message) {

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return null;
    }
}
