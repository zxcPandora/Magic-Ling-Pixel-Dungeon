package com.shatteredpixel.shatteredpixeldungeon.scenes;

import static com.shatteredpixel.shatteredpixeldungeon.BGMPlayer.playBGM;
import static com.shatteredpixel.shatteredpixeldungeon.Challenges.PRO;
import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.depth;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class JAmuletScene extends PixelScene {

    private static final int WIDTH			= 120;
    private static final int BTN_HEIGHT		= 18;
    private static final float SMALL_GAP	= 2;
    private static final float LARGE_GAP	= 8;

    public static boolean noText = false;

    private Image amulet;

    @Override
    public void create() {
        super.create();
       playBGM( Assets.Music.NBPL, true );
        RenderedTextBlock text = null;
        if (!noText) {
            text = renderTextBlock( Dungeon.isChallenged(PRO) ? Messages.get(this, "text") : Messages.get(this, "text_le"), 8 );
            text.maxWidth(WIDTH);
            add( text );
        }

        amulet = new Image( Assets.Sprites.JAMULET );
        add( amulet );

        RedButton btnExit = new RedButton( Messages.get(this, "exit") ) {
            @Override
            protected void onClick() {
                Dungeon.deleteGame( GamesInProgress.curSlot, true );
                Game.switchScene( TitleScene.class );
            }
        };
        btnExit.setSize( WIDTH, BTN_HEIGHT );
        add( btnExit );

        RedButton btnStay = new RedButton( Messages.get(this, "stay") ) {
            @Override
            protected void onClick() {
                onBackPressed();
            }
        };
        btnStay.setSize( WIDTH, BTN_HEIGHT );
        add( btnStay );

        float height;
        if (noText) {
            height = amulet.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();

            amulet.x = (Camera.main.width - amulet.width) / 2;
            amulet.y = (Camera.main.height - height) / 2;
            align(amulet);

            btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, amulet.y + amulet.height + LARGE_GAP );
            btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );

        } else {
            height = amulet.height + LARGE_GAP + text.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();

            amulet.x = (Camera.main.width - amulet.width) / 2;
            amulet.y = (Camera.main.height - height) / 2;
            align(amulet);

            text.setPos((Camera.main.width - text.width()) / 2, amulet.y + amulet.height + LARGE_GAP);
            align(text);

            btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, text.top() + text.height() + LARGE_GAP );
            btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );
        }

        new Flare( 8, 48 ).color( 0xFFDDBB, true ).show( amulet, 0 ).angularSpeed = +30;

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (timeFreeze != null) timeFreeze.disarmPresses();
        Swiftthistle.TimeBubble timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (timeBubble != null) timeBubble.disarmPresses();
        InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
        InterlevelScene.curTransition = new LevelTransition();
        InterlevelScene.curTransition.destDepth = depth;
        InterlevelScene.curTransition.destType = LevelTransition.Type.REGULAR_EXIT;
        InterlevelScene.curTransition.destBranch = 0;
        InterlevelScene.curTransition.type = LevelTransition.Type.REGULAR_EXIT;
        InterlevelScene.curTransition.centerCell  = -1;
        Game.switchScene( InterlevelScene.class );
    }

    private float timer = 0;

    @Override
    public void update() {
        super.update();

        if ((timer -= Game.elapsed) < 0) {
            timer = Random.Float( 0.5f, 5f );

            Speck star = (Speck)recycle( Speck.class );
            star.reset( 0, amulet.x + 10.5f, amulet.y + 5.5f, Speck.DISCOVER );
            add( star );
        }
    }
}
