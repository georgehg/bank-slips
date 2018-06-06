package br.com.conta.bankslips.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.repository.BankslipRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
public class BankslipControllerIT {
	
	@Autowired
    private BankslipRepository repo;

    @Autowired
    private JacksonTester<BankslipPostDto> json;
    
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                    MediaType.APPLICATION_JSON.getSubtype(),
                                                    Charset.forName("utf8"));

    @Before
    public void setup() throws Exception {
    	repo.deleteAll();
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void shouldCreateBankslip() throws Exception {
        //Arrange
        BankslipPostDto inputDto = new BankslipPostDto("2018-01-01", "100000", "Trillian Company", "PENDING");
        
        //Act
        ResultActions result =
                this.mvc.perform(post("/rest/bankslips").contextPath("/rest")
                                .contentType(contentType)
                                .content(json.write(inputDto).getJson()));

        //Assert
        result.andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("message", is("Bankslip created")));
    }

    @Test
    public void createBankslip_shouldReturn422_NullDueDate() throws Exception {
        //Arrange
        BankslipPostDto inputDto = new BankslipPostDto(null, "100000", "Trillian Company", "PENDING");

        //Act
        ResultActions result =
                this.mvc.perform(post("/rest/bankslips").contextPath("/rest")
                        .contentType(contentType)
                        .content(json.write(inputDto).getJson()));

        //Assert
        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message", is("Invalid bankslip provided.The possible reasons are: A field of the provided bankslip was null or with invalid values")));
    }

    @Test
    public void shouldListBankslips() throws Exception {
        //Arrange
    	repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING));
    	repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING));

        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips").contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("[*]", hasSize(2)));
    }
    
    @Test
    public void shouldGetBankslip_OnDue() throws Exception {
        //Arrange
    	String dueDate = LocalDate.now().plus(30, ChronoUnit.DAYS).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Bankslip entity = repo.save(Bankslip.of(LocalDate.now().plus(30, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Ford Prefect Company", SlipStatus.PENDING));
        
        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", entity.getSerial()).contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(entity.getSerial().toString())))
                .andExpect(jsonPath("$.due_date", is(dueDate)))
                .andExpect(jsonPath("$.total_in_cents", is("100000")))
                .andExpect(jsonPath("$.customer", is("Ford Prefect Company")))
                .andExpect(jsonPath("$.fine", is("0")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    public void shouldGetBankslip_5DaysOverdue() throws Exception {
        //Arrange
    	String dueDate = LocalDate.now().minus(5, ChronoUnit.DAYS).format(DateTimeFormatter.ISO_LOCAL_DATE);
    	Bankslip entity = repo.save(Bankslip.of(LocalDate.now().minus(5, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Ford Prefect Company", SlipStatus.PENDING));
        
    	//Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", entity.getSerial()).contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(entity.getSerial().toString())))
                .andExpect(jsonPath("$.due_date", is(dueDate)))
                .andExpect(jsonPath("$.total_in_cents", is("100000")))
                .andExpect(jsonPath("$.customer", is("Ford Prefect Company")))
                .andExpect(jsonPath("$.fine", is("2500")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }
    
    @Test
    public void shouldGetBankslip_15DaysOverdue() throws Exception {
        //Arrange
    	String dueDate = LocalDate.now().minus(15, ChronoUnit.DAYS).format(DateTimeFormatter.ISO_LOCAL_DATE);
    	Bankslip entity = repo.save(Bankslip.of(LocalDate.now().minus(15, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Ford Prefect Company", SlipStatus.PENDING));
    	
        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", entity.getSerial()).contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(entity.getSerial().toString())))
                .andExpect(jsonPath("$.due_date", is(dueDate)))
                .andExpect(jsonPath("$.total_in_cents", is("100000")))
                .andExpect(jsonPath("$.customer", is("Ford Prefect Company")))
                .andExpect(jsonPath("$.fine", is("15000")))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    public void shouldIssueError_GetBankslip_NotFound() throws Exception {
        //Arrange
        UUID serial = UUID.randomUUID();

        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", serial).contextPath("/rest"));

        //Assert
        result.andExpect(status().isNotFound());
                
    }
    
    @Test
    public void shouldPayBankslip() throws Exception {
        //Arrange
    	Bankslip entity = repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING));

        //Act
        ResultActions result =
                this.mvc.perform(put("/rest/bankslips/{id}", entity.getSerial().toString()).contextPath("/rest")
                                .contentType(contentType)
                                .content("{\"status\":\"PAID\"}"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("message", is("Bankslip paid")));
    }
    
    @Test
    public void shouldCancelBankslip() throws Exception {
        //Arrange
    	Bankslip entity = repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING));

        //Act
        ResultActions result =
                this.mvc.perform(put("/rest/bankslips/{id}", entity.getSerial().toString()).contextPath("/rest")
                                .contentType(contentType)
                                .content("{\"status\":\"CANCELED\"}"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("message", is("Bankslip canceled")));
    }
    
    @Test
    public void shouldIssueError_PayBankslip_NotFound() throws Exception {
        //Arrange
    	UUID serial = UUID.randomUUID();

        //Act
        ResultActions result =
                this.mvc.perform(put("/rest/bankslips/{id}", serial).contextPath("/rest")
                                .contentType(contentType)
                                .content("{\"status\":\"PAID\"}"));

        //Assert
        result.andExpect(status().isNotFound())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("message", is("Bankslip not found with the specified id: " + serial.toString())));
    }
}