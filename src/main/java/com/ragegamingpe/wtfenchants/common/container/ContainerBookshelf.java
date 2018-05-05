package com.ragegamingpe.wtfenchants.common.container;

import com.ragegamingpe.wtfenchants.common.block.te.TileEntityBookshelf;
import com.ragegamingpe.wtfenchants.common.container.base.BaseContainer;
import net.minecraft.inventory.IInventory;

public class ContainerBookshelf extends BaseContainer
{
    public ContainerBookshelf(IInventory player, TileEntityBookshelf te)
    {
        super(player, te);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                this.addSlotToContainer(new BaseSlot(te, j + i * 7, 26 + j * 18, 20 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new BaseSlot(player, j + i * 9 + 9, 8 + j * 18, 72 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new BaseSlot(player, i, 8 + i * 18, 130));
        }
    }
}
