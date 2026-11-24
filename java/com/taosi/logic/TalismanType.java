package com.taosi.logic;

public enum TalismanType {
    EXPLOSION("explosion", true),
    SPEED("speed", false),
    TELEPORT("teleport", false),
    SWAP("swap", false),
    SLOW("slow", true),
    WEAKEN("weaken", true),
    INVISIBLE("invisible", false),
    SUMMON("summon", true),
    LIGHTNING("lightning", true),
    BARRIER("barrier", false);

    public final String id;
    /** true = nem duoc va dat duoc xuong dat (phim K) */
    public final boolean throwable;

    TalismanType(String id, boolean throwable) {
        this.id = id;
        this.throwable = throwable;
    }
}
