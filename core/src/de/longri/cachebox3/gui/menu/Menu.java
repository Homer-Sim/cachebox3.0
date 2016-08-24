/*
 * Copyright (C) 2016 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.cachebox3.gui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import de.longri.cachebox3.CB;
import de.longri.cachebox3.gui.Window;
import de.longri.cachebox3.gui.views.ListView;
import de.longri.cachebox3.translation.Translation;
import de.longri.cachebox3.utils.CB_RectF;
import de.longri.cachebox3.utils.lists.CB_List;
import org.slf4j.LoggerFactory;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Longri on 13.08.16.
 */
public class Menu extends Window {
    final static org.slf4j.Logger log = LoggerFactory.getLogger(Menu.class);
    final static boolean ALL = true;

    CB_List<MenuItem> mItems = new CB_List();
    MenuStyle style;
    final String name;
    ListView listView;
    OnItemClickListener onItemClickListener;
    private VisLabel titleLabel, parentTitleLabel;
    protected Menu parentMenu;

    InputListener clickListener = new InputListener() {

        private final CB_RectF backClickReckArea = new CB_RectF();

        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            // close menu if outside of listView
            backClickReckArea.set(Menu.this.mainMenuWidgetGroup.getX(), Menu.this.titleLabel.getY(),
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - Menu.this.titleLabel.getY());
            if (backClickReckArea.contains(x, y)) {
                hide(ALL);
                return true;
            }
            return false;
        }
    };


    public Menu(String name) {
        this.style = VisUI.getSkin().get("default", MenuStyle.class);
        this.name = name;
        this.setStageBackground(style.stageBackground);
    }

    public Menu(String name, MenuStyle style) {
        this.style = style;
        this.name = name;
        this.setStageBackground(style.stageBackground);
    }

    public Menu(String name, String styleName) {
        this(name, VisUI.getSkin().get(styleName, MenuStyle.class));
    }

    public MenuItem addItem(int ID, String StringId, Sprite icon) {
        MenuItem item = addItem(ID, StringId);
        if (icon != null)
            item.setIcon(new SpriteDrawable(icon));
        return item;
    }

    public void addItem(MenuItem menuItem) {
        mItems.add(menuItem);
    }

    public MenuItem addItem(int ID, String StringId) {
        return addItem(ID, StringId, "", false);
    }

    public MenuItem addItem(int ID, String StringId, boolean withoutTranslation) {
        return addItem(ID, StringId, "", withoutTranslation);
    }

    public MenuItem addItem(int ID, String StringId, String appendix, Sprite icon) {
        MenuItem item = addItem(ID, StringId, appendix);
        if (icon != null)
            item.setIcon(new SpriteDrawable(icon));
        return item;
    }

    public MenuItem addItem(int ID, String StringId, String appendix, Drawable icon) {
        MenuItem item = addItem(ID, StringId, appendix);
        if (icon != null)
            item.setIcon(icon);
        return item;
    }

    public MenuItem addItem(int ID, String StringId, String appendix) {
        return addItem(ID, StringId, appendix, false);
    }

    public MenuItem addItem(int index, String text, Drawable drawable, boolean withoutTranslation) {
        MenuItem item = addItem(index, text, "", withoutTranslation);
        if (drawable != null)
            item.setIcon(drawable);
        return item;
    }

    public MenuItem addItem(int ID, String StringId, String appendix, boolean withoutTranslation) {
        String trans;
        if (StringId == null || StringId.equals("")) {
            trans = appendix;
        } else {
            if (withoutTranslation)
                trans = StringId + appendix;
            else
                trans = Translation.Get(StringId) + appendix;
        }

        MenuItem item = new MenuItem(0, ID, "Menu Item@" + ID, this);
        item.setTitle(trans);
        addItem(item);

        return item;
    }

    public MenuItem addCheckableItem(int ID, String StringId, boolean checked) {
        MenuItem item = addItem(ID, StringId, "", false);
        item.setCheckable(true);
        item.setChecked(checked);
        return item;
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void show() {
        initialLayout();
        showWidgetGroup();
        if (this.parentMenu != null) {
            showAsChild();
        }
        this.setTouchable(Touchable.enabled);
        CB.windowStage.addListener(clickListener);
        log.debug("Show menu: " + this.name);
    }


    protected WidgetGroup mainMenuWidgetGroup;

    private void showWidgetGroup() {
        clearActions();
        pack();

        mainMenuWidgetGroup = new WidgetGroup();
        mainMenuWidgetGroup.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        mainMenuWidgetGroup.addActor(this);
        CB.windowStage.addActor(mainMenuWidgetGroup);
        CB.windowStage.setKeyboardFocus(this);
        CB.windowStage.setScrollFocus(this);
        if (this.parentMenu == null)
            addAction(sequence(Actions.alpha(0), Actions.fadeIn(CB.WINDOW_FADE_TIME, Interpolation.fade)));

        //switch input processor to window stage
        CB.inputMultiplexer.removeProcessor(CB.mainStage);
        CB.inputMultiplexer.addProcessor(CB.windowStage);

    }


    private final float MORE_MENU_ANIMATION_TIME = 0.3f;

    private void showAsChild() {
        float nextXPos = Gdx.graphics.getWidth() + CB.scaledSizes.MARGIN;
        parentMenu.mainMenuWidgetGroup.addAction(Actions.moveTo(0 - nextXPos, 0, MORE_MENU_ANIMATION_TIME));

        // remove stageBackground
        this.setStageBackground(null);

        mainMenuWidgetGroup.setPosition(nextXPos, 0);
        mainMenuWidgetGroup.addAction(Actions.moveTo(0, 0, MORE_MENU_ANIMATION_TIME));
        log.debug("Show child menu: " + this.name);
    }

    public void hide() {
        hide(false);
    }

    public void hide(boolean all) {
        if (this.parentMenu != null) {

            if (all) {
                super.hide();
                parentMenu.hide(ALL);
            } else {
                float nextXPos = Gdx.graphics.getWidth() + CB.scaledSizes.MARGIN;
                mainMenuWidgetGroup.addAction(Actions.sequence(Actions.moveTo(0 + nextXPos, 0, MORE_MENU_ANIMATION_TIME), Actions.removeActor()));
                parentMenu.mainMenuWidgetGroup.addAction(Actions.moveTo(0, 0, MORE_MENU_ANIMATION_TIME));
            }
        } else {
            super.hide();
        }

        CB.windowStage.removeListener(clickListener);
        log.debug("Hide menu: " + this.name);
    }


    private void initialLayout() {

        //remove all childs
        this.clear();

        float topY = Gdx.graphics.getHeight() - CB.scaledSizes.MARGIN_HALF;
        float xPos = CB.scaledSizes.MARGIN_HALF;


        // add the titleLabel on top
        if (style.menu_back != null) {
            Image backImage = new Image(style.menu_back);
            backImage.setPosition(xPos, topY - backImage.getHeight());
            xPos += backImage.getWidth() + CB.scaledSizes.MARGIN;
            this.addActor(backImage);
        }

        titleLabel = new VisLabel(this.name, "menu_title_act");

        if (parentMenu != null) {
            parentTitleLabel = new VisLabel(parentMenu.name, "menu_title_parent");
            parentTitleLabel.setPosition(xPos, topY - parentTitleLabel.getHeight());
            xPos += parentTitleLabel.getWidth() + CB.scaledSizes.MARGINx2;
            this.addActor(parentTitleLabel);
        } else {
            //center titleLable
            xPos = (Gdx.graphics.getWidth() - titleLabel.getWidth()) / 2;
        }

        titleLabel.setPosition(xPos, topY - titleLabel.getHeight());
        this.addActor(titleLabel);

        final OnItemClickListener clickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(final MenuItem item) {

                // have the clicked item a moreMenu, just show it
                if (item.hasMoreMenu()) {
                    item.getMoreMenu(Menu.this).show();
                    return;
                }


                if (onItemClickListener != null) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            onItemClickListener.onItemClick(item);
                        }
                    });
                    thread.start();
                }


                //close Menu with sub menu's
                hide(ALL);

            }
        };

        listView = new ListView(mItems.size()) {
            @Override
            public VisTable createView(Integer index) {
                MenuItem item = mItems.get(index);
                item.setOnItemClickListener(clickListener);
                return item;
            }
        };
        listView.setBackground(this.style.background);
        this.addActor(listView);
    }

    @Override
    public void pack() {
        for (MenuItem item : mItems) {
            item.initial();
            item.pack();
        }

        listView.rebuildView();
        super.pack();

        float maxListViewHeight = CB.scaledSizes.WINDOW_HEIGHT - (listView.getHeight() + CB.scaledSizes.MARGINx2);
        listView.setBounds(((CB.windowStage.getWidth() - CB.scaledSizes.WINDOW_WIDTH) / 2f), CB.scaledSizes.MARGIN,
                CB.scaledSizes.WINDOW_WIDTH, maxListViewHeight);
    }

    public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListeners() {
        return this.onItemClickListener;
    }

    public CB_List<MenuItem> getItems() {
        return mItems;
    }

    public void addItems(CB_List<MenuItem> items) {
        mItems.addAll(items);
    }

    public void addDivider() {

        if(this.style.divider!=null){
            MenuItem item = new MenuItem(this);
            item.overrideBackground(this.style.divider);
            addItem(item);
        }

        log.debug("add Divider");
        //TODO add divider item
    }

    public static class MenuStyle {
        public BitmapFont font;
        public Color fontColor;
        public Drawable background, stageBackground, menu_back, menu_for, divider;

    }

}
