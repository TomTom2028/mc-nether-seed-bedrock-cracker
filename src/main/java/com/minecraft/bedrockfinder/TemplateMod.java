package com.minecraft.bedrockfinder;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("template-mod");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("foo_client")
				.executes(context -> {
					MinecraftClient client = MinecraftClient.getInstance();
					Chunk localChunk = client.world.getChunk(client.player.getBlockPos());
					if (localChunk instanceof WorldChunk) {
						WorldChunk worldChunk = (WorldChunk) localChunk;
						LOGGER.info("The command is executed in the client! The world time is " + worldChunk.getWorld().getTime());
						if(worldChunk.getWorld().getRegistryKey() != World.NETHER) {
							context.getSource().sendFeedback(Text.literal("this command needs to be executed in the nether!"));
							return -1;
						}
						StringBuilder sb = new StringBuilder();
						// get the bounds of the chunk
						int x_chunk = worldChunk.getPos().x;
						int z_chunk = worldChunk.getPos().z;
						ChunkSection topBottomSection = worldChunk.getSection(7);
						for(int x = 0; x < 16; x++) {
							for(int z = 0; z < 16; z++) {
								for (int y = 0; y < 16; y++) {
									Block block = topBottomSection.getBlockState(x, y, z).getBlock();
									if (block != Blocks.BEDROCK) {
										continue;
									}
									int x_real = x_chunk * 16 + x;
									int z_real = z_chunk * 16 + z;
									int y_real = y + 7 * 16;
									if (y_real > 124) {
										continue;
									}
									sb.append(x_real).append(" ").append(y_real).append(" ").append(z_real).append(" Bedrock\n");
									System.out.println(x_real + " " + y_real + " " + z_real + " Bedrock");
								}

							}
						}

						topBottomSection = worldChunk.getSection(0);
						for(int x = 0; x < 16; x++) {
							for(int z = 0; z < 16; z++) {
								for (int y = 0; y < 16; y++) {
									Block block = topBottomSection.getBlockState(x, y, z).getBlock();
									if (block != Blocks.BEDROCK) {
										continue;
									}
									int x_real = x_chunk * 16 + x;
									int z_real = z_chunk * 16 + z;
									int y_real = y;
									if (y_real < 3) {
										continue;
									}
									sb.append(x_real).append(" ").append(y_real).append(" ").append(z_real).append(" Bedrock\n");
									System.out.println(x_real + " " + y_real + " " + z_real + " Bedrock");
								}

							}
						}
						worldChunk.getBlockEntities().forEach((pos, entity) -> {
							LOGGER.info("BlockEntity: " + entity.getType().toString() + " at " + pos.toString());
						});
						// copy the locations to the clipboard with fabric methods
						client.keyboard.setClipboard(sb.toString());
						context.getSource().sendFeedback(Text.literal("found bedrock locations copied to clipboard!"));

					}
					return 1;
				}
				)));
	}
}