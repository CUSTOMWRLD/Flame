package com.customwrld.flame.pigeon;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.ClassUtils;
import com.customwrld.flame.Flame;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class Pigeon {

    private String identifier;

    @Getter
    private Channel channel;

    private String queue;

    private Set<Payload> payloadTypes;

    public Pigeon(String identifier) {
        this.identifier = identifier;
        payloadTypes = new HashSet<>();
        for (Class<?> aClass : ClassUtils.getClassesInPackage(Flame.get().getClass(), "com.customwrld.flame.pigeon.payloads")) {
            registerPayload(aClass);
        }
    }

    public boolean registerPayload(Class<?> payloadClass) {
        try {
            if (!Payload.class.isAssignableFrom(payloadClass)) {
                return false;
            }
            Payload payload = (Payload) payloadClass.newInstance();
            payloadTypes.add(payload);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void setupAQMP() {
        try {
            channel = CustomLib.getInstance().getRabbitMqConnection().createChannel();

            queue = channel.queueDeclare().getQueue();

            channel.exchangeDeclare("pigeon-fanout", "fanout");

            for (Payload payload : payloadTypes) {
                channel.queueBind(queue, "pigeon-fanout", identifier + ":" + payload.name);
            }

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String retrievedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);

                String payload = delivery.getEnvelope().getRoutingKey().substring(identifier.length() + 1);
                Payload payloadObject = null;
                for (Payload payloadType : payloadTypes) {
                    if (payloadType.name.equals(payload)) {
                        payloadObject = payloadType;
                    }
                }

                if (payloadObject == null)
                    return;

                payloadObject.receive(payloadObject.fromJson(retrievedMessage));
            };
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void produce(String routingKey, String message) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.basicPublish("pigeon-fanout", identifier + ":" + routingKey, null, message.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException | TimeoutException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static String decodeString(ArrayList<Double> msgBytes) {
        byte[] bytes = new byte[msgBytes.size()];
        for (int i = 0; i < msgBytes.size(); i++) {
            bytes[i] = msgBytes.get(i).byteValue();
        }
        return new String(bytes, StandardCharsets.UTF_16);
    }
}
