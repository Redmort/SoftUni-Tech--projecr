package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;

public class ReplyBindengModel {
    @NotNull
    protected String content;

    protected  String statusId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }
}
