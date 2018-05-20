package com.ragegamingpe.wtfenchants.client.model;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ragegamingpe.wtfenchants.common.block.BlockCommonBookshelf;
import com.ragegamingpe.wtfenchants.common.block.te.TileEntityCommonBookshelf;
import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Amended from Tinkers Construct
 */
@SideOnly(Side.CLIENT)
public class CommonBookshelfModel implements IBakedModel
{
    static final Logger log = LogManager.getLogger(LibMisc.MOD_NAME + " - Common Bookshelf Model");

    private final IBakedModel standard;
    private final IModel[] bookshelfModels;

    private final Map<Pair<String, Integer>, IBakedModel> cache = Maps.newHashMap();
    private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    private final VertexFormat format;
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

    private final Cache<TableItemCombinationCacheKey, IBakedModel> tableItemCombinedCache = CacheBuilder
            .newBuilder()
            .maximumSize(30)
            .build();

    public CommonBookshelfModel(IBakedModel standard, IModel[] bookshelfModels, VertexFormat format)
    {
        this.standard = standard;
        this.bookshelfModels = bookshelfModels;

        this.textureGetter = location -> {
            assert location != null;
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        };
        this.format = format;
        this.transforms = getTransforms(standard);
    }

    protected IBakedModel getActualModel(String texture, int books, EnumFacing facing)
    {
        IBakedModel bakedModel = standard;

        if (texture != null) {
            for (Pair<String, Integer> cachePair : cache.keySet()) {
                if (cachePair.getLeft() == texture && cachePair.getRight() == books) {
                    bakedModel = cache.get(cachePair);
                }
            }

            if (bookshelfModels != null && bakedModel == standard) {
                IModel model = bookshelfModels[books];
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                builder.put("planks", texture);
                builder.put("particle", texture);
                IModel retexturedModel = model.retexture(builder.build());
                IModelState modelState = new SimpleModelState(transforms);

                bakedModel = retexturedModel.bake(modelState, format, textureGetter);
                cache.put(new ImmutablePair<>(texture, books), bakedModel);
            }
        }

        final IBakedModel parentModel = bakedModel;
        try {
            bakedModel = tableItemCombinedCache.get(new TableItemCombinationCacheKey(books, bakedModel, facing), () -> getCombinedBakedModel(facing, parentModel));
        } catch (ExecutionException e) {
            log.error(e);
        }

        return bakedModel;
    }

    private IBakedModel getCombinedBakedModel(EnumFacing facing, IBakedModel parentModel)
    {
        IBakedModel out = parentModel;
        // add all the items to display on the table

        if (facing != null) out = new TRSRBakedModel(out, facing);
        return out;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        // get texture from state
        String texture = null;
        EnumFacing face = EnumFacing.SOUTH;
        int books = 0;

        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            if (extendedState.getUnlistedNames().contains(BlockCommonBookshelf.TEXTURE)) {
                texture = extendedState.getValue(BlockCommonBookshelf.TEXTURE);
            }

            if (extendedState.getUnlistedNames().contains(BlockCommonBookshelf.FACING)) {
                face = extendedState.getValue((IUnlistedProperty<EnumFacing>) BlockCommonBookshelf.FACING);
            }

            if (extendedState.getUnlistedNames().contains(BlockCommonBookshelf.BOOKS)) {
                books = extendedState.getValue((IUnlistedProperty<Integer>) BlockCommonBookshelf.BOOKS);
            }

            // remove all world specific data
            // This is so that the next call to getQuads from the transformed TRSRModel doesn't do this again
            // otherwise table models inside table model items recursively calls this with the state of the original table
            state = extendedState.withProperty((IUnlistedProperty<EnumFacing>) BlockCommonBookshelf.FACING, null);
        }

        // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
        if (texture == null) {
            return standard.getQuads(state, side, rand);
        }

        // the model returned by getActualModel should be a simple model with no special handling
        return getActualModel(texture, books, face).getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return standard.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return standard.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return standard.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return standard.getParticleTexture();
    }

    @Nonnull
    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return standard.getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides()
    {
        return TableItemOverrideList.INSTANCE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
        return Pair.of(this, pair.getRight());
    }

    public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IBakedModel model)
    {
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        for (ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
            TRSRTransformation transformation = new TRSRTransformation(model.handlePerspective(type).getRight());
            if (!transformation.equals(TRSRTransformation.identity())) {
                builder.put(type, TRSRTransformation.blockCenterToCorner(transformation));
            }
        }
        return builder.build();
    }

    public static TextureAtlasSprite getTextureFromBlock(Block block, int meta)
    {
        IBlockState state = block.getStateFromMeta(meta);
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

    public static NBTTagCompound getTagSafe(ItemStack stack)
    {
        // yes, the null checks aren't needed anymore, but they don't hurt either.
        // After all the whole purpose of this function is safety/processing possibly invalid input ;)
        if (stack == null || stack.getItem() == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return new NBTTagCompound();
        }

        return stack.getTagCompound();
    }

    private static class TableItemOverrideList extends ItemOverrideList
    {

        static TableItemOverrideList INSTANCE = new TableItemOverrideList();

        private TableItemOverrideList()
        {
            super(ImmutableList.of());
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            if (originalModel instanceof CommonBookshelfModel) {
                // read out the data on the itemstack
                ItemStack blockStack = new ItemStack(getTagSafe(stack).getCompoundTag(TileEntityCommonBookshelf.PLANKS_TAG));
                if (!blockStack.isEmpty()) {
                    // get model from data
                    Block block = Block.getBlockFromItem(blockStack.getItem());
                    String texture = getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();

                    return ((CommonBookshelfModel) originalModel).getActualModel(texture, 14, null);
                }
            }

            return originalModel;
        }
    }

    private static class TableItemCombinationCacheKey
    {
        private final int books;
        private final IBakedModel bakedBaseModel;
        private final EnumFacing facing;

        public TableItemCombinationCacheKey(int books, IBakedModel bakedBaseModel, EnumFacing facing)
        {
            this.books = books;
            this.bakedBaseModel = bakedBaseModel;
            this.facing = facing;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TableItemCombinationCacheKey that = (TableItemCombinationCacheKey) o;

            if (bakedBaseModel != null ? !bakedBaseModel.equals(that.bakedBaseModel) : that.bakedBaseModel != null) {
                return false;
            }

            if (books == that.books)
                return false;

            return facing == that.facing;
        }

        @Override
        public int hashCode()
        {
            int result = 31 * books;
            result = 31 * result + (bakedBaseModel != null ? bakedBaseModel.hashCode() : 0);
            result = 31 * result + (facing != null ? facing.hashCode() : 0);
            return result;
        }
    }
}
