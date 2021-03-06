/*
 * Copyright (C) 2014-2017 team-cachebox.de
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
package de.longri.cachebox3.settings;

import de.longri.cachebox3.CB;
import de.longri.cachebox3.settings.types.*;
import de.longri.cachebox3.utils.HSV_Color;

/**
 * Holds all skin relevant settings
 * <p>
 * Created by Longri on 12.01.2017.
 */
public abstract class Settings_Skin extends Settings_Map {

    public static final SettingFolder SkinFolder = (SettingFolder) SettingsList.addSetting(new SettingFolder("SkinFolder", SettingCategory.Folder, DEVELOPER, CB.WorkPath + "/skins", SettingStoreType.Global, SettingUsage.ACB, false));

    public static final SettingBool nightMode = (SettingBool) SettingsList.addSetting(new SettingBool("nightMode", SettingCategory.Internal, NEVER, false, SettingStoreType.Global, SettingUsage.ACB));


    public static final SettingString daySkinName = (SettingString) SettingsList.addSetting(new SettingString("daySkinName", SettingCategory.Skin, NORMAL, "internalDefault", SettingStoreType.Global, SettingUsage.ACB));
    public static final SettingString nightSkinName = (SettingString) SettingsList.addSetting(new SettingString("nightSkinName", SettingCategory.Skin, NORMAL, "internalNight", SettingStoreType.Global, SettingUsage.ACB));


    public static final SettingBool useMipMap = (SettingBool) SettingsList.addSetting(new SettingBool("useMipMap", SettingCategory.Skin, EXPERT, false, SettingStoreType.Global, SettingUsage.ACB));
    public static final SettingBool dontUseAmbient = (SettingBool) SettingsList.addSetting(new SettingBool("dontUseAmbient", SettingCategory.Skin, EXPERT, true, SettingStoreType.Global, SettingUsage.ACB));
    public static final SettingInt ambientTime = (SettingInt) SettingsList.addSetting(new SettingInt("ambientTime", SettingCategory.Skin, EXPERT, 10, SettingStoreType.Global, SettingUsage.ACB));

    public static final SettingColor SolvedMysteryColor = (SettingColor) SettingsList.addSetting(new SettingColor("SolvedMysteryColor", SettingCategory.Skin, EXPERT, new HSV_Color(0.2f, 1f, 0.2f, 1f), SettingStoreType.Global, SettingUsage.ACB));

}
