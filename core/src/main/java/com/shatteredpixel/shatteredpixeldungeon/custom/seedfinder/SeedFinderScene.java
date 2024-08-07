package com.shatteredpixel.shatteredpixeldungeon.custom.seedfinder;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

import java.util.Arrays;

public class SeedFinderScene extends PixelScene {

    public static String result;

    @Override
    public void create() {
        super.create();

        final float colWidth = 120;
        final float fullWidth = colWidth * (landscape() ? 2 : 1);

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        //darkens the arches
        add(new ColorBlock(w, h, 0));

        ScrollPane list = new ScrollPane(new Component());
        add(list);

        Component content = list.content();
        content.clear();

        ShatteredPixelDungeon.scene().addToFront(new WndSeedTextInput(Messages.get(this, "title"), Messages.get(this, "body2",SPDSettings.challenges(),SPDSettings.timeOutSeed()), Messages.get(this, "initial_value",SPDSettings.FoundDepth()), 1000, true, Messages.get(this, "find"), Messages.get(HeroSelectScene.class, "custom_seed_clear")) {
            @Override
            public void onSelect(boolean positive, String text) {
                int floor = 30;
                String up_to_floor = "floor end";
                String strFloor = "floor";

                if (text.contains(up_to_floor)) {
                    String fl = text.split(strFloor)[0].trim();
                    try {
                        floor = Integer.parseInt(fl);
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (positive) {
                    if (text.contains("floor end")) {
                        String[] itemList = Arrays.copyOfRange(text.split("\n"), 1, text.split("\n").length);

                        Component content = list.content();
                        content.clear();

                        CreditsBlock txt = new CreditsBlock(true, Window.TITLE_COLOR, new SeedFinder().findSeed(itemList, floor));
                        txt.visible = false;
                        txt.setRect((Camera.main.width - colWidth) / 2f, 12, colWidth, 0);
                        content.add(txt);

                        content.setSize(fullWidth, txt.bottom() + 10);

                        list.setRect(0, 0, w, h);
                        list.scrollTo(0, 0);

                        result = new SeedFinder().findSeed(itemList, floor);
                    }
                } else {
                    SPDSettings.customSeed("");
                    ShatteredPixelDungeon.switchNoFade(TitleScene.class);
                }
            }
        });

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchScene(TitleScene.class);
        System.gc();
    }

    public static class CreditsBlock extends Component {

        boolean large;

        RenderedTextBlock body;

        public CreditsBlock(boolean large, int highlight, String body) {
            super();

            this.large = large;

            this.body = PixelScene.renderTextBlock(body, 6);
            if (highlight != -1)
                this.body.setHightlighting(true, highlight);
            if (large)
                this.body.align(RenderedTextBlock.CENTER_ALIGN);
            add(this.body);
        }

        @Override
        protected void layout() {
            super.layout();

            float topY = top();

            if (large){
                body.maxWidth((int)width());
                body.setPos( x + (width() - body.width())/2f, topY);
            } else {
                topY += 1;
                body.maxWidth((int)width());
                body.setPos( x, topY);
            }

            topY += body.height();

            height = Math.max(height, topY - top());
        }

        @Override
        public void update() {
            super.update();
            if (result != null) {
                body.text(result);
                visible = true;
            }
        }
    }
}
