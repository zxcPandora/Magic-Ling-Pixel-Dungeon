/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import static com.shatteredpixel.shatteredpixeldungeon.Challenges.EXSG;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio extends Food {

	{
		image = ItemSpriteSheet.CARPACCIO;
		energy = Hunger.HUNGRY/2f;
	}
	
	@Override
	protected void satisfy(Hero hero) {
		if (Dungeon.isChallenged(Challenges.EXSG)) {
				//~75% of a potion of healing
				Buff.affect(hero, Barrier.class).setShield((int) (0.3f * hero.HT + 5));
				Talent.onPotionUsed(curUser, curUser.pos, energy/15f );
		}
		super.satisfy(hero);

		effect(hero);
	}
	
	public int value() {
		return 10 * quantity;
	}

	public static void effect(Hero hero){
		switch (Random.Int( 5 )) {
			case 0:
				GLog.i( Messages.get(FrozenCarpaccio.class, "invis") );
				Buff.affect( hero, Invisibility.class, Invisibility.DURATION );
				break;
			case 1:
				GLog.i( Messages.get(FrozenCarpaccio.class, "hard") );
				Barkskin.conditionallyAppend( hero, hero.HT / 4, 1 );
				break;
			case 2:
				GLog.i( Messages.get(FrozenCarpaccio.class, "refresh") );
				PotionOfHealing.cure(hero);
				break;
			case 3:
				GLog.i( Messages.get(FrozenCarpaccio.class, "better") );
				hero.HP = Math.min( hero.HP + hero.HT / 4, hero.HT );
				break;
		}
	}
	
	public static Food cook( MysteryMeat ingredient ) {
		FrozenCarpaccio result = new FrozenCarpaccio();
		result.quantity = ingredient.quantity();
		return result;
	}
	@Override
	public String desc() {
		//三元一次逻辑运算
		return Dungeon.isChallenged(EXSG) ? Messages.get(this, "descx") : Messages.get(this, "desc");
	}
}
