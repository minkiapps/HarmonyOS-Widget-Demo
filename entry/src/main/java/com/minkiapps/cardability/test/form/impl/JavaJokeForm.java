package com.minkiapps.cardability.test.form.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.cardability.test.slice.JokeAbilitySlice;
import com.minkiapps.form.FormController;
import com.minkiapps.form.model.FormProperties;
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

public class JavaJokeForm extends FormController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JavaJokeForm.class.getName());

    private static final int DEFAULT_DIMENSION_2X4 = 3;
    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    private final String pattern = "HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X4, ResourceTable.Layout_form_joke_widget_2_4);
    }

    public JavaJokeForm(final FormContext formContext, final FormProperties formProperties) {
        super(formContext, formProperties);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        loadJoke();
        return new ProviderFormInfo(RESOURCE_ID_MAP.get(formProperties.getDimension()), formContext);
    }

    @Override
    public void updateFormData() {
        loadJoke();
    }

    @Override
    public void onTriggerFormEvent(final String message) {

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return JokeAbilitySlice.class;
    }

    private void loadJoke() {
        formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
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

    private void updateForm(final long formId, final Joke joke) {
        if(!formContext.isFormStillAlive(formId))
            return;

        formContext.getMainTaskDispatcher().asyncDispatch(() -> {
            final ComponentProvider componentProvider = new ComponentProvider(ResourceTable.Layout_form_joke_widget_2_4, formContext);
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_joke, joke.getValue());
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_created_time, "Joke is from " + simpleDateFormat.format(new Date()));
            formContext.updateFormWidget(formId, componentProvider);
        });
    }
}
