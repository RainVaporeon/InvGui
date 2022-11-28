package com.spiritlight.invgui.cat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CatBuilder {
    private String name;
    private String breed;
    private int intelligence;
    private Map<String, String> relationMap;

    public CatBuilder() {}

    // reminder to not make it too smart
    public CatBuilder setIntelligence(int intelligence) {
        this.intelligence = intelligence;
        return this;
    }

    public CatBuilder setBreed(String breed) {
        this.breed = breed;
        return this;
    }

    public CatBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CatBuilder putRelationMap(String name, String content) {
        relationMap.put(name, content);
        return this;
    }

    public CatBuilder putRelationMap(@NotNull Map<String, String> map) {
        this.relationMap = Stream.concat(relationMap.entrySet().stream(), map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this;
    }

    public CatBuilder setRelationMap(Map<String, String> map) {
        relationMap = new HashMap<>(map);
        return this;
    }

    public CatBuilder setInherit(@NotNull Cat inherit) {
        putRelationMap("parentBreed", inherit.getBreed());
        putRelationMap("parentIntelligence", inherit.getIntString());
        return setInherit(inherit.getName());
    }

    private CatBuilder setInherit(String name) {
        relationMap.put("parent", name);
        return this;
    }

    /**
     * Builds a cat
     * @return Cat with supplied parameters.
     */
    public Cat build() {
        return new Cat(name, breed, intelligence, relationMap);
    }
}
