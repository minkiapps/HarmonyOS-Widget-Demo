package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.MyApplication;
import com.minkiapps.cardability.test.net.model.Joke;
import com.minkiapps.widgetmanager.WidgetController;
import com.minkiapps.widgetmanager.model.WidgetInfo;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONObject;
import retrofit2.Response;

import java.io.IOException;

public class JSJokeWidget extends WidgetController {

    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, JSJokeWidget.class.getName());

    private static final String ACTION_RELOAD_JOKE = "RELOAD_JOKE";

    public JSJokeWidget(final WidgetContext widgetContext, final WidgetInfo widgetInfo) {
        super(widgetContext, widgetInfo);
    }

    @Override
    public ProviderFormInfo bindWidgetData() {
        loadJoke();
        return new ProviderFormInfo();
    }

    @Override
    public void updateWidgetData() {
        loadJoke();
    }

    private void loadJoke() {
        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","slategrey");
        zsonObject.put("joke_text","Loading joke...");
        widgetContext.updateWidget(widgetInfo.getWidgetId(), new FormBindingData(zsonObject));
        widgetContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final Response<Joke> jokeResponse = MyApplication.getApiService().fetchJokes().execute();
                if(jokeResponse.isSuccessful()) {
                    final Joke joke = jokeResponse.body();
                    updateWidget(widgetInfo.getWidgetId(), joke);
                }
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch jokes: " + e.getMessage(),e);
            }
        });
    }

    private void updateWidget(final long WidgetId, final Joke joke) {
        if(!widgetContext.isWidgetStillAlive(WidgetId))
            return;

        final ZSONObject zsonObject = new ZSONObject();
        zsonObject.put("joke_color","grey");
        zsonObject.put("joke_text", joke.getValue());
        widgetContext.updateWidget(WidgetId,new FormBindingData(zsonObject));
    }

    @Override
    public void onTriggerWidgetEvent(final String message) {
        if(message.equals(ACTION_RELOAD_JOKE)) {
            loadJoke();
        }
    }
}
