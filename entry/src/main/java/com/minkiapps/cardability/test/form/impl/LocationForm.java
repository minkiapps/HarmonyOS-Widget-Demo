package com.minkiapps.cardability.test.form.impl;

import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.slice.LocationAbilitySlice;
import com.minkiapps.form.FormController;
import com.minkiapps.form.model.FormProperties;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentProvider;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.*;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class LocationForm extends FormController implements LocatorCallback{
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, LocationForm.class.getName());

    private static final int DEFAULT_DIMENSION_1X2 = 1;
    private static final int DEFAULT_DIMENSION_2X2 = 2;

    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    private final String pattern = "HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_1X2, ResourceTable.Layout_form_location_widget_1_2);
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X2, ResourceTable.Layout_form_location_widget_2_2);
    }

    private final Locator locator;
    private final GeoConvert geoConvert = new GeoConvert();

    public LocationForm(final FormContext formContext,
                        final FormProperties formProperties) {
        super(formContext, formProperties);
        this.locator = new Locator(formContext);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        final ProviderFormInfo providerFormInfo = new ProviderFormInfo(RESOURCE_ID_MAP.get(formProperties.getDimension()), formContext);
        final ComponentProvider componentProvider = providerFormInfo.getComponentProvider();
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.HIDE);
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.HIDE);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateFormData();
            }
        },100);
        return providerFormInfo;
    }

    @Override
    public void updateFormData() {
        HiLog.debug(TAG, "update form data timing, default 30 minutes");

        final long formId = formProperties.getFormId();
        final ComponentProvider componentProvider = new ComponentProvider(RESOURCE_ID_MAP.get(formProperties.getDimension()), formContext);
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.HIDE);
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.HIDE);
        requestLocation(componentProvider);
        formContext.updateFormWidget(formId, componentProvider);
    }

    private void requestLocation(final ComponentProvider componentProvider) {
        switch (formContext.canUseLocation()) {
            case USE_IN_BACKGROUND_READY:
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.VISIBLE);
                locator.stopLocating(this);
                locator.requestOnce(new RequestParam(RequestParam.PRIORITY_FAST_FIRST_FIX, 0, 0), this);
                break;
            case WHILE_APP_IN_USE_READY:
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.VISIBLE);
                final Location cachedLocation = locator.getCachedLocation();
                if(cachedLocation != null) {
                    renderWidget(cachedLocation);
                }
                break;

            case PERMISSION_NOT_GRANTED:
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.VISIBLE);
                componentProvider.setText(ResourceTable.Id_t_form_location_widget_disabled_explanation, "Location permission is not granted");
                break;

            case DISABLED:
                locator.requestEnableLocation();
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.VISIBLE);
                componentProvider.setText(ResourceTable.Id_t_form_location_widget_disabled_explanation, "Location is disabled");
                break;
        }
    }

    private void setAddress(final ComponentProvider componentProvider,
                            final GeoAddress address) {
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.VISIBLE);
        componentProvider.setText(ResourceTable.Id_t_form_location_widget_address,
                String.format("%s %s %.4f, %.4f",
                        address.getAdministrativeArea(),
                        address.getCountryName(),
                        address.getLatitude(),
                        address.getLongitude())
        );

        if(formProperties.getDimension() == DEFAULT_DIMENSION_2X2) {
            componentProvider.setText(ResourceTable.Id_t_form_location_widget_last_updated_time, "Last updated time: " + simpleDateFormat.format(new Date()));
            formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
                try {
                    final InputStream in = new URL(String.format("https://www.countryflags.io/%s/shiny/64.png", address.getCountryCode())).openStream();
                    final PixelMap pixelmap = ImageSource.create(in, new ImageSource.SourceOptions()).createPixelmap(new ImageSource.DecodingOptions());
                    componentProvider.setImagePixelMap(ResourceTable.Id_i_form_location_widget_country_flag, pixelmap);
                    formContext.updateFormWidget(formProperties.getFormId(), componentProvider);
                } catch (IOException e) {
                    HiLog.error(TAG, "Failed to fetch country flag: " + e.getMessage());
                }
            });
        }

        formContext.updateFormWidget(formProperties.getFormId(), componentProvider);
    }

    @Override
    public void onTriggerFormEvent(final String message) {

    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(final Intent intent) {
        return LocationAbilitySlice.class;
    }

    @Override
    public void onDelete() {
        locator.stopLocating(this);
    }

    @Override
    public void onLocationReport(final Location location) {
        renderWidget(location);
    }

    private void renderWidget(final Location location) {
        if(!formContext.isFormStillAlive(formProperties.getFormId())) {
            return;
        }

        try {
            final List<GeoAddress> addressList = geoConvert
                    .getAddressFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (!addressList.isEmpty()) {
                if (!formContext.isFormStillAlive(formProperties.getFormId()))
                    return;

                final GeoAddress address = addressList.get(0);
                HiLog.debug(TAG, "Address: " + address.toString());

                final ProviderFormInfo providerFormInfo = new ProviderFormInfo(RESOURCE_ID_MAP.get(formProperties.getDimension()), formContext);
                final ComponentProvider componentProvider = providerFormInfo.getComponentProvider();
                setAddress(componentProvider, address);
            }
        } catch (IOException e) {
            HiLog.error(TAG, "Failed to convert lat and long to address");
        }
    }

    @Override
    public void onStatusChanged(final int i) {
        HiLog.debug(TAG, "Location status changed: " + i);
    }

    @Override
    public void onErrorReport(final int i) {
        HiLog.debug(TAG, "On location error: " + i);
    }
}