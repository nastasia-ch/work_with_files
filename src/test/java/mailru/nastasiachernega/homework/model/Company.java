package mailru.nastasiachernega.homework.model;

import java.util.List;

public class Company {
      public String nameOfCompany;
      public String address;
      public String phoneNumber;
      public int numberOfEmployees;
      public int numberOfDepartments;
      public List<String> productTypes;
      public Structure structure;
      public boolean doesCompanyHaveTaxBenefits;

      public static class Structure {
          public int firstDepartment;
          public int secondDepartment;
          public int thirdDepartment;
          public int forthDepartment;
          public int fifthDepartment;
          public int sixthDepartment;
          public int seventhDepartment;
      }
}

