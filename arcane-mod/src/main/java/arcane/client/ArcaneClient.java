package arcane.client;

import arcane.ArcaneMod;
import arcane.data.ArcaneData;
import arcane.entity.ModEntities;
import arcane.net.SyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ArcaneClient implements ClientModInitializer {

	/** Последние данные прокачки, полученные от сервера. */
	public static ArcaneData data = ArcaneData.EMPTY;

	private static KeyBinding skillsKey;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.CRYSTAL_GOLEM, CrystalGolemRenderer::new);
		EntityRendererRegistry.register(ModEntities.ARCANE_SKELETON, ArcaneSkeletonRenderer::new);
		EntityRendererRegistry.register(ModEntities.ARCANE_WARDEN, ArcaneWardenRenderer::new);

		skillsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.arcane.skills", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.arcane"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (skillsKey.wasPressed()) {
				if (client.player != null && client.currentScreen == null) {
					client.setScreen(new SkillScreen());
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SyncPayload.ID, (payload, context) ->
				data = new ArcaneData(payload.xp(), payload.ranks(), 0));
	}
}
