package com.hm.almundo.callcenter;

import com.hm.almundo.callcenter.config.AppConfig;
import com.hm.almundo.callcenter.config.Constants;
import com.hm.almundo.callcenter.controller.CallController;
import com.hm.almundo.callcenter.controller.Dispatcher;
import com.hm.almundo.callcenter.model.Call;
import com.hm.almundo.callcenter.model.Employee;
import com.hm.almundo.callcenter.util.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.*;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@WebFluxTest({CallController.class, Dispatcher.class, AppConfig.class})
public class CallControllerTest {


    @Autowired
    private WebTestClient client;

    private Call call;
    private Queue<Employee> employees;
    private final int MAX_EMPLOYEE = 12;

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

    @Test
    public void makeMultiCallsTest() throws Exception {

        runMultiEmployee(10);
        runMultiCalls(11);

    }

    private void runMultiEmployee(final int threadCount) throws InterruptedException, ExecutionException {
        //final BrokenUniqueIdGenerator domainObject = new BrokenUniqueIdGenerator();
        Callable<Employee> task = new Callable<Employee>() {
            @Override
            public Employee call() {
                WebTestClient.ResponseSpec response = client.post().uri("/employee").accept(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(employees.remove())).exchange();
                response = response.expectStatus().isCreated();
                WebTestClient.BodySpec bodySpec= response.expectBody(Employee.class);
                Employee employee = (Employee) bodySpec.returnResult().getResponseBody();

                return employee;

            }
        };
        List<Callable<Employee>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Employee>> futures = executorService.invokeAll(tasks);
        List<Long> resultList = new ArrayList<Long>(futures.size());
        // Check for exceptions
        for (Future<Employee> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().getCode());
        }

    }


    private void runMultiCalls(final int threadCount) throws InterruptedException, ExecutionException {
        //final BrokenUniqueIdGenerator domainObject = new BrokenUniqueIdGenerator();
        Callable<Call> task = new Callable<Call>() {
            @Override
            public Call call() {
                WebTestClient.ResponseSpec response = client.get().uri("/call/{id}", 2).accept(MediaType.APPLICATION_JSON).exchange();
                response = response.expectStatus().isOk();
                 WebTestClient.BodySpec bodySpec= response.expectBody(Call.class);
                 Call call = (Call) bodySpec.returnResult().getResponseBody();

                return call;

            }
        };
        List<Callable<Call>> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Call>> futures = executorService.invokeAll(tasks);
        List<Long> resultList = new ArrayList<Long>(futures.size());
        // Check for exceptions
        for (Future<Call> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().getId());
        }
        // Validate the IDs
        Assert.assertEquals(threadCount, futures.size());
        List<Long> expectedList = new ArrayList<Long>(threadCount);
        for (long i = 1; i <= threadCount; i++) {
            expectedList.add(i);
        }
        Collections.sort(resultList);
        Assert.assertEquals(expectedList, resultList);
    }

}
