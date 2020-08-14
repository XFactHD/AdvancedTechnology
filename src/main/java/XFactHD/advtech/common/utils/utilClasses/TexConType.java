/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.advtech.common.utils.utilClasses;

import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public enum TexConType
{
    //      up,  down, right,  left
    NONE(false, false, false, false),
    T   (true,  false, false, false),
    B   (false, true,  false, false),
    R   (false, false, true,  false),
    L   (false, false, false, true),
    TB  (true,  true,  false, false),
    RL  (false, false, true,  true),
    TR  (true,  false, true,  false),
    TL  (true,  false, false, true),
    BR  (false, true,  true,  false),
    BL  (false, true,  false, true),
    TBR (true,  true,  true,  false),
    TBL (true,  true,  false, true),
    TRL (true,  false, true,  true),
    BRL (false, true,  true,  true),
    TBRL(true,  true,  true,  true);

    private boolean up;
    private boolean down;
    private boolean right;
    private boolean left;

    TexConType(boolean up, boolean down, boolean right, boolean left)
    {
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
    }

    public static TexConType getForBooleans(boolean up, boolean down, boolean right, boolean left)
    {
        if (up && down && right && left)
        {
            return TBRL;
        }
        else if (up && right && left)
        {
            return TRL;
        }
        else if (down && right && left)
        {
            return BRL;
        }
        else if (up && right && down)
        {
            return TBR;
        }
        else if (up && left && down)
        {
            return TBL;
        }
        else if (up && right)
        {
            return TR;
        }
        else if (up && left)
        {
            return TL;
        }
        else  if (up && down)
        {
            return TB;
        }
        else if (down && right)
        {
            return BR;
        }
        else if (down && left)
        {
            return BL;
        }
        else if (right && left)
        {
            return RL;
        }
        else if (up)
        {
            return T;
        }
        else if (down)
        {
            return B;
        }
        else if (right)
        {
            return R;
        }
        else if (left)
        {
            return L;
        }
        else
        {
            return NONE;
        }
    }

    public static TexConType getForBooleansAndFacing(EnumFacing side, boolean up, boolean down, boolean north, boolean east, boolean south, boolean west)
    {
        switch (side)
        {
            case UP:
            {
                return getForBooleans(south, north, west, east);
            }
            case DOWN:
            {
                return getForBooleans(north, south, west, east);
            }
            case NORTH:
            {
                return getForBooleans(down, up, east, west);
            }
            case EAST:
            {
                return getForBooleans(down, up, south, north);
            }
            case SOUTH:
            {
                return getForBooleans(down, up, west, east);
            }
            case WEST:
            {
                return getForBooleans(down, up, north, south);
            }
            default: return NONE;
        }
    }

    public boolean isUp()
    {
        return up;
    }

    public boolean isDown()
    {
        return down;
    }

    public boolean isRight()
    {
        return right;
    }

    public boolean isLeft()
    {
        return left;
    }
}