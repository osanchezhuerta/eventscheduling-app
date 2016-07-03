package org.osanchezhuerta.eventscheduling.engine.soa.services.producer.impl;


import org.osanchezhuerta.eventscheduling.engine.soa.commons.constant.EventSchedulingConstant;
import org.osanchezhuerta.eventscheduling.engine.soa.commons.exception.EngineAmqpException;
import org.osanchezhuerta.eventscheduling.engine.soa.commons.util.EngineDataUtil;
import org.osanchezhuerta.eventscheduling.engine.soa.commons.vo.BusinessMessage;
import org.osanchezhuerta.eventscheduling.engine.soa.services.producer.BusinessMessageSender;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;


public class EngineIdentityRequestSenderImpl implements BusinessMessageSender<String> {


	private AmqpTemplate amqpTemplate;
	
    private String routingKey;

	@Override
	public void sendMessage(BusinessMessage<String> businessMessage) throws EngineAmqpException {
		MessageProperties properties = new MessageProperties();
		properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		properties.setAppId(businessMessage.getApplicationId());
		properties.setCorrelationIdString(businessMessage.getCorrelationId());
		properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		properties.setTimestamp(EngineDataUtil.currentDate());
		properties.setMessageId(businessMessage.getMessageId());
		properties.setPriority(businessMessage.getPriority());
		
		byte[] messageContent = businessMessage.getMessage().getBytes(EventSchedulingConstant.DEFAULT_CHARSET);
		Message message = MessageBuilder
				.withBody(messageContent)
				.copyProperties(properties)
				.copyHeaders(businessMessage.getHeaders()).build();
		
		amqpTemplate.send(message);
		amqpTemplate.send(routingKey, message);
	}
	

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

}
