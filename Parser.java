// Parser class for CE204 assignment 2, 2015
// teen methods need to be modified to use your contructor(s) - seven for part 3 and three for part 6
// (these are the static ExpTree methods at the end of the Parser class)
// these methods currently return null to allow this file to compile
// (in order to compile the current version you must have an ExpTree class in a file ExpTree.java
// but it doesn't have to have any attributes or methods)

import java.io.*;

class ParseException extends RuntimeException {
    public ParseException(String s) {
        super("Invalid expression: "+s);
    }
}

public class Parser
{ private Lexer lex;

    public Parser()
    { lex = new Lexer();
    }

    public ExpTree parseLine()
    { lex.getToken();
        ExpTree result = parseExp(true);
        if (lex.token==Lexer.where)
        { lex.getToken();
            ExpTree defs = parseDefList();
            result = makeWhereTree(result, defs);
        }
        if (lex.token!=Lexer.semico) {
            throw new ParseException("semicolon expected");
        }
        return result;
    }

    private ExpTree parseExp(boolean idsAllowed)
    { ExpTree result = parseTerm(idsAllowed);
        { while (lex.token==Lexer.plus || lex.token==Lexer.minus)
        { int op = lex.token;
            lex.getToken();
            if (op==Lexer.plus)
                result = makePlusTree(result, parseTerm(idsAllowed));
            else
                result = makeMinusTree(result, parseTerm(idsAllowed));
        }
        }
        return result;
    }

    private ExpTree parseTerm(boolean idsAllowed)
    { ExpTree result = parsePow(idsAllowed);
        { while (lex.token==Lexer.times || lex.token==Lexer.div)
        { int op = lex.token;
            lex.getToken();
            if (op==Lexer.times)
                result = makeTimesTree(result, parsePow(idsAllowed));
            else
                result = makeDivideTree(result, parsePow(idsAllowed));
        }
        }
        return result;
    }

    private ExpTree parsePow(boolean idsAllowed)
    { ExpTree result = parseOpd(idsAllowed);
        if (lex.token==Lexer.pow)
        { lex.getToken();
            result = makePowerTree(result, parsePow(idsAllowed));
        }
        return result;
    }

    private ExpTree parseOpd(boolean idsAllowed)
    { ExpTree result;
        switch(lex.token)
        { case Lexer.num:
            result = makeNumberLeaf(lex.numval);
            lex.getToken();
            return result;
            case Lexer.id:
                if (!idsAllowed)
                    throw new ParseException("identifier not allowed in identifier definition");
                result = makeIdLeaf(lex.idval);
                lex.getToken();
                return result;
            case Lexer.lp:
                lex.getToken();
                result = parseExp(idsAllowed);
                if (lex.token!=Lexer.rp)
                    throw new ParseException("right parenthesis expected");
                lex.getToken();
                return result;
            default:
                throw new ParseException("invalid operand");
        }
    }

    private ExpTree parseDefList()
    { ExpTree result = parseDef();
        while (lex.token==Lexer.and)
        { lex.getToken();
            result = makeAndTree(result, parseDef());
        }
        return result;
    }

    private ExpTree parseDef()
    { if (lex.token!=Lexer.id)
        throw new ParseException("definition must start with identifier");
        char id = lex.idval;
        if (Character.isUpperCase(id))
            throw new ParseException("upper-case identifiers cannot be used in definition list");
        lex.getToken();
        if (lex.token!=Lexer.eq)
            throw new ParseException("'=' expected");
        lex.getToken();
        return makeDefTree(id, parseExp(false));
    }

    // the next seven methods need to be modified for part 3 of the assignment

    static ExpTree makeNumberLeaf(int n)
    { return new ExpTree(ExpTree.NumNode, new Integer(n), null, null);
        // this method should return a new number leaf with value n created using your constructor
        // if you've used the abstract class approach you will probably need something like
        // return new NumLeaf(n);
        // if you've used an ExpTree class that stores the node kind you will probably need something like
        // return new ExpTree(ExpTree.numNode, n , null, null);
    }

    static ExpTree makeIdLeaf(char c)
    { return new ExpTree(ExpTree.IDNode, c, null, null);
        // this method should return a new id leaf with value c
    }

    static ExpTree makePlusTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.OpNode, '+', l, r);
        // this method should return a new plus node with children l and r created using your constructor
        // if you've used the abstract class approach you will probably need something like
        // return new OpNode('+', l, r);
        // or
        // return new PlusNode(l, r);
        // if you've used an ExpTree class that stores the node kind you will probably need something like
        // return new ExpTree(ExpTree.opMode, '+', l, r);
    }

    static ExpTree makeMinusTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.OpNode, '-', l, r);
        // this method should return a new minus node with children l and r
    }

    static ExpTree makeTimesTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.OpNode, '*', l, r);
        // this method should return a new times node with children l and r
    }

    static ExpTree makeDivideTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.OpNode, '/', l, r);
        // this method should return a new divide node with children l and r
    }

    static ExpTree makePowerTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.OpNode, '^', l, r);
        // this method should return a new power (^) node with children l and r
    }

    // the next three methods need to be modified for part 6 of the assignment - do not modify them if you have not attempted part 6

    static ExpTree makeWhereTree(ExpTree l, ExpTree r)
    { // remove the following line if you modify this method; leave it here if you do not attempt part 6
        return new ExpTree(ExpTree.WhereNode, "where", l, r);
        // this method should return a new 'where' node with children l and r
    }

    static ExpTree makeAndTree(ExpTree l, ExpTree r)
    { return new ExpTree(ExpTree.AndNode, "and", l, r);
        // this method should return a new 'and' node with children l and r
    }

    static ExpTree makeDefTree(char c, ExpTree t)
    { return new ExpTree(ExpTree.IDNode, c, makeIdLeaf(c), t);
        // this method should return a new definition node with identifier c and child t
        // if your definition nodes have 2 children you should put a new id leaf in the left child and use t as the right child
    }
}

class Lexer
{ static final int err = 0, num = 1, id = 2, plus = 3, minus = 4, times = 5, div = 6, pow = 7,
        lp = 8, rp = 9, semico = 10, where = 11, and = 12, eq = 13;
    int token;
    char idval;
    int numval;
    private String line = "";

    Lexer()
    { BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        do
            try
            { line = buf.readLine().trim();
            }
            catch(Exception e)
            { System.out.println("Error in input");
                System.exit(1);
            }
        while (line.length()==0);
    }

    void getToken()
    { if (line.length()==0)
        token = err;
    else switch (line.charAt(0))
        { case '+':
            token = plus;
            line = line.substring(1).trim();
            break;
            case '-':
                token = minus;
                line = line.substring(1).trim();
                break;
            case '*':
                token = times;
                line = line.substring(1).trim();
                break;
            case '/':
                token = div;
                line = line.substring(1).trim();
                break;
            case '^':
                token = pow;
                line = line.substring(1).trim();
                break;
            case '(':
                token = lp;
                line = line.substring(1).trim();
                break;
            case ')':
                token = rp;
                line = line.substring(1).trim();
                break;
            case ';':
                token = semico;
                line = line.substring(1).trim();
                break;
            case '=':
                token = eq;
                line = line.substring(1).trim();
                break;
            default:
                if (Character.isDigit(line.charAt(0)))
                { token = num;
                    numval = line.charAt(0) - '0';
                    int i = 1;
                    while (i<line.length()&&Character.isDigit(line.charAt(i)))
                    { numval = numval*10+line.charAt(i)-'0';
                        i++;
                    }
                    line = line.substring(i).trim();
                }
                else if (Character.isLetter(line.charAt(0)))
                { char c = line.charAt(0);
                    if (c=='w' && line.length()>=5 && line.charAt(1)=='h' && line.charAt(2)=='e' && line.charAt(3)=='r' &&
                            line.charAt(4)=='e')
                    { token = where;
                        line = line.substring(5).trim();
                    }
                    else if (c=='a' && line.length()>=3 && line.charAt(1)=='n' && line.charAt(2)=='d')
                    { token = and;
                        line = line.substring(3).trim();
                    }
                    else
                    { token = id;
                        idval = c;
                        line = line.substring(1).trim();
                    }
                }
                else
                    token = err;
        }
    }
}
