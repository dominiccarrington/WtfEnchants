package com.ragegamingpe.wtfenchants.common.network;

import com.ragegamingpe.wtfenchants.common.lib.LibMisc;
import com.ragegamingpe.wtfenchants.common.network.message.MessageXpStore;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MessageHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LibMisc.MOD_ID);

    private static int i = 0;

    public static void init()
    {
        register(MessageXpStore.class, Side.SERVER);
    }

    @SuppressWarnings("unchecked")
    private static void register(Class clazz, Side handlerSide)
    {
        INSTANCE.registerMessage(clazz, clazz, i++, handlerSide);
    }
}
