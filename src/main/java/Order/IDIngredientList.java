package Order;

import java.util.ArrayList;
import java.util.List;

public class IDIngredientList {
    private List<String> ingredients;

    public IDIngredientList() {
        this.ingredients = new ArrayList<String>();
    }

    public IDIngredientList(List<String>  ingredients) {
        this.ingredients = ingredients;
    }

    public List<String>  getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String>  ingredients) {
        this.ingredients = ingredients;
    }

    public void addId (String id) {
        this.ingredients.add(id);
    }
}
