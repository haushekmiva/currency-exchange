package utils;

public class LogMessageCreator {
    public static final String NO_SPECIFIC_CODE = "";

    public static String createMessage(MessageType messageType, OperationType operationType, String tableName, String elementCode) {
        String message = "";
        switch (messageType) {
            case FAILED -> message += "Failed ";
        }

        message += "to ";

        switch (operationType) {
            case ADD -> message += "add to ";
            case GET -> message += "get from ";
            case UPDATE -> message += "update in ";
        }

        message += tableName + " ";

        if (!operationType.equals(OperationType.ADD)) {
            if (elementCode == NO_SPECIFIC_CODE) {
                message += "all elements";
            } else message += "an element with code = " + elementCode + " ";
        }

        return message;



    }

}
