package br.com.conta.bankslips.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.BankslipProjection;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.exceptions.BankslipNotFoundException;
import br.com.conta.bankslips.exceptions.BankslipValidationException;
import br.com.conta.bankslips.repository.BankslipRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(ServiceConfig.class)
public class BankslipServiceTest {

    @Autowired
    private BankslipRepository repo;

    @Autowired
    private BankslipService service;

    @Before
    public void setup() {
        repo.deleteAll();
    }

    @Test
    public void shouldCreateNewBankslip() throws Exception {
        Bankslip newBankslip = service.createBankslip(new BankslipPostDto("2018-01-01", "100000", "Trillian Company", "PENDING"));

        Bankslip bankslip = repo.getOne(newBankslip.getId());
        assertThat(bankslip).isNotNull();
        assertThat(bankslip.getDueDate()).isEqualTo(LocalDate.parse("2018-01-01"));
        assertThat(bankslip.getCustomer()).isEqualTo("Trillian Company");
        assertThat(bankslip.getTotalInCents()).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(bankslip.getStatus()).isEqualTo(SlipStatus.PENDING);
    }

    @Test
    public void shoulReturnBankslipDetails() throws Exception {
        Bankslip banckSlip = repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING));
        BankslipProjection projection = service.getDetailsBySerial(banckSlip.getSerial());

        assertThat(projection).isNotNull();
        assertThat(projection.getSerial()).isEqualTo(banckSlip.getSerial().toString());
        assertThat(projection.getDueDate()).isEqualTo(banckSlip.getDueDate());
        assertThat(projection.getTotalInCents()).isEqualByComparingTo(banckSlip.getTotalInCents());
        assertThat(projection.getCustomer()).isEqualTo(banckSlip.getCustomer());
    }

    @Test
    public void shouldReturnBankslipList() throws Exception {
        repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING));
        repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Ford Prefect Company", SlipStatus.PENDING));

        List<BankslipProjection> projections = service.getAllBankslips();

        assertThat(projections).isNotEmpty();
        assertThat(projections).hasSize(2);
    }
    
    @Test
    public void shouldMarkBankslipPaid()  throws Exception {
    	Bankslip newBankslip = service.createBankslip(new BankslipPostDto("2018-01-01", "100000", "Trillian Company", "PENDING"));
    	
    	service.payBankslip(newBankslip.getSerial());
    	
    	Bankslip paidBankslip = repo.getOne(newBankslip.getId());
    	
        assertThat(paidBankslip.getDueDate()).isEqualTo(LocalDate.parse("2018-01-01"));
        assertThat(paidBankslip.getCustomer()).isEqualTo("Trillian Company");
        assertThat(paidBankslip.getTotalInCents()).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(paidBankslip.getStatus()).isEqualTo(SlipStatus.PAID);
    }
    
    @Test
    public void shouldMarkBankslipCanceled()  throws Exception {
    	Bankslip newBankslip = service.createBankslip(new BankslipPostDto("2018-01-01", "100000", "Trillian Company", "PENDING"));
    	
    	service.cancelBankslip(newBankslip.getSerial());
    	
    	Bankslip paidBankslip = repo.getOne(newBankslip.getId());
    	
        assertThat(paidBankslip.getDueDate()).isEqualTo(LocalDate.parse("2018-01-01"));
        assertThat(paidBankslip.getCustomer()).isEqualTo("Trillian Company");
        assertThat(paidBankslip.getTotalInCents()).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(paidBankslip.getStatus()).isEqualTo(SlipStatus.CANCELED);
    }

    @Test
    public void shouldIssueError_BankslipValidation_NullDueDate() {
        assertThatThrownBy(() ->
                service.createBankslip(new BankslipPostDto(null, "100000", "Trillian Company", "PENDING")))
                .isInstanceOf(BankslipValidationException.class);
    }

    @Test
    public void shouldIssueError_BankslipValidation_NullTotalCents() {
        assertThatThrownBy(() ->
                service.createBankslip(new BankslipPostDto("2018-01-01", null, "Trillian Company", "PENDING")))
                .isInstanceOf(BankslipValidationException.class);
    }

    @Test
    public void shouldIssueError_BankslipValidation_NullCustomer() {
        assertThatThrownBy(() ->
                service.createBankslip(new BankslipPostDto("2018-01-01", "100000", null, "PENDING")))
                .isInstanceOf(BankslipValidationException.class)
                .hasMessage("Field customer can not be null");
    }

    @Test
    public void shouldIssueError_BankslipValidation_EmptyCustomer() {
        assertThatThrownBy(() ->
                service.createBankslip(new BankslipPostDto("2018-01-01", "100000", "", "PENDING")))
                .isInstanceOf(BankslipValidationException.class)
                .hasMessage("Field customer can not be empty");
    }

    @Test
    public void shouldIssueError_BankslipValidation_NullStatus() {
        assertThatThrownBy(() ->
                service.createBankslip(new BankslipPostDto("2018-01-01", "100000", "Trillian Company", null)))
                .isInstanceOf(BankslipValidationException.class)
                .hasMessage("Name is null");
    }

    @Test
    public void shouldIssueError_BankslipNotFound() {
        UUID serial = UUID.randomUUID();
        assertThatThrownBy(() ->
                service.getDetailsBySerial(serial))
            .isInstanceOf(BankslipNotFoundException.class)
            .hasMessage("Bankslip not found with the specified id: " + serial);
    }

}