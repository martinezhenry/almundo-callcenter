package com.hm.almundo.callcenter.controller;

import com.hm.almundo.callcenter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.validation.Valid;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Clase Controllador en donde se declaran los servicios que se expondran.
 */
@RestController
public class CallController implements ICallController {

    Logger logger = LoggerFactory.getLogger(CallController.class);

    @Autowired
    private Dispatcher dispatcher;

    public CallController() {
        dispatcher = new Dispatcher();
    }

    /**
     * Recurso tipo GET que se expone para realizar una llamada recibiendo solamente el numero de telefono
     * @param phoneNumber Numero de telefono al que se le realiza la llamada
     * @return Obejcto tipo llamada con los datos de la llamada realizada
     */
    @Override
    @GetMapping(path = "/call/{phoneNumber}", produces = "application/json")
    public Call makeCall(@PathVariable String phoneNumber) {
        logger.debug("Making a call");
        Future<CallImpl> call = dispatcher.dispatcherCall(phoneNumber);
        while (!call.isDone()){

        }
        try {
            CallImpl call1mp = call.get();
            call1mp.processCall();
            call1mp.getCall().setDateTimeEnd(new Date());
            dispatcher.putAvailableEmployeeToQueue(call1mp.getCall().getEmployee());
            dispatcher.removeOnlineCall();
            return call1mp.getCall();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Recurso tipo POST que se expone para asignar el empleado recibido como cuerpo de la petición a la fila de empleados disponibles
     * @param employee Empleado que se desea asignar a la fila de empleados disponibles
     * @return Empleado con los valores asignados
     */
    @Override
    @PostMapping(path = "/employee", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Employee> assignEmployee(@Valid @RequestBody Employee employee) {
        logger.debug("Creating a employee");
        employee = dispatcher.putAvailableEmployeeToQueue(employee);

        return Mono.just(employee);
    }

}
