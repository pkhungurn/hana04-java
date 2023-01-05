package hana04.distrib.request.params;

public interface SaveInterval {
    SaveInterval SAVE_AT_THE_END = AtTheEnd.v();

    static SaveInterval atTheEnd() {
        return SAVE_AT_THE_END;
    }

    static SaveInterval every(int intervalInSeconds) {
        return new Every(intervalInSeconds);
    }

    boolean isAtTheEnd();
    int getIntervalInSeconds();

    class AtTheEnd implements SaveInterval {
        private static final AtTheEnd instance = new AtTheEnd();

        private AtTheEnd() {
            // NO-OP
        }

        static AtTheEnd v() {
            return instance;
        }

        @Override
        public boolean isAtTheEnd() {
            return true;
        }

        @Override
        public int getIntervalInSeconds() {
            return Integer.MAX_VALUE;
        }
    }

    class Every implements SaveInterval {
        private int intervalInSeconds;

        public Every(int intervalInSeconds) {
            this.intervalInSeconds = intervalInSeconds;
        }

        @Override
        public boolean isAtTheEnd() {
            return false;
        }

        @Override
        public int getIntervalInSeconds() {
            return intervalInSeconds;
        }
    }
}
