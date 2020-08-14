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

package XFactHD.advtech.client.gui.elements;

import org.apache.commons.lang3.tuple.Pair;

public enum ButtonType
{
    EMPTY      (12, Pair.of( 70, 0), Pair.of( 70, 12), Pair.of( 70, 24), ""),
    PLUS       (12, Pair.of( 82, 0), Pair.of( 82, 12), Pair.of( 82, 24), ""),
    MINUS      (12, Pair.of( 94, 0), Pair.of( 94, 12), Pair.of( 94, 24), ""),
    ARROW_RIGHT(12, Pair.of(106, 0), Pair.of(106, 12), Pair.of(106, 24), ""),
    ARROW_LEFT (12, Pair.of(118, 0), Pair.of(118, 12), Pair.of(118, 24), ""),
    ARROW_UP   (12, Pair.of(130, 0), Pair.of(130, 12), Pair.of(130, 24), ""),
    ARROW_DOWN (12, Pair.of(142, 0), Pair.of(142, 12), Pair.of(142, 24), ""),
    HOOK       (15, Pair.of(169, 0), Pair.of(169, 15), Pair.of(169, 30), ""),
    CROSS      (15, Pair.of(154, 0), Pair.of(154, 15), Pair.of(154, 30), ""),
    SET_IO_IN  (20, Pair.of(184, 0), Pair.of(184, 20), Pair.of(184, 40), "SET_IO_OUT"),
    SET_IO_OUT (20, Pair.of(204, 0), Pair.of(204, 20), Pair.of(204, 40), "SET_IO_NONE"),
    SET_IO_NONE(20, Pair.of(224, 0), Pair.of(224, 20), Pair.of(224, 40), "SET_IO_IN");

    private int size;
    private Pair<Integer, Integer> offsetOff;
    private Pair<Integer, Integer> offsetOn;
    private Pair<Integer, Integer> offsetHover;
    private String next;

    ButtonType(int size, Pair<Integer, Integer> offsetOn, Pair<Integer, Integer> offsetHover, Pair<Integer, Integer> offsetOff, String next)
    {
        this.size = size;
        this.offsetOff = offsetOff;
        this.offsetOn = offsetOn;
        this.offsetHover = offsetHover;
        this.next = next.equals("") ? null : next;
    }

    public int getSize()
    {
        return size;
    }

    public Pair<Integer, Integer> getOffsetOff()
    {
        return offsetOff;
    }

    public Pair<Integer, Integer> getOffsetOn()
    {
        return offsetOn;
    }

    public Pair<Integer, Integer> getOffsetHover()
    {
        return offsetHover;
    }

    public ButtonType getNext()
    {
        return next == null ? this : valueOf(next);
    }
}