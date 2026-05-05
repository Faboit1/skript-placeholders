package io.github.apickledwalrus.skriptplaceholders.util;

import io.github.apickledwalrus.skriptplaceholders.SkriptPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public final class SchedulerUtil {

	private SchedulerUtil() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static boolean isFolia() {
		try {
			Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
			return true;
		} catch (NoSuchMethodException ignored) {
			return false;
		}
	}

	public static void runMainThread(Runnable runnable) {
		if (Bukkit.isPrimaryThread()) {
			runnable.run();
			return;
		}

		if (isFolia()) {
			runFoliaGlobal(runnable);
		} else {
			Bukkit.getScheduler().runTask(SkriptPlaceholders.getInstance(), runnable);
		}
	}

	private static void runFoliaGlobal(Runnable runnable) {
		try {
			Method getGlobalRegionScheduler = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
			Object globalRegionScheduler = getGlobalRegionScheduler.invoke(Bukkit.getServer());

			Method execute = globalRegionScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
			execute.invoke(globalRegionScheduler, SkriptPlaceholders.getInstance(), runnable);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Failed to schedule task on Folia global region scheduler.", e);
		}
	}

}
