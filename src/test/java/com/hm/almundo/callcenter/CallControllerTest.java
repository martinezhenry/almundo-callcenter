package com.hm.almundo.callcenter;

import com.hm.almundo.callcenter.config.AppConfig;
import com.hm.almundo.callcenter.config.Constants;
import com.hm.almundo.callcenter.controller.CallController;
import com.hm.almundo.callcenter.controller.Dispatcher;
import com.hm.almundo.callcenter.model.Call;
import com.hm.almundo.callcenter.model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@RunWith(SpringRunner.class)
@WebFluxTest({CallController.class, Dispatcher.class, AppConfig.class})
public class CallControllerTest {


    @Autowired
    private WebTestClient client;

    private Call call;
    private Employee employee;

    @Before
    public void createEmployeeDefault(){
        employee = new Employee();
        employee.setName("HM");
        employee.setRole(Constants.OPERATOR);
    }


    @Test
    public void assignEmployeeTest() throws Exception {

        client.post().uri("/api/customer/post")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(employee))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Employee.class);
    }


    @Test
    public void makeCallTest() throws Exception {

        client.get().uri("/call/{id}", 2).accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Call.class);


    }

    // POST Test-case


}
