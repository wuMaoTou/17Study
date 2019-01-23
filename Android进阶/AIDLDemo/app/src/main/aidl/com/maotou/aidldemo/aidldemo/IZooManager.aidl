// IZooManager.aidl
package com.maotou.aidldemo.aidldemo;

import com.maotou.aidldemo.aidldemo.Animal;
// Declare any non-default types here with import statements

interface IZooManager {
    void addAnimal(in Animal animal);

    List<Animal> getAnimalList();
}
