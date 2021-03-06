/*
 * Copyright (c) 2017 <l_iupeiyu@qq.com> All rights reserved.
 */

package com.geekcattle.conf.redis;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * redis实现共享session
 * author geekcattle
 * date 2017/3/22 0022 下午 15:32
 */
public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

    private Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);

    // session 在redis过期时间是30分钟30*60
    private static int expireTime = 1800;

    private static String redisPrefix = "shiro-redis-session:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String getKey(String originalKey) {
        return redisPrefix + originalKey;
    }

    // 创建session，保存到数据库
    @Override
    protected Serializable doCreate(Session session) throws UnknownSessionException {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        logger.debug("createSession:{}", session.getId().toString());
        try {
            redisTemplate.opsForValue().set(getKey(session.getId().toString()), session);
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return sessionId;
    }

    // 获取session
    @Override
    protected Session doReadSession(Serializable sessionId) {
        logger.debug("readSession:{}", sessionId.toString());
        // 先从缓存中获取session，如果没有再去数据库中获取
        Session session = null ;
        try {
            session = (Session) redisTemplate.opsForValue().get(getKey(sessionId.toString()));
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return session;
    }

    // 更新session的最后一次访问时间
    @Override
    public void update(Session session) {
        logger.debug("updateSession:{}", session.getId().toString());
        String key = getKey(session.getId().toString());
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, session);
        }
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    // 删除session
    @Override
    public void delete(Session session) {
        logger.debug("delSession:{}", session.getId());
        redisTemplate.delete(getKey(session.getId().toString()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        logger.debug("activeSession");
        return Collections.emptySet();
    }
}
