/* SimpleApp.java */
//import org.apache.spark.api.java.*;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.function.Function;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.lang.String;

public class SimpleApp {
  public static void main(String[] args) {

    String[][] results;
    boolean testPass = true;

    String logFile = "README.md"; // Should be some file on your system
    //SparkConf conf = new SparkConf().setAppName("Simple Application");
    //JavaSparkContext sc = new JavaSparkContext(conf);

    Object[][] urlCollection = new Object[][]{
        {"https://dl.google.com", false},
        {"https://www.googleapis.com", true},
        {"https://drive.google.com", false},
        {"https://google.com", false},
        {"https://yahoo.com", false},
        {"https://amazon.com", false},
        {"https://x.realreadme.com", false},
        {"https://127.0.0.100:8443", false}
    };

    results = new String[urlCollection.length][3];

    System.out.format("%-32s%-10s%-32s%n", "URL", "RESULT", "REASON");

    for (int i = 0; i < urlCollection.length; i++) {
      try {
        URL url = new URL(urlCollection[i][0].toString());

        if ( ! urlProcess(i, url, (Boolean) urlCollection[i][1], results) ) {
            testPass = false;
        }
      }

      catch (MalformedURLException e) {
        System.out.println(String.format("ERROR: %s", e.getMessage()));
        testPass = false;
      }

      System.out.format("%-32s%-10s%-32s%n", results[i][0], results[i][1], results[i][2]);
    }

    System.out.println(String.format("\n\t==== TEST RESULT: %s ====\n", (testPass == true) ? "PASS" : "FAIL"));
    System.exit((testPass == true) ? 0 : 1);
  }

  private static boolean urlProcess(int index, URL url, boolean expected, String[][] results) {

    int responseCode;
    HttpURLConnection conn;

    results[index][0] = url.toString();

    try {
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      responseCode = conn.getResponseCode();

      results[index][2] = String.format("Endpoint reachable, HTTP/%d", responseCode);

      if (responseCode != -1 == expected) {
        results[index][1] = "PASS";
        return true;

      } else {
        results[index][1] = "FAIL";
        return false;
      }
    }

    catch (UnknownHostException e) {
      results[index][2] = "UHX: Host name not resolved";

      if (expected == false) {
        results[index][1] = "PASS";
        return true;

      } else {
        results[index][1] = "FAIL";
        return false;
      }
    }

    catch (IOException e) {
      results[index][2] = String.format("IOX: %s", e.getMessage());

      if (expected == false) {
        results[index][1] = "PASS";
        return true;

      } else {
        results[index][1] = "FAIL";
        return false;
      }
    }

  }

}
