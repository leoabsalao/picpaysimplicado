package com.picpaysimplicado.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplicado.domain.transaction.Transaction;
import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.TransactionDTO;
import com.picpaysimplicado.repositories.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificatioService notificatioService;

    public boolean authorizeTransaction(User user, BigDecimal value){
        //  ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a", null)            
        return true;
    }

    public Transaction createTransaction(TransactionDTO transaction) throws Exception{
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender,transaction.value()); 
        
        boolean IsAuthorized = this.authorizeTransaction(sender, transaction.value());
        if (!IsAuthorized){
            throw new Exception("Transação não autorizada!");
        }

        // Montando a transação
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        //Atualizando os usuários
        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        //Salvando a transação
        this.repository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificatioService.sendNotification(sender, "Transação realizada com sucesso!");
        this.notificatioService.sendNotification(receiver, "Transação recebida com sucesso!");

        return newTransaction;
    }
    
}
