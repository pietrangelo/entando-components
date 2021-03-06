/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.entando.aps.system.services.digitalexchange.job;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.init.model.Component;
import org.jdom.Element;

/**
 * Parses the component.xml file with the specific additions necessary for the
 * Digital Exchange.
 */
public class ComponentDescriptor extends Component {

    private final List<String> installationCommands;
    private final List<String> uninstallationCommands;

    public ComponentDescriptor(Element rootElement, ComponentStorageManager storageManager) throws Throwable {
        super(rootElement, new HashMap<>());

        installationCommands = parseCommands(rootElement, "installation");
        uninstallationCommands = parseCommands(rootElement, "uninstallation");

        super.getEnvironments().replaceAll((key, value)
                -> new DigitalExchangeComponentEnvironment(storageManager, value));
    }

    private List<String> parseCommands(Element rootElement, String childElementName) {

        Element childElement = rootElement.getChild(childElementName);
        if (childElement != null) {

            Element executionElement = childElement.getChild("execution");

            if (executionElement != null) {

                @SuppressWarnings("unchecked")
                List<Element> operationElements = executionElement.getChildren();

                return operationElements.stream()
                        .map(e -> e.getText().trim())
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    public List<String> getInstallationCommands() {
        return installationCommands;
    }

    public List<String> getUninstallationCommands() {
        return uninstallationCommands;
    }
}
