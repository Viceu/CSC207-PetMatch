package use_case.edit;

import entities.User;

import java.util.Map;

public class EditOutputData {

    final private String petname;
    // info regarding new pet passed out to presenter
    final private String pet_bio;
    final private String owner;
    final private User user;

    private  boolean editFail;

    public EditOutputData(User user, String petname, String pet_bio, String owner, boolean editFail) {
        this.petname = petname;
        this.pet_bio = pet_bio;
        this.owner = owner;
        this.editFail = editFail;
        this.user = user;
    }

    public String getPetname() {
        return petname;
    }

    public String getPet_bio() {
        return pet_bio;
    }

    public String getOwner() {
        return owner;
    }
    public User getUser() {
        return user;
    }

}
