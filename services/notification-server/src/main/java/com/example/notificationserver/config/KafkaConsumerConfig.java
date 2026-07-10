package com.example.notificationserver.config;

import com.example.common_lib.payload.event.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class.getName());
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.example.common_lib.payload.event");
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, targetType.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> factoryFor(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> userRegisteredFactory() {
        return factoryFor(UserRegisteredEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailVerificationEvent> emailVerificationFactory() {
        return factoryFor(EmailVerificationEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PasswordResetEvent> passwordResetFactory() {
        return factoryFor(PasswordResetEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SongUploadedEvent> songUploadedFactory() {
        return factoryFor(SongUploadedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AlbumCreatedEvent> albumCreatedFactory() {
        return factoryFor(AlbumCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PlaylistCreatedEvent> playlistCreatedFactory() {
        return factoryFor(PlaylistCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SongAddedToPlaylistEvent> songAddedToPlaylistFactory() {
        return factoryFor(SongAddedToPlaylistEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AlbumDeletedEvent> albumDeletedFactory() {
        return factoryFor(AlbumDeletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SongDeletedEvent> songDeletedFactory() {
        return factoryFor(SongDeletedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PlaylistDeletedEvent> playlistDeletedFactory() {
        return factoryFor(PlaylistDeletedEvent.class);
    }
}