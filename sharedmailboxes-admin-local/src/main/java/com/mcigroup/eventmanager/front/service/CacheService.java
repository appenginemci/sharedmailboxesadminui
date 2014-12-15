package com.mcigroup.eventmanager.front.service;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class CacheService {

	protected static MemcacheService memcache = getCacheService();

	private static MemcacheService getCacheService(){
		MemcacheService toReturn = null;

		if(memcache == null){
			toReturn = MemcacheServiceFactory.getMemcacheService();
		}else{
			toReturn = memcache;
		}

		return toReturn;

	}
}
