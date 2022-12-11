package org.hockey.hockeyware.loader;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name( "hwl" )
@IFMLLoadingPlugin.MCVersion( "1.12.2" )
public class LoaderCoreMod implements IFMLLoadingPlugin
{
    public static Thread thread = Thread.currentThread();

    @SuppressWarnings( "deprecation" )
    public LoaderCoreMod()
    {
        if ( !Loader.runningFromIntellij() )
        {
            Loader.init();
            thread.suspend();
        } else
        {
            MixinBootstrap.init();
            Mixins.addConfiguration( "mixins.hockey.json" );
            MixinEnvironment.getCurrentEnvironment().setObfuscationContext( "searge" );
        }
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[ 0 ];
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData( Map< String, Object > data )
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
