package com.pmart5a.myclasses;

import java.util.Random;

public class CarFactory {

    public static final int TIME_ASSEMBLY_CAR = 3000;
    private String carBrand = "ЖМИ";
    private String[] carModels = {"Тарантас", "Вжик", "Вездеход"};
    private int[] enginePowers = {90, 370, 700};

    public CarFactory() {}

    public CarFactory(String carBrand, String[] carModels, int[] enginePowers) {
        this.carBrand = carBrand;
        this.carModels = carModels;
        this.enginePowers = enginePowers;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public String[] getCarModels() {
        return carModels;
    }

    public int[] getEnginePowers() {
        return enginePowers;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public void setCarModels(String[] carModels) {
        this.carModels = carModels;
    }

    public void setEnginePowers(int[] enginePowers) {
        this.enginePowers = enginePowers;
    }

    public Car assembleTheCar() {
        int modelNumber = getCarModelRandom();
        String carModel = carModels[modelNumber];
        int enginePower = enginePowers[modelNumber];
        try {
            Thread.sleep(CarFactory.TIME_ASSEMBLY_CAR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Car(carBrand, carModel, enginePower);
    }

    private int getCarModelRandom() {
        return new Random().nextInt(carModels.length);
    }
}