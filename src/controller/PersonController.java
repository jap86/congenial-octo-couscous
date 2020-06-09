package controller;

import gui.FormPerson;
import model.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonController {
    private boolean tableChanged;
    private boolean personsDeleted;
    private PersonSearchList personSearchList;
    private FileSaver fileSaver;
    private List<Person> personList;

    public PersonController() {
        personSearchList = new PersonSearchList();
        fileSaver = new FileSaver();
        personList = new ArrayList<>();
    }

    public void connectToDatabase() throws SQLException {
        new DatabaseAcces().connect();
    }

    public void disconnectDatabase() throws SQLException {
        new DatabaseAcces().disconnect();
    }

    public void addPerson(FormPerson formPerson) {
        Person person = new Person(
                formPerson.name,
                formPerson.occupation,
                determineAgeCategory(formPerson),
                determineMaritalStatus(formPerson),
                formPerson.isClubMember,
                formPerson.memberId,
                determineGender(formPerson)
        );

        personSearchList.addPerson(person);
        updatePersonList();
        tableChanged = true;
    }

    public void deletePerson(int row) {
        personSearchList.deletePerson(row);
        updatePersonList();
        tableChanged = true;
        personsDeleted = true;
    }

    public List<FormPerson> getFormPersonList() {
        List<FormPerson> formPersonList = new ArrayList<>();

        for (Person person : personList) {
            formPersonList.add(fillFormPerson(person));
        }

        return formPersonList;
    }

    public void save() throws SQLException {
        personSearchList.save();
        updatePersonList();
        tableChanged = false;
        personsDeleted = false;
    }

    public boolean load() throws SQLException {
        personSearchList.findPersons();
        updatePersonList();
        return !personList.isEmpty();
    }

    public void savePersonsToFile(File file) throws IOException {
        fileSaver.savePersonsToFile(file);
    }

    public List<FormPerson> loadFromFile(File file) throws IOException, ClassNotFoundException {
        personList.clear();
        fileSaver.loadPersonsFromFile(file);
        fileSaver.getResult();

        return getFormPersonList();
    }

    private FormPerson fillFormPerson(Person person) {
        FormPerson formPerson = new FormPerson();

        formPerson.name = person.getName();
        formPerson.occupation = person.getOccupation();
        formPerson.ageCategory = fillAgeCategory(person.getAgeCategory());
        formPerson.maritalStatus = fillMaritalStatus(person.getMaritalStatus());
        formPerson.isClubMember = person.getIsClubMember();
        formPerson.memberId = person.getMemberID();
        formPerson.gender = fillGender(person.getGender());

        return formPerson;
    }

    private String fillMaritalStatus(MaritalStatus maritalStatus) {
        switch (maritalStatus) {
            case SINGLE : return "single";
            case COHABITING: return  "cohabiting";
            case MARRIED: return "married";
            case DIVORCED: return "divorced";
            case WIDOWED: return "widowed";
            default: return "";
        }
    }

    private String fillGender(Gender gender) {
        switch (gender) {
            case MALE : return "male";
            case FEMALE: return "female";
            default: return "";
        }
    }

    private Integer fillAgeCategory(AgeCategory ageCategory) {
        switch (ageCategory) {
            case CHILD : return 0;
            case ADULT: return 1;
            case SENIOR: return 2;
            default: return null;
        }
    }

    private AgeCategory determineAgeCategory(FormPerson formPerson) {
        switch (formPerson.ageCategory) {
            case 0: return AgeCategory.CHILD;
            case 1: return AgeCategory.ADULT;
            case 2: return AgeCategory.SENIOR;
            default:return null;
        }
    }

    private MaritalStatus determineMaritalStatus(FormPerson formPerson) {
        switch (formPerson.maritalStatus) {
            case "single": return MaritalStatus.SINGLE;
            case "married": return MaritalStatus.MARRIED;
            case "cohabiting": return MaritalStatus.COHABITING;
            case "divorced": return MaritalStatus.DIVORCED;
            case "widowed": return MaritalStatus.WIDOWED;
            default: return null;
        }
    }

    private Gender determineGender(FormPerson formPerson) {
        switch (formPerson.gender) {
            case "male" : return Gender.MALE;
            case "female" : return Gender.FEMALE;
            default: return null;
        }
    }

    private void updatePersonList() {
        personList = personSearchList.getResult();
    }

    public boolean isTableChanged() {
        return tableChanged;
    }

    public boolean arePersonsDeleted() {
        return personsDeleted;
    }
}
