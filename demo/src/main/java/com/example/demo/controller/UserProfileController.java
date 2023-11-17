package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.UserProfile;

import jakarta.annotation.PostConstruct;

@RestController
public class UserProfileController {
	
	private Map<String, UserProfile> userMap;
	
	@PostConstruct
	public void init() {
		userMap = new HashMap<String, UserProfile>();
		userMap.put("1", new UserProfile("1", "홍길동", "111-1111", "서울시 강남구 대치동"));
		userMap.put("2", new UserProfile("2", "김길동", "112-1111", "서울시 강남구 대치동"));
		userMap.put("3", new UserProfile("3", "엄길동", "113-1111", "서울시 강남구 대치동"));
	}

	@GetMapping("/user/{id}")
	public UserProfile getUserProfile(@PathVariable("id") String id) {
		return userMap.get(id);
	}
	
	@GetMapping("/user/all")
	public List<UserProfile>getUserProfileList(){
		return new ArrayList<UserProfile>(userMap.values());
	}
	
	@PutMapping("/user/{id}")
	public void putUserProfile(@PathVariable("id") String id ,@RequestParam("name") String name,@RequestParam("phone") String phone, @RequestParam("adress") String adress) {
		UserProfile userProfile = new UserProfile(id, name, phone, adress);
		userMap.put(id, userProfile);
	}
	
	@PostMapping("/user/{id}")
	public void postUsetProfile(@PathVariable("id") String id ,@RequestParam("name") String name,@RequestParam("phone") String phone, @RequestParam("adress") String adress) {
		UserProfile userProfile = userMap.get(id);
		userProfile.setName(name);
		userProfile.setPhone(phone);
		userProfile.setadress(adress);
		
		
	}
	
	@DeleteMapping("/user/{id}")
	public void deleteUserProfile(@PathVariable("id") String id) {
		userMap.remove(id);
	}
	

}
