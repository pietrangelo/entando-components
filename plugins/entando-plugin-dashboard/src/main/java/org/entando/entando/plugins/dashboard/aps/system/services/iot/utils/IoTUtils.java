package org.entando.entando.plugins.dashboard.aps.system.services.iot.utils;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.entando.entando.plugins.dashboard.aps.system.services.dashboardconfig.model.DashboardConfigDto;
import org.entando.entando.plugins.dashboard.aps.system.services.iot.model.DashboardDatasourceDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class IoTUtils {


	public static FieldSearchFilter[] addFilter(FieldSearchFilter[] filters, FieldSearchFilter filterToAdd) {
		int len = filters.length;
		FieldSearchFilter[] newFilters = new FieldSearchFilter[len + 1];
		for(int i=0; i < len; i++){
			newFilters[i] = filters[i];
		}
		newFilters[len] = filterToAdd;
		return newFilters;
	}

	public static String getMeasurementKey(String applicationToken, String loggerId, int version){
		return StringUtils.join(applicationToken,"_",loggerId,"_version",version);
	}

	public static HttpHeaders getHeaders(DashboardConfigDto server) {
		HttpHeaders headers = getAuthenticationHeader(server);
		headers.add("Content-Type", "application/json");
		return headers;
	}

	public static HttpHeaders getAuthenticationHeader(DashboardConfigDto dashboardConfigDto) {
		String encoding = Base64.getEncoder().encodeToString(
				StringUtils.join(dashboardConfigDto.getUsername(), ":", dashboardConfigDto.getPassword()).getBytes());
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, StringUtils.join("Basic ", encoding));
		return headers;
	}

	public static <T> T getObjectFromJson(String jsonString, Type type, Class<T> tClass) {
		T object = new Gson().fromJson(jsonString, type);
		return tClass.cast(object);
	}

	public static <T> T getObjectFromJson(JsonElement json, Type type, Class<T> tClass) {
		String jsonString = new Gson().toJson(json);
		return getObjectFromJson(jsonString, type,tClass);
	}

	public static JsonElement getNestedFieldFromJson(JsonObject jsonObject, String fieldNames){

		String  fieldName = fieldNames.split(IoTConstants.JSON_FIELD_SEPARATOR)[0];

		List<String> fildNameList = Arrays.asList(fieldNames.split(IoTConstants.JSON_FIELD_SEPARATOR));

		if(fildNameList.size() == 1) {
			return jsonObject.get(fieldName);
		}

		if(jsonObject.get(fieldName) == null) {
			return null;
		}

		else if (jsonObject.get(fieldName).isJsonObject()) {
			fieldNames = StringUtils.join(fildNameList.subList(1,fildNameList.size()), IoTConstants.JSON_FIELD_SEPARATOR);
			return getNestedFieldFromJson(jsonObject.getAsJsonObject(fieldName),fieldNames);
		}

		else if(jsonObject.get(fieldName).isJsonPrimitive() && fildNameList.size() > 0) {
			jsonObject = new Gson().fromJson(jsonObject.get(fieldName).getAsString(), JsonObject.class);
			fieldNames = StringUtils.join(fildNameList.subList(1,fildNameList.size()), IoTConstants.JSON_FIELD_SEPARATOR);
			return getNestedFieldFromJson(jsonObject,fieldNames);
		}

		else if (jsonObject.get(fieldName).isJsonArray() && fildNameList.size() == 2) {
			JsonArray jsonArray = new JsonArray();
			for (JsonElement json : jsonObject.get(fildNameList.get(0)).getAsJsonArray()) {

				if (json.isJsonObject() && json.getAsJsonObject().get(fildNameList.get(1)) != null) {
					jsonArray.add(json.getAsJsonObject().get(fildNameList.get(1)));
				}
			}
			return jsonArray;
		}

		return null;
	}

	public static JsonElement getNestedFieldFromJson(String jsonObject, String fieldNames) {
		JsonObject json = getObjectFromJson(jsonObject,new TypeToken<JsonObject>(){}.getType(),JsonObject.class);
		return getNestedFieldFromJson(json,fieldNames);
	}

	public static <T extends DashboardDatasourceDto> T getDashboardDataSourceConnecDto(
			DashboardDatasourceDto dashboardDatasourceDto, Class<T> clazz) {
		return clazz.cast(dashboardDatasourceDto);
	}

	public static <T> String formatQueryUrl(Map<String, T> params) {
		String query = "";
		for (Entry<String, T> entry : params.entrySet()) {
			if (entry.getValue() != null) {
				query = StringUtils.join(query, entry.getKey(), "=", entry.getValue(), "&");
			}
		}
		if(query.length() > 1) {
			return query.substring(0, query.length() - 1);
		}
		return query;
	}

	public static Map<String, Object> getMapFromJson (JsonObject payload) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(payload), HashMap.class);
	}
	
	public static ResponseEntity<String> getRequestMethod(String url, HttpHeaders headers) {
		HttpEntity httpEntity = new HttpEntity(headers);

		RestTemplate restTemplate = new RestTemplate();

		try {
		return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class); 
		}
		catch (ResourceAccessException e){
		  return new ResponseEntity(e, HttpStatus.REQUEST_TIMEOUT);
    }
	}

	public static boolean isValidResponse(ResponseEntity<String> response) {
		if(response.getStatusCode().is2xxSuccessful()) {
			return true;
		}
		return false;
	}
}
