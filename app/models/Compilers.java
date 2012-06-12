package models;

import java.util.*;

/**
 * Provides a list of compilers that can be used.
 */
public class Compilers {

  public static List<String> all = new ArrayList<String>();

  public static List<String> list() {
    all.add("JRE 7");
    all.add("JRE 6");
    //all.add("jsJVM");
    return all;
  }
}
