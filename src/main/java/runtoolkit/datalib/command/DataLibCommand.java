package runtoolkit.datalib.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class DataLibCommand implements ModInitializer {

    public static final String MOD_ID = "datalib-command";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[DataLib] Command module initialised.");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // /datalib  (and alias /dl)
            var root = literal("datalib")
                .requires(src -> src.hasPermissionLevel(2))

                .then(literal("version")
                    .executes(ctx -> sendVersion(ctx)))

                .then(literal("reload")
                    .executes(ctx -> triggerReload(ctx)))

                .then(literal("debug")
                    .executes(ctx -> toggleDebug(ctx)))

                .executes(ctx -> sendVersion(ctx));

            dispatcher.register(root);
            dispatcher.register(literal("dl").redirect(dispatcher.getRoot().getChild("datalib")));
        });
    }

    private int sendVersion(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().sendFeedback(
            () -> Text.literal("[DataLib] v5.1.2 — runtoolkit/dataLib-FabricMod"),
            false
        );
        return 1;
    }

    private int triggerReload(CommandContext<ServerCommandSource> ctx) {
        // Delegates to vanilla /reload; datapack engine re-runs dl_load:_
        ctx.getSource().getServer().reloadResources(
            ctx.getSource().getServer().getDataPackManager().getEnabledNames()
        );
        ctx.getSource().sendFeedback(
            () -> Text.literal("[DataLib] Reload triggered."),
            true
        );
        return 1;
    }

    private int toggleDebug(CommandContext<ServerCommandSource> ctx) {
        // Runs the datapack-side debug toggle function
        ctx.getSource().getServer().getCommandManager().getDispatcher()
            .execute("function datalib:debug", ctx.getSource().withLevel(4));
        ctx.getSource().sendFeedback(
            () -> Text.literal("[DataLib] Debug toggled — check datalib:debug storage."),
            false
        );
        return 1;
    }
}
