package softuniBlog.bindingModel;

import javax.validation.constraints.NotNull;
import javax.xml.crypto.Data;
import java.time.LocalDate;

public class ArticleBindingModel {
    @NotNull
    private String title;

    @NotNull
    protected String content;

    private Integer categoryId;

    private Data date;




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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Data getDate() {
        return date;
    }

    public void setDate(Data date) {
        this.date = date;
    }


}
