package com.black_dog20.friendlygriefing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(FriendlyGriefing.MOD_ID)
public class FriendlyGriefing {

    public static final String MOD_ID = "friendlygriefing";
    private static final Logger LOGGER = LogManager.getLogger();

    public FriendlyGriefing() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-server.toml"));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMobGriefing(EntityMobGriefingEvent event){
        Entity entity = event.getEntity();
        if (entity.getLevel().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }

        Optional<String> entityId = Optional.of(entity)
                .map(Entity::getType)
                .map(ForgeRegistries.ENTITIES::getKey)
                .map(ResourceLocation::toString);

        if (entityId.isPresent() && Config.FRIENDLY_GRIEFING_MOBS.get().contains(entityId.get())) {
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MOD_ID, "network"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static Logger getLogger() {
        return LOGGER;
    }
}
