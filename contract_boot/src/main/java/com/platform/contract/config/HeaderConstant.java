package com.platform.contract.config;

public enum HeaderConstant {

    ACCEPT("*/*"),
    DATE(""),
    HEADERS(""),
    CONTENTTYPE_FORMDATA("application/x-www-form-urlencoded"),
    CONTENTTYPE_JSON("application/json; charset=UTF-8"),
    CONTENTTYPE_PDF("application/pdf"),
    CONTENTTYPE_STREAM("application/octet-stream"),
    AUTHMODE("Signature");

    private final String value;

    HeaderConstant(String value) {
        this.value = value;
    }

    public String VALUE() {
        return this.value;
    }

}
