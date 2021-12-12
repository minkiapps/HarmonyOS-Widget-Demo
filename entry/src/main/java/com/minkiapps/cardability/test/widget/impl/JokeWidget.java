package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.widgetmanager.WidgetController;
import com.minkiapps.widgetmanager.model.WidgetInfo;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.agp.components.ComponentProvider;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JokeWidget extends WidgetController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JokeWidget.class.getName());

    private static final int DEFAULT_DIMENSION_2X4 = 3;
    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    private final String pattern = "HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X4, ResourceTable.Layout_form_joke_widget_2_4);
    }

    public JokeWidget(final WidgetContext widgetContext, final WidgetInfo widgetInfo) {
        super(widgetContext, widgetInfo);
    }

    @Override
    public ProviderFormInfo bindWidgetData() {
        loadJoke();
        return new ProviderFormInfo(RESOURCE_ID_MAP.get(widgetInfo.getDimension()), widgetContext);
    }

    @Override
    public void updateWidgetData() {
        loadJoke();
    }

    @Override
    public void onTriggerWidgetEvent(final String message) {

    }

    private void loadJoke() {
        widgetContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final Response<Joke> jokeResponse = MyApplication.getApiService().fetchJokes().execute();
                if(jokeResponse.isSuccessful()) {
                    final Joke joke = jokeResponse.body();
                    updateForm(widgetInfo.getWidgetId(), joke);
                }
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch jokes: " + e.getMessage(),e);
            }
        });
    }

    private void updateForm(final long formId, final Joke joke) {
        if(!widgetContext.isWidgetStillAlive(formId))
            return;

        widgetContext.getMainTaskDispatcher().asyncDispatch(() -> {
            final ComponentProvider componentProvider = new ComponentProvider(ResourceTable.Layout_form_joke_widget_2_4, widgetContext);
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_joke, joke.getValue());
            componentProvider.setText(ResourceTable.Id_t_form_joke_widget_created_time, "Joke is from " + simpleDateFormat.format(new Date()));
            widgetContext.updateWidget(formId, componentProvider);
        });
    }
}
