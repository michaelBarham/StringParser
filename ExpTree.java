import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael on 09/03/2015.
 */
public class ExpTree{
    static final int OpNode = 1, NumNode = 2, IDNode = 3, WhereNode = 4, AndNode = 5;
    // whereNodeComplete used to check it can add to variable map or get from map
    private static boolean whereNodeComplete = false;
    protected ExpTree lChild, rChild;
    private int Type;
    private Object Value;
    // used to store the variable name and final number produced by maths
    static private HashMap<String, Integer> whereVari = new HashMap<String, Integer>();

    public ExpTree(int T, Object val, ExpTree l, ExpTree r) {
        Value = val;
        // stores node type number as such as above: static final int OpNode = 1,...
        Type = T;
        lChild = l;
        rChild = r;
    }

    public int Evaluation(){
        // goes through +-/*^ or number or variable or Where or And
        switch (Type) {
            case 1:
                // goes through +-/*^
                if(Value.toString().charAt(0) == '+'){
                    //System.out.println("hit a + block");
                    return lChild.Evaluation() + rChild.Evaluation();
                } else if(Value.toString().charAt(0) == '*'){
                    //System.out.println("hit a * block");
                    return lChild.Evaluation() * rChild.Evaluation();
                } else if(Value.toString().charAt(0) == '/'){
                    //System.out.println("hit a / block");
                    return lChild.Evaluation() / rChild.Evaluation();
                } else if(Value.toString().charAt(0) == '-'){
                    //System.out.println("hit a - block");
                    return lChild.Evaluation() - rChild.Evaluation();
                } else if(Value.toString().charAt(0) == '^'){
                    //System.out.println("hit a ^ block");
                    if(rChild.Evaluation() > 0) {
                        return (int) Math.pow(lChild.Evaluation(), rChild.Evaluation());
                    } else {
                        throw new ParseException("Power less than zero");
                    }
                } else{
                    //System.out.println("hit a blah block");
                    throw new ParseException("invalid Character");
                }
            case 2:
                // return integer
                //System.out.println("hit leaf return: " + Value.toString());
                return (Integer)Value;
            case 3:
                // variable
                // if uppercase letter
                if( 64 < (int)Value.toString().charAt(0) && (int)Value.toString().charAt(0) < 91){
                    return (int)Value.toString().charAt(0) - 64;
                } else if( 96 < (int)Value.toString().charAt(0) && (int)Value.toString().charAt(0) < 123){
                    // if lowercase
                    if(!whereNodeComplete){
                        // add if not existing and still in where node
                        //System.out.println(rChild.Evaluation());
                        whereVari.put(Value.toString(), rChild.Evaluation());
                        // return 0 just for completeness
                        return 0;
                    } else if(whereVari.containsKey(Value.toString())){
                        // check if exists and return value
                        return whereVari.get(Value.toString());
                    } else {
                        // if non-existent return 0
                        //System.out.println("Failed to find variable");
                        return 0;
                    }
                } else {
                    // for completeness
                    throw new ParseException("invalid Character used for variable");
                }
            case 4:
                // where node do eval on rChild but don't return as this is pointless, then return lChild
                //System.out.println("Val stored case 4: " + Value);
                rChild.Evaluation();
                whereNodeComplete = true;
                return lChild.Evaluation();
            case 5:
                //System.out.println("Val stored case 5: " + Value);
                //System.out.println("entered and node");
                // calculate variables however don't return anything because this would be pointless
                rChild.Evaluation();
                lChild.Evaluation();
                return 0;
            default:
                // for completeness
                System.out.println("Type: " + Type);
                System.out.println("Value: " + Value);
                throw new ParseException("invalid Character");
        }
    }

    public String DisplayInOrder() {
        // call main function
        String temp = DisplayInOrderBeforeCut();
        // cut beginning and end brackets off
        if(temp.length() > 1){
            return temp.substring(1, (temp.length() -1));
        }
        return temp;
    }

    private String DisplayInOrderBeforeCut() {
        // if not a where node continue display in order
        if (lChild != null && rChild != null) {// don't need to check rChild as this must have been created with the lChild
            return "(" + lChild.DisplayInOrderBeforeCut() + "" + Value + "" + rChild.DisplayInOrderBeforeCut() + ")";
        } else if(rChild != null){
            return "(" + Value + "" + rChild.DisplayInOrderBeforeCut() + ")";
        } else {
                // or just return value
                return Value + "";
        }
    }

    public String DisplayPostOrder() {
        if (Type != 4) {
            // if not a where node continue display in post order
            if (lChild != null && rChild != null) {
                return Value + lChild.DisplayPostOrder() + rChild.DisplayPostOrder() +"";
            } else {
                // or just return value
                return Value + "";
            }
        } else {
            // if where node just return left child in post order as right child contains variable elements
            if (lChild != null && rChild != null) {
                return lChild.DisplayPostOrder() +"";
            } else {
                // or just return value
                return Value + "";
            }
        }
    }

    @Override
    public String toString() {
        // oops
        return DisplayInOrder();
    }
}