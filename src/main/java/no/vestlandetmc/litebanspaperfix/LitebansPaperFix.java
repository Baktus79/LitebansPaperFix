package no.vestlandetmc.litebanspaperfix;

import litebans.api.Entry;
import litebans.api.Events;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public final class LitebansPaperFix extends JavaPlugin {

	@Override
	public void onEnable() {
		Events.get().register(new Events.Listener() {

			@Override
			public void entryAdded(Entry e) {
				final String type = e.getType();
				if (type == null) return;

				switch (type) {
					case "ban":
						forceKick(e, true);
						break;
					case "kick":
						forceKick(e, false);
						break;
				}
			}
		});

		getLogger().info("Registrerte LiteBans events.");
	}

	private void forceKick(Entry e, boolean fromBan) {
		final String reason = Optional.ofNullable(e.getReason()).filter(s -> !s.isBlank()).orElse(fromBan ? "Du er bannet!" : "Kicked!");
		final UUID uuid = UUID.fromString(e.getUuid());

		getServer().getScheduler().runTask(this, () -> {
			final Player target = Bukkit.getPlayer(uuid);
			final Component reasonAdventure = Component.text(reason).color(NamedTextColor.RED);
			final Component ban = Component.text("Du ble utestengt fra Vestlandet.").color(NamedTextColor.RED);

			if (target != null && target.isOnline()) {
				target.kick(ban.appendNewline().append(reasonAdventure));
				getLogger().info("Force-kick er utført på " + target.getName() + " (" + (fromBan ? "ban" : "kick") + ").");
			}
		});
	}

}
