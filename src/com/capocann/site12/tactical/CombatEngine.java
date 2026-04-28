package com.capocann.site12.tactical;

import java.util.*;

public class CombatEngine {
    private final Random rng = new Random();

    public static class Result {
        public int totalDamage;
        public List<Integer> perHit = new ArrayList<>();
    }

    // Execute a move from attacker to target, applying multi-hit and stress modifiers
    public Result executeMove(Entity attacker, Entity target, Move move) {
        Result r = new Result();
        double moralePenalty = 1.0 - (0.05 * attacker.moraleStacks); // stacks reduce stats
        for (int i=0;i<move.hits;i++) {
            int base = move.minDamage;
            if (move.maxDamage > move.minDamage) base = move.minDamage + rng.nextInt(move.maxDamage - move.minDamage + 1);
            int dmg = base + attacker.flatDamageBuff; // flat buff applies to each hit

            // Apply morale penalty as percentage
            dmg = (int) Math.round(dmg * moralePenalty);

            // Sanity effects on the target
            if (!target.hardened) {
                if (target.sanity > 10) { // Level 1
                    dmg += 5; // target receives +5 damage on all hits
                    // attacker outgoing could be reduced elsewhere; we do not mutate attacker here
                } else if (target.sanity <= 0) { // Level 2 - Insane
                    dmg += 20;
                    // cannot gain energy: caller should check target.hardened/sanity before restoring energy
                }
            }

            // Damage type special handling
            if (move.damageType == Move.DamageType.BLEED) {
                // immediate hit: 1 per hit already covered by dmg ranges; add stacking
                target.addBleed(1);
            } else if (move.damageType == Move.DamageType.BURN) {
                target.addBurn(1);
            }

            // apply damage
            target.applyDamage(dmg);
            r.perHit.add(dmg);
            r.totalDamage += dmg;
            if (!target.isAlive()) break;
        }
        return r;
    }

    // Utility: compute expected damage (no state change)
    public int estimateTotalDamage(Entity attacker, Entity target, Move move) {
        int sum = 0;
        double moralePenalty = 1.0 - (0.05 * attacker.moraleStacks);
        for (int i=0;i<move.hits;i++) {
            int base = (move.minDamage + move.maxDamage) / 2;
            int dmg = (int)Math.round((base + attacker.flatDamageBuff) * moralePenalty);
            if (!target.hardened) {
                if (target.sanity > 10) dmg += 5;
                else if (target.sanity <= 0) dmg += 20;
            }
            sum += dmg;
        }
        return sum;
    }
}
