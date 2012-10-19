package models;

import javax.persistence.*;

import java.security.MessageDigest;

import play.db.ebean.Model;

import play.data.validation.Constraints.*;

import difflib.*;

@Entity
public class Revision extends Model {

  @Id
  public String uuid;

  public String compilationErrors;

  public String arguments;
  
  public Patch patch;

  public long revision;

  @Required
  public String compilerLevel;

  @Required
  public String classname;

  //Also may need to store the Class file in here
  
  public static Finder<String, Revision> find = 
    new Finder<String, Revision>(String.class, Revision.class);
  
  public static void create(Revision project) {
    project.save();
  }

}
