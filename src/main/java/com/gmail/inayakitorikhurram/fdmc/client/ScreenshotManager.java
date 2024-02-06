package com.gmail.inayakitorikhurram.fdmc.client;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.squareup.gifencoder.FloydSteinbergDitherer;
import com.squareup.gifencoder.GifEncoder;
import com.squareup.gifencoder.ImageOptions;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.FDMC_TEMP_FOLDER;
import static net.minecraft.client.util.ScreenshotRecorder.SCREENSHOTS_DIRECTORY;

public class ScreenshotManager {
    private MinecraftClient client;

    private ScreenshotStatus screenshotStatus;
    int screenshotSlice;
    public enum ScreenshotStatus{
        DISABLED,
        ENABLED,
        SAVING
    }
    private final File tempDir;
    private final File tempScreenshotDir;
    private final File outputDir;

    public ScreenshotManager(MinecraftClient client) {
        this.client = client;
        this.screenshotStatus = ScreenshotStatus.DISABLED;
        this.screenshotSlice = 0;
        tempDir = new File(this.client.runDirectory, FDMC_TEMP_FOLDER);
        tempScreenshotDir = new File(tempDir, SCREENSHOTS_DIRECTORY);
        outputDir = new File(this.client.runDirectory, "screenshots");
    }

    public boolean try4Screenshot(boolean placeWPressed){
        return switch (this.screenshotStatus) {
            case DISABLED -> {
                if (placeWPressed) {
                    this.clearDir();
                    this.screenshotStatus = ScreenshotStatus.ENABLED;
                    screenshotSlice=0;
                    this.try4Screenshot(false);
                    yield false;
                } else {
                    yield true;
                }
            }
            case ENABLED -> {
                if(placeWPressed) {
                    this.screenshotStatus = ScreenshotStatus.SAVING;
                    new Thread(this::dispatchScreenshotCreation).start();
                } else {
                    takeScreenshotSlice(screenshotSlice);
                }
                yield false;
            }
            case SAVING -> {
                if(placeWPressed) {
                    overlay(Text.of("Cannot start new screenshot while current is saving"));
                    yield false;
                } else {
                    yield true;
                }
            }
        };
    }

    private void clearDir() {
        if (tempScreenshotDir.exists()) {
            for (File file : tempScreenshotDir.listFiles()) {
                file.delete();
            }
            tempScreenshotDir.delete();
        }
    }

    private void send_message(Text text) {

    }

    private void overlay(Text text) {
        this.client.inGameHud.setOverlayMessage(text, false);
    }

    private void dispatchScreenshotCreation() {
        FDMCConfig config = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig();
        File typelessLocation = getScreenshotFilename(outputDir);
        var images = consumeFiles(tempScreenshotDir);
        if(images.isEmpty()) {
            this.client.inGameHud.getChatHud().addMessage(Text.of("Error: no images present"));
            this.screenshotStatus = ScreenshotStatus.DISABLED;
            return;
        }
        if(config.screenshot.png_enabled) {
            this.client.inGameHud.getChatHud().addMessage(Text.of("creating png..."));
            try {//png
                File png = buildScreenshotPng(images, typelessLocation);
                MutableText text = Text.literal(png.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, png.getAbsolutePath())));
                this.client.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.success", text));
            } catch (Exception e) {
                this.client.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.failure", e.getMessage()));
            }
        }
        if(config.screenshot.gif_enabled) {
            this.client.inGameHud.getChatHud().addMessage(Text.of("creating gif..."));
            try {//gif
                File gif = buildScreenshotGif(config, images, typelessLocation);
                MutableText text = Text.literal(gif.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, gif.getAbsolutePath())));
                this.client.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.success", text));
            } catch (Exception e) {
                this.client.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.failure", e.getMessage()));
            }
        }
        if(!config.screenshot.png_enabled && !config.screenshot.gif_enabled) {
            this.client.inGameHud.getChatHud().addMessage(Text.of("No screenshot taken. Enable png or gif in config"));
        }
        this.screenshotStatus = ScreenshotStatus.DISABLED;
    }

    private void takeScreenshotSlice(int slice){
        ScreenshotRecorder.saveScreenshot(
                tempDir,
                "screenshot_" + slice + ".png",
                this.client.getFramebuffer(),
                message -> this.client.execute(() -> {})
        );
        screenshotSlice++;
        this.overlay(
                Text.of(screenshotSlice + " slice/s screenshotted. Step and screenshot again or end with placeW + F2")
        );

    }

    private File buildScreenshotPng(ArrayList<int[][]> images, File outputLocation) throws IOException {
        File outputFile = new File(outputLocation.toString() + ".png");

        int tiles_x = (int)Math.floor(Math.sqrt(images.size()));
        int tiles_y = Math.floorDiv(images.size()-1, tiles_x) + 1;
        int tile_width = images.get(0)[0].length;
        int tile_height = images.get(0).length;
        int width = tiles_x * tile_width;
        int height = tiles_y * tile_height;

        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int image_index = 0; image_index < images.size(); image_index++) {
            int[][] image = images.get(image_index);
            int tile_x = image_index % tiles_x;
            int tile_y = Math.floorDiv(image_index, tiles_x);
            int offset_x = tile_x * tile_width;
            int offset_y = tile_y * tile_height;
            for(int i = 0; i < tile_width; i++) {
                for(int j = 0; j < tile_height; j++) {
                    finalImage.setRGB(
                            offset_x + i,
                            offset_y + j,
                            image[j][i]
                    );
                }
            }
        }

        ImageIO.write(finalImage, "png", outputFile);
        return outputFile;
    }

    private File buildScreenshotGif(FDMCConfig config, ArrayList<int[][]> images, File outputLocation) throws IOException{
        File outputFile = new File(outputLocation.toString() + ".gif");
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        ImageOptions options = new ImageOptions();
        options.setDelay(config.screenshot.gif_wait_time, TimeUnit.MILLISECONDS);
        options.setDitherer(FloydSteinbergDitherer.INSTANCE);

        GifEncoder encoder = new GifEncoder(outputStream, client.getWindow().getWidth(), client.getWindow().getHeight(), 0);
        for (var image : images) {
            encoder.addImage(image, options);
        }
        encoder.finishEncoding();
        outputStream.close();
        return outputFile;
    }

    private static int[][] convertImageToArray(File file) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(file);
        int[][] rgbArray = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                rgbArray[i][j] = bufferedImage.getRGB(j, i);
            }
        }
        return rgbArray;
    }

    private static ArrayList<int[][]> consumeFiles(File directory){
        int screenshotIndex = 0;
        ArrayList<int[][]> images = new ArrayList<>();
        try{
            while (true) {
                File imageFile = new File(directory + "/screenshot_" + screenshotIndex + ".png");
                int[][] image = convertImageToArray(imageFile);
                images.add(image);
                screenshotIndex++;
                imageFile.delete();
            }
        } catch (IOException ignored) {}
        directory.delete();
        return images;
    }

    private static File getScreenshotFilename(File directory) {
        String string = Util.getFormattedCurrentTime();
        int i = 1;
        File file;
        while ((file = new File(directory, string + (String)(i == 1 ? "" : "_" + i))).exists()) {
            ++i;
        }
        return file;
    }
}
