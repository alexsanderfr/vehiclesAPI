package com.udacity.vehicles.api;

import com.jayway.jsonpath.JsonPath;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.Collections;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        BDDMockito.given(carService.save(ArgumentMatchers.any())).willReturn(car);
        BDDMockito.given(carService.findById(ArgumentMatchers.any())).willReturn(car);
        BDDMockito.given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     *
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        URI createUri = URI.create("/cars");
        mvc.perform(
                MockMvcRequestBuilders.post(createUri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     *
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();
        URI uri = URI.create("/cars");
        MvcResult createResult = mvc.perform(
                MockMvcRequestBuilders.post(uri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Integer id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        MvcResult listResult = mvc.perform(
                MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();

        Integer idFromList = JsonPath.read(listResult.getResponse().getContentAsString(), "$._embedded.carList[0].id");
        Assert.assertEquals(id, idFromList);
    }

    /**
     * Tests the read operation for a single car by ID.
     *
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        URI createUri = URI.create("/cars");
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(createUri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        URI findUri = URI.create("/cars/" + id);
        mvc.perform(
                MockMvcRequestBuilders.get(findUri)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    /**
     * Tests for successfully updating car in the system
     *
     * @throws Exception when update fails in the system
     */
    @Test
    public void updateCar() throws Exception {
        Car car = getCar();
        URI createUri = URI.create("/cars");
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(createUri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        car.setCondition(Condition.NEW);
        URI updateUri = URI.create("/cars/" + id);
        mvc.perform(
                MockMvcRequestBuilders.put(updateUri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    /**
     * Tests the deletion of a single car by ID.
     *
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        URI createUri = URI.create("/cars");
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders.post(createUri)
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        URI deleteUri = URI.create("/cars/" + id);
        mvc.perform(
                MockMvcRequestBuilders.delete(deleteUri)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     *
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }
}