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
package de.longri.cachebox3.locator.events;

import de.longri.cachebox3.CB;
import de.longri.cachebox3.GlobalCore;
import de.longri.cachebox3.gui.stages.ViewManager;
import de.longri.cachebox3.locator.GPS;
import de.longri.cachebox3.locator.Location;
import de.longri.cachebox3.locator.Locator;
import de.longri.cachebox3.settings.Config;
import de.longri.cachebox3.sqlite.Database;
import de.longri.cachebox3.types.Cache;
import de.longri.cachebox3.types.CacheTypes;
import de.longri.cachebox3.types.CacheWithWP;
import de.longri.cachebox3.utils.MathUtils;
import de.longri.cachebox3.utils.SoundCache;
import org.slf4j.LoggerFactory;

/**
 * Empfängt alle Positions Änderungen und sortiert Liste oder spielt Sounds ab.
 *
 * @author Longri
 */
public class GlobalLocationReceiver implements PositionChangedEvent, GPS_FallBackEvent {
    final static org.slf4j.Logger log = LoggerFactory.getLogger(GlobalLocationReceiver.class);
    public final static boolean DEBUG_POSITION = true;

    public final static String GPS_PROVIDER = "gps";
    public final static String NETWORK_PROVIDER = "network";

    private boolean initialResortAfterFirstFixCompleted = false;
    private boolean initialFixSoundCompleted = false;
    private static boolean approachSoundCompleted = false;

    public GlobalLocationReceiver() {

        PositionChangedEventList.Add(this);
        GPS_FallBackEventList.Add(this);
        try {
            SoundCache.loadSounds();
        } catch (Exception e) {
            log.error("GlobalLocationReceiver", "Load sound", e);
            e.printStackTrace();
        }
    }

    private static boolean PlaySounds = false;

    @Override
    public void PositionChanged() {

        PlaySounds = !Config.GlobalVolume.getValue().Mute;

        if (newLocationThread != null) {
            if (newLocationThread.getState() != Thread.State.TERMINATED)
                return;
            else
                newLocationThread = null;
        }

        if (newLocationThread == null)
            newLocationThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        if (PlaySounds && !approachSoundCompleted) {
                            if (GlobalCore.isSetSelectedCache()) {
                                float distance = GlobalCore.getSelectedCache().Distance(MathUtils.CalculationType.FAST, false);
                                if (GlobalCore.getSelectedWaypoint() != null) {
                                    distance = GlobalCore.getSelectedWaypoint().Distance();
                                }

                                if (!approachSoundCompleted && (distance < Config.SoundApproachDistance.getValue())) {
                                    SoundCache.play(SoundCache.Sounds.Approach);
                                    approachSoundCompleted = true;

                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("GlobalLocationReceiver", "Global.PlaySound(Approach.ogg)", e);
                        e.printStackTrace();
                    }

                    try {
                        if (!initialResortAfterFirstFixCompleted && Locator.getProvider() != Location.ProviderType.NULL) {
                            if (GlobalCore.getSelectedCache() == null) {
                                synchronized (Database.Data.Query) {
                                    CacheWithWP ret = Database.Data.Query.Resort(GlobalCore.getSelectedCoord(), new CacheWithWP(GlobalCore.getSelectedCache(), GlobalCore.getSelectedWaypoint()));

                                    if (ret != null && ret.getCache() != null) {
                                        GlobalCore.setSelectedWaypoint(ret.getCache(), ret.getWaypoint(), false);
                                        GlobalCore.setNearestCache(ret.getCache());
                                        ret.dispose();
                                        ret = null;
                                    }

                                }
                            }
                            initialResortAfterFirstFixCompleted = true;
                        }
                    } catch (Exception e) {
                        log.error("GlobalLocationReceiver", "if (!initialResortAfterFirstFixCompleted && GlobalCore.LastValidPosition.Valid)", e);
                        e.printStackTrace();
                    }

                    try {
                        // schau die 50 nächsten Caches durch, wenn einer davon näher ist
                        // als der aktuell nächste -> umsortieren und raus
                        // only when showing Map or cacheList
                        if (!Database.Data.Query.ResortAtWork) {
                            if (GlobalCore.getAutoResort()) {
                                if ((GlobalCore.NearestCache() == null)) {
                                    GlobalCore.setNearestCache(GlobalCore.getSelectedCache());
                                }
                                int z = 0;
                                if (!(GlobalCore.NearestCache() == null)) {
                                    boolean resort = false;
                                    if (GlobalCore.NearestCache().isFound()) {
                                        resort = true;
                                    } else {
                                        if (GlobalCore.getSelectedCache() != GlobalCore.NearestCache()) {
                                            GlobalCore.setSelectedWaypoint(GlobalCore.NearestCache(), null, false);
                                        }
                                        float nearestDistance = GlobalCore.NearestCache().Distance(MathUtils.CalculationType.FAST, true);

                                        for (int i = 0, n = Database.Data.Query.size(); i < n; i++) {
                                            Cache cache = Database.Data.Query.get(i);
                                            z++;
                                            if (z >= 50) {
                                                return;
                                            }
                                            if (cache.isArchived())
                                                continue;
                                            if (!cache.isAvailable())
                                                continue;
                                            if (cache.isFound())
                                                continue;
                                            if (cache.ImTheOwner())
                                                continue;
                                            if (cache.Type == CacheTypes.Mystery)
                                                if (!cache.CorrectedCoordiantesOrMysterySolved())
                                                    continue;
                                            if (cache.Distance(MathUtils.CalculationType.FAST, true) < nearestDistance) {
                                                resort = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (resort || z == 0) {
                                        CacheWithWP ret = Database.Data.Query.Resort(GlobalCore.getSelectedCoord(), new CacheWithWP(GlobalCore.getSelectedCache(), GlobalCore.getSelectedWaypoint()));

                                        GlobalCore.setSelectedWaypoint(ret.getCache(), ret.getWaypoint(), false);
                                        GlobalCore.setNearestCache(ret.getCache());
                                        ret.dispose();

                                        SoundCache.play(SoundCache.Sounds.AutoResortSound);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("GlobalLocationReceiver", "Resort", e);
                        e.printStackTrace();
                    }

                }
            });

        newLocationThread.start();

    }

    Thread newLocationThread;

    @Override
    public String getReceiverName() {
        return "GlobalLocationReceiver";
    }

    public static void resetApproach() {

        // set approach sound if the distance low

        if (GlobalCore.isSetSelectedCache()) {
            float distance = GlobalCore.getSelectedCache().Distance(MathUtils.CalculationType.FAST, false);
            boolean value = distance < Config.SoundApproachDistance.getValue();
            approachSoundCompleted = value;
            GlobalCore.switchToCompassCompleted = value;
        } else {
            approachSoundCompleted = true;
            GlobalCore.switchToCompassCompleted = true;
        }

    }

    @Override
    public void OrientationChanged() {
    }

    @Override
    public Priority getPriority() {
        return Priority.High;
    }

    @Override
    public void SpeedChanged() {
    }

    @Override
    public void Fix() {
        PlaySounds = !Config.GlobalVolume.getValue().Mute;

        try {

            if (!initialFixSoundCompleted && Locator.isGPSprovided() && GPS.getFixedSats() > 3) {

                log.debug( "Play Fix");
                if (PlaySounds)
                    SoundCache.play(SoundCache.Sounds.GPS_fix);
                initialFixSoundCompleted = true;
                loseSoundCompleated = false;

            }
        } catch (Exception e) {
            log.error("GlobalLocationReceiver", "Global.PlaySound(GPS_Fix.ogg)", e);
            e.printStackTrace();
        }

    }

    boolean loseSoundCompleated = false;

    @Override
    public void FallBackToNetworkProvider() {
        PlaySounds = !Config.GlobalVolume.getValue().Mute;

        if (initialFixSoundCompleted && !loseSoundCompleated) {

            if (PlaySounds)
                SoundCache.play(SoundCache.Sounds.GPS_lose);

            loseSoundCompleated = true;
            initialFixSoundCompleted = false;
        }
        CB.viewmanager.toast("Network-Position", ViewManager.ToastLength.LONG);
    }

}
