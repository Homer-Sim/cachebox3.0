/* 
 * Copyright (C) 2014 team-cachebox.de
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
package de.longri.cachebox3.gui.events;


import de.longri.cachebox3.CB;
import de.longri.cachebox3.settings.Config;
import de.longri.cachebox3.sqlite.Database;
import de.longri.cachebox3.types.Cache;
import de.longri.cachebox3.types.CacheTypes;

import java.util.ArrayList;

/**
 * @author Longri
 */
public class CacheListChangedEventList {
    public static ArrayList<CacheListChangedEventListener> list = new ArrayList<CacheListChangedEventListener>();

    public static void Add(CacheListChangedEventListener event) {
        synchronized (list) {
            if (!list.contains(event))
                list.add(event);
        }
    }

    public static void Remove(CacheListChangedEventListener event) {
        synchronized (list) {
            list.remove(event);
        }
    }

    private static Thread threadCall;

    public static void Call() {
        if (CB.isDisplayOff())
            return;

        synchronized (Database.Data.Query) {
            Cache cache = Database.Data.Query.GetCacheByGcCode("CBPark");

            if (cache != null)
                Database.Data.Query.remove(cache);

            // add Parking Cache
            if (Config.ParkingLatitude.getValue() != 0) {
                cache = new Cache(Config.ParkingLatitude.getValue(), Config.ParkingLongitude.getValue(), "My Parking area", CacheTypes.MyParking, "CBPark");
                Database.Data.Query.add(0, cache);
            }

            //TODO add Live Caches
//            // add all Live Caches
//            for (int i = 0; i < LiveMapQue.LiveCaches.getSize(); i++) {
//                if (FilterInstances.isLastFilterSet()) {
//                    Cache ca = LiveMapQue.LiveCaches.get(i);
//                    if (ca == null)
//                        continue;
//                    if (!Database.Data.Query.contains(ca)) {
//                        if (FilterInstances.getLastFilter().passed(ca)) {
//                            ca.setLive(true);
//                            Database.Data.Query.add(ca);
//                        }
//                    }
//                } else {
//                    Cache ca = LiveMapQue.LiveCaches.get(i);
//                    if (ca == null)
//                        continue;
//                    if (!Database.Data.Query.contains(ca)) {
//                        ca.setLive(true);
//                        Database.Data.Query.add(ca);
//                    }
//                }
//            }
        }

        if (threadCall != null) {
            if (threadCall.getState() != Thread.State.TERMINATED)
                return;
            else
                threadCall = null;
        }

        if (threadCall == null)
            threadCall = new Thread(new Runnable() {

                @Override
                public void run() {
                    synchronized (list) {
                        for (CacheListChangedEventListener event : list) {
                            if (event == null)
                                continue;
                            event.CacheListChangedEvent();
                        }
                    }

                }
            });

        threadCall.start();
    }

}
