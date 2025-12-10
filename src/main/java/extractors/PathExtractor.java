package extractors;

import java.util.Optional;

public class PathExtractor {

    public static Optional<String> extractFirstPathSegment(String path) {
        if (path != null && path.length() > 1) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 2 && !pathParts[1].isBlank()) {
                return Optional.of(pathParts[1]);
            }
        }
        return Optional.empty();
    }

}
