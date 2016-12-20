package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;

public class MessageBindingModel {

    @NotNull
    private String title;

    @NotNull
    protected String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
