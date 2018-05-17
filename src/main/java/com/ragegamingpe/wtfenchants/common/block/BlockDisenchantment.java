package com.ragegamingpe.wtfenchants.common.block;

import com.ragegamingpe.wtfenchants.common.WtfEnchants;
import com.ragegamingpe.wtfenchants.common.block.base.ModBlock;
import com.ragegamingpe.wtfenchants.common.network.GuiHandler;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDisenchantment extends ModBlock
{
    private static final AxisAlignedBB BOUNDING_BOX_AABB = createAABB(0, 0, 0, 16, 12, 16);

    public BlockDisenchantment()
    {
        super(Material.ROCK, MapColor.RED, "disenchanting_table");
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX_AABB;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            playerIn.openGui(WtfEnchants.instance, GuiHandler.DISENCHANTING_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    public static String generateBlockModel()
    {
        return "{\n" +
                " \"credit\": \"Made with Blockbench\",\n" +
                " \"parent\": \"block/block\",\n" +
                " \"textures\": {\n" +
                "  \"bottom\": \"wtfenchants:blocks/disenchanting_table_bottom\",\n" +
                "  \"top\": \"wtfenchants:blocks/disenchanting_table_top\",\n" +
                "  \"side\": \"wtfenchants:blocks/disenchanting_table_side\"\n" +
                " },\n" +
                " \"elements\": [\n" +
                "  {\n" +
                "   \"from\": [0, 0, 0],\n" +
                "   \"to\": [16, 12, 16],\n" +
                "   \"faces\": {\n" +
                "    \"north\": {\"uv\": [0, 4, 16, 16], \"texture\": \"#side\", \"cullface\": \"north\"},\n" +
                "    \"east\": {\"uv\": [0, 4, 16, 16], \"texture\": \"#side\", \"cullface\": \"east\"},\n" +
                "    \"south\": {\"uv\": [0, 4, 16, 16], \"texture\": \"#side\", \"cullface\": \"south\"},\n" +
                "    \"west\": {\"uv\": [0, 4, 16, 16], \"texture\": \"#side\", \"cullface\": \"west\"},\n" +
                "    \"up\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#top\"},\n" +
                "    \"down\": {\"uv\": [0, 0, 16, 16], \"texture\": \"#bottom\", \"cullface\": \"down\"}\n" +
                "   }\n" +
                "  }\n" +
                " ]\n" +
                "}";
    }
}
