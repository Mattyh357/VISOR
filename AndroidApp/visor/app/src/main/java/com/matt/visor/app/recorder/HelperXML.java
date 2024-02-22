/**
 * This class is part of the V.I.S.O.R app.
 * The HelperXML is a simpler XML <-> Map<String, String> parser.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperXML {

    /**
     * Converts a single item's data into XML format with default root and item tags.
     *
     * @param data The data to convert into XML.
     * @return XML string representation of the data.
     */
    public static String oneItemToXml(Map<String, String> data) {
        return oneItemToXml(data, "root", "item");
    }

    /**
     * Converts a single item's data into XML format with custom root and item tags.
     *
     * @param data The data to convert into XML.
     * @param root The root element's tag name.
     * @param item The item element's tag name.
     * @return XML string representation of the data.
     */
    public static String oneItemToXml(Map<String, String> data, String root, String item) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<").append(root).append(">\n");

        xml.append(formatItem(data, item));

        xml.append("</").append(root).append(">\n");
        return xml.toString();
    }

    /**
     * Formats a single item's data into an XML element.
     *
     * @param data The item data to format.
     * @param itemName The tag name for each item.
     * @return Formatted XML string of the item.
     */
    public static String formatItem(Map<String, String> data, String itemName) {
        StringBuilder xml = new StringBuilder();

        xml.append("  <").append(itemName).append(">\n");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            xml.append("    <").append(entry.getKey()).append(">")
                    .append(entry.getValue())
                    .append("</").append(entry.getKey()).append(">\n");
        }
        xml.append("  </").append(itemName).append(">\n");

        return xml.toString();
    }

    /**
     * Parses an XML string into a map of data.
     *
     * @param xml The XML string to parse.
     * @return A map containing the parsed data.
     */
    public static Map<String, String> fromXml(String xml) {
        Map<String, String> data = new HashMap<>();

        // Regex pattern to match "<key>value</key>" format
        Pattern pattern = Pattern.compile("<(\\w+)>([^<]+)</\\1>");
        Matcher matcher = pattern.matcher(xml);

        while (matcher.find()) {
            data.put(matcher.group(1), matcher.group(2));
        }

        return data;
    }
}
