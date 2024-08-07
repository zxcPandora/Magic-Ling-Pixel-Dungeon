package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_SP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WATER;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PinkGhost;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class HeartRoom extends SpecialRoom {

    @Override
    public int minWidth() {
        return 15;
    }

    @Override
    public int minHeight() {
        return 15;
    }

    @Override
    public int maxWidth() {
        return 15;
    }

    @Override
    public int maxHeight() {
        return 15;
    }

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		return false;
	}

    @Override
    public void paint(Level level) {
        Point center = new Point((left + right) / 2, (top + bottom) / 2);
        int centerX = center.x;
        int centerY = center.y;
        int radius = (right - left) / 2;

        int MiddlePos = (top + 7) * level.width() + left + 7;

        Mob n = new PinkGhost();
        n.pos = MiddlePos;
        level.mobs.add(n);

        Painter.drawLine(level, new Point(left, top), new Point(right, top), WATER);
        Painter.drawLine(level, new Point(right, top), new Point(right, bottom), WATER);
        Painter.drawLine(level, new Point(right, bottom), new Point(left, bottom), WATER);
        Painter.drawLine(level, new Point(left, bottom), new Point(left, top),  WATER);

        Painter.fill( level, this,0, Random.Int(10) == 1 ? Terrain.FURROWED_GRASS : Terrain.WALL );
        Painter.fill( level, this,1, FURROWED_GRASS );

        // 绘制爱心
        // 绘制眼睛外圈和门
        int eyeRadius = radius /4;
        Painter.drawCircle(level, center, eyeRadius + 5, EMPTY);

        Painter.drawCircle(level, center, eyeRadius, WATER);

        for (Door door : connected.values()) {
            door.set( Door.Type.REGULAR );
            if (door.x == left || door.x == right){
                Painter.drawInside(level, this, door, width()/2, FURROWED_GRASS);
            } else {
                Painter.drawInside(level, this, door, height()/2, FURROWED_GRASS);
            }
        }

        Painter.drawCircle(level, center, eyeRadius - 2, EMPTY_SP);

        // 左侧爱心
        Painter.drawLine(level, new Point(centerX - 4, centerY - 3), new Point(centerX - 2, centerY - 5), EMPTY_SP);
        Painter.drawLine(level, new Point(centerX - 2, centerY - 5), new Point(centerX, centerY - 3), EMPTY_SP);
        Painter.drawLine(level, new Point(centerX, centerY - 3), new Point(centerX - 4, centerY + 1),EMPTY_SP);
        Painter.drawLine(level, new Point(centerX - 5, centerY), new Point(centerX - 5, centerY - 2), EMPTY_SP);

        Painter.drawLine(level, new Point(centerX + 3, centerY + 2), new Point(centerX + 1, centerY + 4), EMPTY_SP);

        Painter.drawLine(level, new Point(centerX, centerY + 5), new Point(centerX, centerY + 5), EMPTY_SP);

        Painter.drawLine(level, new Point(centerX - 3, centerY + 2), new Point(centerX - 1, centerY + 4), EMPTY_SP);

        // 右侧爱心
        Painter.drawLine(level, new Point(centerX + 4, centerY - 3), new Point(centerX + 2, centerY - 5), EMPTY_SP);
        Painter.drawLine(level, new Point(centerX + 2, centerY - 5), new Point(centerX, centerY - 3), EMPTY_SP);
        Painter.drawLine(level, new Point(centerX, centerY - 3), new Point(centerX + 4, centerY + 1), EMPTY_SP);
        Painter.drawLine(level, new Point(centerX + 5, centerY), new Point(centerX + 5, centerY - 2), EMPTY_SP);
    }
}