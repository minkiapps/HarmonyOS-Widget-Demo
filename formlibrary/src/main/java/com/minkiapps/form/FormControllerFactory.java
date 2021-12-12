package com.minkiapps.form;

import com.minkiapps.form.model.FormProperties;

public interface FormControllerFactory {

    FormController createFormController(final FormController.FormContext formContext,
                                        final FormProperties formProperties);
}
