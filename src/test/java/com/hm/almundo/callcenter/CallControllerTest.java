package com.hm.almundo.callcenter;

import com.hm.almundo.callcenter.config.AppConfig;
import com.hm.almundo.callcenter.config.Constants;
import com.hm.almundo.callcenter.controller.CallController;
import com.hm.almundo.callcenter.controller.Dispatcher;
import com.hm.almundo.callcenter.model.Call;
import com.hm.almundo.callcenter.model.Employee;
import com.hm.almundo.callcenter.util.Utils;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RunWith(SpringRunner.class)
@WebFluxTest({CallController.class, Dispatcher.class, AppConfig.class})
public class CallControllerTest {


    @Autowired
    private WebTestClient client;

    private Call call;
    private Queue<Employee> employees;
    private final int MAX_EMPLOYEE = 10;

    @Before
    public void createEmployeeDefault(){
        employees = new LinkedList<>();
        for (int i = 0; i < MAX_EMPLOYEE; i++) {
            Employee employee = new Employee();
            employee.setName("HM " + 1);
            employee.setRole(getRandomRole());
            employees.add(employee);
        }
    }

    public String getRandomRole() {

        int random = Utils.getRandomBeetwen(0,2);
        String role = "";
        switch (random){
            case 0:
                role = Constants.OPERATOR;
                break;
            case 1:
                role = Constants.SUPERVISOR;
                break;
            case 2:
                role = Constants.DIRECTOR;
                break;

        }

        return role;

    }


    @Test
    public void assignEmployeeTest() throws Exception {

        client.post().uri("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(employees.remove()))
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
