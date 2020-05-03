package net.sourceforge.plantuml.servlet.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UmlDecorator {

    private static final Logger LOGGER = Logger.getLogger(UmlDecorator.class.getName());

    private static Map<String, String> preUmlIncludes = new HashMap<String, String>();

    public static String decorateUml(String uml) {
        StringBuilder decoratedUmlBuilder = new StringBuilder();
        Scanner umlScanner = new Scanner(uml);
        String umlStart = umlScanner.nextLine();
        String type = umlStart.replace("@start", "").trim().toUpperCase();
        decoratedUmlBuilder.append(umlStart).append("\n");
        decoratedUmlBuilder.append(getPreUmlInclude(type));
        while (umlScanner.hasNextLine()) {
            decoratedUmlBuilder.append(umlScanner.nextLine()).append("\n");
        }
        return decoratedUmlBuilder.toString();
    }

    private static String getPreUmlIncludePath(String type) {
        return System.getenv("PLANTUML_PRE_INCLUDE_" + type);
    }

    private static String getPreUmlInclude(String type) {
        String preUmlInclude = preUmlIncludes.get(type);
        if (preUmlInclude != null) {
            return preUmlInclude;
        } else if (getPreUmlIncludePath(type) != null) {
            try {
                preUmlInclude = readUmlFile(getPreUmlIncludePath(type));
                preUmlIncludes.put(type, preUmlInclude);
                return preUmlInclude;

            } catch (FileNotFoundException e) {
                LOGGER.log(Level.WARNING, "The given include file could not be found", e);
                return "";
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Include file could not be loaded", e);
                return "";
            }
        }
        return "";
    }

    private static String readUmlFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder includeUmlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Filter out any @start../@end.. lines
                if (!line.startsWith("@")) {
                    includeUmlBuilder.append(line).append("\n");
                }
            }
            return includeUmlBuilder.toString();

        }
    }

    protected UmlDecorator() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }
}
