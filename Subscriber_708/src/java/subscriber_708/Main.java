/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package subscriber_708;

/**
 *
 * @author AVI03
 */
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Main {
    @Resource(mappedName = "jms/SimpleJMSTopic")
    private static Topic topic;
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue queue;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String destType = null;
        Connection connection = null;
        Session session = null;
        Destination dest = null;
        MessageConsumer consumer = null;
        TextMessage message = null;
        char answer = '\0';
        InputStreamReader inputStreamReader = null;

        if (args.length != 1) {
            System.err.println("Program takes one argument: <dest_type>");
            System.exit(1);
        }

        destType = args[0];
        System.out.println("Destination type is " + destType);

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or \"topic\"");
            System.exit(1);
        }

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue;
            } else {
                dest = (Destination) topic;
            }
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(dest);
            connection.start();
            //int num = 0; /* use in the case to show session recover */
            System.out.println(
                    "To end program, type Q or q, " + "then <return>");
            inputStreamReader = new InputStreamReader(System.in);
            while (!((answer == 'q') || (answer == 'Q'))) {
                Message m = consumer.receive();
                if (m != null) {
                    if (m instanceof TextMessage) {
                        message = (TextMessage) m;
                        System.out.println(
                                "Updated! : " + message.getText());
                        /* use in the case to show session recover */
                        //num++;
                        /*if (num == 10) {
                            System.out.println("Stop connection");
                            connection.stop();
                            System.out.println("Recover session");
                            connection.start();
                            session.recover();
                            
                        }*/
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                  try {
                    answer = (char) inputStreamReader.read();
                } catch (IOException e) {
                    System.err.println("I/O exception: " + e.toString());
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
