package models;

import javax.persistence.*;

import java.security.MessageDigest;

import play.db.ebean.Model;


@Entity
public class RevisionModel extends Model {

  public String uuid;

  public long number;

  public String diff;

  public static Finder<String, RevisionModel> find = 
    new Finder<String, RevisionModel>(String.class, RevisionModel.class);
  
  public static void create(RevisionModel rev) {
    rev.save();
  }

}
