/*
 * Copyright (c) 2015. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.exporter.dto;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SceneDTO {

    private String name;
    private long id;

    private List<ModelInstanceDTO> entities;
    private List<TerrainInstanceDTO> terrains;

    public SceneDTO() {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ModelInstanceDTO> getEntities() {
        return entities;
    }

    public void setEntities(List<ModelInstanceDTO> entities) {
        this.entities = entities;
    }

    public List<TerrainInstanceDTO> getTerrains() {
        return terrains;
    }

    public void setTerrains(List<TerrainInstanceDTO> terrains) {
        this.terrains = terrains;
    }

}