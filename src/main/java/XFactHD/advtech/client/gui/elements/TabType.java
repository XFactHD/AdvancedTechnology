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

package XFactHD.advtech.client.gui.elements;

import org.apache.commons.lang3.tuple.Pair;

public enum TabType
{
    EMPTY(Pair.of(  0, 0), Pair.of(  0,  20), "desc.advtech:hover.empty.name"),
    IO   (Pair.of( 20, 0), Pair.of( 76,  20), "desc.advtech:hover.io.name"),
    NULL1(Pair.of( 40, 0), Pair.of(152,  20), "desc.advtech:hover.null.name"),
    NULL2(Pair.of( 60, 0), Pair.of(  0,  96), "desc.advtech:hover.null.name"),
    NULL3(Pair.of( 80, 0), Pair.of( 76,  96), "desc.advtech:hover.null.name"),
    NULL4(Pair.of(100, 0), Pair.of(152,  96), "desc.advtech:hover.null.name"),
    NULL5(Pair.of(120, 0), Pair.of(  0, 172), "desc.advtech:hover.null.name"),
    NULL6(Pair.of(140, 0), Pair.of( 76, 172), "desc.advtech:hover.null.name"),
    NULL7(Pair.of(160, 0), Pair.of(152, 172), "desc.advtech:hover.null.name");

    private Pair<Integer, Integer> offsetClosed;
    private Pair<Integer, Integer> offsetOpen;
    private String hoverMessage;

    TabType(Pair<Integer, Integer> offsetClosed, Pair<Integer, Integer> offsetOpen, String hoverMessage)
    {
        this.offsetClosed = offsetClosed;
        this.offsetOpen = offsetOpen;
        this.hoverMessage = hoverMessage;
    }

    public Pair<Integer, Integer> getOffsetClosed()
    {
        return offsetClosed;
    }

    public Pair<Integer, Integer> getOffsetOpen()
    {
        return offsetOpen;
    }

    public String getHoverMessage()
    {
        return hoverMessage;
    }
}