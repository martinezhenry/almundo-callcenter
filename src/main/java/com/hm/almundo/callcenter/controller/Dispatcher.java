package com.hm.almundo.callcenter.controller;

import com.hm.almundo.callcenter.config.AppConfig;
import com.hm.almundo.callcenter.config.Constants;
import com.hm.almundo.callcenter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase Dispatcher encargada de gestionar las llamada que se reciben
 */
@RestController
public class Dispatcher implements Constants {

    @Autowired
    private AppConfig appConfig;

    private Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    //private Queue<Employee> queueAvailableOperator;
    //private Queue<Employee> queueAvailableSupervisor;
    //private Queue<Employee> queueAvailableDirector;
    private Queue<Employee> queueAvailableLow;
    private Queue<Employee> queueAvailableMedium;
    private Queue<Employee> queueAvailableHigh;
    private Queue<Call> onlineCalls;
    private static ReentrantLock lock;
    private static Condition notAvailableEmployeeCondition;
    private static Condition maxOnlineCallsCondition;
    private AtomicLong callsId;
    private AtomicLong employeesCode;

    public Dispatcher(){
        //appConfig = new AppConfig();
        lock = new ReentrantLock(true);
        notAvailableEmployeeCondition = lock.newCondition();
        maxOnlineCallsCondition = lock.newCondition();
        //queueAvailableOperator = new LinkedList<>();
        //queueAvailableSupervisor = new LinkedList<>();
        //queueAvailableDirector = new LinkedList<>();
        queueAvailableLow = new LinkedList<>();
        queueAvailableMedium = new LinkedList<>();
        queueAvailableHigh = new LinkedList<>();
        onlineCalls = new LinkedList<>();
        callsId = new AtomicLong();
        employeesCode = new AtomicLong();

    }


    /**
     * Asigna un empleado a la llamada entrante y la retorna un objecto CallImpl con la
     * llamada asignada.
     * @param phoneNumber numero de telefono al que se realiza la llamada
     * @return Objecto de tipo CallImpl
     */
    @Async
    public Future<CallImpl> dispatcherCall(String phoneNumber) {

        Future<CallImpl> mono = null;
        try {
            lock.lock();
            long initHoldOn = 0;
            long endHoldOn = 0;
            long holdOnTimeByMaxCalls = 0;
            long holdOnTimeByEmployee = 0;
            if (isMaxCallsOnline()){
                initHoldOn += System.currentTimeMillis();
                logger.debug("Waiting to finished a call");
                await(maxOnlineCallsCondition);
                endHoldOn = System.currentTimeMillis();
                holdOnTimeByMaxCalls = (endHoldOn - initHoldOn);
            }
            Employee employee;

            Call call = new Call(phoneNumber);
            putOnlineCall(call);
            call.setId(callsId.incrementAndGet());
            initHoldOn = System.currentTimeMillis();
            employee = getAvailableEmployee();
            endHoldOn = System.currentTimeMillis();
            holdOnTimeByEmployee = (endHoldOn - initHoldOn);
            call.setEmployee(employee);
            call.setHoldOnTime(TimeUnit.MILLISECONDS.toSeconds(holdOnTimeByEmployee + holdOnTimeByMaxCalls));
            CallImpl callImp = new CallImpl();
            callImp.setCall(call);
            mono = new AsyncResult<>(callImp);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return mono;

    }


    /**
     * Retorna la validación de si las llamadas actualemnte en linea son mayores o iguales al maximo permitido
     * designado en la configuración de la aplicación
     * @return validación de maximo de llamada en linea
     */
    public boolean isMaxCallsOnline(){
        return  (onlineCalls.size() >= appConfig.getMaxSessions());
    }

    /**
     * Retorna el primer empleado disponible de acuerdo a la prioridad por cargo que posea el empleado.
     * Si no existen empleados disponibles, espera hasta ser notificado de que hay un empleado disponible.
     * Se remueve de la fila el empleado disponible obtenido
     * @return Empleado disponible
     */
    private Employee getAvailableEmployee(){

        Employee employee = null;
        try {

            if (queueAvailableLow.isEmpty() && queueAvailableMedium.isEmpty() && queueAvailableHigh.isEmpty()) {
                logger.debug("waiting to employee");
                await(notAvailableEmployeeCondition);
            }

            int priority = appConfig.getPriorityDefault();
            Queue<Employee> queue;
            while (employee == null) {
                queue = getQueueByPriority(priority);
                if (!queue.isEmpty()) {
                    employee = queue.remove();
                } else {
                    priority = ++priority % 3;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return employee;

    }

    /**
     * Saca de la fila de llamada en linea la primera llamada y la retorna
     * @return llamada en linea de primero en la fila
     */
    public Call removeOnlineCall(){
        Call call = null;
        try {
            lock.lock();
            call = onlineCalls.remove();
            maxOnlineCallsCondition.signal();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return call;
    }


    /**
     * Coloca al final de la fila de llamadas en linea la llamada recibida de parametro
     * @param call llamada a ser colocalada en la fila
     * @return regresa el mismo objecto de llamada que se recibio de parametro
     */

    public Call putOnlineCall(Call call){

        try {

            onlineCalls.add(call);
            logger.debug("Total OnlineCalls: " + onlineCalls.size());
        } catch (Exception e){
            e.printStackTrace();
        }

        return call;

    }

    /**
     * Coloca al final de la fila que le corresponde segun el tipo de cargo del empleado recibido como parametro.
     * Valida si el empleado tiene un codigo de empleado, si no, se lo asigna.
     * @param employee Empleado a ser colocado en la fila
     * @return Empleado recibido como parametro
     */
    public Employee putAvailableEmployeeToQueue(Employee employee){

        try {
            lock.lock();
            employee = validateDataEmployee(employee);
            Queue<Employee> queue = getQueueByPriority(employee.getPriority());
            queue.add(employee);
            notAvailableEmployeeCondition.signal();
            logger.debug("Total available queue: " + queue.size());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return employee;

    }

    public Employee validateDataEmployee(Employee employee){
        employee = assignCodeToEmployee(employee);
        employee = assignPriorityByRole(employee);
        return employee;
    }


    public Queue<Employee> getQueueByPriority(int priority) {
        Queue<Employee> queue = null;
        switch (priority) {
            case LOW:
                queue = queueAvailableLow;
                break;
            case MEDIUM:
                queue = queueAvailableMedium;
                break;
            case HIGH:
                queue = queueAvailableHigh;
                break;
        }
        return queue;
    }

    public Employee assignCodeToEmployee(Employee employee){
        if (employee.getCode() == 0){
            employee.setCode(employeesCode.incrementAndGet());
        }
        return employee;
    }

    /**
     * Coloca un objecto tipo Condition en espera
     * @param condition Condition que se desea colocar en espera
     */
    public void await(Condition condition){
        try {
            condition.await();
        } catch (InterruptedException e){
            e.printStackTrace();
            logger.warn(e.getMessage() + "(" + Arrays.toString(e.getStackTrace()) + ")");
        }
    }


    public Employee assignPriorityByRole(Employee employee){
        if(employee.getPriority() == 0) {
            employee.setPriority(appConfig.getPriorityByRoleHash().get(employee.getRole().toLowerCase()));
        }
        return employee;
    }


    public Queue<Call> getOnlineCalls() {
        return onlineCalls;
    }

    public void setOnlineCalls(Queue<Call> onlineCalls) {
        this.onlineCalls = onlineCalls;
    }
}
