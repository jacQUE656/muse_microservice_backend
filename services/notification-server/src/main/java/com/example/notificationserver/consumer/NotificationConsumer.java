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

    @KafkaListener(
            topics = KafkaTopics.USER_REGISTERED,
            groupId = "notification-service",
            containerFactory = "userRegisteredFactory")
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

    @KafkaListener(
            topics = KafkaTopics.EMAIL_VERIFICATION,
            groupId = "notification-service",
            containerFactory = "emailVerificationFactory")
    public void onEmailVerification(EmailVerificationEvent event) {
        log.info("Email verification event: {}", event.getEmail());

        emailService.sendHtml(event.getEmail(),
                "Verify your Muse account 📧",
                templates.emailVerification(
                        event.getFirstName(), event.getVerificationLink()));
    }

    @KafkaListener(
            topics = KafkaTopics.PASSWORD_RESET,
            groupId = "notification-service",
            containerFactory = "passwordResetFactory")
    public void onPasswordReset(PasswordResetEvent event) {
        log.info("Password reset event: {}", event.getEmail());

        emailService.sendHtml(event.getEmail(),
                "Reset your Muse password 🔐",
                templates.passwordReset(
                        event.getFirstName(), event.getResetLink()));
    }

    @KafkaListener(
            topics = KafkaTopics.SONG_UPLOADED,
            groupId = "notification-service",
            containerFactory = "songUploadedFactory")
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

    @KafkaListener(
            topics = KafkaTopics.ALBUM_CREATED,
            groupId = "notification-service",
            containerFactory = "albumCreatedFactory")
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

    @KafkaListener(
            topics = KafkaTopics.PLAYLIST_CREATED,
            groupId = "notification-service",
            containerFactory = "playlistCreatedFactory")
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

    @KafkaListener(
            topics = KafkaTopics.SONG_ADDED_TO_PLAYLIST,
            groupId = "notification-service",
            containerFactory = "songAddedToPlaylistFactory")
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

    @KafkaListener(
            topics = KafkaTopics.ALBUM_DELETED,
            groupId = "notification-service",
            containerFactory = "albumDeletedFactory")
    public void onAlbumDeleted(AlbumDeletedEvent event) {
        log.info("Album deleted event: {}", event.getAlbumName());

        emailService.sendHtml(event.getCreatorEmail(),
                "Album deleted 🗑️",
                templates.albumDeleted(event.getAlbumName()));

        inAppService.save(event.getDeletedByUserId(),
                "Album deleted",
                "Your album \"" + event.getAlbumName() + "\" has been deleted.");

        pushService.sendPush(event.getFcmToken(),
                "Album deleted",
                "\"" + event.getAlbumName() + "\" has been removed from Muse.");
    }

    @KafkaListener(
            topics = KafkaTopics.SONG_DELETED,
            groupId = "notification-service",
            containerFactory = "songDeletedFactory")
    public void onSongDeleted(SongDeletedEvent event) {
        log.info("Song deleted event: {}", event.getSongName());

        emailService.sendHtml(event.getUploaderEmail(),
                "Song deleted 🗑️",
                templates.songDeleted(event.getSongName()));

        inAppService.save(event.getDeletedByUserId(),
                "Song deleted",
                "\"" + event.getSongName() + "\" has been deleted.");

        pushService.sendPush(event.getFcmToken(),
                "Song deleted",
                "\"" + event.getSongName() + "\" has been removed from Muse.");
    }

    @KafkaListener(
            topics = KafkaTopics.PLAYLIST_DELETED,
            groupId = "notification-service",
            containerFactory = "playlistDeletedFactory")
    public void onPlaylistDeleted(PlaylistDeletedEvent event) {
        log.info("Playlist deleted event: {}", event.getPlaylistName());

        emailService.sendHtml(event.getCreatorEmail(),
                "Playlist deleted 🗑️",
                templates.playlistDeleted(event.getPlaylistName()));

        inAppService.save(event.getDeletedByUserId(),
                "Playlist deleted",
                "Your playlist \"" + event.getPlaylistName() + "\" has been deleted.");

        pushService.sendPush(event.getFcmToken(),
                "Playlist deleted",
                "\"" + event.getPlaylistName() + "\" has been removed from Muse.");
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_SUCCESS,
            groupId = "notification-service",
            containerFactory = "paymentSuccessFactory")
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        log.info("Payment success event: user={}, plan={}", event.getUserId(), event.getPlanId());

        emailService.sendHtml(event.getEmail(),
                "You're now Muse " + event.getPlanId() + "! 🎉",
                templates.premiumActivated(
                        event.getFirstName(),
                        event.getPlanId(),
                        event.getBillingCycle(),
                        event.getAmount(),
                        event.getCurrency()));

        inAppService.save(event.getUserId(),
                "Welcome to Premium!",
                "Your " + event.getPlanId() + " plan is now active.");

        pushService.sendPush(event.getFcmToken(),
                "You're Premium now! 🎉",
                "Enjoy ad-free listening and offline downloads.");
    }
}