package cc.thonly.polymc_extra;

import cc.thonly.polymc_extra.command.PolymerExtraCommands;
import cc.thonly.polymc_extra.util.ExtraModelType;
import cc.thonly.polymc_extra.util.PolymerBlockHelper;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PolyMcExtra implements ModInitializer {
	public static final String MOD_ID = "polymc-extra";
	public static final List<Runnable> LATE_INIT = new ArrayList<>();

	@Override
	public void onInitialize() {
        try {
            Class.forName(ExtraModelType.class.getName());
        } catch (Exception e) {
            log.error("Can't init ExtraModelType", e);
        }
        PolymerBlockHelper.registers();
		PolymerExtraCommands.bootstrap();
	}

}