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
package de.longri.cachebox3.gui.activities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import de.longri.cachebox3.CB;
import de.longri.cachebox3.gui.ActivityBase;
import de.longri.cachebox3.gui.menu.Menu;
import de.longri.cachebox3.gui.menu.MenuID;
import de.longri.cachebox3.gui.menu.MenuItem;
import de.longri.cachebox3.gui.menu.OnItemClickListener;
import de.longri.cachebox3.gui.views.listview.Adapter;
import de.longri.cachebox3.gui.views.listview.ListView;
import de.longri.cachebox3.gui.views.listview.ListViewItem;
import de.longri.cachebox3.logging.Logger;
import de.longri.cachebox3.logging.LoggerFactory;
import de.longri.cachebox3.settings.Config;
import de.longri.cachebox3.settings.types.SettingBase;
import de.longri.cachebox3.translation.Translation;

/**
 * Created by Longri on 24.08.2016.
 */
public class Settings_Activity extends ActivityBase {

    final static Logger log = LoggerFactory.getLogger(Settings_Activity.class);
    private static final boolean FORCE = true;
    private VisTextButton btnOk, btnCancel, btnMenu;
    private final SettingsActivityStyle style;

    public Settings_Activity() {
        super("Settings_Activity");
        this.style = VisUI.getSkin().get("default", SettingsActivityStyle.class);
        this.setStageBackground(style.background);
        createButtons();
    }

    @Override
    public void onShow() {
        log.debug("Show Settings");
        Config.SaveToLastValue();
        fillContent();
    }

    @Override
    public void layout() {
        super.layout();

        float x = Gdx.graphics.getWidth() - (CB.scaledSizes.MARGIN + btnCancel.getWidth());
        float y = CB.scaledSizes.MARGIN;

        btnCancel.setPosition(x, y);
        x -= CB.scaledSizes.MARGIN + btnMenu.getWidth();

        btnMenu.setPosition(x, y);
        x -= CB.scaledSizes.MARGIN + btnOk.getWidth();

        btnOk.setPosition(x, y);
        log.debug("Layout Settings");
    }


    private void createButtons() {

        btnOk = new VisTextButton(Translation.Get("save"));
        btnMenu = new VisTextButton("...");
        btnCancel = new VisTextButton(Translation.Get("cancel"));

        this.addActor(btnOk);
        this.addActor(btnMenu);
        this.addActor(btnCancel);

        btnMenu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Menu icm = new Menu(Translation.Get("changeSettingsVisibility"));
                icm.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public boolean onItemClick(MenuItem item) {
                        switch (item.getMenuItemId()) {
                            case MenuID.MI_SHOW_EXPERT:
                                Config.SettingsShowExpert.setValue(!Config.SettingsShowExpert.getValue());
                                Config.SettingsShowAll.setValue(false);
                                layoutActListView(true);
                                return true;

                            case MenuID.MI_SHOW_ALL:
                                Config.SettingsShowAll.setValue(!Config.SettingsShowAll.getValue());
                                Config.SettingsShowExpert.setValue(false);
                                layoutActListView(true);
                                return true;
                            case MenuID.MI_SHOW_Normal:
                                Config.SettingsShowAll.setValue(false);
                                Config.SettingsShowExpert.setValue(false);
                                layoutActListView(true);
                                return true;
                        }
                        return false;
                    }
                });

                if (Config.SettingsShowAll.getValue())
                    Config.SettingsShowExpert.setValue(false);

                boolean normal = !Config.SettingsShowAll.getValue() && !Config.SettingsShowExpert.getValue();

                icm.addCheckableItem(MenuID.MI_SHOW_Normal, "Settings_Normal", normal);
                icm.addCheckableItem(MenuID.MI_SHOW_EXPERT, "Settings_Expert", Config.SettingsShowExpert.getValue());
                icm.addCheckableItem(MenuID.MI_SHOW_ALL, "Settings_All", Config.SettingsShowAll.getValue());
                icm.show();
            }

        });


        btnOk.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

//                String ActionsString = "";
//                int counter = 0;
//                for (int i = 0, n = SettingsItem_QuickButton.tmpQuickList.size(); i < n; i++) {
//                    QuickButtonItem tmp = SettingsItem_QuickButton.tmpQuickList.get(i);
//                    ActionsString += String.valueOf(tmp.getAction().ordinal());
//                    if (counter < SettingsItem_QuickButton.tmpQuickList.size() - 1) {
//                        ActionsString += ",";
//                    }
//                    counter++;
//                }
//                Config.quickButtonList.setValue(ActionsString);

                Config.SaveToLastValue();
                Config.AcceptChanges();
                finish();
            }
        });


        btnCancel.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Config.LoadFromLastValue();
                finish();
            }
        });
    }


    private Array<WidgetGroup> listViews = new Array<WidgetGroup>();
    private Array<String> listViewsNames = new Array<String>();
    Label.LabelStyle nameStyle, descStyle, defaultValuStyle, valueStyle;


    private void fillContent() {

        //set LabelStyles
        nameStyle = new Label.LabelStyle();
        nameStyle.font = style.nameFont;
        nameStyle.fontColor = style.nameFontColor;

        descStyle = new Label.LabelStyle();
        descStyle.font = style.descFont;
        descStyle.fontColor = style.descFontColor;

        defaultValuStyle = new Label.LabelStyle();
        defaultValuStyle.font = style.defaultValueFont;
        defaultValuStyle.fontColor = style.defaultValueFontColor;

        valueStyle = new Label.LabelStyle();
        valueStyle.font = style.valueFont;
        valueStyle.fontColor = style.valueFontColor;


        final Array<de.longri.cachebox3.settings.types.SettingCategory> settingCategories = new Array<de.longri.cachebox3.settings.types.SettingCategory>();
        de.longri.cachebox3.settings.types.SettingCategory[] tmp = de.longri.cachebox3.settings.types.SettingCategory.values();
        for (de.longri.cachebox3.settings.types.SettingCategory item : tmp) {
            if (item != de.longri.cachebox3.settings.types.SettingCategory.Button) {
                settingCategories.add(item);
            }
        }


        Adapter listViewAdapter = new Adapter() {
            @Override
            public int getCount() {
                return settingCategories.size;
            }

            @Override
            public ListViewItem getView(int index) {
                final de.longri.cachebox3.settings.types.SettingCategory category = settingCategories.get(index);
                return getCategoryItem(index, category);
            }

            @Override
            public void update(ListViewItem view) {

            }

            @Override
            public float getItemSize(int position) {
                return 0;
            }
        };

        showListView(new ListView(listViewAdapter, true), Translation.Get("setting"), true);
    }

    private void showListView(ListView listView, String name, boolean animate) {

        float y = btnOk.getY() + btnOk.getHeight() + CB.scaledSizes.MARGIN;


        WidgetGroup widgetGroup = new WidgetGroup();
        widgetGroup.setBounds(CB.scaledSizes.MARGIN, y, Gdx.graphics.getWidth() - CB.scaledSizes.MARGINx2, Gdx.graphics.getHeight() - (y + CB.scaledSizes.MARGIN));

        // title
        WidgetGroup titleGroup = new WidgetGroup();

        float topY = widgetGroup.getHeight() - CB.scaledSizes.MARGIN_HALF;
        float xPos = 0;

        ClickListener backClickListener = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                backClick();
            }
        };

        // add the titleLabel on top
        if (style.backIcon != null && listViewsNames.size > 0) {
            Image backImage = new Image(style.backIcon);
            backImage.setPosition(xPos, 0);
            xPos += backImage.getWidth() + CB.scaledSizes.MARGIN;
            titleGroup.addActor(backImage);
        }

        VisLabel titleLabel = new VisLabel(name, "menu_title_act");

        if (listViewsNames.size > 0) {
            VisLabel parentTitleLabel = new VisLabel(listViewsNames.get(listViewsNames.size - 1), "menu_title_parent");
            parentTitleLabel.setPosition(xPos, 0);
            xPos += parentTitleLabel.getWidth() + CB.scaledSizes.MARGINx2;
            titleGroup.addActor(parentTitleLabel);
        } else {
            //center titleLabel
            xPos = (Gdx.graphics.getWidth() - titleLabel.getWidth()) / 2;
        }

        titleLabel.setPosition(xPos, 0);
        titleGroup.addActor(titleLabel);

        float titleHeight = titleLabel.getHeight() + CB.scaledSizes.MARGIN;
        titleGroup.setBounds(0, Gdx.graphics.getHeight() - (y + titleHeight), Gdx.graphics.getWidth(), titleHeight);
        titleGroup.addListener(backClickListener);
        widgetGroup.addActor(titleGroup);


        listView.setBounds(0, 0, widgetGroup.getWidth(), titleGroup.getY() - CB.scaledSizes.MARGIN);
        listView.layout();
        listView.setBackground(null); // remove default background
        widgetGroup.addActor(listView);


        if (listViews.size > 0) {
            // animate
            float nextXPos = Gdx.graphics.getWidth() + CB.scaledSizes.MARGIN;
            if (animate) {
                listViews.get(listViews.size - 1).addAction(Actions.moveTo(0 - nextXPos, y, Menu.MORE_MENU_ANIMATION_TIME));
                widgetGroup.setPosition(nextXPos, y);
                widgetGroup.addAction(Actions.moveTo(CB.scaledSizes.MARGIN, y, Menu.MORE_MENU_ANIMATION_TIME));
            } else {
                widgetGroup.setPosition(CB.scaledSizes.MARGIN, y);
            }
        }
        listViews.add(widgetGroup);
        listViewsNames.add(name);
        this.addActor(widgetGroup);
    }

    private void backClick() {
        float nextXPos = Gdx.graphics.getWidth() + CB.scaledSizes.MARGIN;

        if (listViews.size == 1) return;

        listViewsNames.pop();
        WidgetGroup actWidgetGroup = listViews.pop();
        WidgetGroup showingWidgetGroup = listViews.get(listViews.size - 1);

        float y = actWidgetGroup.getY();
        actWidgetGroup.addAction(Actions.sequence(Actions.moveTo(nextXPos, y, Menu.MORE_MENU_ANIMATION_TIME), Actions.removeActor()));
        showingWidgetGroup.addAction(Actions.moveTo(CB.scaledSizes.MARGIN, y, Menu.MORE_MENU_ANIMATION_TIME));
    }

    private void layoutActListView(boolean itemCountChanged) {
        //get act listView
        WidgetGroup widgetGroup = listViews.get(listViews.size - 1);
        ListView actListView = null;
        for (Actor actor : widgetGroup.getChildren()) {
            if (actor instanceof ListView) {
                actListView = (ListView) actor;
                break;
            }
        }

        if (itemCountChanged) {
            Object object = actListView.getUserObject();
            if (object instanceof de.longri.cachebox3.settings.types.SettingCategory) {
                WidgetGroup group = listViews.pop();
                listViewsNames.pop();

                //remove all Listener
                for (Actor actor : group.getChildren())
                    for (EventListener listener : actor.getListeners())
                        actor.removeListener(listener);

                this.removeActor(group);
                showCategory((de.longri.cachebox3.settings.types.SettingCategory) object, false);
            }
        }


    }

    private ListViewItem getCategoryItem(int listIndex, final de.longri.cachebox3.settings.types.SettingCategory category) {
        ListViewItem table = new ListViewItem(listIndex) {
            @Override
            public void dispose() {
            }
        };

        // add label with category name, align left
        table.left();
        VisLabel label = new VisLabel(category.name());
        label.setAlignment(Align.left);
        table.add(label).pad(CB.scaledSizes.MARGIN).expandX().fillX();

        // add next icon
        Image next = new Image(style.nextIcon);
        table.add(next).width(next.getWidth()).pad(CB.scaledSizes.MARGIN / 2);

        // add clicklistener
        table.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    showCategory(category, true);
                }
            }
        });
        return table;
    }

    private void showCategory(de.longri.cachebox3.settings.types.SettingCategory category, boolean animate) {
        log.debug("Show settings categoriy: " + category.name());


        //get all settings items of this category if the category mode correct

        final Array<SettingBase<?>> categorySettingsList = new Array<SettingBase<?>>();

        boolean expert = Config.SettingsShowAll.getValue() || Config.SettingsShowExpert.getValue();
        boolean developer = Config.SettingsShowAll.getValue();

        for (SettingBase<?> setting : de.longri.cachebox3.settings.types.SettingsList.that) {
            if (setting.getCategory() == category) {
                boolean show = false;

                switch (setting.getMode()) {

                    case DEVELOPER:
                        show = developer;
                        break;
                    case Normal:
                        show = true;
                        break;
                    case Expert:
                        show = expert;
                        break;
                    case Never:
                        show = developer;
                        break;
                }

                if (show) {
                    categorySettingsList.add(setting);
                    log.debug("    with setting for: " + setting.getName());
                }
            }
        }

        // show new ListView for this category

        Adapter listViewAdapter = new Adapter() {
            @Override
            public int getCount() {
                return categorySettingsList.size;
            }

            @Override
            public ListViewItem getView(int index) {
                final SettingBase<?> setting = categorySettingsList.get(index);
                return getSettingItem(index, setting);
            }

            @Override
            public void update(ListViewItem view) {

            }

            @Override
            public float getItemSize(int index) {
                return 0;
            }
        };

        ListView newListView = new ListView(listViewAdapter);
        newListView.setUserObject(category);
        showListView(newListView, category.name(), animate);
    }

    private ListViewItem getSettingItem(int listIndex, SettingBase<?> setting) {
        if (setting instanceof de.longri.cachebox3.settings.types.SettingBool) {
            return getBoolView(listIndex, (de.longri.cachebox3.settings.types.SettingBool) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingIntArray) {
            return getIntArrayView((de.longri.cachebox3.settings.types.SettingIntArray) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingStringArray) {
            return getStringArrayView((de.longri.cachebox3.settings.types.SettingStringArray) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingTime) {
            return getTimeView((de.longri.cachebox3.settings.types.SettingTime) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingInt) {
            return getIntView(listIndex, (de.longri.cachebox3.settings.types.SettingInt) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingDouble) {
            return getDblView(listIndex, (de.longri.cachebox3.settings.types.SettingDouble) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingFloat) {
            return getFloatView(listIndex, (de.longri.cachebox3.settings.types.SettingFloat) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingFolder) {
            return getFolderView((de.longri.cachebox3.settings.types.SettingFolder) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingFile) {
            return getFileView((de.longri.cachebox3.settings.types.SettingFile) setting);

//            }  else if (setting instanceof de.longri.cachebox3.settings.types.SettingEnum<?>) {
//                return getEnumView((de.longri.cachebox3.settings.types.SettingEnum<?>) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingString) {
            return getStringView((de.longri.cachebox3.settings.types.SettingString) setting);
//        } else if (setting instanceof SettingsListCategoryButton) {
//            return getButtonView((SettingsListCategoryButton<?>) setting);
//        } else if (setting instanceof SettingsListGetApiButton) {
//            return getApiKeyButtonView((SettingsListGetApiButton<?>) setting);
//        } else if (setting instanceof SettingsListButtonLangSpinner) {
//            return getLangSpinnerView((SettingsListButtonLangSpinner<?>) setting);
//        } else if (setting instanceof SettingsListButtonSkinSpinner) {
//            return getSkinSpinnerView((SettingsListButtonSkinSpinner<?>) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingsAudio) {
            return getAudioView((de.longri.cachebox3.settings.types.SettingsAudio) setting);
        } else if (setting instanceof de.longri.cachebox3.settings.types.SettingColor) {
            return getColorView((de.longri.cachebox3.settings.types.SettingColor) setting);
        }

        return null;
    }

    private ListViewItem getColorView(de.longri.cachebox3.settings.types.SettingColor setting) {
        return null;
    }

    private ListViewItem getAudioView(de.longri.cachebox3.settings.types.SettingsAudio setting) {
        return null;
    }

    private ListViewItem getStringView(de.longri.cachebox3.settings.types.SettingString setting) {
        return null;
    }

    private ListViewItem getEnumView(de.longri.cachebox3.settings.types.SettingEnum<?> setting) {
        return null;
    }

    private ListViewItem getFileView(de.longri.cachebox3.settings.types.SettingFile setting) {
        return null;
    }

    private ListViewItem getFolderView(de.longri.cachebox3.settings.types.SettingFolder setting) {
        return null;
    }

    private ListViewItem getFloatView(int listIndex, final de.longri.cachebox3.settings.types.SettingFloat setting) {
        final VisLabel valueLabel = new VisLabel(Float.toString(setting.getValue()), valueStyle);
        ListViewItem table = getNumericItemTable(listIndex, valueLabel, setting);

        // add clickListener
        table.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    new NumericInput_Activity<Float>(setting.getValue()) {
                        public void returnValue(Float value) {
                            setting.setValue(value);
                            WidgetGroup group = listViews.peek();
                            for (Actor actor : group.getChildren()) {
                                if (actor instanceof ListView) {
                                    final ListView listView = (ListView) actor;
                                    final float scrollPos = listView.getScrollPos();
                                    listView.layout(FORCE);
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            listView.setScrollPos(scrollPos);
                                        }
                                    });

                                }
                            }
                        }
                    }.show();
                }
            }
        });

        return table;
    }

    private ListViewItem getDblView(int listIndex, final de.longri.cachebox3.settings.types.SettingDouble setting) {
        final VisLabel valueLabel = new VisLabel(Double.toString(setting.getValue()), valueStyle);
        ListViewItem table = getNumericItemTable(listIndex, valueLabel, setting);

        // add clickListener
        table.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    new NumericInput_Activity<Double>(setting.getValue()) {
                        public void returnValue(Double value) {
                            setting.setValue(value);
                            WidgetGroup group = listViews.peek();
                            for (Actor actor : group.getChildren()) {
                                if (actor instanceof ListView) {
                                    final ListView listView = (ListView) actor;
                                    final float scrollPos = listView.getScrollPos();
                                    listView.layout(FORCE);
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            listView.setScrollPos(scrollPos);
                                        }
                                    });

                                }
                            }
                        }
                    }.show();
                }
            }
        });

        return table;
    }

    private ListViewItem getIntView(int listIndex, final de.longri.cachebox3.settings.types.SettingInt setting) {
        final VisLabel valueLabel = new VisLabel(Integer.toString(setting.getValue()), valueStyle);
        final ListViewItem table = getNumericItemTable(listIndex, valueLabel, setting);

        // add clickListener
        table.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    new NumericInput_Activity<Integer>(setting.getValue()) {
                        public void returnValue(Integer value) {
                            setting.setValue(value);
                            WidgetGroup group = listViews.peek();
                            for (Actor actor : group.getChildren()) {
                                if (actor instanceof ListView) {
                                    final ListView listView = (ListView) actor;
                                    final float scrollPos = listView.getScrollPos();
                                    listView.layout(FORCE);
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            listView.setScrollPos(scrollPos);
                                        }
                                    });

                                }
                            }
                        }
                    }.show();
                }
            }
        });
        return table;
    }

    private ListViewItem getTimeView(de.longri.cachebox3.settings.types.SettingTime setting) {
        return null;
    }

    private ListViewItem getStringArrayView(de.longri.cachebox3.settings.types.SettingStringArray setting) {
        return null;
    }

    private ListViewItem getIntArrayView(de.longri.cachebox3.settings.types.SettingIntArray setting) {
        return null;
    }

    private ListViewItem getBoolView(int listIndex, final de.longri.cachebox3.settings.types.SettingBool setting) {
        ListViewItem table = new ListViewItem(listIndex) {
            @Override
            public void dispose() {
            }
        };

        // add label with category name, align left
        table.left();
        VisLabel label = new VisLabel(Translation.Get(setting.getName()), nameStyle);
        label.setWrap(true);
        label.setAlignment(Align.left);
        table.add(label).pad(CB.scaledSizes.MARGIN).expandX().fillX();

        // add check icon
        final Image[] checkImage = new Image[1];
        if (setting.getValue()) {
            checkImage[0] = new Image(CB.getSprite("check_on"));
        } else {
            checkImage[0] = new Image(CB.getSprite("check_off"));
        }
        table.add(checkImage[0]).width(checkImage[0].getWidth()).pad(CB.scaledSizes.MARGIN / 2);

        // add clicklistener
        table.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    setting.setValue(!setting.getValue());
                    if (setting.getValue()) {
                        checkImage[0].setDrawable(new SpriteDrawable(CB.getSprite("check_on")));
                    } else {
                        checkImage[0].setDrawable(new SpriteDrawable(CB.getSprite("check_off")));
                    }
                }
            }
        });


        // add description line if description exist
        String description = Translation.Get("Desc_" + setting.getName());
        if (!description.contains("$ID:")) {
            table.row();
            VisLabel desclabel = new VisLabel(description, descStyle);
            desclabel.setWrap(true);
            desclabel.setAlignment(Align.left);
            table.add(desclabel).colspan(2).pad(CB.scaledSizes.MARGIN).expandX().fillX();
        }

        // add defaultValue line

        table.row();
        VisLabel desclabel = new VisLabel("default: " + String.valueOf(setting.getDefaultValue()), defaultValuStyle);
        desclabel.setWrap(true);
        desclabel.setAlignment(Align.left);
        table.add(desclabel).colspan(2).pad(CB.scaledSizes.MARGIN).expandX().fillX();

        return table;
    }

    private ListViewItem getNumericItemTable(int listIndex, VisLabel valueLabel, SettingBase<?> setting) {
        ListViewItem table = new ListViewItem(listIndex) {
            @Override
            public void dispose() {
            }
        };

        // add label with category name, align left
        table.left();
        VisLabel label = new VisLabel(Translation.Get(setting.getName()), nameStyle);
        label.setWrap(true);
        label.setAlignment(Align.left);
        table.add(label).pad(CB.scaledSizes.MARGIN).expandX().fillX();

        // add value lable
        table.add(valueLabel).width(valueLabel.getWidth()).pad(CB.scaledSizes.MARGIN / 2);

        // add description line if description exist
        String description = Translation.Get("Desc_" + setting.getName());
        if (!description.contains("$ID:")) {
            table.row();
            VisLabel desclabel = new VisLabel(description, descStyle);
            desclabel.setWrap(true);
            desclabel.setAlignment(Align.left);
            table.add(desclabel).colspan(2).pad(CB.scaledSizes.MARGIN).expandX().fillX();
        }

        // add defaultValue line

        table.row();
        VisLabel desclabel = new VisLabel("default: " + String.valueOf(setting.getDefaultValue()), defaultValuStyle);
        desclabel.setWrap(true);
        desclabel.setAlignment(Align.left);
        table.add(desclabel).colspan(2).pad(CB.scaledSizes.MARGIN).expandX().fillX();

        return table;
    }


    public static class SettingsActivityStyle extends ActivityBaseStyle {
        public Drawable nextIcon, backIcon;
        public BitmapFont nameFont, descFont, defaultValueFont, valueFont;
        public Color nameFontColor, descFontColor, defaultValueFontColor, valueFontColor;

    }


}
