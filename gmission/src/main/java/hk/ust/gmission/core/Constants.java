

package hk.ust.gmission.core;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "hk.ust.gmission";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "gMission";



        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;
        public static final String USER_ID = "USER_ID";
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}

        public static final String CONTENT_TYPE_JSON = "application/json";
        public final static String URL_BASE = "http://lccpu3.cse.ust.hk/gmission-dev";
        public final static String URL_REST = "/rest";
        public final static String URL_IMAGE = "/image";
        public final static String URL_VIDEO = "/video";
        public final static String URL_AUDIO = "/audio";
        public  final static String URL_IMAGE_ORI = URL_BASE + URL_IMAGE + "/original";
        public  final static String URL_VIDEO_ORI = URL_BASE + URL_VIDEO + "/original";
        public  final static String URL_VIDEO_THUMB = URL_BASE + URL_VIDEO + "/thumb";
        public  final static String URL_AUDIO_ORI = URL_BASE + URL_AUDIO + "/original";

        public  final static String URL_AUTH = URL_BASE + "/user/auth";
        public  final static String URL_REG = URL_BASE + "/user/register";

        public static final String URL_CAMPAIGNS_FRAG =  "/rest/campaign";

        public static final String URL_HITS_FRAG =  "/rest/hit";

        public static final String URL_MESSAGES_FRAG =  "/rest/message";

        public static final String URL_SELECTIONS_FRAG =  "/rest/selection";

        public static final String URL_ANSWERS_FRAG =  "/rest/answer";

        public static final String URL_ATTACHMENTS_FRAG =  "/rest/attachment";

        public static final String URL_USERS_FRAG =  "/rest/user";

        public static final String URL_LOCATIONS_FRAG =  "/rest/location";

        public static final String URL_COORDINATES_FRAG =  "/rest/coordinate";




        public static String PARAM_SESSION_TOKEN = "sessionToken";
        public static String PARAM_USER_ID = "userID";
        public static String PARAM_USERNAME = "username";


    }


    public static final class Extra {
        private Extra() {}

        public static final String USER = "user";
        public static final String HIT = "hit";


        public static final String CAMPAIGN_ID = "campaign_id";
        public static final String MESSAGE_ID = "message_id";


    }

    public static final class Intent {
        private Intent() {}

        /**
         * Action prefix for all intents created
         */
        public static final String INTENT_PREFIX = "hk.ust.gmission.";

    }

    public static class Notification {
        private Notification() {
        }


    }



}


