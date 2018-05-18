package com.ragegamingpe.wtfenchants.common.lib;

import com.ragegamingpe.wtfenchants.common.block.BlockBookshelf;
import com.ragegamingpe.wtfenchants.common.block.BlockDisenchantment;
import com.ragegamingpe.wtfenchants.common.block.BlockSorter;
import com.ragegamingpe.wtfenchants.common.block.BlockXpStore;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks
{
    public static final List<ModBlock> ALL_BLOCKS = new ArrayList<>();

    public static final ModBlock BOOKSHELF;
    public static final ModBlock SORTER;
    public static final ModBlock DISENCHANTING_TABLE;
    public static final ModBlock XP_STORE;

    static {
        BOOKSHELF = new BlockBookshelf();
        SORTER = new BlockSorter();
        DISENCHANTING_TABLE = new BlockDisenchantment();
        XP_STORE = new BlockXpStore();
    }
}
