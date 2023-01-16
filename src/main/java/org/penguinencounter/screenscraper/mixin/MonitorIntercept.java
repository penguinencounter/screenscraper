package org.penguinencounter.screenscraper.mixin;

import dan200.computercraft.client.platform.AbstractClientNetworkContext;
import dan200.computercraft.shared.computer.terminal.TerminalState;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import org.penguinencounter.screenscraper.ScreenScraperClient;
import org.penguinencounter.screenscraper.TextOnlyPackage;
import org.penguinencounter.screenscraper.quack.TSDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractClientNetworkContext.class)
public abstract class MonitorIntercept {
    @Inject(
            method = "handleMonitorData",
            at = @At(
                    value = "INVOKE",
                    target = "Ldan200/computercraft/shared/peripheral/monitor/MonitorBlockEntity;read(Ldan200/computercraft/shared/computer/terminal/TerminalState;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void saveMonitorData(BlockPos pos, TerminalState terminal, CallbackInfo ci) {
        ByteBuf buffer = ((TSDuck) terminal).getBuffer().copy();
        ScreenScraperClient.LOG_MAIN.info("capture " + terminal.width + " x " + terminal.height + " monitor at " + pos.toString());
        TextOnlyPackage pack = TextOnlyPackage.import_(terminal.width, terminal.height, buffer);
        if (ScreenScraperClient.INSTANCE == null) {
            ScreenScraperClient.LOG_MAIN.error("ScreenScraperClient.INSTANCE is null!?");
            return;
        }
        if (ScreenScraperClient.INSTANCE.saveFile(pos, pack)) {
            ScreenScraperClient.LOG_MAIN.info("Saved monitor data");
        } else {
            ScreenScraperClient.LOG_MAIN.warn("Failed to save monitor data");
        }
    }
}
