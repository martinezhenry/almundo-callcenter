package com.hm.almundo.callcenter.controller;

import com.hm.almundo.callcenter.model.Call;
import com.hm.almundo.callcenter.model.Employee;
import reactor.core.publisher.Mono;

public interface ICallController {

    Call makeCall(String phoneNumber);

    Mono<Employee> assignEmployee(Employee employee);



}
