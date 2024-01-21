package parser.utils;

import parser.entity.Document;
import parser.entity.Entity;
import parser.entity.User;
import parser.entity.View;
import parser.parserIF.ParserIF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser implements ParserIF {
    private final static String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS";
    private final static String DATE_PATTERN = "\\d{2}.\\d{2}.\\d{4}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}";
    private final static String ACTION_ID_PATTERN = "\\[worker:\\d+\\Waction\\W\\d+\\]";
    private final static String DOCUMENT_PATTERN = "\\(typeId: 'document.*',\\sid: '\\d+',\\sversionId: '\\d+'\\)";
    private final static String VIEW_PATTERN = "\\(typeId: 'view',\\sid: '\\d+',\\sversionId: '\\d+'\\)";
    char quotationMark = '\'';

    @Override
    public Long parseActionId(String line) {
        Pattern pattern = Pattern.compile(ACTION_ID_PATTERN);
        Matcher matcher = pattern.matcher(line);
        String actionString;
        if (matcher.find()) {
            actionString = line.substring(matcher.start(), matcher.end());
            try {
                return Long.parseLong(actionString.substring(actionString.lastIndexOf("-") + 1, actionString.lastIndexOf("]")));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Document parseDocument(String line) {
        Pattern pattern = Pattern.compile(DOCUMENT_PATTERN);
        Document document = (Document) giveMeThisShit(new Document(), pattern, line);
        return document == null ? new Document("NOT_FOUND", -1, -1) : document;
    }

    @Override
    public View parseView(String line) {
        Pattern pattern = Pattern.compile(VIEW_PATTERN);
        View view = (View) giveMeThisShit(new View(), pattern, line);
        return view == null ? new View("NOT_FOUND", -1, -1) : view;
    }

    public static Date parseDates(String line) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(line);
        String dateString = null;
        if (matcher.find()) {
            dateString = line.substring(matcher.start(), matcher.end());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (dateString != null) {
            try {
                Date date = dateFormat.parse(dateString);
                System.out.println("my date: " + dateFormat.format(date));
                return date;
            } catch (ParseException e) {
                //skip
            }
        }
        return null;
    }

    @Override
    public Date parseDate(String line) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(line);
        String dateString = null;
        if (matcher.find()) {
            dateString = line.substring(matcher.start(), matcher.end());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (dateString != null) {
            try {
                Date date = dateFormat.parse(dateString);
                System.out.println("my date: " + dateFormat.format(date));
                return date;
            } catch (ParseException e) {
                //skip
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String line = "22.12 01:42:36.600  WARN [worker:17-action-1816] - 99; Starting action: (typeId: 'action', id: '1816', versionId: 'null') by (typeId: 'user', id: '40767', versionId: '1')";
        System.out.println(parseDates(line));
    }

    @Override
    public User parseUser(String line) {
        Pattern pattern = Pattern.compile("\\(typeId: 'user',\\sid: '\\d+',\\sversionId: '\\d+'\\)");
        User user = (User) giveMeThisShit(new User(), pattern, line);
        return user == null ? new User("NOT_FOUND", -1, -1) : user;
    }

    private Entity giveMeThisShit(Entity entity, Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String documentLine = line.substring(matcher.start(), matcher.end());

            int q1 = documentLine.indexOf(quotationMark) + 1;
            int q2 = documentLine.indexOf(quotationMark, q1);
            String typeId = documentLine.substring(q1, q2);

            documentLine = documentLine.substring(q2 + 1);
            int q3 = documentLine.indexOf(quotationMark) + 1;
            int q4 = documentLine.indexOf(quotationMark, q3);
            long id;
            try {
                id = Long.parseLong(documentLine.substring(q3, q4));
            } catch (NumberFormatException e) {
                id = -1;
            }

            documentLine = documentLine.substring(q4 + 1);
            int q5 = documentLine.indexOf(quotationMark) + 1;
            int q6 = documentLine.indexOf(quotationMark, q5);
            int versionId;
            try {
                versionId = Integer.parseInt(documentLine.substring(q5, q6));
            } catch (NumberFormatException e) {
                versionId = -1;
            }
            entity.setId(id);
            entity.setTypeId(typeId);
            entity.setVersionId(versionId);
            return entity;
        }
        return null;
    }
}
