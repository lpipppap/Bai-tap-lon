package com.auction.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Timer {
    public static void timer(Label label, LocalDateTime endTime) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long duration = ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);

            if (duration > 0 && duration <= 300) label.setTextFill(Color.RED);
            else if (duration <= 0) {
                label.setText("AUCTION ENDED");
                return;
            }

            long hours = duration / 3600;
            long minutes = (duration % 3600) / 60;
            long seconds = duration % 60;
            String stringTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            label.setText("Time left: " + stringTime);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
