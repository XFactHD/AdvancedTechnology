package xfacthd.advtech.common.util.interfaces;

import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;

public interface IMaterialObject
{
    MaterialType getMaterial();

    MaterialCategory getCategory();
}