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
package de.longri.cachebox3.gui.views;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Created by Longri on 12.08.2016.
 */
public abstract class ListView extends WidgetGroup {


    //TODO use extended CachedItemListner and dispose Items that draw state to old (10 sec)


    private static class IndexListAdapter extends ArrayAdapter<Integer, VisTable> {

        interface CreateViewListner {
            VisTable createView(Integer index);
        }

        private float pad, padLeft, padRight, padTop, padBottom;

        private CreateViewListner createViewListner;

        private void setCreateViewListner(CreateViewListner listner) {
            this.createViewListner = listner;
        }

        static Array<Integer> getArray(int size) {
            Array<Integer> items = new Array<Integer>();
            for (int i = 0; i < size; i++) {
                items.add(i);
            }
            return items;
        }

        public IndexListAdapter(int size) {
            super(getArray(size));
        }


        @Override
        public void fillTable(VisTable itemsTable) {
            if (this.createViewListner == null) return;
            for (final Integer item : iterable()) {
                final VisTable view = getView(item);
                prepareViewBeforeAddingToTable(item, view);
                itemsTable.add(view).padLeft(padLeft).padRight(padRight).padTop(padTop).padBottom(padBottom).growX();
                itemsTable.row();
            }
        }


        @Override
        protected VisTable createView(Integer index) {
            return this.createViewListner == null ? null : this.createViewListner.createView(index);
        }


    }

    private final ListViewStyle style;
    private final com.kotcrab.vis.ui.widget.ListView<Integer> listView;
    private boolean needsLayout;
    IndexListAdapter adapter;


    public ListView(int size) {
        this(size, VisUI.getSkin().get("default", ListViewStyle.class));
    }

    public ListView(int size, ListViewStyle style) {
        this.listView = new com.kotcrab.vis.ui.widget.ListView<Integer>(new IndexListAdapter(size));
        this.style = style;
        adapter = ((IndexListAdapter) this.listView.getAdapter());
        adapter.pad = style.pad;
        adapter.padLeft = style.padLeft > 0 ? style.padLeft : style.pad;
        adapter.padRight = style.padRight > 0 ? style.padRight : style.pad;
        adapter.padTop = style.padTop > 0 ? style.padTop : style.pad;
        adapter.padBottom = style.padBottom > 0 ? style.padBottom : style.pad;

        adapter.setCreateViewListner(new IndexListAdapter.CreateViewListner() {
            @Override
            public VisTable createView(Integer index) {
                VisTable table = ListView.this.createView(index);
                if (table == null) return new VisTable(); //return a empty table
                ListViewStyle style = ListView.this.style;
                boolean backGroundChanger = ((index % 2) == 1);
                if (backGroundChanger) {
                    table.setBackground(style.firstItem);
                } else {
                    table.setBackground(style.secondItem != null ? style.secondItem : style.firstItem);
                }
                return table;
            }
        });

        this.rebuildView();
        this.listView.getMainTable().setBackground(style.background);
        this.listView.getScrollPane().setFlickScroll(true);
    }

    public void rebuildView() {
        adapter.itemsChanged();
        reLayout();
    }

    public void reLayout() {
        adapter.itemsDataChanged();
        this.listView.rebuildView();
    }

    public abstract VisTable createView(Integer index);

    @Override
    public void invalidate() {
        super.invalidate();
        needsLayout = true;
    }

    public void layout() {
        if (!needsLayout) return;
        this.clear();
        this.addActor(this.listView.getMainTable());
        this.listView.getMainTable().setBounds(0, 0, this.getWidth(), this.getHeight());
        needsLayout = false;
    }


    public void setBackground(Drawable background) {
        this.listView.getMainTable().setBackground(background);
    }

    protected void sizeChanged() {
        needsLayout = true;
        invalidate();
    }


    public float getScrollPos() {
        return listView.getScrollPane().getScrollY();
    }

    public void setScrollPos(float pos) {
        listView.getScrollPane().setScrollY(pos);
    }


    public static class ListViewStyle {
        public Drawable background, firstItem, secondItem, selectedItem;
        public float pad, padLeft, padRight, padTop, padBottom;
    }


}