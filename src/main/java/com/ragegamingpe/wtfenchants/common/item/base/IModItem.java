package com.ragegamingpe.wtfenchants.common.item.base;

import com.ragegamingpe.wtfenchants.common.block.base.IModBlock;
import net.minecraft.client.renderer.ItemMeshDefinition;

public interface IModItem extends IModBlock
{
    default ItemMeshDefinition registerCustomMeshDefinition()
    {
        return null;
    }
}
