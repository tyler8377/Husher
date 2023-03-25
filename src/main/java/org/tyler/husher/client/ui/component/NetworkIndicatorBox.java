package org.tyler.husher.client.ui.component;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class NetworkIndicatorBox extends HBox {

    public NetworkIndicatorBox(double size, int bars, double barWidth, int spacing, Paint color) {
        this.setMaxHeight(size);
        this.setMinHeight(size);
        this.setMaxWidth(size);
        this.setMinWidth(size);
        this.setSpacing(spacing);
        this.setAlignment(Pos.BOTTOM_CENTER);
        // this.setStyle("-fx-background-color: red;");

        Rectangle[] rectangles = new Rectangle[bars];

        for (int i = 0; i < bars; i++) {
            // rectangles[i] = new Rectangle(size / bars - barSpacing, size, color);
            rectangles[i] = new Rectangle(barWidth / bars, size * ((i + 1) / (bars * 1.0)), color);
        }

        this.getChildren().addAll(rectangles);
    }

}
