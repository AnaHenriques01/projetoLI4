package GuiaTuristicoLN;

import java.io.IOException;

public interface IGestAddAndSeek {

    boolean add_favourite(String userId, String placeId);

    boolean remove_favourite(String userId, String placeId);

    boolean login(String name, String password) throws IOException;

    boolean register(String id, String password, String name, String email);

    void logout();
}