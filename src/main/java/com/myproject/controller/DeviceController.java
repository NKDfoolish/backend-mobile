package com.myproject.controller;

import com.myproject.service.MqttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final MqttService mqttService;

    public DeviceController(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @PostMapping("/{deviceId}/relay")
    public ResponseEntity<?> toggleRelay(
            @PathVariable int deviceId,
            @RequestParam String state) {

        // Validate state input
        if (!state.equals("ON") && !state.equals("OFF")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "State must be either ON or OFF"));
        }

        try {
            mqttService.publishMessage(deviceId, "relay", state);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Command sent successfully");
            response.put("deviceId", deviceId);
            response.put("state", state);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to send command: " + e.getMessage()));
        }
    }
}
