package com.bmazurkiewicz01.client.view;

public enum View {
    LOGIN("/com/bmazurkiewicz01/client/view/login.fxml"),
    REGISTER("/com/bmazurkiewicz01/client/view/register.fxml"),
    ADD_USER_ALERT("/com/bmazurkiewicz01/client/view/addUserAlert.fxml"),
    MAIN("/com/bmazurkiewicz01/client/view/main.fxml"),
    ADD_ROOM_DIALOG("/com/bmazurkiewicz01/client/view/addRoomDialog.fxml"),
    USER_PROPERTIES_VALIDATION("/com/bmazurkiewicz01/client/view/userPropertiesValidation.fxml"),
    ROOM("/com/bmazurkiewicz01/client/view/room.fxml");


    private final String fileName;

    View(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
