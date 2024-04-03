package com.picpaysimplicado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpaysimplicado.domain.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{    
}
