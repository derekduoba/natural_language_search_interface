// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.parse;

import org.lemurproject.galago.core.types.DocumentSplit;
import org.lemurproject.galago.utility.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Reads data from a single text file of type HTML, XML or txt.
 *
 * @author trevor
 */
class FileParser extends DocumentStreamParser {

  BufferedReader reader;
  String identifier;

  public FileParser(DocumentSplit split, Parameters parameters) throws IOException {
    super(split, parameters);
//          Parameters parameters, String fileName, BufferedReader bufferedReader) {
    this.identifier = getIdentifier(parameters, getFileName(split));
    this.reader = getBufferedReader(split);
  }

  public String getIdentifier(Parameters parameters, String fileName) {
    String idType = parameters.get("identifier", "filename");
    if (idType.equals("filename")) {
      return fileName;
    } else {
      String id = stripExtensions(fileName);
      id = new File(id).getName();
      return id;
    }
  }

  public String stripExtension(String name, String extension) {
    if (name.endsWith(extension)) {
      name = name.substring(0, name.length() - extension.length());
    }
    return name;
  }

  public String stripExtensions(String name) {
    name = stripExtension(name, ".bz");
    name = stripExtension(name, ".bz2");
    name = stripExtension(name, ".gz");
    name = stripExtension(name, ".html");
    name = stripExtension(name, ".xml");
    name = stripExtension(name, ".txt");
    return name;
  }

  /**
   * finds the first <title> tag in the string
   * UPDATED: 04-2015: Match title tags with attributes
   */
  public String getTitle(String text) {
    String titlePattern = "<title(\\s.*)*>";
    Pattern pattern = Pattern.compile(titlePattern);
    Matcher matcher = pattern.matcher(text);

    if (!matcher.matches()) {
        return "";
    } 
    String titleTag = matcher.group();
    //System.out.println("TITLE TAG IS: " + titleTag);

    int start = matcher.start();
    //int start = text.indexOf("<title ");
    
    //System.out.println("INDEX OF TITLE IS:" + start);
    
    if (start < 0) {
      return "";
    }
    int end = text.indexOf("</title>");
    if (end < 0) {
      return "";
    }

    System.out.println("INDEX OF /TITLE IS:" + end);

    //String testString = new String(text.substring(start + "<title>".length(), end));
    String testString = new String(text.substring(matcher.end(), end));
    
    System.out.println("OUTPUT OF getTitle: " + testString);

    //return new String(text.substring(start + "<title>".length(), end));
    return testString;
  }

  @Override
  public Document nextDocument() throws IOException {
    if (reader == null || !reader.ready()) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      builder.append(line);
      builder.append("\n");
    }

    Document result = new Document();
    result.name = identifier;
    result.text = builder.toString();
    result.metadata.put("title", getTitle(result.text));

    return result;
  }

  @Override
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
      reader = null;
    }
  }
}
