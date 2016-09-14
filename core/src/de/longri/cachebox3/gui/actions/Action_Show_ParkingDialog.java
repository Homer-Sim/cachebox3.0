/* 
 * Copyright (C) 2014-2016 team-cachebox.de
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
package de.longri.cachebox3.gui.actions;


import com.badlogic.gdx.graphics.g2d.Sprite;
import de.longri.cachebox3.CB;
import de.longri.cachebox3.gui.Window;
import de.longri.cachebox3.gui.dialogs.*;
import de.longri.cachebox3.gui.menu.MenuID;
import de.longri.cachebox3.translation.Translation;
import de.longri.cachebox3.utils.IconNames;
import org.slf4j.LoggerFactory;

/**
 * Created by Longri on 14.09.2016.
 */
public class Action_Show_ParkingDialog extends AbstractAction {
    public Action_Show_ParkingDialog() {
        super("MyParking", MenuID.AID_SHOW_PARKING_DIALOG);
    }

    @Override
    public void execute() {
        ParkingDialog d = new ParkingDialog();
        d.show();
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    @Override
    public Sprite getIcon() {
        return CB.getSprite(IconNames.my_parking.name());
    }
}
