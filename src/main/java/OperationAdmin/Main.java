package OperationAdmin;

import OperationAdmin.Control.Activemq;
import OperationAdmin.View.Menu;

import javax.jms.JMSException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws JMSException, IOException {
        //control.redis_link();
        Menu menu = new Menu();
        menu.Menu();
        //View.Test test = new View.Test();
        //test.View.Test();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            Activemq activeMQListenerService;
            public void run() {
                activeMQListenerService = new Activemq();
                activeMQListenerService.writeMessage("IsViewOpen","0");
            }
        });
    }
}