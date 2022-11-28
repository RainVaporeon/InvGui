package com.spiritlight.invgui.cat;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A complex data structure that loosely represents a Cat.<br>
 * Limitations of this object: intelligence must be less than 200, even though there are no hard cap of this.
 */
public class Cat implements ICat {
    private final Random RANDOM = new Random();
    private String name;
    private final String breed;
    private int intelligence;
    private final Map<String, String> relationMap;

        @Override
        public void meow() {
            if(RANDOM.nextBoolean()) {
                System.out.println("*" + name + " meows.*");
            } else {
                System.out.println("*" + name + ": mao*");
            }
        }

        @Override
        public void scratch() {
            if(RANDOM.nextBoolean()) {
                System.out.println("*" + name + " scratches something.*");
            } else {
                System.out.println("*" + name + " accidentally knocks off a vase while scratching something.*");
            }
        }

        @Override
        public void stare() {
            if(RANDOM.nextBoolean()) {
                System.out.println("*" + name + " stares at you.*");
            } else {
                System.out.println("*" + name + " stares into nothingness.*");
            }
        }

        @Override
        public void purr() {
            if(RANDOM.nextBoolean()) {
                System.out.println("*" + name + " purrs.*");
            } else {
                System.out.println("*" + name + " nudges against you and purrs*");
            }
        }

        @Override
        public Cat reproduce() {
            if(this.name.equals("Rain") && this.breed.equals("FishCat")) {
                throw new AssertionError("This specific type of Cat cannot reproduce!");
            }
            return new Cat("Unnamed Cat", breed, this);
        }

        /**
         * Performs a random action for this cat.
         * @throws WorldDominationException if the cat is too smart.
         */
        public void randomAction() {
            int actionNumber = RANDOM.nextInt() % 4;
            if(intelligence >= 200 && !Objects.equals(name, "Rain") && !Objects.equals(breed, "FishCat")) actionNumber = 4;
            switch(actionNumber) {
                case 0:
                    meow();
                    break;
                case 1:
                    purr();
                    break;
                case 2:
                    scratch();
                    break;
                case 3:
                    stare();
                    break;
                case 4:
                    // world domination
                    throw new WorldDominationException(this);
            }
        }

    /**
     * Creates a Cat with a name. It's always an orange cat that's kind of dum.
     * @param name The name of your newly created Cat.
     */
    public Cat(String name) {
        this.name = name;
        this.breed = "Orange";
        this.intelligence = 80;
        this.relationMap = Maps.newHashMap();
    }

    /**
     * Creates a Cat with not only the name, but also the breed. The intelligence will be ranging from 50 to 150.
     * @param name The name of the cat.
     * @param breed The breed of the cat.
     */
    public Cat(String name, String breed) {
        this.name = name;
        this.breed = breed;
        this.intelligence = (int) ((RANDOM.nextDouble() + 0.5) * 100);
        this.relationMap = Maps.newHashMap();
    }

    /**
     * Constructs a cat with name, breed and intelligence. Fully sandbox.
     * @param name The name of your cat.
     * @param breed The breed of your cat.
     * @param intelligence The intelligence of your cat.
     */
    public Cat(String name, String breed, int intelligence) {
        this.name = name;
        this.breed = breed;
        this.intelligence = intelligence;
        this.relationMap = Maps.newHashMap();
    }

    /**
     * Constructs a cat with name, breed and intelligence, even parents?!<br>
     * This will create a {@code treeNodeId} entry in the relation map if not already existed.<br>
     * 0 if the id cannot be parsed, otherwise it's whatever the inherited cat has, added by one.
     * @param name The cat name.
     * @param breed Their breed.
     * @param intelligence The intelligence of it
     * @param inherit The cat it inherits from.
     */
    public Cat(String name, String breed, int intelligence, Cat inherit) {
        this.name = name;
        this.breed = breed;
        this.intelligence = intelligence;
        Map<String, String> futureRelation = new HashMap<>(inherit.getRelationMap());
        int treeNode;
        try {
            treeNode = Integer.parseInt(futureRelation.get("treeNodeId"));
        } catch (NumberFormatException | NullPointerException ex) {
            treeNode = 0;
        }
        futureRelation.put("treeNodeId", String.valueOf(treeNode + 1));
        this.relationMap = futureRelation;
    }

    /**
     * Constructs a fully custom cat.
     * @param name You should already know what this does.
     * @param breed This too.
     * @param intelligence As well as this.
     * @param relationMap The relationMap of this Cat object.
     */
    public Cat(String name, String breed, int intelligence, Map<String, String> relationMap) {
        this.name = name;
        this.breed = breed;
        this.intelligence = intelligence;
        this.relationMap = relationMap;
    }

    /**
     * Internal uses. For the constructed Cat to invoke the {@link ICat#reproduce()} method.
     * @param name The new Cat name, defaults to be Unnamed Cat.
     * @param breed The breed, default inheriting from the parent.
     * @param inherit The parent it should inherit from.
     */
    @SuppressWarnings("SameParameterValue") // shut u
    private Cat(String name, String breed, Cat inherit) {
        if(inherit.name.equals("Rain") && inherit.breed.equals("FishCat")) {
            throw new RuntimeException("Invalid inheritance!");
        }
        this.name = name;
        this.breed = breed;
        int iq = (int) ((RANDOM.nextDouble() + 0.5) * inherit.intelligence);
        this.intelligence = (iq >= 200 ? 199 : iq); // Prevents world domination
        Map<String, String> futureRelation = new HashMap<>(inherit.getRelationMap());
        int treeNode;
        try {
            treeNode = Integer.parseInt(futureRelation.get("treeNodeId"));
        } catch (NumberFormatException | NullPointerException ex) {
            // Either doesn't exist or some dumb fuck decided to make a weird ID
            treeNode = 0;
        }
        futureRelation.put("treeNodeId", String.valueOf(treeNode));
        this.relationMap = futureRelation;
    }

    public String getBreed() {
        return breed;
    }

    public int getIntelligence() {
        return intelligence;
    }

    /**
     * Utility method that converts intelligence to a {@link String}
     * @return The String representation of this cat's intelligence
     */
    public String getIntString() {
        return String.valueOf(intelligence);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getRelationMap() {
        return relationMap;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Serializes the relation map. This cannot be deserialized.
     * @return A serialized {@link com.google.gson.JsonObject} representing this object.
     */
    public JsonObject serializeRelationMap() {
        JsonObject ret = new JsonObject();
        for(Map.Entry<String, String> entry : relationMap.entrySet()) {
            ret.addProperty(entry.getKey(), entry.getValue());
        }
        return ret;
    }
}
