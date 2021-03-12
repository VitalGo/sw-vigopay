package com.othr.swvigopay.repository;

import com.othr.swvigopay.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepositoryIF extends CrudRepository<Account, Long> {
    Account findById(long id);
    Account findByUserId(long id);
}
