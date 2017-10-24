package cz.fi.muni.carshop;

import com.sun.corba.se.impl.protocol.RequestCanceledException;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.junit.rules.ExpectedException;
import org.junit.Rule;

import cz.fi.muni.carshop.entities.Car;
import cz.fi.muni.carshop.enums.CarTypes;
import cz.fi.muni.carshop.exceptions.RequestedCarNotFoundException;
import cz.fi.muni.carshop.services.CarShopStorageService;
import cz.fi.muni.carshop.services.CarShopStorageServiceImpl;
import java.util.Optional;
import org.junit.Assert;

public class CarShopStorageServiceTest {

	private CarShopStorageService service = new CarShopStorageServiceImpl();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test()
	public void testPriceCantBeNegative() {
		// JUnit 4.11
		//thrown.expect(IllegalArgumentException.class);
		// JUnit 4.12
		 thrown.reportMissingExceptionWithMessage("We expect exception on"
                         + "negative price").expect(IllegalArgumentException.class);

		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, -1));
	}

	@Test
	public void testGetCar() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));

		assertTrue(service.isCarAvailable(Color.BLACK, CarTypes.AUDI).isPresent());
	}

	@Test
	public void testCarShopStorage_containsTypeForExistingCar() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));
		Map<CarTypes, List<Car>> cars = CarShopStorage.getInstancce().getCars();

		assertThat(cars, hasKey(CarTypes.AUDI));
	}
        
	// expected to fail with JUnit < 4.11
	@Test
	public void testGetCheaperCars_returnsCorrectResult() {
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000));
		service.addCarToStorage(new Car(Color.BLACK, CarTypes.AUDI, 2016, 889000));
		service.addCarToStorage(new Car(Color.WHITE, CarTypes.AUDI, 2016, 859000));
		service.addCarToStorage(new Car(Color.BLUE, CarTypes.AUDI, 2016, 909000));
                service.addCarToStorage(new Car(Color.BLUE, CarTypes.AUDI, 2014, 909000));

		assertThat(service.getCheaperCarsOfSameTypeAndYear(new Car(Color.BLACK, CarTypes.AUDI, 2016, 900000)),
				hasSize(3));

	}
        
        
        @Test
        public void testSellCar_OK(){
            
            Optional<Car> cars = service.isCarAvailable(Color.BLACK, CarTypes.AUDI);
            
            Car car  = new Car(Color.BLACK, CarTypes.AUDI, 2016, 899000);
            service.addCarToStorage(car);
            
            try {
                service.sellCar(car);
            } catch (RequestedCarNotFoundException ex) {
                Assert.fail();
            }
            
            assertTrue(service.isCarAvailable(Color.BLACK, CarTypes.AUDI).equals(cars));
        }
        
        @Test
        public void testSellCar_NOK() throws RequestedCarNotFoundException {
            thrown.reportMissingExceptionWithMessage("We expect RequestedCarNotFound exception")
                    .expect(RequestedCarNotFoundException.class);
            
            Car car  = new Car(Color.BLACK, CarTypes.AUDI, 2014, 899000);
            service.sellCar(car);
        }

}
