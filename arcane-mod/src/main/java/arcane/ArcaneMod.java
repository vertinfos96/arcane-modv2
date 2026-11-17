package arcane;

import arcane.data.ArcaneLogic;
import arcane.entity.ModEntities;
import arcane.item.ModItems;
import arcane.net.ArcaneNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ArcaneMod implements ModInitializer {
	public static final String MOD_ID = "arcane";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
		ArcaneNetworking.register();
		ArcaneLogic.registerEvents();
	}
}
