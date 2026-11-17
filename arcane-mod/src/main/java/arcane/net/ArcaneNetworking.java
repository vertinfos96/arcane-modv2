package arcane.net;

import arcane.data.ArcaneLogic;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ArcaneNetworking {
	private ArcaneNetworking() {
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(SyncPayload.ID, SyncPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(UpgradePayload.ID, UpgradePayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(UpgradePayload.ID, (payload, context) ->
				ArcaneLogic.upgrade(context.player(), payload.skill()));
	}
}
