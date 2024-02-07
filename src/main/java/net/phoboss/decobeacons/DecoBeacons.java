package net.phoboss.decobeacons;

import net.fabricmc.api.ModInitializer;

import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.ModBlocks;
import net.phoboss.decobeacons.items.ModItemGroups;
import net.phoboss.decobeacons.items.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoBeacons implements ModInitializer {
	public static final String MOD_ID = "decobeacons";
public static final Logger LOGGER = LoggerFactory.getLogger("decobeacons");

	@Override
	public void onInitialize() {
		ModItemGroups.registerAll();
		ModBlocks.registerAll();
		ModBlockEntities.registerAll();
		ModItems.registerAll();
	}
}