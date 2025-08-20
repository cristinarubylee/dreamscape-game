package com.badlogic.dream.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Shape2D;

import java.util.ArrayList;
import java.util.List;

public class ColliderComponent implements Component {
    public List<Shape2D> shapes = new ArrayList<>();
    public boolean isStatic = false; // set static for objects like walls/terrain


}
