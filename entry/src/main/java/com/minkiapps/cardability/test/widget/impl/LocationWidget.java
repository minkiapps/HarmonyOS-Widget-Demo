package com.minkiapps.cardability.test.widget.impl;

import com.minkiapps.cardability.test.ResourceTable;
import com.minkiapps.cardability.test.widget.controller.FormController;
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

public class LocationWidget extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, LocationWidget.class.getName());
    private static final int DEFAULT_DIMENSION_2X2 = 2;

    private static final Map<Integer, Integer> RESOURCE_ID_MAP = new HashMap<>();

    private final String pattern = "HH:mm";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

    static {
        RESOURCE_ID_MAP.put(DEFAULT_DIMENSION_2X2, ResourceTable.Layout_form_location_widget_2_2);
    }

    private final Locator locator;
    private final GeoConvert geoConvert = new GeoConvert();

    public LocationWidget(final FormContext formContext, final String formName, final Integer dimension) {
        super(formContext, formName, dimension);
        locator = new Locator(formContext);
    }

    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.debug(TAG, "bind form data when create form");
        return new ProviderFormInfo(RESOURCE_ID_MAP.get(dimension), formContext);
    }

    @Override
    public void updateFormData(long formId) {
        HiLog.debug(TAG, "update form data timing, default 30 minutes");

        final ComponentProvider componentProvider = new ComponentProvider(ResourceTable.Layout_form_location_widget_2_2, formContext);
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.HIDE);
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.HIDE);
        componentProvider.setText(ResourceTable.Id_t_form_location_widget_last_updated_time, "Last updated time: " + simpleDateFormat.format(new Date()));

        switch (formContext.canUseLocation()) {
            case READY:
                locator.requestOnce(new RequestParam(RequestParam.PRIORITY_FAST_FIRST_FIX, 0, 0), new LocatorCallback() {
                    @Override
                    public void onLocationReport(final Location location) {
                        try {
                            final List<GeoAddress> addressList = geoConvert
                                    .getAddressFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            if (!addressList.isEmpty()) {
                                if (!formContext.isWidgetStillAlive(formId))
                                    return;

                                final GeoAddress address = addressList.get(0);
                                HiLog.debug(TAG, "Address: " + address.toString());
                                setAddress(formId, componentProvider, address);
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
                });
                break;

            case PERMISSION_NOT_GRANTED:
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.VISIBLE);
                componentProvider.setText(ResourceTable.Id_t_form_location_widget_disabled_explanation, "Permanent location permission is not granted");
                formContext.updateWidget(formId, componentProvider);
                break;

            case DISABLED:
                locator.requestEnableLocation();
                componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_disabled, Component.VISIBLE);
                componentProvider.setText(ResourceTable.Id_t_form_location_widget_disabled_explanation, "Location is disabled");
                formContext.updateWidget(formId, componentProvider);
                break;
        }
    }

    private void setAddress(final long formId,
                            final ComponentProvider componentProvider,
                            final GeoAddress address) {
        componentProvider.setVisibility(ResourceTable.Id_dl_form_location_widget_container, Component.VISIBLE);
        componentProvider.setText(ResourceTable.Id_t_form_location_widget_address,
                //Wagramer Straße 17-19, 22. Bezirk Donaustadt, 1220 Wien, Österreich
                String.format("%s %s, %s, %s %s, %s",
                        address.getRoadName(),
                        address.getSubRoadName(),
                        address.getLocality(),
                        address.getPostalCode(),
                        address.getAdministrativeArea(),
                        address.getCountryName())
        );

        formContext.getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(() -> {
            try {
                final InputStream in = new URL(String.format("https://www.countryflags.io/%s/shiny/64.png", address.getCountryCode())).openStream();
                final PixelMap pixelmap = ImageSource.create(in, new ImageSource.SourceOptions()).createPixelmap(new ImageSource.DecodingOptions());
                componentProvider.setImagePixelMap(ResourceTable.Id_i_form_location_widget_country_flag, pixelmap);
                formContext.updateWidget(formId, componentProvider);
            } catch (IOException e) {
                HiLog.error(TAG, "Failed to fetch country flag: " + e.getMessage());
            }

        });
        formContext.updateWidget(formId, componentProvider);
    }

    @Override
    public void onTriggerFormEvent(long formId, String message) {
        HiLog.debug(TAG, "handle card click event.");
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        HiLog.debug(TAG, "get the default page to route when you click card.");
        return null;
    }
}