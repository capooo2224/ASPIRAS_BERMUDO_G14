package com.capocann.site12.tactical;

import java.util.*;

public class Entity {
    public String id;
    public String name;
    public int maxHp;
    public int hp;
    public int energy;
    public int sanity; // arbitrary numeric sanity value
    public int moraleStacks; // 0..6
    public boolean hardened;

    // status stacking
    public int bleedStacks;
    public int burnStacks;

    // flat damage buff applied to each hit (e.g., Terry Smith)
    public int flatDamageBuff;

    public Set<String> traits = new HashSet<>();

    public Entity() {}

    public Entity(String id, String name, int maxHp) {
        this.id = id; this.name = name; this.maxHp = maxHp; this.hp = maxHp;
    }

    public boolean isAlive() { return hp > 0; }

    public void applyDamage(int d) {
        hp -= d;
        if (hp < 0) hp = 0;
    }

    public void heal(int v) {
        hp += v; if (hp > maxHp) hp = maxHp;
    }

    public void addBleed(int stacks) { bleedStacks += stacks; }
    public void addBurn(int stacks) { burnStacks += stacks; }

    public void addMoraleStack(int s) { moraleStacks = Math.max(0, Math.min(6, moraleStacks + s)); }
    public void modifySanity(int delta) { if (!hardened) sanity += delta; }

    public String toString() {
        return name + "[HP="+hp+"/"+maxHp+",San="+sanity+",Mor="+moraleStacks+"]";
    }
}
