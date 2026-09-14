public class DuplicateBodyException extends Exception {
    DuplicateBodyException(String name){
        super("A body named \"" + name + "\" is already in this galaxy");
    }
}
