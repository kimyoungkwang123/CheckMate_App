package com.example.demo.model;

public class UserProfile {
	private String id;
	private String name;
	private String phone;
	private String adress;
	
	public UserProfile(String id, String name, String phone, String adress) {
		super();
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.adress = adress;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getadress() {
		return adress;
	}

	/**
	 * @param adress the adress to set
	 */
	public void setadress(String adress) {
		this.adress = adress;
	}
	
	
}
