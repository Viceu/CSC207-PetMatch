package data_access;

import entities.Pet;
import entities.PetFactory;
import api.ApiResults;

import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import use_case.search.SearchPetDataAccessInterface;

public class ApiPetDataAccessObject implements SearchPetDataAccessInterface {

    private final Map<Integer, Pet> profiles = new HashMap<>();

    private PetFactory petFactory;

    /**
     * To declare factory for creating pet
     * @param petFactory
     */
    public ApiPetDataAccessObject(PetFactory petFactory){
        this.petFactory = petFactory;
    }

    /**
     * helper function to transform certain pet data points to Map<String, String>
     * @param petJson given JSON info
     * @param param given information needed to extract
     * @return return info in Map
     */
    public Map<String, String> toMapSS(JSONObject petJson, String param){
        JSONObject row = (JSONObject) petJson.get(param);
        JSONArray keys = row.names();

        Map<String, String> mapped = new HashMap<>();
        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i);
            if(row.get(key)==JSONObject.NULL) {
                mapped.put(key, null);
            }
            else {
                String value = (String) row.get(key);
                mapped.put(key, value);
            }
        }
        return mapped;
    }
    /**
     * helper function to transform certain pet data points to Map<String, Boolean>
     * @param petJson given JSON info
     * @param param given information needed to extract
     * @return return info in Map
     */
    public Map<String, Boolean> toMapSB(JSONObject petJson, String param){
        JSONObject row = (JSONObject) petJson.get(param);
        JSONArray keys = row.names();

        Map<String, Boolean> mapped = new HashMap<>();
        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i);
            if(row.get(key)==JSONObject.NULL) {
                mapped.put(key, null);
            }
            else {
                Boolean value = (Boolean) row.get(key);
                mapped.put(key, value);
            }
        }
        return mapped;
    }

    /**
     * To save local temporary copy of retrieved pet
     * @param pet
     */
    @Override
    public void save(Pet pet) {
        profiles.put(pet.getPetID(), pet);
    }

    /**
     * Return if a pet with given id is in local profiles
     * @param id
     * @return
     */
    @Override
    public boolean existsByName(Integer id) {
        return profiles.containsKey(id);
    }

    /**
     * Return the corresponding pet entity object to the given id
     * @param id
     * @return
     */
    @Override
    public Pet getPet(Integer id) {
        assert existsByName(id);
        return profiles.get(id);
    }

    /**
     * Retrieve pet information from the API given the user requirements
     * @param params user requirements
     * @return Map of pet id to created pet entities
     */
    @Override
    public Map<Integer, Pet> accessApi(Map<String, String> params) {
        Map<Integer, Pet> resultPets = new HashMap<>();

        // Read API retrieved results (List of String)
        List<String> apiResults = ApiResults.getAnimals(params);

        for (String petInfo:apiResults) {

            // Construct a JSONObject using above string for easier parsing
            JSONObject petJson = new JSONObject(petInfo);

            // store all needed data points for construction a Pet object
            Integer petID = Integer.valueOf(String.valueOf(petJson.get("id")));
            String organizationID = String.valueOf(petJson.get("organization_id"));
            String profileURL = String.valueOf(petJson.get("url"));
            String species = String.valueOf(petJson.get("species"));
            String age = String.valueOf(petJson.get("age"));
            String gender = String.valueOf(petJson.get("gender"));
            String size = String.valueOf(petJson.get("size"));
            String name = String.valueOf(petJson.get("name"));
            String description = String.valueOf(petJson.get("description"));
            String status = String.valueOf(petJson.get("status"));
            Boolean adoptable = status.equals("adoptable");
            String coat = String.valueOf(petJson.get("coat"));

            Map<String, String> colorsMap = toMapSS(petJson, "colors");
            List<String> colors = new ArrayList<>(colorsMap.values());
            Map<String, Boolean> attributes = toMapSB(petJson, "attributes");
            Map<String, Boolean> environment = toMapSB(petJson, "environment");
            JSONObject contactRow = (JSONObject) petJson.get("contact");
            Map<String, String> contact = new HashMap<>();
            if(contactRow.get("email")==JSONObject.NULL) {
                contact.put("email", null);
            }
            else {
                contact.put("email", (String) contactRow.get("email"));
            }
            if(contactRow.get("phone")==JSONObject.NULL) {
                contact.put("phone", null);
            }
            else {
                contact.put("phone", (String) contactRow.get("phone"));
            }

            JSONObject BreedsRow = (JSONObject) petJson.get("breeds");
            Map<String, String> breed = new HashMap<>();
            for (int i = 0; i < BreedsRow.names().length (); i++) {
                String key = BreedsRow.names().getString (i);
                if(BreedsRow.get(key)==JSONObject.NULL) {
                    breed.put(key, null);
                }
                else if(BreedsRow.get(key) instanceof Boolean){
                    Boolean boolValue = (Boolean) BreedsRow.get(key);
                    String value = "";
                    if(boolValue){
                        value = "true";
                    }
                    else{
                        value = "false";
                    }
                    breed.put(key, value);
                }
                else {
                    String value = (String) BreedsRow.get(key);
                    breed.put(key, value);
                }
            }

            String bio = null;
            String owner = null;
            Pet pet = petFactory.create(petID, organizationID, profileURL, name, colors,
                    breed, species, coat, age, attributes, environment, description, adoptable,
                    contact, gender, size, bio, owner);

            resultPets.put(petID, pet);
        }

        return resultPets;
    }

    @Override
    public void deleteAll() {
        profiles.clear();
    }

    @Override
    public List<Integer> getIDs() {
        return new ArrayList<Integer>(profiles.keySet());
    }

    /**
     * Returns list of pets exiting in lcoal profiles
     * @return
     */
    @Override
    public List<Pet> getPets() {
        return (List<Pet>) profiles.values();
    }

}