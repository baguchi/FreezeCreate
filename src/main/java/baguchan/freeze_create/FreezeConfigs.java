package baguchan.freeze_create;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FreezeConfigs {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue rottenDay;

        public Common(ForgeConfigSpec.Builder builder) {
            rottenDay = builder
                    .comment("It will rot on the set date.")
                    .defineInRange("Setting rotten day", 10, 3, 100);
        }
    }

}