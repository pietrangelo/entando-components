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
package org.entando.entando.web.digitalexchange.install;

import com.agiletec.aps.system.services.i18n.I18nManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import org.entando.entando.aps.system.init.IInitializerManager;
import org.entando.entando.aps.system.init.model.SystemInstallationReport;
import org.entando.entando.aps.system.services.digitalexchange.client.DigitalExchangeOAuth2RestTemplateFactory;
import org.entando.entando.aps.system.services.digitalexchange.client.DigitalExchangesMocker;
import org.entando.entando.aps.system.services.digitalexchange.install.ComponentZipUtil;
import org.entando.entando.aps.system.services.group.IGroupService;
import org.entando.entando.aps.system.services.pagemodel.IPageModelService;
import org.entando.entando.aps.system.services.role.IRoleService;
import org.entando.entando.aps.system.services.role.model.RoleDto;
import org.entando.entando.web.AbstractControllerIntegrationTest;
import org.entando.entando.web.common.model.PagedMetadata;
import org.entando.entando.web.common.model.PagedRestResponse;
import org.entando.entando.web.digitalexchange.component.DigitalExchangeComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.entando.entando.aps.system.services.widgettype.IWidgetService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.ResultActions;
import org.entando.entando.aps.system.services.digitalexchange.install.ComponentInstallationJob;
import org.entando.entando.aps.system.services.digitalexchange.install.InstallationStatus;
import org.entando.entando.aps.system.services.storage.IStorageManager;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.junit.After;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.entando.entando.aps.system.services.digitalexchange.DigitalExchangeTestUtils.*;

@ActiveProfiles("DEinstallTest")
public class DigitalExchangeInstallResourceIntegrationTest extends AbstractControllerIntegrationTest {

    private static final String BASE_URL = "/digitalExchange";

    private static File tempWidgetZipFile;
    private static File tempPageModelZipFile;
    private static File tempGroupZipFile;
    private static File tempRoleZipFile;

    @BeforeClass
    public static void setupZips() {
        tempWidgetZipFile = ComponentZipUtil.getTestWidgetZip();
        tempPageModelZipFile = ComponentZipUtil.getTestPageModelZip();
        tempGroupZipFile = ComponentZipUtil.getTestGroupZip();
        tempRoleZipFile = ComponentZipUtil.getTestRoleZip();
    }

    @AfterClass
    public static void deleteZips() {
        if (tempWidgetZipFile != null) {
            tempWidgetZipFile.delete();
        }
        if (tempPageModelZipFile != null) {
            tempPageModelZipFile.delete();
        }
        if (tempGroupZipFile != null) {
            tempGroupZipFile.delete();
        }
        if (tempRoleZipFile != null) {
            tempRoleZipFile.delete();
        }
    }

    @Autowired
    private IWidgetService widgetService;

    @Autowired
    private IStorageManager storageManager;

    @Autowired
    private IInitializerManager initializerManager;

    @Autowired
    private IPageModelService pageModelService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private I18nManager i18nManager;

    @After
    public void cleanup() throws Exception {
        storageManager.deleteDirectory("components", true);
        storageManager.deleteDirectory("components", false);
    }

    @Configuration
    @Profile("DEinstallTest")
    public static class TestConfig {

        @Bean
        @Primary
        public DigitalExchangeOAuth2RestTemplateFactory getRestTemplateFactory() {
            return new DigitalExchangesMocker()
                    .addDigitalExchange(DE_1_ID, (request) -> {
                        try {
                            if (request.getEndpoint().contains("packages")) {
                                Resource resource = mock(Resource.class);
                                if (request.getEndpoint().contains("de_test_widget")) {
                                    when(resource.getInputStream()).thenReturn(new FileInputStream(tempWidgetZipFile));
                                } else if (request.getEndpoint().contains("de_test_page_model")) {
                                    when(resource.getInputStream()).thenReturn(new FileInputStream(tempPageModelZipFile));
                                } else if (request.getEndpoint().contains("de_test_group")) {
                                    when(resource.getInputStream()).thenReturn(new FileInputStream(tempGroupZipFile));
                                } else {
                                    when(resource.getInputStream()).thenReturn(new FileInputStream(tempRoleZipFile));
                                }
                                return resource;
                            } else {
                                return getComponentInfoResponse();
                            }
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    })
                    .initMocks();
        }
    }

    private static PagedRestResponse<DigitalExchangeComponent> getComponentInfoResponse() {
        PagedMetadata<DigitalExchangeComponent> pagedMetadata = new PagedMetadata<>();
        DigitalExchangeComponent component = new DigitalExchangeComponent();
        pagedMetadata.setBody(Collections.singletonList(component));
        return new PagedRestResponse<>(pagedMetadata);
    }

    @Test
    public void shouldInstallWidget() throws Exception {

        String componentCode = "de_test_widget";

        installAndCheckForCompletion(componentCode);

        assertThat(widgetService.getWidget(componentCode)).isNotNull();

        widgetService.removeWidget(componentCode);
    }

    @Test
    public void shouldInstallPageModel() throws Exception {

        String componentCode = "de_test_page_model";

        installAndCheckForCompletion(componentCode);

        assertThat(pageModelService.getPageModel(componentCode)).isNotNull();
        assertThat(i18nManager.getLabel("DE_TEST_LABEL", "en")).isEqualTo("Test label DE");
        assertThat(storageManager.exists("components/de_test_page_model/test.css", false)).isTrue();

        pageModelService.removePageModel(componentCode);
    }

    @Test
    public void shouldInstallGroup() throws Exception {

        String componentCode = "de_test_group";

        installAndCheckForCompletion(componentCode);

        assertThat(groupService.getGroup(componentCode)).isNotNull();

        groupService.removeGroup(componentCode);
    }

    @Test
    public void shouldInstallRole() throws Exception {

        String componentCode = "de_test_role";

        installAndCheckForCompletion(componentCode);

        RoleDto role = roleService.getRole(componentCode);
        assertThat(role).isNotNull();
        assertThat(role.getPermissions().get("superuser")).isTrue();

        roleService.removeRole(componentCode);
    }

    private void installAndCheckForCompletion(String componentId) throws Exception {

        ResultActions result = createAuthRequest(post(BASE_URL + "/{exchange}/install/{component}", DE_1_ID, componentId)).execute();

        result.andExpect(jsonPath("$.errors").isEmpty())
                .andExpect(jsonPath("$.metaData").isEmpty())
                .andExpect(jsonPath("$.payload").isNotEmpty());

        parseJob(result);

        InstallationStatus status = InstallationStatus.CREATED;
        int attempts = 0;
        while (status != InstallationStatus.COMPLETED && attempts < 10) {
            status = checkJobStatus(componentId);
            assertThat(status).isNotEqualTo(InstallationStatus.ERROR);
            attempts++;
        }

        assertThat(status).isEqualTo(InstallationStatus.COMPLETED);

        SystemInstallationReport report = initializerManager.getCurrentReport();

        assertThat(report.getComponentReport(componentId, false))
                .isNotNull()
                .matches(cr -> cr.getStatus() == SystemInstallationReport.Status.OK);
    }

    private InstallationStatus checkJobStatus(String componentId) throws Exception {

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        ResultActions result = createAuthRequest(get(BASE_URL + "/install/{componentId}", componentId)).execute();

        result.andExpect(jsonPath("$.errors").isEmpty())
                .andExpect(jsonPath("$.metaData").isEmpty());

        ComponentInstallationJob job = parseJob(result);

        assertThat(job.getProgress()).isBetween(0d, 1d);

        return job.getStatus();
    }

    private ComponentInstallationJob parseJob(ResultActions result) throws IOException {
        String jsonResponse = result.andReturn().getResponse().getContentAsString();

        SimpleRestResponse<ComponentInstallationJob> response = new ObjectMapper()
                .readValue(jsonResponse, new TypeReference<SimpleRestResponse<ComponentInstallationJob>>() {
                });

        ComponentInstallationJob job = response.getPayload();

        assertThat(job.getDigitalExchange()).isNotNull();
        assertThat(job.getStarted()).isNotNull();
        assertThat(job.getUser()).isEqualTo("jack_bauer");

        return job;
    }
}