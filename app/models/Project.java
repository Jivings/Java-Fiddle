package models

public class Project extends Model {

    //So the project obj contains the code, hash, uuid, compilation error
    //  And possibly the class file data
  @Id
  public String uuid;

  public String code;

  public MessageDigest hash;

  public String compilationError;

  //Also may need to store the Class file in here
  
  public static Finder<String, Project> find = new Finder<String, Project>(
    String.class, Project.class
  );

}
