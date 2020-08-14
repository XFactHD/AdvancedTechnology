/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.api.util;

import net.minecraft.util.math.BlockPos;

public class DimensionalBlockPos extends BlockPos
{
    private int dimension;

    public DimensionalBlockPos(int x, int y, int z, int dimension)
    {
        super(x, y, z);
        this.dimension = dimension;
    }

    public DimensionalBlockPos(BlockPos pos, int dimension)
    {
        super(pos);
        this.dimension = dimension;
    }

    public int getDimension()
    {
        return dimension;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof DimensionalBlockPos)
        {
            return super.equals(o) && dimension == ((DimensionalBlockPos)o).dimension;
        }
        return super.equals(o);
    }
}