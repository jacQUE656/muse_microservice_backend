package com.example.notificationserver.consumer;


import com.example.common_lib.kafka.KafkaTopics;
import com.example.common_lib.payload.event.*;
import com.example.notificationserver.service.EmailService;
import com.example.notificationserver.service.InAppNotificationService;
import com.example.notificationserver.service.PushNotificationService;
import com.example.notificationserver.templates.EmailTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final PushNotificationService pushService;
    private final InAppNotificationService inAppService;
    private final EmailTemplates templates;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED,
            groupId = "notification-service")
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("User registered event: {}", event.getEmail());

        emailService.sendHtml(event.getEmail(),
                "Welcome to Muse! 🎵",
                templates.welcome(event.getFirstName()));

        inAppService.save(event.getUserId(),
                "Welcome to Muse!",
                "Your account is ready. Start exploring music!");

        pushService.sendPush(event.getFcmToken(),
                "Welcome to Muse! 🎵",
                "Hi " + event.getFirstName() + ", your account is ready!");
    }

    @KafkaListener(topics = KafkaTopics.EMAIL_VERIFICATION,
            groupId = "notification-service")
    public void onEmailVerification(EmailVerificationEvent event) {
        log.info("Email verification event: {}", event.getEmail());

        emailService.sendHtml(event.getEmail(),
                "Verify your Muse account 📧",
                templates.emailVerification(
                        event.getFirstName(), event.getVerificationLink()));
    }

    @KafkaListener(topics = KafkaTopics.PASSWORD_RESET,
            groupId = "notification-service")
    public void onPasswordReset(PasswordResetEvent event) {
        log.info("Password reset event: {}", event.getEmail());

        emailService.sendHtml(event.getEmail(),
                "Reset your Muse password 🔐",
                templates.passwordReset(
                        event.getFirstName(), event.getResetLink()));
    }

    @KafkaListener(topics = KafkaTopics.SONG_UPLOADED,
            groupId = "notification-service")
    public void onSongUploaded(SongUploadedEvent event) {
        log.info("Song uploaded event: {}", event.getSongName());

        emailService.sendHtml(event.getUploaderEmail(),
                "Your song is live! 🎶",
                templates.songUploaded(event.getSongName()));

        inAppService.save(event.getUploadedByUserId(),
                "Song uploaded!",
                "\"" + event.getSongName() + "\" is now live on Muse.");

        pushService.sendPush(event.getFcmToken(),
                "Song uploaded! 🎶",
                "\"" + event.getSongName() + "\" is now live.");
    }

    @KafkaListener(topics = KafkaTopics.ALBUM_CREATED,
            groupId = "notification-service")
    public void onAlbumCreated(AlbumCreatedEvent event) {
        log.info("Album created event: {}", event.getAlbumName());

        emailService.sendHtml(event.getCreatorEmail(),
                "Album created! 💿",
                templates.albumCreated(event.getAlbumName()));

        inAppService.save(event.getCreatedByUserId(),
                "Album created!",
                "Your album \"" + event.getAlbumName() + "\" is ready.");

        pushService.sendPush(event.getFcmToken(),
                "Album created! 💿",
                "\"" + event.getAlbumName() + "\" is live on Muse.");
    }

    @KafkaListener(topics = KafkaTopics.PLAYLIST_CREATED,
            groupId = "notification-service")
    public void onPlaylistCreated(PlaylistCreatedEvent event) {
        log.info("Playlist created event: {}", event.getPlaylistName());

        emailService.sendHtml(event.getCreatorEmail(),
                "Playlist ready! 🎧",
                templates.playlistCreated(event.getPlaylistName()));

        inAppService.save(event.getCreatedByUserId(),
                "Playlist created!",
                "Your playlist \"" + event.getPlaylistName() + "\" is ready.");

        pushService.sendPush(event.getFcmToken(),
                "Playlist ready! 🎧",
                "\"" + event.getPlaylistName() + "\" is ready to fill.");
    }

    @KafkaListener(topics = KafkaTopics.SONG_ADDED_TO_PLAYLIST,
            groupId = "notification-service")
    public void onSongAddedToPlaylist(SongAddedToPlaylistEvent event) {
        log.info("Song added to playlist event: {}", event.getSongName());

        emailService.sendHtml(event.getUserEmail(),
                "Song added to playlist! 🎵",
                templates.songAddedToPlaylist(
                        event.getSongName(), event.getPlaylistName()));

        inAppService.save(event.getUserId(),
                "Song added!",
                "\"" + event.getSongName() + "\" added to \""
                        + event.getPlaylistName() + "\".");

        pushService.sendPush(event.getFcmToken(),
                "Song added! 🎵",
                "\"" + event.getSongName() + "\" is in \""
                        + event.getPlaylistName() + "\".");
    }
}