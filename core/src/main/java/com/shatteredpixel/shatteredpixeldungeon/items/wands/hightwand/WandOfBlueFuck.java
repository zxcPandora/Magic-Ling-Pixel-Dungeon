/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.wands.hightwand;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.HalomethaneFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HalomethaneBurning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlameX;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CrivusFruitsFlake;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.HaloBlazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WandOfBlueFuck extends DamageWand {

    {
        image = ItemSpriteSheet.HIGHTWAND_3;

        collisionProperties = Ballistica.MAGIC_BOLT;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{PotionOfLiquidFlameX.class, WandOfFireblast.class, CrivusFruitsFlake.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 15+Dungeon.depth/2;

            output = WandOfBlueFuck.class;
            outQuantity = 1;
        }

        public final Item sampleOutput(ArrayList<Item> ingredients){
            try {
                Item result = Reflection.newInstance(output);
                result.quantity(outQuantity).level(Random.NormalIntRange(2,4));
                return result;
            } catch (Exception e) {
                ShatteredPixelDungeon.reportException( e );
                return null;
            }
        }

    }


    //1x/2x/3x damage
    public int min(int lvl){
        return (2+lvl+Dungeon.depth/5) * chargesPerCast();
    }

    //1x/2x/3x damage
    public int max(int lvl){
        return (3*lvl+Dungeon.depth/5) * chargesPerCast();
    }

    ConeAOE cone;

    @Override
    public void onZap(Ballistica bolt) {
        ArrayList<Char> affectedChars = new ArrayList<>();
        ArrayList<Integer> adjacentCells = new ArrayList<>();
        for (int cell : cone.cells) {
            // 忽略施法者所在的格子
            if (cell == bolt.sourcePos) {
                continue;
            }

            // 打开门
            if (Dungeon.level.map[cell] == Terrain.DOOR) {
                Level.set(cell, Terrain.OPEN_DOOR);
                GameScene.updateMap(cell);
            }

            // 如果格子直接相邻并且不可燃，则加入到 adjacentCells 列表
            if (Dungeon.level.adjacent(bolt.sourcePos, cell) && !Dungeon.level.flamable[cell]) {
                adjacentCells.add(cell);
            } else {
                GameScene.add(Blob.seed(cell, 6 + chargesPerCast(), HalomethaneFire.class));
            }

            Char ch = Actor.findChar(cell);
            if (ch != null) {
                affectedChars.add(ch);
            }
        }

        // 点燃与 adjacentCells 相邻的可燃格子（排除已经点燃的格子）
        for (int cell : adjacentCells) {
            for (int i : PathFinder.CIRCLE8) {
                if (Dungeon.level.trueDistance(cell + i, bolt.sourcePos) > Dungeon.level.trueDistance(cell, bolt.sourcePos)
                        && Dungeon.level.flamable[cell + i]
                        && HalomethaneFire.volumeAt(cell + i, HalomethaneFire.class) == 0) {
                    GameScene.add(Blob.seed(cell + i, 12 + chargesPerCast(), HalomethaneFire.class));
                }
            }
        }

        for (Char ch : affectedChars) {
            processSoulMark(ch, chargesPerCast());
            ch.damage(damageRoll(), this);
            if (ch.isAlive()) {
                Buff.affect(ch, HalomethaneBurning.class).reignite(ch);
                switch (chargesPerCast()) {
                    case 1:
                        break; // 没有额外效果
                    case 2:
                        Buff.affect(ch, Blindness.class, 4f);
                        break;
                    case 3:
                        Buff.affect(ch, Paralysis.class, 4f);
                        break;
                }
            }
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        //acts like blazing enchantment
        new HaloBlazing().proc( staff, attacker, defender, damage);
    }

    @Override
    public void fx( Ballistica bolt, Callback callback ) {
        //need to perform flame spread logic here so we can determine what cells to put flames in.

        // 5/7/9 distance
        int maxDist = (1 + chargesPerCast())*level/5+3;
        int dist = Math.min(bolt.dist, maxDist);

        cone = new ConeAOE( bolt,
                maxDist,
                30 + 40*chargesPerCast(),
                collisionProperties | Ballistica.STOP_TARGET);

        //cast to cells at the tip, rather than all cells, better performance.
        for (Ballistica ray : cone.rays){
            ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                    MagicMissile.SHAMAN_PURPLE,
                    curUser.sprite,
                    ray.path.get(ray.dist),
                    null
            );
        }

        //final zap at half distance, for timing of the actual wand effect
        MagicMissile.boltFromChar( curUser.sprite.parent,
                MagicMissile.SHAMAN_PURPLE,
                curUser.sprite,
                bolt.path.get(dist/4),
                callback );
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
        Sample.INSTANCE.play( Assets.Sounds.BURNING );
    }

    @Override
    protected int chargesPerCast() {
        //consumes 30% of current charges, rounded up, with
        // a minimum of one.
        return Math.max(1, (int)Math.ceil(curCharges*0.3f));
    }

    @Override
    public String statsDesc() {
        if (levelKnown)
            return Messages.get(this, "stats_desc", chargesPerCast(), min(), max());
        else
            return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color( 0xEE7722 );
        particle.am = 0.5f;
        particle.setLifespan(0.6f);
        particle.acc.set(0, -20);
        particle.setSize( 0f, 1.5f);
        particle.shuffleXY( 0.4f );
    }

}
