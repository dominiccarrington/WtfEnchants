package com.ragegamingpe.wtfenchants.common.block.base;

import net.minecraft.util.IStringSerializable;

public interface IModBlockVariants extends IModBlock
{
    interface IBaseVariant extends IStringSerializable
    {
        @Override
        public default String getName()
        {
            return ((Enum) this).name().toLowerCase();
        }
    }
}
