/*
 * Copyright (C) 2017 team-cachebox.de
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
package de.longri.cachebox3.gui.skin.styles;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import de.longri.cachebox3.CB;

/**
 * Created by Longri on 29.01.17.
 */
public class MenuIconStyle extends AbstractIconStyle {


    @Override
    protected int getPrefWidth() {
        return (int) CB.scaledSizes.ICON_WIDTH;
    }

    @Override
    protected int getPrefHeight() {
        return (int) CB.scaledSizes.ICON_WIDTH;
    }

    public Drawable favorit;

    public Drawable docIcon;
    public Drawable addCacheIcon;
    public Drawable deleteIcon;
    public Drawable sortIcon;
    public Drawable filterIcon;
    public Drawable resetFilterIcon;
    public Drawable searchIcon;
    public Drawable importIcon;
    public Drawable cacheListIcon;
    public Drawable addWp;
    public Drawable navigate;
    public Drawable fieldNote;
    public Drawable videoIcon;
    public Drawable voiceRecIcon;
    public Drawable hintIcon;
    public Drawable my_parking;
    public Drawable autoSortOffIcon;
    public Drawable torchOn;
    public Drawable torchOff;
    public Drawable takePhoto;
    public Drawable dayNight;
    public Drawable uploadFieldNote;
    public Drawable manageDB;
    public Drawable settingsIcon;
    public Drawable compassIcon;
    public Drawable creditsIcon;
    public Drawable fieldNoteList;
    public Drawable logViewIcon;
    public Drawable mapIcon;
    public Drawable noteIcon;
    public Drawable solverIcon;
    public Drawable solver2Icon;
    public Drawable imagesIcon;
    public Drawable imagesIconOff;
    public Drawable cb;
    public Drawable tbListIcon;
    public Drawable waypointListIcon;
    public Drawable trackListIcon;
    public Drawable GC_Live;
}
