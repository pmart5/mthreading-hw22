package com.pmart5a.myclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarDealership {

    public static final int TIME_TO_SHOP = 1000;
    public static final int CAR_SUPPLY_PLAN = 11;
    private final List<Car> cars = new ArrayList<>(CAR_SUPPLY_PLAN);
    private final CarFactory carFactory = new CarFactory();
    private boolean stopSales = false;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Condition conditionCarFactory = lock.newCondition();

    public CarFactory getCarFactory() {
        return carFactory;
    }

    public void acceptCar() {
        try {
            lock.lock();
            for (int i = 0; i < CAR_SUPPLY_PLAN; i++) {
                System.out.printf("%s: собираю автомобиль.\n", Thread.currentThread().getName());
                getCars().add(carFactory.assembleTheCar());
                System.out.printf("%s: выпущен 1 автомобиль.\n", Thread.currentThread().getName());
                if (i == CAR_SUPPLY_PLAN - 1) {
                    stopSales = true;
                    condition.signalAll();
                    break;
                } else {
                    condition.signal();
                    conditionCarFactory.await();
                }
            }
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public List<Car> sellCar() {
        List<Car> purchasedCars = new ArrayList<>();
        while (!stopSales) {
            try {
                lock.lock();
                System.out.printf("%s зашёл в автосалон.\n", Thread.currentThread().getName());
                if (getCars().isEmpty()) {
                    System.out.println("Машин нет!");
                } else {
                    Thread.sleep(TIME_TO_SHOP);
                    Car car = getCars().remove(0);
                    System.out.printf("%s уехал на новом авто марки '%s' модель '%s %d'.\n",
                            Thread.currentThread().getName(), car.getCarBrand(), car.getCarModel(),
                            car.getEnginePower());
                    purchasedCars.add(car);
                    conditionCarFactory.signal();
                }
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }
        System.out.printf("%s, всего куплено автомобилей: %d.\n", Thread.currentThread().getName(), purchasedCars.size());
        Thread.currentThread().interrupt();
        return purchasedCars;
    }

    private List<Car> getCars() {
        return cars;
    }
}