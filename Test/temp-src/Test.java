package controller;
import annotationClass.*;
import java.util.HashMap;
import modelClass.ModelView;

@AnnotationController()
public class Test {
    private String name = "Anthony";
    private ModelView mv = new ModelView("test",36);

    @Get("kozy")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setMv(ModelView mv) {
        this.mv = mv;
    }

    @Get("test")
    public ModelView getMv() {
        return mv;
    }
}
