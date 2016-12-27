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
package de.longri.cachebox3.gui.map.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.ScaledSvg;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import de.longri.cachebox3.PlatformConnector;
import de.longri.cachebox3.gui.events.CacheListChangedEventList;
import de.longri.cachebox3.gui.events.CacheListChangedEventListener;
import de.longri.cachebox3.gui.map.layer.cluster.ClusterSymbol;
import de.longri.cachebox3.gui.map.layer.cluster.ItemizedClusterLayer;
import de.longri.cachebox3.locator.geocluster.ClusterRunnable;
import de.longri.cachebox3.locator.geocluster.GeoCluster;
import de.longri.cachebox3.logging.Logger;
import de.longri.cachebox3.logging.LoggerFactory;
import de.longri.cachebox3.sqlite.Database;
import de.longri.cachebox3.types.Cache;
import de.longri.cachebox3.types.CacheTypes;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.map.Map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Longri on 27.11.16.
 */
public class WaypointLayer extends ItemizedClusterLayer<GeoCluster> implements CacheListChangedEventListener, Disposable {
    final static Logger log = LoggerFactory.getLogger(WaypointLayer.class);

    private static final ClusterSymbol defaultMarker = getClusterSymbol("myterie");
    protected final List<GeoCluster> mAllItemList = new ArrayList<GeoCluster>();
    private double lastFactor = 2.0;

    public WaypointLayer(Map map) {
        super(map, new ArrayList<GeoCluster>(), defaultMarker, null);
        mOnItemGestureListener = gestureListener;

        //register as cacheListChanged eventListener
        CacheListChangedEventList.Add(this);
        CacheListChangedEvent();
    }

    private final OnItemGestureListener<GeoCluster> gestureListener = new OnItemGestureListener<GeoCluster>() {
        @Override
        public boolean onItemSingleTapUp(int index, GeoCluster item) {
            return false;
        }

        @Override
        public boolean onItemLongPress(int index, GeoCluster item) {
            return false;
        }
    };

    @Override
    public void dispose() {
        CacheListChangedEventList.Remove(this);
        ClusterSymbolHashMap.clear();
        mOnItemGestureListener = null;
    }

    @Override
    public void CacheListChangedEvent() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (Database.Data.Query) {

                    //clear item list
                    mItemList.clear();

                    //add WayPoint items
                    for (Cache cache : Database.Data.Query) {
                        double lat = cache.Latitude(), lon = cache.Longitude();
                        GeoCluster geoCluster = new GeoCluster(cache.Pos);
                        geoCluster.setCluster(getClusterSymbolByCache(cache));
                        mAllItemList.add(geoCluster);
                    }
                    mItemList.addAll(mAllItemList);
                    WaypointLayer.this.populate();
                    reduceCluster(0.0003);
                }
            }
        });
        thread.start();

    }

    Thread clusterThread;
    ClusterRunnable clusterRunnable;


    private void reduceCluster(final double factor) {

        if (lastFactor == factor) {
            log.debug("GeoClustering  no factor changes");
            return;
        }

        if (clusterThread != null && clusterThread.isAlive()) {
            clusterRunnable.terminate();
            try {
                clusterThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        log.debug("START GeoClustering factor:" + factor + " ZoomLevel:" + lastZoomLevel);


        final List<GeoCluster> workList;
        if (factor < lastFactor) {
            workList = mAllItemList;
        } else {
            workList = mItemList;
        }


        lastFactor = factor;
        clusterRunnable = new ClusterRunnable(factor, workList, new ClusterRunnable.CallBack() {
            @Override
            public void callBack(List<GeoCluster> reduced) {
                log.debug("Cluster Reduce from " + mItemList.size() + " items to " + reduced.size() + " items");
                mItemList.clear();
                mItemList.addAll(reduced);
                WaypointLayer.this.populate();
            }
        });
        clusterThread = new Thread(clusterRunnable);
        clusterThread.start();
    }


    private final HashMap<String, ClusterSymbol> ClusterSymbolHashMap = new HashMap<String, ClusterSymbol>();


    private ClusterSymbol getClusterSymbolByCache(Cache cache) {
        ClusterSymbol symbol = null;
        String symbolName = getMapIconName(cache);
        symbol = ClusterSymbolHashMap.get(symbolName);
        if (symbol == null) {
            symbol = getClusterSymbol(symbolName);
            ClusterSymbolHashMap.put(symbolName, symbol);
        }
        return symbol;
    }


    private static String getMapIconName(Cache cache) {
        if (cache.ImTheOwner())
            return "star";
        else if (cache.isFound())
            return "mapFound";
        else if ((cache.Type == CacheTypes.Mystery) && cache.CorrectedCoordiantesOrMysterySolved())
            return "mapSolved";
        else if ((cache.Type == CacheTypes.Multi) && cache.HasStartWaypoint())
            return "mapMultiStartP"; // Multi with start point
        else if ((cache.Type == CacheTypes.Mystery) && cache.HasStartWaypoint())
            return "mapMysteryStartP"; // Mystery without Final but with start point
        else
            return "map" + cache.Type.name();

    }

    private static ClusterSymbol getClusterSymbol(String name) {
        Skin skin = VisUI.getSkin();
        ScaledSvg scaledSvg = skin.get(name, ScaledSvg.class);
        FileHandle fileHandle = Gdx.files.internal(scaledSvg.path);
        Bitmap bitmap = null;
        try {
            bitmap = PlatformConnector.getSvg(fileHandle.read(), PlatformConnector.SvgScaleType.DPI_SCALED, scaledSvg.scale);
        } catch (IOException e) {
            return null;
        }
        return new ClusterSymbol(bitmap, ClusterSymbol.HotspotPlace.CENTER, true);
    }


    private int lastZoomLevel = -1;

    public void setZoomLevel(int zoomLevel) {

        log.debug("Set zoom level to " + zoomLevel);

        if (lastZoomLevel == zoomLevel) {
            log.debug("no zoom level changes");
            return;
        }
        lastZoomLevel = zoomLevel;

        double factor = 0.0;

        if (lastZoomLevel < 15) {
            if (lastZoomLevel > 13) {
                factor = 0.0008;
            } else {
                factor = 0.002;
            }
        }

        log.debug("call reduce cluster with factor: " + factor);
        reduceCluster(factor);

    }
}