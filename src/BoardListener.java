import java.awt.event.*;
import java.util.EventListener;

public interface BoardListener extends EventListener {


    void boardUpdated();
    
    char promptForPromote(Move move);

}
