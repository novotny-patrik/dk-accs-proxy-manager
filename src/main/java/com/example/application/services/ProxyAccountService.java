package com.example.application.services;

import com.example.application.data.ProxyAccount;
import com.example.application.data.ProxyAccountRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProxyAccountService {

    private final ProxyAccountRepository repository;

    public ProxyAccountService(ProxyAccountRepository repository) {
        this.repository = repository;
    }

    public Optional<ProxyAccount> get(Long id) {
        return repository.findById(id);
    }

    public ProxyAccount update(ProxyAccount entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<ProxyAccount> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ProxyAccount> list(Pageable pageable, Specification<ProxyAccount> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
