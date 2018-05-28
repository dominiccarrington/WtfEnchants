package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.client.render.TESRXpStore;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlockExperienceContainer;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityXpStore;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockXpStore extends ModBlockExperienceContainer
{
    public BlockXpStore()
    {
        super(Material.ROCK, MapColor.RED, "xp_store");
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityXpStore();
    }

    @Override
    protected int getGuiID()
    {
        return GuiHandler.XP_STORE;
    }

    @Override
    public void registerRender()
    {
        super.registerRender();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityXpStore.class, new TESRXpStore());
    }

    public static String generateBlockModel()
    {
        return "{\n" +
                " \"credit\": \"Made with Blockbench\",\n" +
                " \"textures\": {\n" +
                "  \"block\": \"blocks/iron_bars\",\n" +
                "  \"particle\": \"blocks/iron_bars\"\n" +
                " },\n" +
                " \"elements\": [\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [7, 0, 0],\n" +
                "   \"to\": [9, 16, 2],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [12, 0, 0],\n" +
                "   \"to\": [14, 16, 2],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [9, 12, 0.5],\n" +
                "   \"to\": [12, 14, 1.5],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [2, 0, 0],\n" +
                "   \"to\": [4, 16, 2],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [4, 2, 0.5],\n" +
                "   \"to\": [7, 4, 1.5],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [14, 0, 7],\n" +
                "   \"to\": [16, 16, 9],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [14, 0, 12],\n" +
                "   \"to\": [16, 16, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [14.5, 12, 9],\n" +
                "   \"to\": [15.5, 14, 12],\n" +
                "   \"faces\": {\n" +
                "    \"east\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"down\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 90}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [14, 0, 2],\n" +
                "   \"to\": [16, 16, 4],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [14.5, 2, 4],\n" +
                "   \"to\": [15.5, 4, 7],\n" +
                "   \"faces\": {\n" +
                "    \"east\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"down\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 90}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [7, 0, 14],\n" +
                "   \"to\": [9, 16, 16],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [12, 0, 14],\n" +
                "   \"to\": [14, 16, 16],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [9, 12, 14.5],\n" +
                "   \"to\": [12, 14, 15.5],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [2, 0, 14],\n" +
                "   \"to\": [4, 16, 16],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"down\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 270}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [4, 2, 14.5],\n" +
                "   \"to\": [7, 4, 15.5],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [0, 0, 7],\n" +
                "   \"to\": [2, 16, 9],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [0, 0, 12],\n" +
                "   \"to\": [2, 16, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [0.5, 12, 9],\n" +
                "   \"to\": [1.5, 14, 12],\n" +
                "   \"faces\": {\n" +
                "    \"east\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"down\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 90}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [0, 0, 2],\n" +
                "   \"to\": [2, 16, 4],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"east\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 180}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [0.5, 2, 4],\n" +
                "   \"to\": [1.5, 4, 7],\n" +
                "   \"faces\": {\n" +
                "    \"east\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"west\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"down\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 90}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [7, 14, 2],\n" +
                "   \"to\": [9, 16, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [12, 14, 2],\n" +
                "   \"to\": [14, 16, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [9, 14.5, 12],\n" +
                "   \"to\": [12, 15.5, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [2, 14, 2],\n" +
                "   \"to\": [4, 16, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [4, 14.5, 2],\n" +
                "   \"to\": [7, 15.5, 4],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Center\",\n" +
                "   \"from\": [7, 0, 2],\n" +
                "   \"to\": [9, 2, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [7.01, 14.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [7.01, 0.01, 8.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [7.01, 0.01, 8.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [8.99, 0.01, 7.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"from\": [12, 0, 2],\n" +
                "   \"to\": [14, 2, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [2.01, 14.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [2.01, 0.01, 3.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [2.01, 0.01, 3.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [3.99, 0.01, 2.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"North-center\",\n" +
                "   \"from\": [9, 0.5, 12],\n" +
                "   \"to\": [12, 1.5, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [4.01, 3.01, 6.99, 3.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [4.01, 2.01, 6.99, 2.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"up\": {\"uv\": [4.01, 2.01, 6.99, 3.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [6.99, 2.01, 4.01, 3.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"from\": [2, 0, 2],\n" +
                "   \"to\": [4, 2, 14],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [12.01, 14.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"east\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 270},\n" +
                "    \"south\": {\"uv\": [12.01, 0.01, 13.99, 1.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"west\": {\"uv\": [12.01, 0.01, 13.99, 15.99], \"texture\": \"#block\", \"rotation\": 90},\n" +
                "    \"up\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [13.99, 0.01, 12.01, 15.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South-center\",\n" +
                "   \"from\": [4, 0.5, 2],\n" +
                "   \"to\": [7, 1.5, 4],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [9.01, 13.01, 11.99, 13.99], \"texture\": \"#block\"},\n" +
                "    \"south\": {\"uv\": [9.01, 12.01, 11.99, 12.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"up\": {\"uv\": [9.01, 12.01, 11.99, 13.99], \"texture\": \"#block\", \"rotation\": 180},\n" +
                "    \"down\": {\"uv\": [11.99, 12.01, 9.01, 13.99], \"texture\": \"#block\"}\n" +
                "   }\n" +
                "  }\n" +
                " ],\n" +
                " \"groups\": [\n" +
                "  {\n" +
                "   \"name\": \"North\",\n" +
                "   \"isOpen\": false,\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [0, 1, 2, 3, 4]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"East\",\n" +
                "   \"isOpen\": false,\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [5, 6, 7, 8, 9]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"South\",\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [10, 11, 12, 13, 14]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"West\",\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [15, 16, 17, 18, 19]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Top\",\n" +
                "   \"isOpen\": false,\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [20, 21, 22, 23, 24]\n" +
                "  },\n" +
                "  {\n" +
                "   \"name\": \"Bottom\",\n" +
                "   \"isOpen\": true,\n" +
                "   \"shade\": true,\n" +
                "   \"display\": {\n" +
                "    \"visibility\": true,\n" +
                "    \"autouv\": true\n" +
                "   },\n" +
                "   \"children\": [25, 26, 27, 28, 29]\n" +
                "  }\n" +
                " ]\n" +
                "}";
    }
}
