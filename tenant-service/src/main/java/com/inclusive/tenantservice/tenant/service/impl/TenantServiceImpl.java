package com.inclusive.tenantservice.tenant.service.impl;

import com.inclusive.tenantservice.tenant.service.TenantService;
import com.inclusive.tenantservice.tenant.entity.Tenant;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TenantServiceImpl implements TenantService {

    private final Map<Long, Tenant> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public List<Tenant> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Tenant findById(Long id) {
        return store.get(id);
    }

    @Override
    public Tenant create(Tenant e) {
        long id = idGen.getAndIncrement();
        try {
            // Si la entidad tiene setId(Long), ÃƒÂºsalo; si no, ignora
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public Tenant update(Long id, Tenant e) {
        if (!store.containsKey(id)) return null;
        try {
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}



