package Utils;

import java.io.*;
import java.util.Properties;

/**
 * Reads properties from a plaintext file
 */
public class PropertyReader {

    private final Properties properties = new Properties();

    /**
     * Reads the properties contained in a plaintext file. The file must be in the format:
     * propertyName = propertyValue
     * otherProperty = otherValue
     * ...
     * @param fileName The path to the plaintext file to read the properties from
     * @return A Property object containing the properties that were read from the file
     */
    public Properties getProperties(String fileName) {
        loadPropertiesFile(fileName);
        return properties;
    }

    /**
     * This method is used to load the properties file
     * @param fileName The path to the file to load the properties from
     */
    private void loadPropertiesFile(String fileName){
        InputStream fis;
        boolean createLocalCopy = false;
        try {
            fis = new FileInputStream("db.props");
        } catch(FileNotFoundException e) {
            fis = getClass().getClassLoader().getResourceAsStream(fileName);
            createLocalCopy = true;
        }
        try {
            properties.load(fis);
            if(createLocalCopy)
                createPropertiesFile();
        } catch (IOException ignored) {

        }
    }

    /**
     * Creates a local "db.props" file that the user can edit based on the one in the classpath
     */
    private void createPropertiesFile() {
        try {
            FileOutputStream outputSteam = new FileOutputStream("db.props");
            properties.store(outputSteam, "Database Properties File");
            outputSteam.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
