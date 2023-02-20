import java.io.File;
import java.io.FilenameFilter;

public class PGNFilter implements FilenameFilter {

    public PGNFilter() {
        super();
    }

    @Override
    public boolean accept(File dir, String name) {
        int extensionIndex = name.lastIndexOf(".") + 1;

        if (extensionIndex < name.length() && extensionIndex > -1) {

            if (name.substring(extensionIndex).toLowerCase().equals("pgn"))
                return true;

        }

        return false;
    }

}