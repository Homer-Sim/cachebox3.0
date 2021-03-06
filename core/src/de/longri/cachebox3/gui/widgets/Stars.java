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
package de.longri.cachebox3.gui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Created by Longri on 07.09.2016.
 */
public class Stars extends VisTable {

    public Stars(int value) {

        Skin skin = VisUI.getSkin();
        Drawable star1 = value >= 2 ? skin.getDrawable("star") : value >= 1 ? skin.getDrawable("star_half") : skin.getDrawable("star0");
        Drawable star2 = value >= 4 ? skin.getDrawable("star") : value >= 3 ? skin.getDrawable("star_half") : skin.getDrawable("star0");
        Drawable star3 = value >= 6 ? skin.getDrawable("star") : value >= 5 ? skin.getDrawable("star_half") : skin.getDrawable("star0");
        Drawable star4 = value >= 8 ? skin.getDrawable("star") : value >= 7 ? skin.getDrawable("star_half") : skin.getDrawable("star0");
        Drawable star5 = value == 10 ? skin.getDrawable("star") : value >= 9 ? skin.getDrawable("star_half") : skin.getDrawable("star0");

        this.add(new Image(star1));
        this.add(new Image(star2));
        this.add(new Image(star3));
        this.add(new Image(star4));
        this.add(new Image(star5));

        this.pack();
        this.layout();
    }
}
