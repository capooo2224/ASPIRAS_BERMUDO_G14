package com.capocann.site12.tactical;

import com.capocann.site12.GameData;
import java.util.Map;

/**
 * Lightweight manager to bridge GameData/combatPanel and the tactical engine.
 */
public class TacticalManager {
    private final CombatEngine engine = new CombatEngine();
    private final GameData gameData;
    private final Map<String, GameData.CharacterStats> enemyStats;

    public static class TacticalResult {
        public int totalDamage;
        public String summary;
    }

    public TacticalManager(GameData gameData, Map<String, GameData.CharacterStats> enemyStats) {
        this.gameData = gameData;
        this.enemyStats = enemyStats;
    }

    // Apply a move from an ally (actorId) to an enemy (enemyId) using the MoveDef info.
    // Returns a summary result and mutates GameData / enemyStats accordingly.
    public TacticalResult applyMove(String actorId, String enemyId, Object moveDefObj) {
        TacticalResult out = new TacticalResult();

        if (actorId == null || enemyId == null) {
            out.totalDamage = 0;
            out.summary = "Invalid actor/target.";
            return out;
        }

        GameData.CharacterStats actorStats = gameData.getCharacterStats(actorId);
        GameData.CharacterStats targetStats = enemyStats.get(enemyId);
        if (actorStats == null || targetStats == null) {
            out.totalDamage = 0;
            out.summary = "Missing stats.";
            return out;
        }

        // Convert to tactical entities
        Entity attacker = new Entity(actorId, actorStats.getCharacterName(), Math.max(1, actorStats.getMaxHealth()));
        attacker.hp = actorStats.getCurrentHealth();
        attacker.sanity = actorStats.getCurrentSanity();
        attacker.hardened = false; // GameData currently doesn't track 'hardened'
        attacker.moraleStacks = actorStats.getStatusStacks(GameData.StatusEffect.MORALE);
        attacker.flatDamageBuff = 0;

        Entity target = new Entity(enemyId, targetStats.getCharacterName(), Math.max(1, targetStats.getMaxHealth()));
        target.hp = targetStats.getCurrentHealth();
        target.sanity = targetStats.getCurrentSanity();
        target.hardened = false;
        target.moraleStacks = targetStats.getStatusStacks(GameData.StatusEffect.MORALE);

        // Convert moveDefObj (from combatPanel) if it's a MoveDef-like object via reflectionless mapping
        Move move = new Move();
        try {
            // moveDefObj is expected to be combatPanel.MoveDef; use toString-ish mapping via fields by name
            java.lang.reflect.Field fName = moveDefObj.getClass().getDeclaredField("moveName");
            java.lang.reflect.Field fHits = moveDefObj.getClass().getDeclaredField("hitMultiplier");
            fName.setAccessible(true); fHits.setAccessible(true);
            String name = (String) fName.get(moveDefObj);
            int hits = (int) fHits.get(moveDefObj);
            move.name = name;
            move.hits = Math.max(1, hits);
            // Simple damage bounds based on hits
            move.minDamage = 6 * move.hits;
            move.maxDamage = 18 * move.hits;
            move.damageType = Move.DamageType.PHYSICAL;
        } catch (Exception ex) {
            // fallback defaults
            move.name = "Basic Strike";
            move.hits = 1;
            move.minDamage = 6;
            move.maxDamage = 12;
            move.damageType = Move.DamageType.PHYSICAL;
        }

        // record before-stacks
        int beforeBleed = target.bleedStacks;
        int beforeBurn = target.burnStacks;

        CombatEngine.Result res = engine.executeMove(attacker, target, move);

        // Apply damage to GameData enemy stats
        int damage = res.totalDamage;
        targetStats.takeDamage(damage);

        // Apply status differences (bleed/burn)
        int deltaBleed = Math.max(0, target.bleedStacks - beforeBleed);
        int deltaBurn = Math.max(0, target.burnStacks - beforeBurn);
        if (deltaBleed > 0) targetStats.addStatusStacks(GameData.StatusEffect.BLEED, deltaBleed);
        if (deltaBurn > 0) targetStats.addStatusStacks(GameData.StatusEffect.FIRE, deltaBurn);

        out.totalDamage = damage;
        out.summary = move.name + " dealt " + damage + " damage (" + res.perHit.size() + " hits).";
        return out;
    }
}
