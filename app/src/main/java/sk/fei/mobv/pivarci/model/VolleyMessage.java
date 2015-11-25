package sk.fei.mobv.pivarci.model;

public class VolleyMessage {
    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_ERROR = -1;
    private Long id;
    private String text;
    private String sent;
    private int status = STATUS_SENT;

    public VolleyMessage(Long id, String text, String sent, int status) {
        this.id = id;
        this.text = text;
        this.sent = sent;
        this.status = status;
    }

    public VolleyMessage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
