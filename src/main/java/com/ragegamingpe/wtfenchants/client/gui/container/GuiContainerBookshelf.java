package com.ragegamingpe.wtfenchants.client.gui.container;

import com.ragegamingpe.wtfenchants.client.gui.container.base.BaseGuiContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityCommonBookshelf;
import com.ragegamingpe.wtfenchants.common.container.ContainerBookshelf;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiContainerBookshelf extends BaseGuiContainer
{
    public GuiContainerBookshelf(IInventory player, TileEntityCommonBookshelf te)
    {
        super(
                new ContainerBookshelf(player, te),
                new ResourceLocation(LibMisc.MOD_ID, "textures/gui/bookshelf.png"),
                176,
                154
        );
    }
}
