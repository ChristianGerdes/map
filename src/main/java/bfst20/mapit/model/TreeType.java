package bfst20.mapit.model;

public enum TreeType {
    ISLAND              (0, Double.POSITIVE_INFINITY),
    FOREST              (1, Constants.LEVEL_3_RENDER_DISTANCE),
    SURFACE             (2, Constants.LEVEL_6_RENDER_DISTANCE),
    WATER               (3, Constants.LEVEL_4_RENDER_DISTANCE),
    OTHER               (4, Constants.LEVEL_7_RENDER_DISTANCE),
    TERMINALS           (5, Constants.LEVEL_5_RENDER_DISTANCE),
    BUILDINGS           (6, Constants.LEVEL_8_RENDER_DISTANCE),
    PATHS               (20, Constants.LEVEL_8_RENDER_DISTANCE),
    SMALLROADS          (7, Constants.LEVEL_7_RENDER_DISTANCE),
    NORMALROADS         (8, Constants.LEVEL_6_RENDER_DISTANCE),
    BIGROADS            (9, Constants.LEVEL_3_RENDER_DISTANCE),
    MAJORROADS          (10, Constants.LEVEL_1_RENDER_DISTANCE),
    CARTOGRAPHICS       (11, Constants.LEVEL_9_RENDER_DISTANCE),
    RELAXEDROADS        (12, Constants.LEVEL_6_RENDER_DISTANCE),
    ROUTES              (13, Constants.LEVEL_1_RENDER_DISTANCE),
    INTERSECTIONNODES   (14, Constants.LEVEL_9_RENDER_DISTANCE),
    ROADNAMES           (15, Constants.LEVEL_9_RENDER_DISTANCE),
    HAMLETS             (16, Constants.LEVEL_6_RENDER_DISTANCE),
    VILLAGES            (17, Constants.LEVEL_3_RENDER_DISTANCE),
    TOWNS               (18, Constants.LEVEL_2_RENDER_DISTANCE),
    CITIES              (19, Constants.LEVEL_1_RENDER_DISTANCE);

    public final int order;

    public final double renderDistance;

    private TreeType(int order, double renderDistance) {
        this.order = order;
        this.renderDistance = renderDistance;
    }

    private static class Constants {
		public static final double LEVEL_1_RENDER_DISTANCE = 0.012076358517526704;
		public static final double LEVEL_2_RENDER_DISTANCE = 9.202326493800105E-4;
        public static final double LEVEL_3_RENDER_DISTANCE = 5.082770288769551E-4;
        public static final double LEVEL_4_RENDER_DISTANCE = 2.5620518462915514E-4;
        public static final double LEVEL_5_RENDER_DISTANCE = 1.7347140076780595E-4;
        public static final double LEVEL_6_RENDER_DISTANCE = 8.025784419386595E-5;
        public static final double LEVEL_7_RENDER_DISTANCE = 3.8720750000757885E-5;
        public static final double LEVEL_8_RENDER_DISTANCE = 6.236896663708177E-6;
        public static final double LEVEL_9_RENDER_DISTANCE = 3.906179291654331E-6;
	}
}