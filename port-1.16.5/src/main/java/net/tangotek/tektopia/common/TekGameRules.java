package net.tangotek.tektopia.common;

import java.lang.reflect.Method;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.tangotek.tektopia.TekTopiaPort;

public final class TekGameRules {
    public static final GameRules.RuleKey<GameRules.BooleanValue> VILLAGER_ITEMS =
            GameRules.register("villagerItems", GameRules.Category.MOBS, booleanRule(false));
    public static final GameRules.RuleKey<GameRules.IntegerValue> VILLAGER_SKILL_RATE =
            GameRules.register("villagerSkillRate", GameRules.Category.MOBS, integerRule(100));
    public static final GameRules.RuleKey<GameRules.IntegerValue> VILLAGE_RADIUS =
            GameRules.register("villageRadius", GameRules.Category.MOBS, integerRule(100));
    public static final GameRules.RuleKey<GameRules.IntegerValue> VILLAGER_PEN_PERCENT =
            GameRules.register("villagerPenPercent", GameRules.Category.MOBS, integerRule(100));
    public static final GameRules.RuleKey<GameRules.BooleanValue> QOL_CRAFT_PRIORITIZE_NEED =
            GameRules.register("tektopiaCraftPrioritizeNeed", GameRules.Category.MOBS, booleanRule(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> QOL_CRAFT_PERSONAL_LIMIT =
            GameRules.register("tektopiaCraftPersonalLimit", GameRules.Category.MOBS, booleanRule(true));
    public static final GameRules.RuleKey<GameRules.BooleanValue> HYBRID_RAIDS =
            GameRules.register("tektopiaHybridRaids", GameRules.Category.MOBS, booleanRule(true));

    private TekGameRules() {
    }

    public static void bootstrap() {
        TekTopiaPort.LOGGER.info("TekTopia gamerules registered: villagerItems, villagerSkillRate, villageRadius, villagerPenPercent, tektopiaCraftPrioritizeNeed, tektopiaCraftPersonalLimit, tektopiaHybridRaids");
    }

    public static boolean villagerItems(ServerWorld level) {
        return level.getGameRules().getBoolean(VILLAGER_ITEMS);
    }

    public static int villagerSkillRate(ServerWorld level) {
        return Math.max(0, level.getGameRules().getInt(VILLAGER_SKILL_RATE));
    }

    public static int villageRadius(ServerWorld level) {
        return Math.max(8, level.getGameRules().getInt(VILLAGE_RADIUS));
    }

    public static int villagerPenPercent(ServerWorld level) {
        return Math.max(0, level.getGameRules().getInt(VILLAGER_PEN_PERCENT));
    }

    public static boolean prioritizeCraftNeed(ServerWorld level) {
        return level.getGameRules().getBoolean(QOL_CRAFT_PRIORITIZE_NEED);
    }

    public static boolean enforceCraftPersonalLimit(ServerWorld level) {
        return level.getGameRules().getBoolean(QOL_CRAFT_PERSONAL_LIMIT);
    }

    public static boolean hybridRaids(ServerWorld level) {
        return level.getGameRules().getBoolean(HYBRID_RAIDS);
    }

    @SuppressWarnings("unchecked")
    private static GameRules.RuleType<GameRules.BooleanValue> booleanRule(boolean defaultValue) {
        try {
            Method method = findRuleFactory(GameRules.BooleanValue.class, boolean.class, "create", "func_223571_a");
            return (GameRules.RuleType<GameRules.BooleanValue>) method.invoke(null, defaultValue);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to register TekTopia boolean gamerule", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static GameRules.RuleType<GameRules.IntegerValue> integerRule(int defaultValue) {
        try {
            Method method = findRuleFactory(GameRules.IntegerValue.class, int.class, "create", "func_223562_a");
            return (GameRules.RuleType<GameRules.IntegerValue>) method.invoke(null, defaultValue);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to register TekTopia integer gamerule", ex);
        }
    }

    private static Method findRuleFactory(Class<?> owner, Class<?> parameterType, String... names) throws NoSuchMethodException {
        for (String name : names) {
            try {
                Method method = owner.getDeclaredMethod(name, parameterType);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }
        throw new NoSuchMethodException(owner.getName());
    }
}
