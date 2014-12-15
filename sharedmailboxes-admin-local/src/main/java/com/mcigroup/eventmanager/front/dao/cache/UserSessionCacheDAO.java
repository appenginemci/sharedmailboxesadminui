package com.mcigroup.eventmanager.front.dao.cache;

import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.UserSession;
import com.mcigroup.eventmanager.front.service.CacheService;

public class UserSessionCacheDAO extends CacheService {
	
	public static boolean save(UserSession userSession){
		boolean hasBeenCached = false;
		
		if(userSession != null){
			hasBeenCached = memcache.put(userSession.getEmail().trim().toUpperCase(), userSession.toJson(), null, SetPolicy.SET_ALWAYS);
		}
		
		return hasBeenCached;
	}
	
	public static UserSession load(String userEmail){
		UserSession toReturn = null;
		
		if(userEmail != null && !userEmail.trim().isEmpty()){
			String json = (String) memcache.get(userEmail.trim().toUpperCase());
			if(json != null && !json.trim().isEmpty()){
				toReturn = Tools.toObject(json, UserSession.class);
			}
		}
		
		return toReturn;
	}
	
	public static boolean isCached(String userEmail){
		boolean isCached = false;
		
		if(userEmail != null && !userEmail.trim().isEmpty()){
			isCached = memcache.contains(userEmail.trim().toUpperCase());
		}
		
		return isCached;
	}
	
	public static boolean remove(String userEmail){
		boolean hasBeenRemoved = false;
		
		if(userEmail != null && !userEmail.trim().isEmpty()){
			hasBeenRemoved = memcache.delete(userEmail.trim().toUpperCase());
		}
		
		return hasBeenRemoved;
	}
	
	public static boolean clear(){
		boolean hasBeenCleared = false;
		
		memcache.clearAll();
		hasBeenCleared = true;
		
		return hasBeenCleared;
	}

}
