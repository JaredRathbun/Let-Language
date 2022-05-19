import java.io.File;
import parser.Parser;
import ast.SyntaxTree;

import java.io.FileNotFoundException;

/**
 * This provides a simple front end to a recursie descent parser for
 * a toy language.
 * @author Zach Kissel
 */
 public class Driver
 {

   /**
    * Prints a usage message to the screen and exits.
    */
   public static void usage()
   {
     System.err.println("Usage: let-lang [-t] <filename> ");
     System.exit(1);
   }

   /**
    * The entry point.
    * @param args the array of strings that represnt the command line arguments.
    */
   public static void main(String[] args)
   {
     Parser parse;
     SyntaxTree ast;
     String fileName;
     boolean doTracing = false;

     // Determine if we are looking at file or command line.
     if (args.length > 2 || args.length < 1)
      usage();

     // Process command line arguments.
     fileName = args[0];
     if (args.length == 2)
     {
        fileName = args[1];
        if (args[0].equals("-t"))
          doTracing = true;
        else
          usage();
     }
     if (fileName.equals("-t"))
      usage();

     // Try to interpret the program.
     try
     {
       parse = new Parser(new File(fileName));

       // Determine if we should turn on tracing.
       if (doTracing)
        parse.toggleTracing();

       ast = parse.parse();
       if (!parse.hasError())
       {
        System.out.println("Parse successful!");
        System.out.println("Result: " + ast.evaluate());
       }
       else
        System.out.println("Parse failed.");
     }
     catch (FileNotFoundException ex)
     {
       System.err.println(ex);
       System.exit(1);
     }
   }
 }
