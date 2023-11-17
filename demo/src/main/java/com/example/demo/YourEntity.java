package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "your_data")
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key1;
    private String key2;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the key1
	 */
	public String getKey1() {
		return key1;
	}
	/**
	 * @param key1 the key1 to set
	 */
	public void setKey1(String key1) {
		this.key1 = key1;
	}
	/**
	 * @return the key2
	 */
	public String getKey2() {
		return key2;
	}
	/**
	 * @param key2 the key2 to set
	 */
	public void setKey2(String key2) {
		this.key2 = key2;
	}

    
}
