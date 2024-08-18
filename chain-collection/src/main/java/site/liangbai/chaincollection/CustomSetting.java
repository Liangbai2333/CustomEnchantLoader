package site.liangbai.chaincollection;

import org.bukkit.Material;

import java.util.List;

public class CustomSetting {
    private final boolean useBuiltIn;
    private final List<Material> extraCollection;

    public CustomSetting(boolean useBuiltIn, List<Material> extraCollection) {
        this.useBuiltIn = useBuiltIn;
        this.extraCollection = extraCollection;
    }

    public boolean isUseBuiltIn() {
        return useBuiltIn;
    }

    public List<Material> getExtraCollection() {
        return extraCollection;
    }

    @Override
    public String toString() {
        return "CustomSetting{" +
                "useBuiltIn=" + useBuiltIn +
                ", extraCollection=" + extraCollection +
                '}';
    }
}
