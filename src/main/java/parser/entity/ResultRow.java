package parser.entity;

import java.util.Date;

public class ResultRow {
    /**
     * дата действия
     */
    private Date date;
    /**
     * id действия
     */
    private Long actionId;
    /**
     * форма, которая была открыта в результате действия
     */
    private View view;
    private Document document;
    /**
     * юзер, который вызывал действие
     */
    private User user;

    public ResultRow() {
    }

    public Date getDate() {
        return date;
    }

    public Long getActionId() {
        return actionId;
    }

    public ResultRow setDate(Date date) {
        this.date = date;
        return this;
    }

    public ResultRow setActionId(Long actionId) {
        this.actionId = actionId;
        return this;
    }

    public ResultRow setView(View view) {
        this.view = view;
        return this;
    }

    public ResultRow setDocument(Document document) {
        this.document = document;
        return this;
    }

    public ResultRow setUser(User user) {
        this.user = user;
        return this;
    }

    public Document getDocument() {
        return document;
    }

    public User getUser() {
        return user;
    }

    public View getView() {
        return view;
    }

    @Override
    public String toString() {
        return "ResultRow{" +
                "date=" + date +
                ", actionId=" + actionId +
                ", view=" + view +
                ", document=" + document +
                ", user=" + user +
                '}';
    }
}
