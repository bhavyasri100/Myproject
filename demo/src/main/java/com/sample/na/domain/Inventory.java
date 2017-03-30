package com.sample.na.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cose_inventory")
public class Inventory extends DongleDevice {

	private String vin;
	private String configurationID;
	private String url;
	private String diaxScript;
	private int serialNumber;
	private String imsi;
	private String iccid;
	private String imei;
	private int pin;
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getConfigurationID() {
		return configurationID;
	}
	public void setConfigurationID(String configurationID) {
		this.configurationID = configurationID;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDiaxScript() {
		return diaxScript;
	}
	public void setDiaxScript(String diaxScript) {
		this.diaxScript = diaxScript;
	}
	public int getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getIccid() {
		return iccid;
	}
	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public int getPin() {
		return pin;
	}
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	

}
