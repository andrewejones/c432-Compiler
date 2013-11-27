package wci.frontend.c.tokens;

import wci.frontend.*;
import wci.frontend.c.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.frontend.c.CTokenType.*;

/**
 * <h1>CNumberToken</h1>
 *
 * <p>C number tokens (integer and real).</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CNumberToken extends CToken
{

    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public CNumberToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a C number token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();  // token's characters
        extractNumber(textBuffer);
        text = textBuffer.toString();
    }

    /**
     * Extract a C number token from the source.
     * @param textBuffer the buffer to append the token's characters.
     * @throws Exception if an error occurred.
     */
    protected void extractNumber(StringBuilder textBuffer)
        throws Exception
    {
        String wholeDigits = null;     // digits before the decimal point
        String fractionDigits = null;  // digits after the decimal point
        char currentChar;              // current character

        type = INT;  // assume INTEGER token type for now

        // Extract the digits of the whole part of the number.
        wholeDigits = unsignedIntegerDigits(textBuffer);
        if (type == ERROR) {
            return;
        }

        // Is there a . ?
        currentChar = currentChar();
        if (currentChar == '.') {
            type = FLOAT;  // decimal point, so token type is REAL
            textBuffer.append(currentChar);
            currentChar = nextChar();  // consume decimal point

            // Collect the digits of the fraction part of the number.
            fractionDigits = unsignedIntegerDigits(textBuffer);
            if (type == ERROR) {
                return;
            }
        }

        currentChar = currentChar();

        // Compute the value of an integer number token.
        if (type == INT) {
            int integerValue = computeIntegerValue(wholeDigits);

            if (type != ERROR) {
                value = new Integer(integerValue);
            }
        }

        // Compute the value of a real number token.
        else if (type == FLOAT) {
            float floatValue = computeFloatValue(wholeDigits, fractionDigits);
            if (type != ERROR) {
                value = new Float(floatValue);
            }
        }
    }

    /**
     * Extract and return the digits of an unsigned integer.
     * @param textBuffer the buffer to append the token's characters.
     * @return the string of digits.
     * @throws Exception if an error occurred.
     */
    private String unsignedIntegerDigits(StringBuilder textBuffer)
        throws Exception
    {
        char currentChar = currentChar();

        // Must have at least one digit.
        if (!Character.isDigit(currentChar)) {
            type = ERROR;
            value = INVALID_NUMBER;
            return null;
        }

        // Extract the digits.
        StringBuilder digits = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            textBuffer.append(currentChar);
            digits.append(currentChar);
            currentChar = nextChar();  // consume digit
        }

        return digits.toString();
    }

    /**
     * Compute and return the integer value of a string of digits.
     * Check for overflow.
     * @param digits the string of digits.
     * @return the integer value.
     */
    private int computeIntegerValue(String digits)
    {
        // Return 0 if no digits.
        if (digits == null) {
            return 0;
        }

        int integerValue = 0;
        int prevValue = -1;    // overflow occurred if prevValue > integerValue
        int index = 0;

        // Loop over the digits to compute the integer value
        // as long as there is no overflow.
        while ((index < digits.length()) && (integerValue >= prevValue)) {
            prevValue = integerValue;
            integerValue = 10*integerValue +
                           Character.getNumericValue(digits.charAt(index++));
        }

        // No overflow:  Return the integer value.
        if (integerValue >= prevValue) {
            return integerValue;
        }

        // Overflow:  Set the integer out of range error.
        else {
            type = ERROR;
            value = RANGE_INTEGER;
            return 0;
        }
    }

    /**
     * Compute and return the float value of a real number.
     * @param wholeDigits the string of digits before the decimal point.
     * @param fractionDigits the string of digits after the decimal point.
     * @return the float value.
     */
    private float computeFloatValue(String wholeDigits, String fractionDigits)
    {
    	double floatValue = 0.0;
        int exponentValue = 0;
        String digits = wholeDigits;  // whole and fraction digits

        // If there are any fraction digits, adjust the exponent value
        // and append the fraction digits.
        if (fractionDigits != null) {
            exponentValue -= fractionDigits.length();
            digits += fractionDigits;
        }

        // Check for a real number out of range error.
        if (Math.abs(exponentValue + wholeDigits.length()) > 37) {
            type = ERROR;
            value = RANGE_REAL;
            return 0.0f;
        }

        // Loop over the digits to compute the float value.
        int index = 0;
        while (index < digits.length()) {
            floatValue = 10*floatValue +
                         Character.getNumericValue(digits.charAt(index++));
        }

        // Adjust the float value based on the exponent value.
        if (exponentValue != 0) {
            floatValue *= Math.pow(10, exponentValue);
        }

        return (float) floatValue;
    }
}
