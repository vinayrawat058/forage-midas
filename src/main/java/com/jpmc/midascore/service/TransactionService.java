package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null || sender.getBalance() < transaction.getAmount()) {
            return; // Discard invalid transaction
        }

        // Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Incentive.class
        );
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

    }

}
