import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class util {
    private static boolean a = false, s = false, f = false;
    private static String filename_int = "integers.txt", filename_fl = "floats.txt", filename_str = "strings.txt";
    private static ArrayList<String> filenames = new ArrayList<String>();
    public static void main(String[] args) throws Exception
    {
        if ((get_args(args) == 0) || (start() == 0)) return;
        System.out.println("Done.");
    }

    private static int get_args(String[] args)
    {
        boolean o = false, p = false;
        if (args.length < 1)
        {
            System.err.println("There is/are no argument(s)");
            return 0;
        }
        for (String str : args)
        {
            if (o || p)
            {
                if (o) str += "/";
                add_pref(str);
                o = false;
                p = false;
                continue;
            }
            switch (str)
            {
                case "-o": o = true; break;
                case "-p": p = true; break;
                case "-a": a = true; break;
                case "-s": s = true; break;
                case "-f": f = true; break;
                default:
                    if (!check_input_filename(str)) return 0;
                    filenames.add(str);
                    break;
            }
        }
        if (!delete_output_files()) return 0;
        return 1;
    }
    private static void add_pref(String pref)
    {
        String[] filename_int2 = filename_int.split("/"),
            filename_fl2 = filename_fl.split("/"),
            filename_str2 = filename_str.split("/");
        filename_int = "";
        filename_fl = "";
        filename_str = "";
        if (!pref.contains("/") && (filename_int2.length > 1))
        {
            for (int i = 0; i < filename_int2.length - 1; i++)
            {
                filename_int += filename_int2[i] + "/";
                filename_fl += filename_fl2[i] + "/";
                filename_str += filename_str2[i] + "/";
            }
        }
        filename_int += pref + filename_int2[filename_int2.length - 1];
        filename_fl += pref + filename_fl2[filename_fl2.length - 1];
        filename_str += pref + filename_str2[filename_str2.length - 1];
    }
    private static boolean check_input_filename(String filename)
    {
        File file = new File(filename);
        String[] split = filename.split("\\.");
        if (!split[split.length - 1].equals("txt"))
        {
            System.err.println("File \"" + filename + "\" isn't .txt");
            return false;
        }
        else if (!file.exists() || file.isDirectory())
        {
            System.err.println("File \"" + filename + "\" doesn't exist/is in another directory");
            return false;
        }
        return true;
    }
    private static boolean delete_output_files()
    {
        if (a) return true;
        try
        {
            Files.deleteIfExists(new File(filename_int).toPath());
            Files.deleteIfExists(new File(filename_fl).toPath());
            Files.deleteIfExists(new File(filename_str).toPath());
        }
        catch (IOException e)
        {
            System.err.println("Input files error: " + e.getMessage());
            return false;
        }
        return true;
    }

    private static int count_int = 0, count_fl = 0, count_str = 0;
    private static long min_int = 0, max_int = 0, sum_int = 0;
    private static double min_fl = 0, max_fl = 0, sum_fl = 0, mean_int, mean_fl;
    private static int min_str = 0, max_str = 0;
    private static ArrayList<String> list_int = new ArrayList<String>(),
            list_fl = new ArrayList<String>(),
            list_str = new ArrayList<String>();
    private static int start()
    {
        if (filenames.isEmpty())
        {
            System.err.println("There is/are no input file(s)");
            return 0;
        }
        for (String filename : filenames)
        {
            list_int.clear();
            list_fl.clear();
            list_str.clear();
            if ((read_n_list(filename) == 0) ||
                (write(filename_int, list_int) == 0) ||
                (write(filename_fl, list_fl) == 0) ||
                (write(filename_str, list_str) == 0))
                return 0;
            stats();
        }
        if (count_int > 0) mean_int = sum_int / count_int;
        if (count_fl > 0) mean_fl = sum_fl / count_fl;
        if (s || f)
        {
            System.out.print("_______________\nStatistics on added rows");
            print_stats(filename_int, Integer.toString(count_int), Long.toString(min_int), Long.toString(max_int), Long.toString(sum_int), Double.toString(mean_int));
            print_stats(filename_fl, Integer.toString(count_fl), Double.toString(min_fl), Double.toString(max_fl), Double.toString(sum_fl), Double.toString(mean_fl));
            print_stats(filename_str, Integer.toString(count_str), Integer.toString(min_str), Integer.toString(max_str));
        }
        return 1;
    }
    private static int read_n_list(String filename)
    {
        try
        {
            Scanner s = new Scanner(new FileReader(filename, StandardCharsets.UTF_8));
            while (s.hasNextLine())
            {
                String str = s.nextLine();
                if (str.matches("^-?[0-9]+"))
                    list_int.add(str);
                else if (str.matches("^-?[0-9]+\\.[0-9]+((E|e)(-|\\+)[0-9]+)?"))
                    list_fl.add(str);
                else
                    list_str.add(str);
            }
            s.close();
        }
        catch (IOException e)
        {
            System.err.println("Error while reading the file \"" + filename + "\": " + e.getMessage());
            return 0;
        }
        return 1;
    }
    private static int write(String filename, ArrayList<String> list)
    {
        try
        {
            PrintWriter w = new PrintWriter(new FileWriter(filename, StandardCharsets.UTF_8, a));
            for (String str : list)
                w.write(str + "\n");
            w.close();
        }
        catch (IOException e)
        {
            System.err.println("Error while writing in the file \"" + filename + "\": " + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static void stats()
    {
        if (!a && ((count_int > 0) || (count_fl > 0) || (count_str > 0)))
        {
            count_int = 0; count_fl = 0; count_str = 0;
            min_int = 0; max_int = 0; sum_int = 0;
            min_fl = 0; max_fl = 0; sum_fl = 0;
            min_str = 0; max_str = 0;
        }
        count_int += list_int.size();
        count_fl += list_fl.size();
        count_str += list_str.size();
        if (!f) return;

        int i = 0;
        for (String str : list_int)
        {
            try
            {
                long num = Long.parseLong(str);
                sum_int += num;
                if (i++ == 0) min_int = max_int = num;
                else
                {
                    min_int = Long.min(min_int, num);
                    max_int = Long.max(max_int, num);
                }
            }
            catch (Exception e) { System.err.println("Error while parsing int \"" + str + "\": " + e.getMessage() +
                "\nThis number will not be used in the full statistics."); }
        }

        i = 0;
        for (String str : list_fl)
        {
            try
            {
                double num = Double.parseDouble(str);
                sum_fl += num;
                if (i++ == 0) min_fl = max_fl = num;
                else
                {
                    min_fl = Double.min(min_fl, num);
                    max_fl = Double.max(max_fl, num);
                }
            }
            catch (Exception e) { System.err.println("Error while parsing float \"" + str + "\": " + e.getMessage() +
                "\nThis number will not be used in the full statistics."); }
        }

        i = 0;
        for (String str : list_str)
        {
            int num = str.length();
            if (i++ == 0) min_str = max_str = num;
            else
            {
                min_str = Integer.min(min_str, num);
                max_str = Integer.max(max_str, num);
            }
        }
    }
    private static void print_stats(String filename, String count, String min, String max, String sum, String mean)
    {
        System.out.println("\nFile \"" + filename + "\": " + count + " row(s) added");
        if (f)
            System.out.println("- minimum number: " + min +
                "\n- maximum number: " + max +
                "\n- sum: " + sum +
                "\n- mean: " + mean);
    }
    private static void print_stats(String filename, String count, String min, String max)
    {
        System.out.println("\nFile \"" + filename + "\": " + count + " row(s) added");
        if (f)
            System.out.println("- minimum string length: " + min +
                "\n- maximum string length: " + max);
    }
}