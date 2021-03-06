/*
 * Copyright 2018-Present Entando Inc. (http://www.entando.com) All rights reserved.
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
package org.entando.entando.aps.system.services.digitalexchange.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
public class DigitalExchange {

    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "digitalExchange.id.invalid")
    @Size(min = 3, max = 20, message = "string.size.invalid")
    @JsonProperty("id")
    @XmlElement
    private String id;

    @NotNull(message = "digitalExchange.name.required")
    @Size(min = 3, max = 20, message = "string.size.invalid")
    @JsonProperty("name")
    @ApiModelProperty(required = true)
    @XmlElement
    private String name;

    @NotNull(message = "digitalExchange.url.required")
    @JsonProperty("url")
    @ApiModelProperty(required = true)
    @XmlElement
    private String url;

    @JsonProperty("timeout")
    @XmlElement
    private int timeout;

    @JsonProperty("active")
    @XmlElement
    private boolean active;

    @JsonProperty("key")
    @XmlElement(name = "key")
    private String clientKey;

    @JsonProperty(value = "secret", access = JsonProperty.Access.WRITE_ONLY)
    @XmlElement(name = "secret")
    private String clientSecret;

    @JsonProperty("publicKey")
    @XmlElement(name = "publicKey")
    private String publicKey;

    public DigitalExchange() {
    }

    public DigitalExchange(DigitalExchange other) {
        this.id = other.id;
        this.name = other.name;
        this.url = other.url;
        this.timeout = other.timeout;
        this.active = other.active;
        this.clientKey = other.clientKey;
        this.clientSecret = other.clientSecret;
        this.publicKey = other.publicKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean hasNoPublicKey() {
        return StringUtils.isEmpty(this.getPublicKey());
    }

    public void invalidate() {
        this.setPublicKey(null);
        this.setActive(false);
    }
}
