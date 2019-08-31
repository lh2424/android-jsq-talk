package jsq.talk.jsq.UntilFuctionClass;

/**
 * Created by lianghong on 2019/8/13.
 */

public class OperationFuction {

    public static boolean isNullString(String str)
    {
        try {
            if (str.length()>0)
            {
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}
