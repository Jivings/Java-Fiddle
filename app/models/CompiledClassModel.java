package models;

import javax.persistence.*;

import java.security.MessageDigest;

import play.db.ebean.Model;

import play.data.validation.Constraints.*;


@Entity
public class CompiledClassModel extends Model {

  @Id
  public String id;

  public MessageDigest hash;

  @Column(length=9999)
  @Required
  public byte[] classdata;


  public String classname;

  public String arguments;
  //Also may need to store the Class file in here
  
  public static Finder<String, CompiledClassModel> find = 
    new Finder<String, CompiledClassModel>(String.class, CompiledClassModel.class);
  

}
