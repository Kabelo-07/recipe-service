package com.km.recipe.dto;

public enum CategoryDTO {
    VEGETARIAN("Vegetarian"),
    COMFORT("Comfort"),
    BEEF("Beef"),
    CHICKEN("Chicken"),
    OTHER("Other");

    private final String value;
    CategoryDTO(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
