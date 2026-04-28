package com.capocann.site12.tactical;

import java.io.*;
import java.util.*;

public class TacticalCSVLoader {
    public static List<Map<String,String>> loadCsv(File f) throws IOException {
        List<Map<String,String>> out = new ArrayList<>();
        if (f == null || !f.exists()) return out;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            String header = r.readLine();
            if (header == null) return out;
            String[] cols = header.split(",");
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(",", -1);
                Map<String,String> map = new HashMap<>();
                for (int i=0;i<cols.length && i<parts.length;i++) map.put(cols[i].trim(), parts[i].trim());
                out.add(map);
            }
        }
        return out;
    }

    public static File findDataFile(File root, String... names) {
        for (String n: names) {
            File f = new File(root, n);
            if (f.exists()) return f;
        }
        return null;
    }
}
