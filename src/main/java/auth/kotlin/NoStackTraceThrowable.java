package auth.kotlin;


public class NoStackTraceThrowable extends RuntimeException {

    public NoStackTraceThrowable(final String msg){
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public String toString() {
        return "java.lang.ArrayIndexOutOfBoundsException: 1\n" +
                "        at \u0003\u0001\u0004.\u0007\u0005(Unknown Source)\n" +
                "        at \u0003\u0001\u0004.<init>(Unknown Source)\n" +
                "        at \u0001\u0003\u0004.<init>(Unknown Source)\n" +
                "        at \u0001\u0003\u0004.\u0007\u0005\u0004(Unknown Source)";
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
