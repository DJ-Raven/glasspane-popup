# Glasspane-popup
Custom glasspane popup like jdialog using java swing. to make your desktop app look more beautiful. This custom with animation library and miglayout.

### Sample Code
```java
GlassPanePopup.install(jfram);
```
#### How to popup component
```java
JPanel panel = new JPanel();
panel.setPreferredSize(new Dimension(300, 300));
GlassPanePopup.showPopup(panel);
```
#### Popup with name and option
```java
Option option = new DefaultOption() {
    @Override
    public float opacity() {
        return 0.3f;
    }

    @Override
    public boolean closeWhenClickOutside() {
        return true;
    }

    @Override
    public Color background() {
        return Color.ORANGE;
    }

};
GlassPanePopup.showPopup(panel, option, "ms1");
```
#### How to close popup
```java
//  Close last popup
GlassPanePopup.closePopupLast();

//  Close by name
GlassPanePopup.closePopup("ms1");

//  Close by index
GlassPanePopup.closePopup(0);

//  Close all popup
GlassPanePopup.closePopupAll();
```
#### How to use layout callback for miglayout
```java
Option option = new DefaultOption() {
    @Override
    public LayoutCallback getLayoutCallBack(Component parent) {
        return new DefaultLayoutCallBack(parent) {
            //  implements your code here...
        };
    }
};
```
#### Screenshot

![2023-01-05_225048](https://user-images.githubusercontent.com/58245926/210823188-0a5533cf-6c90-4bdd-86dd-b04ea5b95258.png)

For more option you can request
#### Follow us
Youtube : https://www.youtube.com/c/HelloWorld-Raven/featured</br>
Facebook : https://www.facebook.com/profile.php?id=100067430015260
