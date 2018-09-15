/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.config;

import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.ReloadingEvent;

public class ConfigurationListener implements EventListener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof ConfigurationErrorEvent) {
            ConfigurationErrorEvent errorEvent = (ConfigurationErrorEvent) event;
            onConfigurationErrorEvent(errorEvent);
            return;
        }
        if (event instanceof ConfigurationEvent) {
            ConfigurationEvent configurationEvent = (ConfigurationEvent) event;
            onConfigrationEvent(configurationEvent);
            return;
        }
        if (event instanceof ReloadingEvent) {
            ReloadingEvent reloadingEvent = (ReloadingEvent) event;
            onReloadingEvent(reloadingEvent);
        }
    }

    private void onConfigrationEvent(ConfigurationEvent event) {


    }

    private void onConfigurationErrorEvent(ConfigurationErrorEvent errorEvent) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println(errorEvent.getCause());
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

    private void onReloadingEvent(ReloadingEvent reloadingEvent) {
    }

}
