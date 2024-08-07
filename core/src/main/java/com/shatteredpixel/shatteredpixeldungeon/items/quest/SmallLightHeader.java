package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.SmallLight;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class SmallLightHeader extends Item {

    public static final String AC_SUMMON = "SummonFish";


    {
        image = ItemSpriteSheet.SMTITEM;
        stackable = true;
        defaultAction = AC_SUMMON;
    }

    @Override
    public String defaultAction() {
        boolean needToSpawn = true;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
            if (mob instanceof SmallLight) {
                needToSpawn = false;
                break;
            }
        }

        if(!needToSpawn){
            return AC_THROW;
        } else {
            return super.defaultAction();
        }
    }

    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        boolean needToSpawn = true;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
            if (mob instanceof SmallLight) {
                needToSpawn = false;
                break;
            }
        }

        if (needToSpawn){
            actions.add(AC_SUMMON);
        }

        return actions;
    }

    @Override
    public void execute(Hero hero, String action ) {
        super.execute(hero, action);
        if (action.equals(AC_SUMMON)) {



            hero.sprite.operate(hero.pos, new Callback() {
                @Override
                public void call() {
                    Buff.affect( hero, SAwareness.class, SAwareness.DURATION );
                    ArrayList<Integer> respawnPoints = new ArrayList<>();
                    for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                        int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                        if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                            respawnPoints.add(p);
                        }
                    }
                    if (!respawnPoints.isEmpty()) {
                        SmallLight smallLight = new SmallLight();
                        smallLight.pos = respawnPoints.get(Random.index( respawnPoints ));
                        GameScene.add(smallLight);
                        smallLight.state = smallLight.WANDERING;
                        smallLight.sprite.emitter().burst(Speck.factory(Speck.STAR), 10);
                        hero.sprite.idle();
                    }
                }
            });
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 75;
    }


    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{PotionOfMindVision.class, ScrollOfMagicMapping.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = SmallLightHeader.class;
            outQuantity = 4;
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


    public static class SAwareness extends FlavourBuff {
        public int distance = 2;
        {
            type = buffType.POSITIVE;
        }

        public static final float DURATION = 123456789f;

        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }
    }

}


