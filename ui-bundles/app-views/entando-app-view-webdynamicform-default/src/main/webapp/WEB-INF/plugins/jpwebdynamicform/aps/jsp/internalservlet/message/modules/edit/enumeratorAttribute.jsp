<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="/apsadmin-form" %>
<%@ taglib prefix="wp" uri="/aps-core" %>
<s:if test="#lang.default">
	<wp:i18n key="jpwebdynamicform_SELECT" var="labelSelectVar" />
	<wpsf:select 
		useTabindexAutoIncrement="true" 
		name="%{#attributeTracer.getFormFieldName(#attribute)}" 
		id="%{#attribute_id}"
		headerValue="%{#labelSelectVar}" 
		headerKey="" 
		list="#attribute.items" 
		value="%{#attribute.getText()}" />
</s:if>