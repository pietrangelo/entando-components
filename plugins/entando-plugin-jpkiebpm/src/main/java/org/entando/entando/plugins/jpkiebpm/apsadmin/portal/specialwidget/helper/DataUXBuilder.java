/*
The MIT License

Copyright 2018 Entando Inc..

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package org.entando.entando.plugins.jpkiebpm.apsadmin.portal.specialwidget.helper;

import freemarker.template.*;
import org.apache.struts2.util.ServletContextAware;
import org.entando.entando.plugins.jpkiebpm.aps.system.services.kie.*;
import org.entando.entando.plugins.jpkiebpm.aps.system.services.kie.api.util.KieApiUtil;
import org.entando.entando.plugins.jpkiebpm.aps.system.services.kie.model.*;
import org.entando.entando.plugins.jpkiebpm.aps.system.services.kie.model.override.*;
import org.entando.entando.plugins.jpkiebpm.apsadmin.portal.specialwidget.helper.dataModels.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;
import java.util.function.Function;

@Service
public class DataUXBuilder<T extends InputField> implements ServletContextAware {
    private static String DEFAULT_FORM_ACTION = "/ExtStr2/do/bpm/FrontEnd/DataTypeForm/save";

    private static final Logger logger = LoggerFactory.getLogger(DataUXBuilder.class);
    
    private IKieFormOverrideManager formOverrideManager;
    private Map<String, String> typeMapping = new HashMap<>();
    private Map<String, String> valueMapping = new HashMap<>();
    private ServletContext servletContext;

    // Template folder path src/main/resources/templates
    public static final String TEMPLATE_FOLDER = "/templates/";
    public static final String MAIN_FTL_TEMPLATE = "PageModel.ftl";

    
    
    
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);

    public void init() throws Exception {
        logger.debug("{} ready.", this.getClass().getName());

        this.typeMapping.put("InputText", "text");
        this.typeMapping.put("InputTextInteger", "number");
        this.typeMapping.put("TextBox", "text");
        this.typeMapping.put("TextArea", "text");
        this.typeMapping.put("IntegerBox", "number");
        this.typeMapping.put("DecimalBox", "number");
        this.typeMapping.put("DatePicker", "text");
        this.typeMapping.put("CheckBox", "checkbox");
        this.typeMapping.put("ListBox", "text");
        this.typeMapping.put("Slider", "number");
        this.typeMapping.put("RadioGroup", "radio");
        this.typeMapping.put("MultipleInput", "number");
        this.typeMapping.put("MultipleSelector", "text");
        this.typeMapping.put("Document", "text");

        this.valueMapping.put("InputText", "$data.%s.text");
        this.valueMapping.put("InputTextInteger", "$data.%s.number");
        this.valueMapping.put("CheckBox", "$data.%s.boolean");
        this.valueMapping.put("TextBox", "$data.%s.text");
        this.valueMapping.put("TextArea", "$data.%s.text");
        this.valueMapping.put("IntegerBox", "$data.%s.number");
        this.valueMapping.put("DecimalBox", "$data.%s.number");
        this.valueMapping.put("DatePicker", "$data.%s.text");
        this.valueMapping.put("Slider", "$data.%s.number");
        this.valueMapping.put("RadioGroup", "$data.%s.text");
        this.valueMapping.put("ListBox", "$data.%s.text");
        this.valueMapping.put("MultipleSelector", "$data.%s.text");
        this.valueMapping.put("MultipleInput", "$data.%s.text");
        this.valueMapping.put("Document", "$data.%s.text");

        cfg.setDefaultEncoding("UTF-8");

        logger.debug("TEMPLATE_FOLDER: {}", TEMPLATE_FOLDER);
        cfg.setClassForTemplateLoading(this.getClass(), TEMPLATE_FOLDER);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);

    }

    public DataUXBuilder() {

    }
    public String createDataUx(KieProcessFormQueryResult kpfr, int widgetInfoId, String containerId, String processId, String title) throws Exception {
        return this.createDataUx( kpfr, widgetInfoId, containerId, processId, title, DEFAULT_FORM_ACTION,"");
    }
    
    public String createDataUx(KieProcessFormQueryResult kpfr, int widgetInfoId, String containerId, String processId, String title, String formAction, String urlParameters) throws Exception {
        logger.info("CreateDataUx in DataUXBuilde for containerId {} with processId {} and title {} -> kpfr: {}", containerId, processId, title, kpfr);
        Template template = cfg.getTemplate(MAIN_FTL_TEMPLATE);
        Map<String, KieFormOverride> formOverridesMap = getFormOverridesMap(widgetInfoId);        
        Map<String, Section> sections = this.getSections(kpfr, formOverridesMap);
        Map<String, Object> root = new HashMap<>();
        Model model = new Model();
        model.setTitle(title);
        model.setContainerId(containerId);
        model.setProcessId(processId);
        model.setFormAction(formAction);
        model.setUrlParameters(urlParameters);
        root.put("model", model);
        logger.info("sections {}", sections);
        root.put("sections", sections);
        Writer stringWriter = new StringWriter();
        template.process(root, stringWriter);
        return stringWriter.toString();

    }

    private Map<String, KieFormOverride> getFormOverridesMap(int widgetInfoId) throws Exception {
        Map<String, KieFormOverride> formOverridesMap = new HashMap<>();
        List<KieFormOverride> overrides = formOverrideManager.getFormOverrides(widgetInfoId, false);
        for (KieFormOverride override : overrides) {
            if (override.isActive()) {
                formOverridesMap.put(override.getField(), override);
            }
        }
        return formOverridesMap;
    }
    
    private Map<String, Section> getSections(KieProcessFormQueryResult kpfr, Map<String, KieFormOverride> formOverridesMap) throws Exception {

        Map<String, Section> sections = new HashMap<String, Section>();
        List<T> fields = new ArrayList<T>();

        kpfr.getFields().forEach(field
                -> {
            logger.debug("getSections field.getName() {}", field.getName());

            Section tempSection = new Section();
            String sectionName = null;
            if (field.getName().contains("_")) {
                sectionName = KieApiUtil.getFormNameFromField(field);
                logger.debug("field.getName().contains(\"_\") sectionName = {}", sectionName);

            } else {
                if (KieApiUtil.getFieldProperty(kpfr.getProperties(), "name").contains(".")) {
                    sectionName = KieApiUtil.getFieldProperty(kpfr.getProperties(), "name")
                            .substring(0, KieApiUtil.getFieldProperty(kpfr.getProperties(), "name").indexOf("."));
                    logger.debug("field.getName().contains(\".\") sectionName = {}", sectionName);
                } else {
                    sectionName = KieApiUtil.getFieldProperty(kpfr.getProperties(), "name");
                    logger.debug("field.getName() not contains(\".\") sectionName = {}", sectionName);
                }
            }

            if (null == sectionName) {
                logger.warn("sectionName is null -> set section name to empty String");
                sectionName = "";
            }

            try {
                fields.add(addField(field, formOverridesMap.get(field.getName())));
            } catch (Exception ex) {
                logger.error("error adding field {}", ex);
            }

            tempSection.setName(sectionName);
            tempSection.setFields(fields);

            if (!sections.containsKey(sectionName)) {
                sections.put(sectionName, tempSection);
                logger.debug("--> add section with name {}", sectionName);
            }

        }
        );
        List<KieProcessFormQueryResult> subForms = kpfr.getNestedForms();
        if (null != subForms && !subForms.isEmpty()) {
            kpfr.getNestedForms().forEach(form -> {
                try {
                    sections.putAll(getSections(form, formOverridesMap));
                } catch (Exception ex) {
                    logger.error("error adding getNestedForms {}", ex);
                }
            });
        }
        sections.keySet().forEach(k -> logger.debug("return sections : {}", k));
        return sections;
    }

    private T addField(KieProcessFormField field, KieFormOverride formOverride) throws Exception {
        logger.info("------------------------------------");
        logger.info("Field getId          -> {}", field.getId());
        logger.info("Field getName        -> {}", field.getName());
        logger.info("Field getPosition    -> {}", field.getPosition());
        logger.info("Field getType        -> {}", field.getType());
        logger.info("Field getProperties  -> ");
        field.getProperties().forEach(p
                -> logger.info("  Property name: {} value: {}", p.getName(), p.getValue()));
        
        T inputField;
        switch (field.getType()) {
            case "TextBox":
                logger.debug("{} recognized as TextBox, inputField set to TextField",field.getName());
                inputField = (T) new TextField();
                break;
            case "TextArea":
                logger.debug("{} recognized as TextArea, inputField set to TextArea", field.getName());
                inputField = (T) new TextField();
                break;
            case "IntegerBox":
                logger.debug("{} recognized as IntegerBox, inputField set to IntegerBox", field.getName());
                inputField = (T) new TextField();
                break;
            case "InputText":
                logger.debug("{} recognized as InputText, inputField set to InputText", field.getName());
                inputField = (T) new TextField();
                break;
            case "InputTextInteger":
                logger.debug("{} recognized as InputTextInteger, inputField set to InputTextInteger", field.getName());
                inputField = (T) new TextField();
                break;
            case "ListBox":
                logger.debug("{} recognized as ListBox, inputField set to ListBox", field.getName());
                inputField = (T) new ListBoxField();
                break;
            case "RadioGroup":
                logger.debug("{} recognized as RadioGroup, inputField set to RadioGroup", field.getName());
                inputField = (T) new RadioGroupField();
                break;
            case "DatePicker":
                logger.debug("{} recognized as DatePicker, inputField set to DatePicker", field.getName());
                inputField = (T) new DatePickerField();
                break;
            case "MultipleSelector":
                logger.debug("{} recognized as MultipleSelector, inputField set to MultipleSelector", field.getName());
                inputField = (T) new MultipleSelectorField();
                break;
            case "CheckBox":
                logger.debug("{} recognized as CheckBox, inputField set to CheckBox", field.getName());
                inputField = (T) new InputField();
                break;
            default:
                logger.debug("{} inputField not recognized, inputField set to default InputField", field.getName());
                inputField = (T) new InputField();
        }
        logger.info("adding default properties to inputField");

        String fieldName = field.getName();
        String fieldTypeHMTL = this.typeMapping.get(field.getType());
        String fieldTypePAM = field.getType();
        String fieldValueExpr = this.valueMapping.get(field.getType());
        String fieldValue = (null != fieldValueExpr) ? String.format(fieldValueExpr, field.getName()) : "";
        fieldValue=fieldValue.replaceAll(" ", "_");
                
                
        logger.info("Field getValue        -> {}", fieldValue);
        logger.info("------------------------------------");

        boolean required = Boolean.parseBoolean(field.getProperty("fieldRequired").getValue());

        boolean readOnly = false;
                
                if (null!=field.getProperty("readOnly")){
                    readOnly =  Boolean.parseBoolean(field.getProperty("readOnly").getValue());
                }
                
                
        String placeHolder = field.getProperty("placeHolder").getValue();
        inputField.setId(field.getId());
        inputField.setName(fieldName);
        inputField.setValue(fieldValue);
        inputField.setRequired(required);
        inputField.setTypePAM(fieldTypePAM);
        inputField.setTypeHTML(fieldTypeHMTL);
        inputField.setReadOnly(readOnly);
        
        if (inputField instanceof DatePickerField) {
            logger.debug("inputField instanceof DatePickerField, adding custom properties");

            DatePickerField datePicker = (DatePickerField) inputField;
            boolean showTime = Boolean.parseBoolean(field.getProperty("showTime").getValue());
            datePicker.setShowTime(showTime);
        }
        if (inputField instanceof ListBoxField) {
            logger.debug("inputField instanceof ListBoxField, adding custom properties");

            ListBoxField select = (ListBoxField) inputField;
            String optionsString = field.getProperty("options").getValue();
            List<String> optionsValues = Arrays.asList(optionsString.split(","));
            List<Option> selectOption = new ArrayList<Option>();
            for (String f : optionsValues) {
                String[] split = f.split("=");
                Option opt = new Option();
                if (split.length > 0) {
                    opt.setName(split[0]);
                    if (split.length > 1) {
                        opt.setValue(split[1]);
                    } else {
                        opt.setValue("");
                    }
                    selectOption.add(opt);
                }
            }
            select.setOptions(selectOption);

            boolean addEmptyOption = Boolean.parseBoolean(field.getProperty("addEmptyOption").getValue());
            String defaultValue = field.getProperty("defaultValue").getValue();

            select.setDefaultValue(defaultValue);
            select.setAddEmptyOption(addEmptyOption);
        }
        if (inputField instanceof RadioGroupField) {
            logger.debug("inputField instanceof RadioGroupField, adding custom properties");

            RadioGroupField select = (RadioGroupField) inputField;
            String optionsString = field.getProperty("options").getValue();
            List<String> optionsValues = Arrays.asList(optionsString.split(","));
            List<Option> radioOptions = new ArrayList<Option>();
            for (String f : optionsValues) {
                String[] split = f.split("=");
                Option opt = new Option();
                opt.setName(split[0]);
                opt.setValue(split[1]);
                radioOptions.add(opt);
            }
            select.setOptions(radioOptions);

            boolean inline = Boolean.parseBoolean(field.getProperty("inline").getValue());
            String defaultValue = field.getProperty("defaultValue").getValue();

            select.setDefaultValue(defaultValue);
            select.setInline(inline);
        }
        if (inputField instanceof MultipleSelectorField) {
            logger.debug("inputField instanceof MultipleSelectorField, adding custom properties");
            MultipleSelectorField multipleSelector = (MultipleSelectorField) inputField;
            String optionsString = field.getProperty("listOfValues").getValue();

            boolean allowClearSelection = Boolean.parseBoolean(field.getProperty("allowClearSelection").getValue());
            boolean allowFilter = Boolean.parseBoolean(field.getProperty("allowFilter").getValue());
            int maxElementsOnTitle = Integer.parseInt(field.getProperty("maxElementsOnTitle").getValue());
            int maxDropdownElements = Integer.parseInt(field.getProperty("maxDropdownElements").getValue());
            List<String> optionsValues = Arrays.asList(optionsString.split(","));
            List<Option> selectOption = new ArrayList<Option>();
            for (String f : optionsValues) {
                Option opt = new Option();
                opt.setName(f);
                opt.setValue(f);
                selectOption.add(opt);
            }

            multipleSelector.setOptions(selectOption);
            multipleSelector.setMaxDropdownElements(maxDropdownElements);
            multipleSelector.setMaxElementsOnTitle(maxElementsOnTitle);
            multipleSelector.setAllowClearSelection(allowClearSelection);
            multipleSelector.setAllowFilter(allowFilter);
        }

        if (inputField instanceof TextField) {
            logger.debug("inputField instanceof TextField, adding custom properties");
            TextField textField = (TextField) inputField;
            textField.setPlaceHolder(placeHolder);
        }

        if(formOverride != null) {
            String defaultValueOverride = getOverrideProperty(formOverride, DefaultValueOverride.class, d -> d.getDefaultValue());
            if(defaultValueOverride != null) {
                inputField.setValue(defaultValueOverride);
            }
            String placeHolderOverride = getOverrideProperty(formOverride, PlaceHolderOverride.class, p -> p.getPlaceHolder());
            if (placeHolderOverride != null) {
                inputField.setLabel(placeHolderOverride);
            }
        }
        
        return inputField;
    }
    
    private <T extends IBpmOverride> String getOverrideProperty(KieFormOverride formOverride, Class<T> type, Function<T, String> getOverrideProperty) {
        if (formOverride != null && formOverride.getOverrides() != null) {
            List<IBpmOverride> overrides = formOverride.getOverrides().getList();
            if (overrides != null) {
                for (IBpmOverride override : overrides) {
                    if (type.isInstance(override)) {
                        return getOverrideProperty.apply((T) override);
                    }
                }
            }
        }
        return null;
    }
    
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    public IKieFormOverrideManager getFormOverrideManager() {
        return formOverrideManager;
    }

    public void setFormOverrideManager(IKieFormOverrideManager formOverrideManager) {
        this.formOverrideManager = formOverrideManager;
    }
}
