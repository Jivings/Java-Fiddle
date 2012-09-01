package models;

import javax.persistence.*;

import java.security.MessageDigest;

import play.db.ebean.Model;

import play.data.validation.Constraints.*;

import difflib.*;

@Entity
public class ProjectModel extends Model {

  @Id
  public long id;

  public String uuid;

  @Required
  public String code;

  public MessageDigest hash;

  public String compilationErrors;

  public String arguments;
  
  public Patch patch;

  public long revision = 0;

  @Required
  public String compilerLevel;

  @Required
  public String classname;

  //Also may need to store the Class file in here
  
  public static Finder<String, ProjectModel> find = 
    new Finder<String, ProjectModel>(String.class, ProjectModel.class);
  
  public static void create(ProjectModel project) {
    project.save();
  }

}
