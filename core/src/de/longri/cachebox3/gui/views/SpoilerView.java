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

import de.longri.cachebox3.gui.menu.Menu;

/**
 * Created by Longri on 14.09.2016.
 */
public class SpoilerView extends AbstractView {
    public SpoilerView() {
        super("SpoilerView");
    }

    @Override
    public void dispose() {

    }

    public Menu getContextMenu() {
        return null; //TODO
    }
}
