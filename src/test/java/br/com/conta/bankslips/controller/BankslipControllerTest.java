package br.com.conta.bankslips.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.BankslipDetailProjection;
import br.com.conta.bankslips.domain.BankslipProjection;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.exceptions.BankslipNotFoundException;
import br.com.conta.bankslips.exceptions.BankslipValidationException;
import br.com.conta.bankslips.service.BankslipService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BankslipController.class)
@AutoConfigureJsonTesters
public class BankslipControllerTest {

    @MockBean
    private BankslipService bankslipService;

    @Autowired
    private JacksonTester<BankslipPostDto> json;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                    MediaType.APPLICATION_JSON.getSubtype(),
                                                    Charset.forName("utf8"));

    @Before
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();

    }

    @Test
    public void shouldCreatePlanet() throws Exception {
        //Arrange
        BankslipPostDto inputDto = new BankslipPostDto("2018-01-01", "100000", "Trillian Company", "PENDING");
        Bankslip resultEntity = Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING);

        when(bankslipService.createBankslip(inputDto)).thenReturn(resultEntity);

        //Act
        ResultActions result =
                this.mvc.perform(post("/rest/bankslips").contextPath("/rest")
                                .contentType(contentType)
                                .content(json.write(inputDto).getJson()));

        //Assert
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "localhost:8080/rest/bankslips/" + resultEntity.getSerial()))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("message", is("Bankslip created")));
    }

    @Test
    public void createPlanet_shouldReturn400_BodyNotProvied() throws Exception {
        //Act
        ResultActions result =
                this.mvc.perform(post("/rest/bankslips").contextPath("/rest")
                        .contentType(contentType)
                        .content(""));

        //Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bankslip not provided in the request body")));
    }

    @Test
    public void createPlanet_shouldReturn400_NullDueDate() throws Exception {
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
    public void shouldListPlanets() throws Exception {
        //Arrange
        Bankslip resultEntity1 = Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING);
        Bankslip resultEntity2 = Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING);

        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        BankslipProjection projection1 = projectionFactory.createProjection(BankslipProjection.class, resultEntity1);
        BankslipProjection projection2 = projectionFactory.createProjection(BankslipProjection.class, resultEntity2);

        when(bankslipService.getAllBankslips()).thenReturn(Arrays.asList(projection1, projection2));

        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips").contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("[*]", hasSize(2)));
    }

    @Test
    public void shouldGetPlanet() throws Exception {
        //Arrange
        Bankslip resultEntity = Bankslip.of(LocalDate.now().minus(5, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Ford Prefect Company", SlipStatus.PENDING);

        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
        BankslipDetailProjection projection = projectionFactory.createProjection(BankslipDetailProjection.class, resultEntity);

        when(bankslipService.getDetailsBySerial(resultEntity.getSerial())).thenReturn(projection);

        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", resultEntity.getSerial()).contextPath("/rest"));

        //Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(resultEntity.getSerial().toString())));
    }

    @Test
    public void shouldIssueError_GetPlanet_InvalidUUID() throws Exception {
        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", "1234567908").contextPath("/rest"));

        //Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Invalid id provided - it must be a valid UUID")));
    }

    @Test
    public void shouldIssueError_GetPlanet_NotFound() throws Exception {
        //Arrange
        UUID serial = UUID.randomUUID();
        when(bankslipService.getDetailsBySerial(serial)).thenThrow(BankslipNotFoundException.class);

        //Act
        ResultActions result =
                this.mvc.perform(get("/rest/bankslips/{id}", serial).contextPath("/rest"));

        //Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("message", is("Bankslip not found with the specified id")));
    }
}