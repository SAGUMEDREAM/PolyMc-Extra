package cc.thonly.polymc_extra;

import cc.thonly.polymc_extra.util.LateRunnable;
import cc.thonly.polymc_extra.util.PolymerBlockHelper;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PolyMcExtra implements ModInitializer {
	public static final String MOD_ID = "polymc-extra";
	public static final List<Runnable> LATE_INIT = new ArrayList<>();

	@Override
	public void onInitialize() {
		PolymerBlockHelper.registers();
	}

}