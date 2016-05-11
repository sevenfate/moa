package de.seven.fate.moa.jms.queue.producer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Mario on 07.05.2016.
 */
@Stateless
public class MessageAsyncProducer {

    @Inject
    private Logger logger;

    @Resource(mappedName = "java:/jms/queue/ExpiryQueue")
    private Queue queue;

    @Inject
    @JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
    private JMSContext context;

    @Inject
    private MessageCompletionListener completionListener;


    public void sendMessage(Serializable objectModel) {
        logger.info("send Message message to queue: " + objectModel);

        TextMessage textMessage = context.createTextMessage(String.valueOf(objectModel));

        applyMessageProperty(textMessage, "type", objectModel.getClass().getSimpleName());

        context.createProducer().setAsync(completionListener).send(queue, textMessage);
    }

    private void applyMessageProperty(TextMessage textMessage, String propertyName, String propertyValue) {

        try {
            textMessage.setStringProperty(propertyName, propertyValue);
        } catch (JMSException e) {
            logger.log(Level.WARNING, e.getMessage(), e);

            throw new IllegalArgumentException("unable to set property: " + propertyName + " with value: " + propertyName);
        }
    }
}