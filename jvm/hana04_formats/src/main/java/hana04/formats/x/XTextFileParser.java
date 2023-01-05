package hana04.formats.x;

import hana04.formats.x.parser.EofException;
import hana04.formats.x.parser.ParseException;
import hana04.formats.x.parser.TextParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A parser for .x file content.
 */
public class XTextFileParser extends TextParser {
  static final Map<String, XType> primitiveTypesByKeyWords;

  static {
    primitiveTypesByKeyWords = new HashMap<String, XType>();
    primitiveTypesByKeyWords.put("BYTE", XPrimitiveType.BYTE);
    primitiveTypesByKeyWords.put("CHAR", XPrimitiveType.CHAR);
    primitiveTypesByKeyWords.put("DOUBLE", XPrimitiveType.DOUBLE);
    primitiveTypesByKeyWords.put("DWORD", XPrimitiveType.DWORD);
    primitiveTypesByKeyWords.put("FLOAT", XPrimitiveType.FLOAT);
    primitiveTypesByKeyWords.put("STRING", XPrimitiveType.STRING);
    primitiveTypesByKeyWords.put("UCHAR", XPrimitiveType.UCHAR);
    primitiveTypesByKeyWords.put("WORD", XPrimitiveType.WORD);
  }

  public XTextFileParser(Reader reader) {
    super(reader);
  }

  void consumeLineComment() throws IOException {
    try {
      char ch = peekNextChar();
      while (ch != '\n') {
        ch = peekNextChar();
      }
    } catch (EofException e) {
      // NO-OP
    }
  }

  void consumeBlockComment() throws IOException {
    char ch;
    try {
      ch = peekNextChar();
    } catch (EofException e) {
      putBack('/');
      return;
    }

    if (ch == '*') {
      while (true) {
        ch = peekNextChar();
        while (ch != '*') {
          ch = peekNextChar();
        }
        ch = peekNextChar();
        if (ch == '/') {
          nextChar();
          return;
        }
      }
    } else {
      putBack('/');
    }
  }

  void consumeWhitespaceAndComment() throws IOException {
    try {
      while (true) {
        consumeWhiteSpace();
        char ch = peek();
        if (ch == '#') {
          consumeLineComment();
        } else if (ch == '/') {
          consumeBlockComment();
        } else {
          return;
        }
      }
    } catch (EofException e) {
      // NO-OP
    }
  }

  void matchToken(String s) throws IOException {
    consumeWhitespaceAndComment();
    matchString(s.trim());
  }

  void matchTokenCaseInsensitive(String s) throws IOException {
    consumeWhitespaceAndComment();
    matchStringCaseInsensitive(s);
  }

  void parseMagic() throws IOException {
    String magic = null;
    try {
      magic = parseFixedLengthString(4);
    } catch (ParseException e) {
      throw new ParseException("incorrect magic");
    }

    if (magic == null || !magic.equals("xof ")) {
      throw new ParseException("incorrect magic");
    }
  }

  void parseHeader(XFile xFile) throws IOException {
    parseMagic();

    xFile.setVersionString(parseVersionString());
    if (!xFile.getVersionString().equals("0302")) {
      throw new ParseException("invalid version string");
    }

    xFile.setFormat(parseFormat());
    if (xFile.getFormat() != XFileFormat.Text) {
      throw new ParseException("format other than text not supported");
    }

    xFile.setFloatSize(parseFloatSize());
  }

  String parseVersionString() throws IOException {
    try {
      return parseFixedLengthString(4);
    } catch (ParseException e) {
      throw new ParseException("could not parse version string");
    }
  }

  XFileFormat parseFormat() throws IOException {
    String formatString;
    try {
      formatString = parseFixedLengthString(4);
    } catch (ParseException e) {
      throw new ParseException("could not parse format");
    }

    if (formatString.equals("txt ")) {
      return XFileFormat.Text;
    } else if (formatString.equals("bin ")) {
      return XFileFormat.Binary;
    } else {
      throw new ParseException("unsuppored format '" + formatString + "'");
    }
  }

  XFileFloatSize parseFloatSize() throws IOException {
    String floatSizeString;
    try {
      floatSizeString = parseFixedLengthString(4);
    } catch (ParseException e) {
      throw new ParseException("could not parse format");
    }

    if (floatSizeString.equals("0032")) {
      return XFileFloatSize.Float32;
    } else if (floatSizeString.equals("0064")) {
      return XFileFloatSize.Float64;
    } else {
      throw new ParseException("invalid float size '" + floatSizeString + "'");
    }
  }

  XField parseField(XFile xFile) throws IOException {
    consumeWhitespaceAndComment();
    try {
      char ch = peek();
      if (ch == '[') {
        return parseChildrenField(xFile);
      } else if (Character.isLetter(ch)) {
        String token = parseIdentifier();
        String upToken = token.toUpperCase();
        putBack(token);
        if (upToken.equals("ARRAY")) {
          return parseArrayField(xFile);
        } else {
          return parseSimpleField(xFile);
        }
      } else {
        throw new ParseException("field expected");
      }
    } catch (EofException e) {
      throw new ParseException("field expected");
    }
  }

  XField parseSimpleField(XFile xfile) throws IOException {
    String token = parseIdentifier();
    String name = parseIdentifier();
    matchToken(";");

    XType type = lookUpType(token, xfile);
    return new XField(name, type);
  }

  XType lookUpType(String token, XFile xFile) {
    String upToken = token.toUpperCase();
    if (primitiveTypesByKeyWords.containsKey(upToken)) {
      return primitiveTypesByKeyWords.get(upToken);
    } else {
      XType type = xFile.lookupTemplateByName(token);
      if (type == null) {
        throw new ParseException("unknown type '" + token + "'");
      } else {
        return type;
      }
    }
  }

  XField parseArrayField(XFile xFile) throws IOException {
    matchTokenCaseInsensitive("array");
    String typeName = parseIdentifier();
    String name = parseIdentifier();
    matchToken("[");
    consumeWhitespaceAndComment();

    char lookAhead = peek();
    XArrayType arrayType = new XArrayType();
    arrayType.elementType = lookUpType(typeName, xFile);
    if (Character.isDigit(lookAhead)) {
      arrayType.fixedLength = true;
      arrayType.numericalLength = parseInt();
    } else {
      arrayType.fixedLength = false;
      arrayType.lengthFieldName = parseIdentifier();
    }
    matchToken("]");
    matchToken(";");

    return new XField(name, arrayType);
  }

  XField parseChildrenField(XFile xFile) throws IOException {
    matchToken("[");
    consumeWhitespaceAndComment();

    XField result = null;
    char lookAhead = peek();
    if (lookAhead == '.') {
      matchString("...");
      result = new XField("[children]", XAnyChildrenType.v());
    } else {
      Collection<XTemplate> templates = parseTemplateSet(xFile);
      result = new XField("[children]",
        new XRestrictedChildrenType(templates));
    }
    matchToken("]");
    return result;
  }

  Collection<XTemplate> parseTemplateSet(XFile xFile) throws IOException {
    char lookAhead = peek();
    HashSet<XTemplate> templateSet = new HashSet<XTemplate>();

    while (Character.isLetter(lookAhead)) {
      String templateName = parseIdentifier();
      XType lookedUp = lookUpType(templateName, xFile);
      if (!(lookedUp instanceof XTemplate)) {
        throw new ParseException("children type '" + templateName + "' is not a template type");
      } else {
        templateSet.add((XTemplate) lookedUp);
      }

      consumeWhitespaceAndComment();

      lookAhead = peek();
      if (lookAhead == ',') {
        matchString(",");
        consumeWhitespaceAndComment();
        lookAhead = peek();
      } else if (lookAhead == ']') {
        // NO-OP
      } else {
        throw new ParseException("encountered unexpected character ('" + lookAhead + "') while parsing children field");
      }
    }

    return templateSet;
  }

  UUID parseUuid() throws IOException {
    matchToken("<");
    String uuidStr = parseFixedLengthString(36);
    matchToken(">");
    return UUID.fromString(uuidStr);
  }

  XTemplate parseTemplate(XFile xFile) throws IOException {
    matchToken("template");
    String name = parseIdentifier();
    matchToken("{");

    UUID uuid = parseUuid();
    XTemplate result = new XTemplate(name, uuid);

    consumeWhitespaceAndComment();
    char lookAhead = peek();
    while (lookAhead != '}') {
      XField field = parseField(xFile);
      result.addField(field);
      consumeWhitespaceAndComment();
      lookAhead = peek();

      if (field.type instanceof XChildrenType) {
        if (lookAhead != '}') {
          throw new ParseException("found extra field after the children field");
        }
      }
    }

    matchToken("}");
    return result;
  }

  public Object parseData(XType type, XFile xFile) throws IOException {
    if (type instanceof XPrimitiveType) {
      return parsePrimitiveData((XPrimitiveType) type);
    } else if (type instanceof XArrayType) {
      return parseArrayData((XArrayType) type, xFile);
    } else if (type instanceof XTemplate) {
      return parseTemplateData((XTemplate) type, xFile);
    } else if (type instanceof XChildrenType) {
      return parseChildrenData((XChildrenType) type, xFile);
    } else {
      throw new ParseException("invalid type to parse");
    }
  }

  public Object parsePrimitiveData(XPrimitiveType type) throws IOException {
    consumeWhitespaceAndComment();
    if (type.equals(XPrimitiveType.WORD)
      || type.equals(XPrimitiveType.DWORD)
      || type.equals(XPrimitiveType.BYTE)
      || type.equals(XPrimitiveType.CHAR)
      || type.equals(XPrimitiveType.UCHAR)) {
      return parseInt();
    } else if (type.equals(XPrimitiveType.FLOAT)) {
      return parseFloat();
    } else if (type.equals(XPrimitiveType.DOUBLE)) {
      return parseDouble();
    } else if (type.equals(XPrimitiveType.STRING)) {
      return parseQuotedString();
    } else {
      throw new ParseException("given type is not a primitive type");
    }
  }

  public List<Object> parseArrayData(XArrayType type, XFile xFile) throws IOException {
    List<Object> result = new ArrayList<Object>();

    consumeWhitespaceAndComment();
    if (!isEof()) {
      char lookAhead = peek();
      while (!isEof() && lookAhead != ';') {
        Object data = parseData(type.elementType, xFile);
        result.add(data);
        consumeWhitespaceAndComment();
        if (!isEof()) {
          lookAhead = peek();
          if (lookAhead == ',') {
            matchToken(",");
            consumeWhitespaceAndComment();
            lookAhead = peek();
            if (isEof() || lookAhead == ';') {
              throw new ParseException("a new element expected");
            }
          }
        }
      }
    }

    return result;
  }

  public XTemplateData parseTemplateData(XTemplate template, XFile xFile) throws IOException {
    if (template.name.equals("MeshMaterialList")) {
      return parseMeshMaterialList(template, xFile);
    } else if (template.name.equals("MeshVertexColors")) {
      return parseMeshVertexColors(template, xFile);
    } else {
      XTemplateData result = new XTemplateData(template);
      for (XField field : template.getFields()) {
        result.addValue(parseData(field.getType(), xFile));
        if (!(field.getType() instanceof XChildrenType)) {
          matchToken(";");
        }
      }
      return result;
    }
  }

  public Object parseChildrenData(XChildrenType childrenType, XFile xFile) throws IOException {
    List<Object> result = new ArrayList<Object>();

    consumeWhitespaceAndComment();
    if (!isEof()) {
      char lookAhead = peek();
      while (!isEof() && Character.isLetter(lookAhead)) {
        String templateName = parseIdentifier();
        XType lookedUp = lookUpType(templateName, xFile);
        if (lookedUp instanceof XTemplate) {
          XTemplate template = (XTemplate) lookedUp;
          if (!childrenType.isAllowedAsChild(template)) {
            throw new ParseException("template '"
              + template.name
              + "' is not allowed "
              + "as a child");
          } else {
            matchToken("{");
            result.add(parseTemplateData(template, xFile));
            matchToken("}");
            consumeWhitespaceAndComment();
            if (!isEof()) {
              lookAhead = peek();
            }
          }
        } else {
          throw new ParseException("template name expected");
        }
      }
    }

    return result;
  }

  public XTemplateData parseMeshMaterialList(XTemplate template, XFile xFile) throws IOException {
    XTemplateData result = new XTemplateData(template);

    result.addValue(parseData(template.getField(0).type, xFile));
    matchToken(";");
    result.addValue(parseData(template.getField(1).type, xFile));
    matchToken(";");
    result.addValue(parseData(template.getField(2).type, xFile));
    matchToken(";");
    matchToken(";");
    result.addValue(parseData(template.getField(3).type, xFile));

    return result;
  }

  public XTemplateData parseMeshVertexColors(XTemplate template, XFile xFile) throws IOException {
    XTemplateData result = new XTemplateData(template);

    XTemplate colorRgbaTemplate = (XTemplate) lookUpType("ColorRGBA", xFile);
    XTemplate indexedColorTemplate = (XTemplate) lookUpType("IndexedColor", xFile);

    int count = parseInt();
    matchToken(";");
    result.addValue(count);

    List<Object> indexedColorArray = new ArrayList<Object>();
    for (int i = 0; i < count; i++) {
      int index = parseInt();
      matchToken(";");
      float red = parseFloat();
      matchToken(";");
      float green = parseFloat();
      matchToken(";");
      float blue = parseFloat();
      matchToken(";");
      float alpha = parseFloat();
      matchToken(";");

      XTemplateData indexedColor = new XTemplateData(indexedColorTemplate,
        Arrays.asList(index,
          new XTemplateData(colorRgbaTemplate, Arrays.asList(red, green, blue, alpha))));
      indexedColorArray.add(indexedColor);

      if (i != count - 1) {
        matchToken(",");
      } else {
        matchToken(";");
      }
    }
    result.addValue(indexedColorArray);

    return result;
  }

  public void parseXFile(XFile xFile) throws IOException {
    parseHeader(xFile);

    consumeWhitespaceAndComment();
    while (!isEof()) {
      String token = parseIdentifier();
      if (token.toUpperCase().equals("TEMPLATE")) {
        putBack(token);
        XTemplate template = parseTemplate(xFile);
        xFile.addTemplate(template);
      } else {
        XType lookedUp = lookUpType(token, xFile);
        if (!(lookedUp instanceof XTemplate)) {
          throw new ParseException("template data expected");
        } else {
          matchToken("{");
          XTemplateData data = parseTemplateData(
            (XTemplate) lookedUp, xFile);
          xFile.addData(data);
          matchToken("}");
        }
      }
      consumeWhitespaceAndComment();
    }
  }

  public static XFile parse(String fileName) throws IOException {
    return parse(fileName, "Shift-JIS");
  }

  public static XFile parse(String fileName, String encoding) throws IOException {
    XFile result = new XFile();

    FileInputStream fileInput = new FileInputStream(fileName);
    InputStreamReader inputReader = new InputStreamReader(fileInput, encoding);
    BufferedReader fin = new BufferedReader(inputReader);
    XTextFileParser parser = new XTextFileParser(fin);
    parser.parseXFile(result);

    File theFile = new File(fileName);
    File absoluteFile = theFile.getAbsoluteFile();
    result.setDirectory(absoluteFile.getParent());

    return result;
  }
}
