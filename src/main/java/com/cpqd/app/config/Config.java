package com.cpqd.app.config;

import java.lang.System;

public class Config {

    private static Config mInstance;

    private String mDeviceManagerAddress;
    private String mImageManagerAddress;
    private String mDataBrokerAddress;
    private String mKafkaAddress;
    private String mInternalDefaultTenant;
    private String mDeviceManagerDefaultSubject;
    private String mDeviceManagerDefaultManager;
    private String mTenancyManagerDefaultSubject;
    private String mTenancyManagerDefaultManager;
    private String mIotagentDefaultSubject;
    private String mKafkaDefaultManager;
    private Integer mKafkaDefaultSessionTimeout;
    private String mKafkaDefaultGroupId;
    private Long mKafkaDefaultConsumerPollTime;
    private String keycloakBasePath;
    private String keycloakIgnoreRealm;
    private String keycloakUsername;
    private String keycloakPassword;
    private String keycloakClientId;
    private String keycloakGrantType;

    private Config() {
        if (System.getenv("DEVM_ADDRESS") != null) {
            this.mDeviceManagerAddress = System.getenv("DEVM_ADDRESS");
        } else {
            this.mDeviceManagerAddress = "http://device-manager:5000";
        }

        if (System.getenv("IMGM_ADDRESS") != null) {
            this.mImageManagerAddress = System.getenv("IMGM_ADDRESS");
        } else {
            this.mImageManagerAddress = "image-manager:5000";
        }


        if (System.getenv("DATA_BROKER_ADDRESS") != null) {
            this.mDataBrokerAddress = System.getenv("DATA_BROKER_ADDRESS");
        } else {
            this.mDataBrokerAddress = "http://data-broker:80";
        }

        if (System.getenv("KAFKA_ADDRESS") != null) {
            this.mKafkaAddress = System.getenv("KAFKA_ADDRESS");
        } else {
            this.mKafkaAddress = "kafka:9092";
        }

        if (System.getenv("KAFKA_CONSUMER_POLL_TIME") != null) {
            this.mKafkaDefaultConsumerPollTime = new Long(System.getenv("KAFKA_CONSUMER_POLL_TIME"));
        } else {
            this.mKafkaDefaultConsumerPollTime = new Long(100);
        }

        if (System.getenv("KEYCLOAK_BASE_PATH") != null) {
        	this.keycloakBasePath = System.getenv("KEYCLOAK_BASE_PATH");
        }else {
        	this.keycloakBasePath = "http://apigw:8000/auth";
        }

        if (System.getenv("KEYCLOAK_IGNORE_REALM") != null) {
        	this.keycloakIgnoreRealm = System.getenv("KEYCLOAK_IGNORE_REALM");
        }else {
        	this.keycloakIgnoreRealm = "master";
        }

        if (System.getenv("KEYCLOAK_USERNAME") != null) {
        	this.keycloakUsername = System.getenv("KEYCLOAK_USERNAME");
        }else {
        	this.keycloakUsername = "admin";
        }

        if (System.getenv("KEYCLOAK_PASSWORD") != null) {
        	this.keycloakPassword = System.getenv("KEYCLOAK_PASSWORD");
        }else {
        	this.keycloakPassword = "admin";
        }

        if (System.getenv("KEYCLOAK_CLIENT_ID") != null) {
        	this.keycloakClientId = System.getenv("KEYCLOAK_CLIENT_ID");
        }else {
        	this.keycloakClientId = "admin-cli";
        }

        if (System.getenv("KEYCLOAK_GRANT_TYPE") != null) {
        	this.keycloakGrantType = System.getenv("KEYCLOAK_GRANT_TYPE");
        }else {
        	this.keycloakGrantType = "password";
        }


        this.mInternalDefaultTenant = "internal";

        this.mDeviceManagerDefaultSubject = "dojot.device-manager.device";
        this.mDeviceManagerDefaultManager = "http://" + this.mDeviceManagerAddress;

        this.mTenancyManagerDefaultSubject = "dojot.tenancy";

        this.mIotagentDefaultSubject = "device-data";

        this.mKafkaDefaultManager = "http://" + this.mKafkaAddress;
        this.mKafkaDefaultSessionTimeout = 15000;

        Integer randomNumber = (int)(Math.random() * 10000 + 1);
        this.mKafkaDefaultGroupId = "dojot-module-java-" + randomNumber.toString();
    }

    public static synchronized Config getInstance() {
        if (mInstance == null) {
            mInstance = new Config();
        }
        return mInstance;
    }

    public String getInternalTenant(){
        return this.mInternalDefaultTenant;
    }

    public String getDeviceManagerAddress() {
        return this.mDeviceManagerAddress;
    }

    public String getImageManagerAddress() {
        return this.mImageManagerAddress;
    }

    public String getDataBrokerAddress() {
        return this.mDataBrokerAddress;
    }

    public String getKafkaAddress() {
        return this.mKafkaAddress;
    }

    public String getDeviceManagerDefaultSubject() {
        return this.mDeviceManagerDefaultSubject;
    }

    public String getDeviceManagerDefaultManager() {
        return this.mDeviceManagerDefaultManager;
    }

    public String getTenancyManagerDefaultSubject() {
        return this.mTenancyManagerDefaultSubject;
    }

    public String getTenancyManagerDefaultManager() {
        return this.mTenancyManagerDefaultManager;
    }

    public String getIotagentDefaultSubject() {
        return this.mIotagentDefaultSubject;
    }

    public String getKafkaDefaultManager() {
        return this.mKafkaDefaultManager;
    }

    public Integer getKafkaDefaultSessionTimeout() {
        return this.mKafkaDefaultSessionTimeout;
    }

    public String getKafkaDefaultGroupId() {
        return this.mKafkaDefaultGroupId;
    }

    public long getKafkaDefaultConsumerPollTime() {
        return this.mKafkaDefaultConsumerPollTime.longValue();
    }

    public String getKeycloakBasePath() {
		return keycloakBasePath;
	}

	public void setKeycloakBasePath(String keycloakBasePath) {
		this.keycloakBasePath = keycloakBasePath;
	}

    public String getKeycloakIgnoreRealm() {
		return keycloakIgnoreRealm;
	}

	public void setKeycloakIgnoreRealm(String keycloakIgnoreRealm) {
		this.keycloakIgnoreRealm = keycloakIgnoreRealm;
	}

	public String getKeycloakUsername() {
		return keycloakUsername;
	}

	public void setKeycloakUsername(String keycloakUsername) {
		this.keycloakUsername = keycloakUsername;
	}

	public String getKeycloakPassword() {
		return keycloakPassword;
	}

	public void setKeycloakPassword(String keycloakPassword) {
		this.keycloakPassword = keycloakPassword;
	}

	public String getKeycloakClientId() {
		return keycloakClientId;
	}

	public void setKeycloakClientId(String keycloakClientId) {
		this.keycloakClientId = keycloakClientId;
	}

	public String getKeycloakGrantType() {
		return keycloakGrantType;
	}

	public void setKeycloakGrantType(String keycloakGrantType) {
		this.keycloakGrantType = keycloakGrantType;
	}
}