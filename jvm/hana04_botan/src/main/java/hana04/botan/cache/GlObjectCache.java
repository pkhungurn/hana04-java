/*
 * This file is part of Wakame2, a research-oriented physically-based renderer by Pramook Khungurn.
 *
 * Copyright (c) 2016 by Pramook Khungurn.
 *
 *  Wakame2 is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License Version 3
 *  as published by the Free Software Foundation.
 *
 *  Wakame is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package hana04.botan.cache;

import hana04.opengl.wrapper.GlObject;
import hana04.opengl.wrapper.GlWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Singleton
public class GlObjectCache {
  private final GlWrapper glWrapper;

  @Inject
  public GlObjectCache(GlWrapper glWrapper) {
    this.glWrapper = glWrapper;
  }

  private HashMap<GlObjectProvider, GlObjectRecord> records = new HashMap<>();
  private long capacity = (1L << 20) * 4096 * 4;

  public GlObject getGLResource(GlObjectProvider provider) {
    GlObjectRecord record = null;
    if (!records.containsKey(provider)) {
      record = new GlObjectRecord();
      records.put(provider, record);
    } else {
      record = records.get(provider);
    }
    provider.updateGlResource(record);
    record.lastUsage = System.nanoTime();
    return record.resource;
  }

  public void collectGarbage() {
    long size = 0;
    for (GlObjectRecord record : records.values()) {
      size += record.sizeInBytes;
    }

    if (size > capacity) {
      ArrayList<Map.Entry<GlObjectProvider, GlObjectRecord>> recordList
        = new ArrayList<>(records.entrySet());
      Collections.sort(recordList, (o1, o2) -> {
        long t1 = o1.getValue().lastUsage;
        long t2 = o2.getValue().lastUsage;
        if (t1 < t2) {
          return -1;
        } else if (t1 == t2) {
          return 0;
        } else {
          return 1;
        }
      });
      for (int i = 0; i < recordList.size(); i++) {
        Map.Entry<GlObjectProvider, GlObjectRecord> record = recordList.get(i);
        record.getValue().resource.disposeGl();
        records.remove(record.getKey());
        size -= record.getValue().sizeInBytes;
        if (size <= capacity / 2) {
          return;
        }
      }
    }
  }

  public GlWrapper getGlWrapper() {
    return glWrapper;
  }

  public long getCapacity() {
    return capacity;
  }

  public void setCapacity(long capacity) {
    this.capacity = capacity;
  }

  public void disposeGl() {
    for (GlObjectRecord record : records.values()) {
      record.resource.disposeGl();
    }
  }

  public <T extends GlObject> void updateRecord(
    GlObjectRecord record,
    Supplier<T> newObject,
    Supplier<Long> objectVersion,
    Supplier<Integer> objectSizeInBytes,
    Consumer<T> updateObject) {
    T glObject = null;
    boolean needUpdate = false;
    if (record.resource == null) {
      glObject = newObject.get();
      record.resource = glObject;
      needUpdate = true;
    } else if (record.version != objectVersion.get()) {
      glObject = (T) record.resource;
      needUpdate = true;
    }
    if (needUpdate) {
      updateObject.accept(glObject);
      record.version = objectVersion.get();
      record.sizeInBytes = objectSizeInBytes.get();
    }
  }
}
