/*
 * Copyright (C) 2016 - 2017 team-cachebox.de
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
package de.longri.cachebox3.gui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import de.longri.cachebox3.CB;
import de.longri.cachebox3.gui.Window;
import de.longri.cachebox3.gui.skin.styles.ButtonDialogStyle;
import de.longri.cachebox3.gui.skin.styles.IconsStyle;
import de.longri.cachebox3.translation.Translation;

/**
 * Created by Longri on 03.08.16.
 */
public class ButtonDialog extends Window {

    // see for layout help ==> https://github.com/libgdx/libgdx/wiki/Table


    static public final int BUTTON_POSITIVE = 1;
    static public final int BUTTON_NEUTRAL = 2;
    static public final int BUTTON_NEGATIVE = 3;


    protected final OnMsgBoxClickListener msgBoxClickListener;
    private ButtonDialogStyle style;
    private Label msgLabel;
    private Label titleLabel;
    private boolean dontRenderDialogBackground = false;
    private Object data;
    private boolean mHasTitle = false;


    protected Table titleTable, contentBox, buttonTable;

    private Skin skin;
    private ObjectMap<Actor, Object> values = new ObjectMap();
    private String titleText;

    public ButtonDialog(String name, String msg, String title, MessageBoxButtons buttons, MessageBoxIcon icon, OnMsgBoxClickListener Listener) {
        this(name, getMsgContentTable(msg, icon), title, buttons, icon, Listener);

    }

    private static Table getMsgContentTable(String msg, MessageBoxIcon icon) {
        Skin skin = VisUI.getSkin();

        ButtonDialogStyle style = skin.get("default", ButtonDialogStyle.class);
        Table contentTable = new Table(skin);
        if (icon != MessageBoxIcon.None && icon != null) {
            Image iconImage = new Image(getIcon(icon));
            contentTable.add(iconImage).width(iconImage.getWidth()).top().pad(CB.scaledSizes.MARGIN);
        }
        contentTable.defaults().space(2).padLeft(3).padRight(3);

        Label msgLabel = new Label(msg, new Label.LabelStyle(style.titleFont, style.titleFontColor));
        msgLabel.setWrap(true);
        contentTable.add(msgLabel).expandX().fillX();
        return contentTable;
    }


    public ButtonDialog(String name, Table contentTable, String title, MessageBoxButtons buttons, MessageBoxIcon icon, OnMsgBoxClickListener Listener) {
        super(name);
        this.contentBox = contentTable;
        this.skin = VisUI.getSkin();
        setSkin(this.skin);
        style = skin.get("default", ButtonDialogStyle.class);
        this.setStageBackground(style.stageBackground);
        if (title != null) {
            this.mHasTitle = true;
            this.titleText = title;
            add(titleTable = new Table(skin)).left();
            titleTable.defaults().padLeft(style.title.getLeftWidth()).padRight(style.title.getLeftWidth())
                    .padTop(style.title.getTopHeight()).padBottom(style.title.getBottomHeight());
            row();
            titleLabel = new Label(titleText, new Label.LabelStyle(style.titleFont, style.titleFontColor));
            titleTable.add(titleLabel).left();
        } else {
            titleLabel = null;
        }

        add(this.contentBox).expand().fill().padLeft(CB.scaledSizes.MARGIN).padRight(CB.scaledSizes.MARGIN);
        row();

        add(buttonTable = new Table(skin)).expand().fill().padLeft(CB.scaledSizes.MARGIN).padRight(CB.scaledSizes.MARGIN)
                .padBottom(CB.scaledSizes.MARGIN / 2).padTop(CB.scaledSizes.MARGIN);


        buttonTable.defaults().padLeft(style.footer.getLeftWidth()).padRight(style.footer.getRightWidth()).padBottom(CB.scaledSizes.MARGIN);

        buttonTable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!values.containsKey(actor)) return;
                while (actor.getParent() != buttonTable)
                    actor = actor.getParent();
                result(values.get(actor));
            }
        });

        this.layout();

        setButtonCaptions(buttons);
        msgBoxClickListener = Listener;
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float versatz = !mHasTitle ? 0 : style.title.getBottomHeight();
        if (style.header != null && !dontRenderDialogBackground) {
            style.header.draw(batch, this.getX(), (contentBox.getY() + versatz + contentBox.getHeight() - style.header.getMinHeight()) + this.getY(),
                    this.getWidth(), style.header.getMinHeight() + versatz);
        }
        if (style.footer != null && !dontRenderDialogBackground) {
            style.footer.draw(batch, this.getX(), this.getY(), this.getWidth(), buttonTable.getHeight() + (2 * CB.scaledSizes.MARGIN));
        }
        if (style.center != null && !dontRenderDialogBackground) {
            style.center.draw(batch, this.getX(), contentBox.getY() + this.getY(), this.getWidth(),
                    contentBox.getHeight());
        }

        if (mHasTitle) {
            // TODO handle drawing if Title width to long for window
            if (style.title != null && !dontRenderDialogBackground) {
                style.title.draw(batch, this.getX(), this.getY() + titleTable.getY(), titleTable.getWidth() + style.title.getRightWidth(), titleTable.getHeight());
            }
        }
        super.drawChildren(batch, parentAlpha);
    }


    @Override
    public void pack() {
        super.pack();
        setPosition(((Gdx.graphics.getWidth() - getWidth()) / 2f),
                ((Gdx.graphics.getHeight() - getHeight()) / 2));
    }

    private static Drawable getIcon(MessageBoxIcon msgIcon) {
        if (msgIcon == null)
            return null;

        IconsStyle style = VisUI.getSkin().get(IconsStyle.class);

        switch (msgIcon) {
            case Asterisk:
                return style.Asterisk;
            case Error:
                return style.Error;
            case Exclamation:
                return style.Exclamation;
            case Hand:
                return style.Hand;
            case Information:
                return style.Information;
            case None:
                return null;
            case Question:
                return style.Question;
            case Stop:
                return style.Stop;
            case Warning:
                return style.Warning;
            case Powerd_by_GC_Live:
                return style.Powerd_by_GC_Live;
            case GC_Live:
                return style.GC_Live;
            default:
                return null;
        }
    }

    private void setButtonCaptions(MessageBoxButtons buttons) {
        float buttonWidth = 0;

        float maxWindowWidth = CB.scaledSizes.WINDOW_WIDTH;

        if (buttons == MessageBoxButtons.YesNoRetry) {
            buttonWidth = (maxWindowWidth / 3) - (4 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("yes"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("no"), buttonWidth, BUTTON_NEGATIVE);
            this.button(Translation.Get("retry"), buttonWidth, BUTTON_NEUTRAL);
        } else if (buttons == MessageBoxButtons.AbortRetryIgnore) {
            buttonWidth = (maxWindowWidth / 3) - (4 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("abort"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("retry"), buttonWidth, BUTTON_NEUTRAL);
            this.button(Translation.Get("ignore"), buttonWidth, BUTTON_NEGATIVE);
        } else if (buttons == MessageBoxButtons.OK) {
            buttonWidth = (maxWindowWidth / 1) - (2 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("ok"), buttonWidth, BUTTON_POSITIVE);
        } else if (buttons == MessageBoxButtons.OKCancel) {
            buttonWidth = (maxWindowWidth / 2) - (3 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("ok"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("cancel"), buttonWidth, BUTTON_NEGATIVE);
        } else if (buttons == MessageBoxButtons.RetryCancel) {
            buttonWidth = (maxWindowWidth / 2) - (3 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("retry"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("cancel"), buttonWidth, BUTTON_NEGATIVE);
        } else if (buttons == MessageBoxButtons.YesNo) {
            buttonWidth = (maxWindowWidth / 2) - (3 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("yes"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("no"), buttonWidth, BUTTON_NEGATIVE);
        } else if (buttons == MessageBoxButtons.YesNoCancel) {
            buttonWidth = (maxWindowWidth / 3) - (4 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("yes"), buttonWidth, BUTTON_POSITIVE);
            this.button(Translation.Get("no"), buttonWidth, BUTTON_NEGATIVE);
            this.button(Translation.Get("cancel"), buttonWidth, BUTTON_NEUTRAL);
        } else if (buttons == MessageBoxButtons.Cancel) {
            buttonWidth = (maxWindowWidth / 1) - (2 * CB.scaledSizes.MARGIN);
            this.button(Translation.Get("cancel"), buttonWidth, BUTTON_NEGATIVE);
        }
    }

    private void button(String text, float buttonWidth, Object object) {
        VisTextButton button = new VisTextButton(text);
        buttonTable.add(button).width(buttonWidth);
        values.put(button, object);
    }

    protected void result(Object object) {
        if (msgBoxClickListener != null) {
            msgBoxClickListener.onClick((Integer) object, null);
            this.hide();
        }
    }
}