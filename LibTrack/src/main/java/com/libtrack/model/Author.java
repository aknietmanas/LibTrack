package com.libtrack.model;

import javafx.beans.property.*;


public class Author {

    private final IntegerProperty authorId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty biography;
    private final IntegerProperty birthYear;
    private final StringProperty country;

    public Author() {
        this(0, "", "", "", 0, "");
    }

    public Author(int authorId, String firstName, String lastName,
                  String biography, int birthYear, String country) {
        this.authorId = new SimpleIntegerProperty(authorId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.biography = new SimpleStringProperty(biography);
        this.birthYear = new SimpleIntegerProperty(birthYear);
        this.country = new SimpleStringProperty(country);
    }



    public int getAuthorId() { return authorId.get(); }
    public void setAuthorId(int value) { authorId.set(value); }
    public IntegerProperty authorIdProperty() { return authorId; }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String value) { firstName.set(value); }
    public StringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String value) { lastName.set(value); }
    public StringProperty lastNameProperty() { return lastName; }

    public String getBiography() { return biography.get(); }
    public void setBiography(String value) { biography.set(value); }
    public StringProperty biographyProperty() { return biography; }

    public Integer getBirthYear() { return birthYear.get(); }
    public void setBirthYear(int value) { birthYear.set(value); }
    public IntegerProperty birthYearProperty() { return birthYear; }

    public String getCountry() { return country.get(); }
    public void setCountry(String value) { country.set(value); }
    public StringProperty countryProperty() { return country; }


    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}