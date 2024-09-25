/*
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shercargo.testkafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SpringBootApplication
@EnableScheduling
public class MultipleFunctionsApplication {

    private final StreamBridge streamBridge;

    public MultipleFunctionsApplication(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public static void main(String[] args) {
        SpringApplication.run(MultipleFunctionsApplication.class, args);
    }

    @ServiceActivator(inputChannel = "metadata")
    public void metaData(Message<?> message) {
        System.out.println(
                "Send event: " + message.getHeaders().get(KafkaHeaders.RECORD_METADATA, RecordMetadata.class));
    }

//    @Bean
//    public Supplier<String> source1() {
//        return () -> {
//            String message = "FromSource1";
//            System.out.println("******************");
//            System.out.println("From Source1");
//            System.out.println("******************");
//            System.out.println("Sending value: " + message);
//            throw new RuntimeException("123");
//
//        };
//    }

    @Scheduled(fixedDelay = 3000)
    public void send() {
        this.streamBridge.send("source1-out-0",
                MessageBuilder.withPayload("123").setHeader(KafkaHeaders.KEY, 1L).build());
    }

    @Bean
    public Consumer<ErrorMessage> errorHandle() {
        return errorMessage -> {
            System.out.println("Error message received: " + errorMessage);
        };
    }

}
