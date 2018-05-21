package com.ragegamingpe.wtfenchants.common.lib;

import com.ragegamingpe.wtfenchants.common.block.*;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks
{
    public static final List<ModBlock> ALL_BLOCKS = new ArrayList<>();

    public static final ModBlock COMMON_BOOKSHELF;
    public static final ModBlock SORTER;
    public static final ModBlock DISENCHANTING_TABLE;
    public static final ModBlock XP_STORE;
    public static final ModBlock XP_FARMER;

    static {
        COMMON_BOOKSHELF = new BlockBookshelf();
        SORTER = new BlockSorter();
        DISENCHANTING_TABLE = new BlockDisenchantment();
        XP_STORE = new BlockXpStore();
        XP_FARMER = new BlockXpFarmer();
    }
}
