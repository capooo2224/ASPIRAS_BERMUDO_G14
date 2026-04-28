package com.capocann.site12.tactical;

public class StatusProcessor {
    // Apply bleed and burn end-of-turn effects
    public void processEndOfTurn(Entity e) {
        if (e.bleedStacks > 0) {
            int bleedDamagePerStack = 2; // spec: Bleed adds +2 stacking damage at end of every turn
            int total = e.bleedStacks * bleedDamagePerStack;
            e.applyDamage(total);
        }
        if (e.burnStacks > 0) {
            int burnDamageFlat = 5; // spec: Burn +5 flat damage at end of every turn
            int total = e.burnStacks * burnDamageFlat;
            e.applyDamage(total);
        }
    }
}
