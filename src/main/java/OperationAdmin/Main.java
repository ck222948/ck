package OperationAdmin;




import OperationAdmin.Control.Activemq;
import OperationAdmin.View.Menu;

import javax.jms.JMSException;
import java.io.IOException;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
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