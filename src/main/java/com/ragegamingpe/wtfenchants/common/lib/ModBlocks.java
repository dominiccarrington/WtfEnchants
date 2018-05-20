package com.ragegamingpe.wtfenchants.common.lib;

import com.ragegamingpe.wtfenchants.common.block.*;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockVariants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModBlocks
{
    public static final List<ModBlock> ALL_BLOCKS = new ArrayList<>();

    public static final Map<BlockBookshelf.Variant, BlockBookshelf> BOOKSHELVES;
    public static final ModBlock COMMON_BOOKSHELF;
    public static final ModBlock SORTER;
    public static final ModBlock DISENCHANTING_TABLE;
    public static final ModBlock XP_STORE;

    static {
        BOOKSHELVES = ModBlockVariants.constructVariants(BlockBookshelf.class, BlockBookshelf.Variant.class);
        COMMON_BOOKSHELF = new BlockCommonBookshelf();
        SORTER = new BlockSorter();
        DISENCHANTING_TABLE = new BlockDisenchantment();
        XP_STORE = new BlockXpStore();
    }
}
