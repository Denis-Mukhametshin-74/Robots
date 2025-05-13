package api;

import java.io.PrintWriter;
import java.util.Scanner;

public interface StateSavable
{
    void saveState(PrintWriter writer);
    void restoreState(Scanner scanner);
}
