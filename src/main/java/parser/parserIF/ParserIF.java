package parser.parserIF;

import parser.entity.Document;
import parser.entity.User;
import parser.entity.View;

import java.util.Date;

public interface ParserIF {
    Long parseActionId(String line);

    Document parseDocument(String line);

    View parseView(String line);

    Date parseDate(String line);

    User parseUser(String line);

}
