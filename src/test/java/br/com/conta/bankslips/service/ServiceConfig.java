package br.com.conta.bankslips.service;

import br.com.conta.bankslips.repository.BankslipRepository;
import org.springframework.context.annotation.Bean;

public class ServiceConfig {

    @Bean
    public BankslipService bankslipService(BankslipRepository bankslipRepo) {
        return new BankslipService(bankslipRepo);
    }
}
