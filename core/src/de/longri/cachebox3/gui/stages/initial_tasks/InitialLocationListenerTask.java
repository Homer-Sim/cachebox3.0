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
package de.longri.cachebox3.gui.stages.initial_tasks;

import de.longri.cachebox3.PlatformConnector;
import de.longri.cachebox3.locator.Locator;
import de.longri.cachebox3.settings.Config;
import de.longri.cachebox3.utils.IChanged;

/**
 * Created by Longri on 02.08.16.
 */
public final class InitialLocationListenerTask extends AbstractInitTask {

    public InitialLocationListenerTask(String name, int percent) {
        super(name, percent);
    }

    @Override
    public void runnable() {

        //TODO initial with last saved location from settings
        new Locator(null);

        //set Settings Values and change listener

        Locator.setUseHardwareCompass(Config.HardwareCompass.getValue());
        Config.HardwareCompass.addChangedEventListener(new IChanged() {
            @Override
            public void isChanged() {
                Locator.setUseHardwareCompass(Config.HardwareCompass.getValue());
            }
        });

        Locator.setHardwareCompassLevel(Config.HardwareCompassLevel.getValue());
        Config.HardwareCompass.addChangedEventListener(new IChanged() {
            @Override
            public void isChanged() {
                Locator.setHardwareCompassLevel(Config.HardwareCompassLevel.getValue());
            }
        });

        PlatformConnector.initLocationListener();
    }
}