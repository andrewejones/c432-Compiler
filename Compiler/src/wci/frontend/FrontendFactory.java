package wci.frontend;

import wci.frontend.c.CParserTD;
import wci.frontend.c.CScanner;

/**
 * <h1>FrontendFactory</h1>
 *
 * <p>A factory class that creates parsers for specific source languages.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class FrontendFactory
{
    /**
     * Create a parser.
     * @param language the name of the source language (e.g., "C").
     * @param type the type of parser (e.g., "top-down").
     * @param source the source object.
     * @return the parser.
     * @throws Exception if an error occurred.
     */
    public static Parser createParser(String language, String type,
                                      Source source)
        throws Exception
    {
        if (language.equalsIgnoreCase("C") &&
            type.equalsIgnoreCase("top-down"))
        {
            Scanner scanner = new CScanner(source);
            return new CParserTD(scanner);
        }
        else if (!language.equalsIgnoreCase("C")) {
            throw new Exception("Parser factory: Invalid language '" +
                                language + "'");
        }
        else {
            throw new Exception("Parser factory: Invalid type '" +
                                type + "'");
        }
    }
}
