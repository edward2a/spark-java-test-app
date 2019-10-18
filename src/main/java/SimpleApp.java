/* SimpleApp.java */
//import org.apache.spark.api.java.*;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.function.Function;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.lang.Boolean;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class SimpleApp {
  public static void main(String[] args) {

    String[][] results;
    boolean testPass = true;

    //SparkConf conf = new SparkConf().setAppName("Simple Application");
    //JavaSparkContext sc = new JavaSparkContext(conf);

    if (System.getProperty("config.location") == null) {
        System.out.println("ERROR: property config.location is null");
        System.exit(1);
    }

    List<List<Object>> urlCollection = getConfig(System.getProperty("config.location"));
    int collectionLength = urlCollection.toArray().length;

    results = new String[collectionLength][3];

    System.out.format("%-32s%-10s%-32s%n", "URL", "RESULT", "REASON");

    for (int i = 0; i < collectionLength; i++) {
      try {
        URL url = new URL(urlCollection.get(i).get(0).toString());

        if ( ! urlProcess(i, url, Boolean.valueOf(urlCollection.get(i).get(1).toString()), results) ) {
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

  @SuppressWarnings("unchecked")
  private static List<List<Object>> getConfig(String configLocation) {

    JSONObject urlCollection = new JSONObject();

    if (! configLocation.startsWith("gs://")) {
        System.out.format("ERROR: URL not supported %s\n", configLocation);
        System.exit(1);
    }

    configLocation = configLocation.replace("gs://", "");
    String bucketName = configLocation.split("/", 2)[0];
    String srcFilename = configLocation.split("/", 2)[1];

    Storage storage = StorageOptions.getDefaultInstance().getService();
    Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
    String data = new String(blob.getContent());

    JSONParser parser = new JSONParser();

    try {
        urlCollection = (JSONObject) parser.parse(data);
    }

    catch (ParseException e) {
        System.out.format("ERROR: Unable to parse JSON object - %s\n", e.getMessage());
        System.exit(1);
    }

    List<List<Object>> urls = (List<List<Object>>) urlCollection.get("urls");

    return urls;
  }

}
