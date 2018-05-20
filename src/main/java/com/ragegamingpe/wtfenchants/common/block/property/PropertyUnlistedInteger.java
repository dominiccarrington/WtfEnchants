package com.ragegamingpe.wtfenchants.common.block.property;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUnlistedInteger extends PropertyInteger implements IUnlistedProperty<Integer>
{
    public PropertyUnlistedInteger(String name, int min, int max)
    {
        super(name, min, max);
    }

    @Override
    public boolean isValid(Integer value)
    {
        return this.getAllowedValues().contains(value);
    }

    @Override
    public Class<Integer> getType()
    {
        return this.getValueClass();
    }

    @Override
    public String valueToString(Integer value)
    {
        return getName(value);
    }
}
