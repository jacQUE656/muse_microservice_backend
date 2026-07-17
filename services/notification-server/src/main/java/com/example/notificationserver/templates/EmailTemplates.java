package com.example.notificationserver.templates;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplates {

    private String wrap(String content) {
        return """
                <div style="font-family:Arial,sans-serif;max-width:600px;
                            margin:auto;padding:20px;border:1px solid #eee;
                            border-radius:8px;">
                    %s
                    <hr style="border:none;border-top:1px solid #eee;margin-top:30px"/>
                    <p style="color:#999;font-size:12px;text-align:center">
                        © Muse Music Platform
                    </p>
                </div>
                """.formatted(content);
    }

    public String welcome(String firstName) {
        return wrap("""
                <h2 style="color:#6C63FF">Welcome to Muse 🎵</h2>
                <p>Hi <strong>%s</strong>,</p>
                <p>Your account has been created. Start exploring music!</p>
                """.formatted(firstName));
    }

    public String emailVerification(String firstName, String link) {
        return wrap("""
                <h2 style="color:#6C63FF">Verify your email 📧</h2>
                <p>Hi <strong>%s</strong>, please verify your email:</p>
                <a href="%s" style="display:inline-block;padding:12px 24px;
                   background:#6C63FF;color:white;text-decoration:none;
                   border-radius:6px;margin:16px 0">Verify Email</a>
                <p style="color:#999;font-size:12px">Expires in 24 hours.</p>
                """.formatted(firstName, link));
    }

    public String passwordReset(String firstName, String link) {
        return wrap("""
                <h2 style="color:#6C63FF">Reset your password 🔐</h2>
                <p>Hi <strong>%s</strong>, click below to reset your password:</p>
                <a href="%s" style="display:inline-block;padding:12px 24px;
                   background:#6C63FF;color:white;text-decoration:none;
                   border-radius:6px;margin:16px 0">Reset Password</a>
                <p style="color:#999;font-size:12px">Expires in 1 hour.</p>
                """.formatted(firstName, link));
    }

    public String songUploaded(String songName) {
        return wrap("""
                <h2 style="color:#6C63FF">Your song is live! 🎶</h2>
                <p>Your song <strong>%s</strong> is now available on Muse.</p>
                """.formatted(songName));
    }

    public String albumCreated(String albumName) {
        return wrap("""
                <h2 style="color:#6C63FF">Album created! 💿</h2>
                <p>Your album <strong>%s</strong> is live on Muse.</p>
                """.formatted(albumName));
    }

    public String playlistCreated(String playlistName) {
        return wrap("""
                <h2 style="color:#6C63FF">Playlist ready! 🎧</h2>
                <p>Your playlist <strong>%s</strong> is ready to fill with songs.</p>
                """.formatted(playlistName));
    }

    public String songAddedToPlaylist(String songName, String playlistName) {
        return wrap("""
                <h2 style="color:#6C63FF">Song added! 🎵</h2>
                <p><strong>%s</strong> was added to your playlist <strong>%s</strong>.</p>
                """.formatted(songName, playlistName));
    }

    public String albumDeleted(String albumName) {
        return wrap("""
                <h2 style="color:#E15554">Album deleted 🗑️</h2>
                <p>Your album <strong>%s</strong> has been permanently deleted.</p>
                """.formatted(albumName));
    }

    public String songDeleted(String songName) {
        return wrap("""
                <h2 style="color:#E15554">Song deleted 🗑️</h2>
                <p>Your song <strong>%s</strong> has been permanently deleted.</p>
                """.formatted(songName));
    }

    public String playlistDeleted(String playlistName) {
        return wrap("""
                <h2 style="color:#E15554">Playlist deleted 🗑️</h2>
                <p>Your playlist <strong>%s</strong> has been permanently deleted.</p>
                """.formatted(playlistName));
    }

    // amount is in the smallest currency unit (cents), same convention as
    // PaymentOrder.amount / PaymentSuccessEvent.amount — formatted here as
    // a decimal for display, not stored or recomputed anywhere.
    public String premiumActivated(String firstName, String planId, String billingCycle, Long amount, String currency) {
        String planName = capitalize(planId);
        String cycleLabel = "annual".equals(billingCycle) ? "yearly" : "monthly";
        String priceDisplay = "%s %.2f".formatted(currency, amount / 100.0);

        return wrap("""
                <h2 style="color:#6C63FF">You're Premium now! 🎉</h2>
                <p>Hi <strong>%s</strong>,</p>
                <p>Your <strong>%s</strong> plan is now active — billed %s at <strong>%s</strong>.</p>
                <p>Enjoy ad-free listening, offline downloads, and full control over your queue.</p>
                """.formatted(firstName, planName, cycleLabel, priceDisplay));
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) return value;
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase();
    }
}