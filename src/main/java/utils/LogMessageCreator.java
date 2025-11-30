package utils;

public class LogMessageCreator {

    public static String createMessage(MessageType messageType, OperationType operationType, String tableName, int elementId) {
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
            if (elementId == -1) {
                message += "all elements";
            } else message += "an element with id = " + elementId + " ";
        }

        return message;



    }

}
