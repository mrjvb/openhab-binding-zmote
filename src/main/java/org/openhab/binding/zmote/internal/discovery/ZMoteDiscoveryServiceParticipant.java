/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zmote.internal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.thing.ThingUID;
import org.openhab.binding.zmote.ZMoteBindingConstants;
import org.openhab.binding.zmote.internal.model.ZMoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexander Maret-Huskinson - Initial contribution
 */
public class ZMoteDiscoveryServiceParticipant extends AbstractDiscoveryService implements IDiscoveryListener {

    private static final boolean USE_BACKGROUND_DISCOVERY = false;

    private final Logger logger = LoggerFactory.getLogger(ZMoteDiscoveryServiceParticipant.class);
    private IZMoteDiscoveryService zmoteDiscovery = null;

    public ZMoteDiscoveryServiceParticipant() {
        super(ZMoteBindingConstants.SUPPORTED_THING_TYPES_UIDS, ZMoteBindingConstants.DISCOVERY_TIMEOUT,
                USE_BACKGROUND_DISCOVERY);
    }

    @Override
    public void deviceDiscovered(final ZMoteDevice device) {
        if (logger.isInfoEnabled()) {
            logger.info("Discovered: {}", device.toString());
        }

        thingDiscovered(createDiscoveryResult(device));
    }

    @Override
    protected void startBackgroundDiscovery() {
        if (zmoteDiscovery != null) {
            zmoteDiscovery.addListener(this);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot start background discovery as the servie is not available!");
            }
        }
    }

    @Override
    protected void startScan() {
        if (zmoteDiscovery != null) {
            zmoteDiscovery.addListener(this);
            zmoteDiscovery.startScan();

        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot start discovery as the servie is not available!");
            }
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        if (zmoteDiscovery != null) {
            zmoteDiscovery.removeListener(this);
        }
    }

    @Override
    protected void stopScan() {
        if ((zmoteDiscovery != null) && !isBackgroundDiscoveryEnabled()) {
            zmoteDiscovery.removeListener(this);
        }

        super.stopScan();
    }

    protected void setZMoteDiscoveryService(final IZMoteDiscoveryService zmoteDiscoveryService) {
        zmoteDiscovery = zmoteDiscoveryService;
    }

    protected void unsetZMoteDiscoveryService(final IZMoteDiscoveryService zmoteDiscoveryService) {
        try {
            if (zmoteDiscovery != null) {
                zmoteDiscovery.removeListener(this);
            }
        } finally {
            zmoteDiscovery = null;
        }
    }

    private DiscoveryResult createDiscoveryResult(final ZMoteDevice device) {
        final String uuid = device.getUuid();
        final String label = String.format("ZMote IR Home Controller (%s)", uuid);

        final Map<String, Object> properties = new HashMap<>(6);
        properties.put(ZMoteBindingConstants.PROP_UUID, uuid);
        properties.put(ZMoteBindingConstants.PROP_URL, device.getUrl());
        properties.put(ZMoteBindingConstants.PROP_MAKE, device.getMake());
        properties.put(ZMoteBindingConstants.PROP_MODEL, device.getModel());
        properties.put(ZMoteBindingConstants.PROP_REVISION, device.getRevision());
        properties.put(ZMoteBindingConstants.PROP_TYPE, device.getType());

        final ThingUID thingUID = new ThingUID(ZMoteBindingConstants.THING_TYPE_ZMT2, uuid);
        return DiscoveryResultBuilder.create(thingUID).withProperties(properties).withLabel(label).build();
    }
}
