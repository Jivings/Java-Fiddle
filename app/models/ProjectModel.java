package models;

import javax.persistence.Id;
import javax.persistence.Entity;

import java.security.MessageDigest;

import play.db.ebean.Model;


@Entity
public class ProjectModel extends Model {

    //So the project obj contains the code, hash, uuid, compilation error
    //  And possibly the class file data
  @Id
  public String uuid;

  public String code;

  public MessageDigest hash;

  public String compilationError;

  public String arguments;

  public String compilerLevel;

  //Also may need to store the Class file in here
  
  public static Finder<String, ProjectModel> find = 
    new Finder<String, ProjectModel>(String.class, ProjectModel.class);
  
  public static void create(ProjectModel project) {
    project.save();
  }

}
