package com.pmart5a.myclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarDealership {

    public static final int TIME_TO_SHOP = 1000;
    public static final int CAR_SUPPLY_PLAN = 10;
    private final List<Car> cars = new ArrayList<>(CAR_SUPPLY_PLAN);
    private final CarFactory carFactory = new CarFactory();
    private boolean stopSales = false;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public CarFactory getCarFactory() {
        return carFactory;
    }

    public void acceptCars() {
        for (int i = 0; i < CAR_SUPPLY_PLAN; i++) {
            acceptOneCar();
            try {
                Thread.sleep(TIME_TO_SHOP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopSales = true;
        Thread.currentThread().interrupt();
    }

    public List<Car> sellCars() {
        List<Car> purchasedCars = new ArrayList<>();
        while (!stopSales) {
            sellOneCar(purchasedCars);
        }
        System.out.printf("%s, всего куплено автомобилей: %d.\n", Thread.currentThread().getName(), purchasedCars.size());
        Thread.currentThread().interrupt();
        return purchasedCars;
    }

    public void acceptOneCar() {
        try {
            lock.lock();
            getCars().add(carFactory.assembleTheCar());
            System.out.printf("%s: выпущен 1 автомобиль.\n", Thread.currentThread().getName());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void sellOneCar(List<Car> purchasedCars) {
        try {
            lock.lock();
            System.out.printf("%s зашёл в автосалон.\n", Thread.currentThread().getName());
            if (getCars().isEmpty()) {
                System.out.println("Машин нет!");
                condition.await();
            } else {
                Car car = getCars().remove(0);
                Thread.sleep(TIME_TO_SHOP);
                System.out.printf("%s уехал на новом авто марки '%s' модель '%s %d'.\n",
                        Thread.currentThread().getName(), car.getCarBrand(), car.getCarModel(),
                        car.getEnginePower());
                purchasedCars.add(car);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private List<Car> getCars() {
        return cars;
    }
}