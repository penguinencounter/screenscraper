package org.penguinencounter.screenscraper.mixin;

import dan200.computercraft.shared.computer.terminal.TerminalState;
import io.netty.buffer.ByteBuf;
import org.penguinencounter.screenscraper.quack.TSDuck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TerminalState.class)
public abstract class TSExpander implements TSDuck {

    @Accessor
    @Override
    public abstract ByteBuf getBuffer();
}
