package com.test.hiki.commonFunction;

public class JsonFormatTool {
    /**
     * Unit indent string.
     */
    private static String SPACE = "   ";
    
    /**
     * Returns a formatted JSON string.
     * 
     * @param Unformatted JSON string.
     * @return Formatted JSON string.
     */
    public String formatJson(String json)
    {
        StringBuffer result = new StringBuffer();
        
        int length = json.length();
        int number = 0;
        char key = 0;
        
        //Iterate over the input string.
        for (int i = 0; i < length; i++)
        {
            //1. Get the current character.
            key = json.charAt(i);
            
            //2. If the current character is square brackets and curly braces, do the following:
            if((key == '[') || (key == '{') )
            {
                //(1) print: newline and indent character string if preceded by a character and the character is ":".
                if((i - 1 > 0) && (json.charAt(i - 1) == ':'))
                {
                    result.append('\n');
                    result.append(indent(number));
                }
                
                //(2) print: current character.
                result.append(key);
                
                //(3) before the square brackets, before the curly brackets, after must newline. Print: line feed.
                result.append('\n');
                
                //(4) front square brackets and front curly brackets appear every time; Indent once more. Print: new line indentation.
                number++;
                result.append(indent(number));
                
                //(5) carry out the next cycle.
                continue;
            }
            
            //3. If the current character is followed by square brackets and curly braces, do the following:
            if((key == ']') || (key == '}') )
            {
                //(1) after the square brackets, after the curly brackets, the front must be newline. Print: line feed.
                result.append('\n');
                
                //(2) square brackets and curly braces after each occurrence; The indentation is reduced once. Print: indent.
                number--;
                result.append(indent(number));
                
                //(3) print: current character.
                result.append(key);
                
                //(4) if there are characters after the current character, and the character is not ", ", print: line feed.
                if(((i + 1) < length) && (json.charAt(i + 1) != ','))
                {
                    result.append('\n');
                }
                
                //(5) continue the next cycle.
                continue;
            }
            
            //4, if the current character is a comma. Comma after the line, and indent, do not change the number of indentation.
            if((key == ','))
            {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            
            //5. Print: current character.
            result.append(key);
        }
        
        return result.toString();
    }
    
    /**
     * Returns a specified number of indented strings. Indent three Spaces at a time, or Spaces.
     * 
     * @param number Indentation number.
     * @return A string specifying the number of indents.
     */
    private String indent(int number)
    {
        StringBuffer result = new StringBuffer();
        for(int i = 0; i < number; i++)
        {
            result.append(SPACE);
        }
        return result.toString();
    }
}
