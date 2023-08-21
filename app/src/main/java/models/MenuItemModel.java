package models;


public class MenuItemModel {
    private String menuUid;
    private String name;
    private String price;
    private String specification;
    private String is_active;
    private String category;
    private String menu_spot_image;
    private String description;
    private String discount;
    private String category_index;

    public MenuItemModel() {
    }

    public MenuItemModel(String menuUid,String name, String price, String specification, String is_active, String category,String menu_spot_image,String description,String discount,String category_index) {
        this.menuUid = menuUid;
        this.name = name;
        this.price = price;
        this.specification = specification;
        this.is_active = is_active;
        this.category = category;
        this.menu_spot_image=menu_spot_image;
        this.description=description;
        this.discount=discount;
        this.category_index=category_index;
    }

    public String getMenuUid() {
        return menuUid;
    }

    public void setMenuUid(String menuUid) {
        this.menuUid = menuUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMenu_spot_image() {
        return menu_spot_image;
    }

    public void setMenu_spot_image(String menu_spot_image) {
        this.menu_spot_image = menu_spot_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCategory_index() {
        return category_index;
    }

    public void setCategory_index(String category_index) {
        this.category_index = category_index;
    }
}
