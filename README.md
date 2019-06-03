# almundo-callcenter
Aplicación de Call Center para atendender llamadas y asignar empleados
Creada en Spring Boot v.2.1.5 RELEASE y Maven

Se emplea el uso de las propiedades de WebFlux que Spring nos proporciona para la generación de dos Endpoints.

Enpoints 

1. makeCall - GET.
2. assignEmployee - POST


MakeCall.
  Method: GET
  Uri: call/{phoneNumber}
  Uso: Servicio de generar una llamada y procesarla con el primer empleado disponible, recibe como unico parametro el numero de telefono al que se desea realizar la llamada.

AssignEmployee.

  Method: POST
  Uri: employee
  Body: Employee Class
  Uso: Servicio para asignar un empleado a la cola de disponibles para atender llamadas, es colocada en la respectiva cola según la prioridad/Rol del empleado recibido como cuerpo de la petición.
  
  
  
