package com.iafenvoy.rollable.render;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;

public class RenderHelper {
    public static BiConsumer<Integer, Integer> blankPixel(MatrixStack matrices) {
        return (x, y) -> {
            int color = 0xffffffff;
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix, (float) x, (float) y + 1, 0.0F).color(color).next();
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y + 1, 0.0F).color(color).next();
            bufferBuilder.vertex(matrix, (float) x + 1, (float) y, 0.0F).color(color).next();
            bufferBuilder.vertex(matrix, (float) x, (float) y, 0.0F).color(color).next();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        };
    }
}
