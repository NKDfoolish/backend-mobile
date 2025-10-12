package com.myproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MqttService {
    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment env;
    private final AtomicBoolean mqttEnabled = new AtomicBoolean(false);

    private String serverUrl;
    private int port;
    private String username;
    private String password;
    private String topic;
    private String clientId;
    private MqttClient mqttClient;

    @Autowired
    public MqttService(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void initialize() {
        try {
            // Get properties from Environment
            serverUrl = env.getProperty("spring.mqtt.server");
            port = Integer.parseInt(env.getProperty("spring.mqtt.port", "8883"));
            username = env.getProperty("spring.mqtt.username");
            password = env.getProperty("spring.mqtt.password");
            topic = env.getProperty("spring.mqtt.topic");
            clientId = env.getProperty("spring.mqtt.client-id");

            // Add a unique suffix to the client ID to avoid connection conflicts
            clientId = clientId + "_" + UUID.randomUUID().toString().substring(0, 6);

            logger.info("MQTT Configuration: server={}, port={}, username={}, clientId={}, topic={}",
                    serverUrl, port, username, clientId, topic);

            if (serverUrl == null || username == null || password == null || topic == null) {
                logger.warn("MQTT configuration incomplete. Missing required properties.");
                return;
            }

            String serverUri = "ssl://" + serverUrl + ":" + port;
            logger.info("Attempting to connect to MQTT broker: {}", serverUri);

            try {
                mqttClient = new MqttClient(serverUri, clientId, new MemoryPersistence());

                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                options.setCleanSession(true);
                options.setConnectionTimeout(10); // Shorter timeout for quicker failure
                options.setKeepAliveInterval(60);
                options.setAutomaticReconnect(true);

                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        logger.error("Connection to MQTT broker lost", cause);
                        mqttEnabled.set(false);
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        String payload = new String(message.getPayload());
                        logger.info("Message received: {}", payload);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        // Not used in this implementation
                    }
                });

                mqttClient.connect(options);
                mqttClient.subscribe(topic, 1);
                logger.info("Successfully connected to MQTT broker: {}", serverUri);
                mqttEnabled.set(true);
            } catch (MqttException e) {
                logger.warn("Failed to connect to MQTT broker: {}. Application will continue without MQTT functionality. Error: {}",
                        serverUri, e.getMessage());
                mqttEnabled.set(false);
            }
        } catch (Exception e) {
            logger.error("Error during MQTT initialization", e);
            mqttEnabled.set(false);
        }
    }

    public boolean publishMessage(int deviceId, String command, String value) {
        if (!mqttEnabled.get() || mqttClient == null) {
            logger.warn("MQTT is not enabled. Command not sent: device={}, command={}, value={}",
                    deviceId, command, value);
            return false;
        }

        try {
            if (!mqttClient.isConnected()) {
                logger.warn("MQTT client disconnected. Attempting to reconnect...");
                try {
                    mqttClient.reconnect();
                } catch (MqttException e) {
                    logger.error("Failed to reconnect to MQTT broker", e);
                    mqttEnabled.set(false);
                    return false;
                }
            }

            Map<String, Object> payload = new HashMap<>();
            String relayKey = "relay" + deviceId;
            payload.put(relayKey, value);
            payload.put("clientId", "BackendServer");

            String jsonPayload = objectMapper.writeValueAsString(payload);
            mqttClient.publish(topic, new MqttMessage(jsonPayload.getBytes()));
            logger.info("Successfully published message: {}", jsonPayload);
            return true;

        } catch (Exception e) {
            logger.error("Error publishing MQTT message", e);
            return false;
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("Disconnected from MQTT broker");
            }
        } catch (MqttException e) {
            logger.error("Error disconnecting MQTT client", e);
        }
    }

    public boolean isMqttEnabled() {
        return mqttEnabled.get();
    }
}