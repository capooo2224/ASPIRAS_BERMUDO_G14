package com.capocann.site12.tactical;

public class Move {
    public enum DamageType { BULLET, PHYSICAL, BLEED, BURN }

    public String id;
    public String name;
    public DamageType damageType;
    public int hits = 1;
    public int minDamage = 1;
    public int maxDamage = 1;
    public int cost = 0;
    public String effect = "";

    public Move() {}

    public String toString() {
        return name+"("+damageType+" x"+hits+" dmg="+minDamage+"-"+maxDamage+")";
    }
}
