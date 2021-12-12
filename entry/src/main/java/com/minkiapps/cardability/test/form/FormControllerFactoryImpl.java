package com.minkiapps.cardability.test.form;

import com.minkiapps.cardability.test.form.impl.JSJokeForm;
import com.minkiapps.cardability.test.form.impl.JavaJokeForm;
import com.minkiapps.cardability.test.form.impl.LocationForm;
import com.minkiapps.cardability.test.form.impl.LogisticForm;
import com.minkiapps.form.FormController;
import com.minkiapps.form.FormControllerFactory;
import com.minkiapps.form.model.FormProperties;

public class FormControllerFactoryImpl implements FormControllerFactory {

    //they should be the same name like in json.config forms declaration
    private static final String WIDGET = "widget"; //for QTZ preview to work, name must be "widget"
    private static final String LOCATION_WIDGET_NAME = "location_widget";
    private static final String JOKE_WIDGET_NAME = "joke_java_widget";
    private static final String JOKE_JS_WIDGET_NAME = "joke_js_widget";

    @Override
    public FormController createFormController(final FormController.FormContext formContext,
                                               final FormProperties formProperties) {

        FormController formController = null;
        final String widgetName = formProperties.getName();
        switch (widgetName) {
            case WIDGET:
                formController = new LogisticForm(formContext, formProperties);
                break;
            case LOCATION_WIDGET_NAME:
                formController = new LocationForm(formContext, formProperties);
                break;
            case JOKE_WIDGET_NAME:
                formController = new JavaJokeForm(formContext, formProperties);
                break;
            case JOKE_JS_WIDGET_NAME:
                formController = new JSJokeForm(formContext, formProperties);
                break;
            default:
                break;
        }
        return formController;
    }
}
